package com.oner.v1;

import com.hazelcast.core.IMap;
import com.oner.api.AbstractDataService;
import com.oner.model.ConfigData;

import java.time.ZonedDateTime;

public class DataServiceV1 extends AbstractDataService {

    private IMap<Long, ConfigData> v1;

    public DataServiceV1() {
        createClient();
        v1 = getMap("v1");
    }

    @Override
    public ConfigData getCurrentConfig(Long key) {
        return getConfig(key, ZonedDateTime.now());
    }

    @Override
    public ConfigData getConfig(Long key, ZonedDateTime effectiveSince) {
        return (ConfigData) v1.executeOnKey(key, new ReadEP(effectiveSince));
    }

    @Override
    public ConfigData getLatestConfig(Long key) {
        return (ConfigData) v1.executeOnKey(key, new ReadEP());
    }

    @Override
    public void storeConfig(Long key, ConfigData value) {
        v1.executeOnKey(key, new WriteEP(value));
    }
}
