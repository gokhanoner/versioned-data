package com.oner;

import com.oner.api.VersionedDataService;
import com.oner.model.ConfigData;

import java.time.ZonedDateTime;

public class LoadClient {
    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Usage: LoadClient [version:v1|v2]");
        }
        TimeIt.timeit(() -> {
            try (VersionedDataService<Long, ConfigData, ZonedDateTime> dataService = DataServiceFactory.createDataService(args[0])) {
                dataService.loadData(Constants.MAX_ENTRY.value(), Constants.VERSION_PER_ENTRY.value());
            }
        });
    }
}
