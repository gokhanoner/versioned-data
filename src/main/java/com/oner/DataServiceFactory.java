package com.oner;

import com.oner.api.VersionedDataService;
import com.oner.model.ConfigData;
import com.oner.v1.DataServiceV1;
import com.oner.v2.DataServiceV2;

import java.time.ZonedDateTime;

public class DataServiceFactory {

    static VersionedDataService<Long, ConfigData, ZonedDateTime> createDataService(String version) {
        switch (version) {
            case "v1": return new DataServiceV1();
            case "v2": return new DataServiceV2();
            default: throw new IllegalArgumentException("No implementation for version " + version);
        }
    }
}
