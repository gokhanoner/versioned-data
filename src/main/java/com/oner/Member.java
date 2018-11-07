package com.oner;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapIndexConfig;
import com.hazelcast.core.Hazelcast;

public class Member {

    public static void main(String[] args) {
        System.setProperty("hazelcast.phone.home.enabled", "false");
        Config config = new Config();
        config.getManagementCenterConfig().setEnabled(true).setUrl("http://localhost:8080/hazelcast-mancenter/");
        config.getMapConfig("v2").getMapIndexConfigs().add(new MapIndexConfig("__key.value", true));
        Hazelcast.newHazelcastInstance(config);
    }
}
