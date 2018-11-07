package com.oner.api;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.oner.Constants;
import com.oner.model.ConfigData;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.stream.LongStream;

public abstract class AbstractDataService implements VersionedDataService<Long, ConfigData, ZonedDateTime> {

    public static final String LOG_FORMAT = "Config valid for %50s  = %s\n";

    private HazelcastInstance client;

    protected void createClient() {
        System.setProperty("hazelcast.client.statistics.enabled", "true");
        client = HazelcastClient.newHazelcastClient();
    }

    protected <MK, MV> IMap<MK, MV> getMap(String map) {
        return client.getMap(map);
    }

    @Override
    public void loadData(int maxEntry, int configPerKey) {
        LongStream.range(0, Constants.MAX_ENTRY.value())
                .parallel()
                .forEach(i -> {
                    for (int j = 0; j < Constants.VERSION_PER_ENTRY.value(); j++) {
                        storeConfig(i, ConfigData.of("config-" + j, ZonedDateTime.now().minusDays(j)));
                    }
                });
    }

    @Override
    public void readData(int maxEntry, int configPerKey) {
        LongStream.range(0, Constants.MAX_ENTRY.value())
                .forEach(i -> {
                    //Get the latest config (even if it's not valid yet)
                    System.out.format(LOG_FORMAT, "LATEST", getLatestConfig(i));
                    //Get the current valid config
                    System.out.format(LOG_FORMAT, "CURRENT", getCurrentConfig(i));
                    for (int j = 0; j < Constants.VERSION_PER_ENTRY.value(); j++) {
                        //Access configs valid on given date
                        ZonedDateTime date = ZonedDateTime.now().minusDays(j + 1);
                        System.out.format(LOG_FORMAT, date, getConfig(i, date));
                    }
                });
    }


    @Override
    public void close() throws IOException {
        client.shutdown();
    }
}
