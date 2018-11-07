package com.oner;

import com.oner.api.VersionedDataService;
import com.oner.model.ConfigData;

import java.time.ZonedDateTime;

public class ReadClient {
    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Usage: ReadClient [version:v1|v2]");
        }
        TimeIt.timeit(() -> {
            try (VersionedDataService<Long, ConfigData, ZonedDateTime> dataService = DataServiceFactory.createDataService(args[0])) {
                dataService.readData(Constants.MAX_ENTRY.value(), Constants.VERSION_PER_ENTRY.value());
            }
        });

    }
}
