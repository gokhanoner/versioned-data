package com.oner.v1;

import com.hazelcast.core.Offloadable;
import com.hazelcast.core.ReadOnly;
import com.hazelcast.map.EntryBackupProcessor;
import com.hazelcast.map.EntryProcessor;
import com.oner.model.ConfigData;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public class ReadEP implements EntryProcessor<Long, List<ConfigData>>, ReadOnly, Offloadable {

    private final ZonedDateTime effectiveSince;

    public ReadEP() {
        effectiveSince = null;
    }

    public ReadEP(ZonedDateTime effectiveSince) {
        this.effectiveSince = effectiveSince;
    }

    @Override
    public Object process(Map.Entry<Long, List<ConfigData>> entry) {
        List<ConfigData> value = entry.getValue();
        if (value == null) return null;
        //ASSUMPTION: Latest record could not be valid yet, inserted but will be active 1-2 days/weeks etc.
        //so client side should always send a effectiveTime to check.
        //null effectiveTime means return the LATEST one, even if it's not effective for now
        //Of course, you can change that behaviour or remove this control & not allow null values at all
        if (effectiveSince == null) return value.get(0);
        //If null effectiveSince means LATEST valid, above line should be replaced with below & effectiveDate should not be a final field:
        //if(effectiveSince == null) effectiveSince = ZonedDateTime.now();
        //Records are ordered desdending on validUntil. (most up to date is the fist one in the list)
        //Instead of returning the EntryValueV1 wrapper object here, you can return the ConfigData instead
        return value.stream().filter(e -> effectiveSince.compareTo(e.getEffectiveSince()) >= 0).findFirst().orElse(null);
    }

    @Override
    public EntryBackupProcessor<Long, List<ConfigData>> getBackupProcessor() {
        return null;
    }

    @Override
    public String getExecutorName() {
        return OFFLOADABLE_EXECUTOR;
    }
}
