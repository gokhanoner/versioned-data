package com.oner.v1;

import com.hazelcast.core.Offloadable;
import com.hazelcast.map.EntryBackupProcessor;
import com.hazelcast.map.EntryProcessor;
import com.oner.Constants;
import com.oner.model.ConfigData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class WriteEP implements EntryProcessor<Long, List<ConfigData>>, EntryBackupProcessor<Long, List<ConfigData>>, Offloadable {

    private final ConfigData val;

    public WriteEP(ConfigData val) {
        if(val == null) throw new IllegalArgumentException("entry cannot be null");
        this.val = val;
    }

    @Override
    public Object process(Map.Entry<Long, List<ConfigData>> entry) {
        List<ConfigData> value = entry.getValue();
        if(value == null) {
            //No record yet, just create;
            value = new ArrayList<>(1);
        }
        //ASSUMPTION: Latest record could not be valid yet, inserted but will be active 1-2 days/weeks etc.
        //Records are ordered descending based on validity. (most up to date is the fist one in the list)
        value.add(val);
        //Sort ascending on effectiveTime;
        value.sort(Comparator.comparing(ConfigData::getEffectiveSince).reversed());
        //check size, remove the last (oldest) is size > MAX_CONFIGS
        if(value.size() > Constants.VERSION_PER_ENTRY.value()) {
            value.remove(value.size() - 1);
        }
        //Store
        entry.setValue(value);
        return null;
    }

    @Override
    public EntryBackupProcessor<Long, List<ConfigData>> getBackupProcessor() {
        return this;
    }

    @Override
    public void processBackup(Map.Entry<Long, List<ConfigData>> entry) {
        process(entry);
    }

    @Override
    public String getExecutorName() {
        return OFFLOADABLE_EXECUTOR;
    }
}
