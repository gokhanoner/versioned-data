package com.oner.v2;

import com.hazelcast.core.PartitionAware;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class EntryKeyV2 implements Serializable, PartitionAware<Long> {
    private Long value;
    private ZonedDateTime effectiveSince;

    @Override
    public Long getPartitionKey() {
        return value;
    }

}
