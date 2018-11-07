package com.oner.v2;

import com.hazelcast.core.IMap;
import com.hazelcast.query.PartitionPredicate;
import com.hazelcast.query.Predicates;
import com.oner.Constants;
import com.oner.api.AbstractDataService;
import com.oner.model.ConfigData;

import java.time.ZonedDateTime;
import java.util.*;

public class DataServiceV2 extends AbstractDataService {

    private IMap<EntryKeyV2, ConfigData> v2;

    public DataServiceV2() {
        createClient();
        v2 = getMap("v2");
    }

    @Override
    public ConfigData getCurrentConfig(Long key) {
        return getConfig(key, ZonedDateTime.now());
    }

    @Override
    public ConfigData getConfig(Long key, ZonedDateTime effectiveSince) {
        return queryByKey(key, effectiveSince);
    }

    @Override
    public ConfigData getLatestConfig(Long key) {
        return queryByKey(key, null);
    }

    @Override
    public void storeConfig(Long key, ConfigData value) {
        //Get the all keys for a given actual key, without effectiveSince part
        Set<EntryKeyV2> keyResults = v2.keySet(new PartitionPredicate(key, Predicates.equal("__key.value", key)));
        EntryKeyV2 wrappedKey = EntryKeyV2.of(key, value.getEffectiveSince());
        //Add results to an arrayList along with new key, sort it & set the new value
        //If there are more than VERSION_PER_ENTRY, remove the first one.
        List<EntryKeyV2> keys = new ArrayList<>(keyResults);
        keys.add(wrappedKey);
        keys.sort(Comparator.comparing(EntryKeyV2::getEffectiveSince).reversed());
        v2.set(wrappedKey, value);
        if (keys.size() > Constants.VERSION_PER_ENTRY.value()) {
            v2.delete(keys.get(keys.size() - 1));
        }
    }

    /**
     * Get the record for given key & effective date
     * @param key
     * @param effectiveSince
     * @return
     */
    private ConfigData queryByKey(Long key, ZonedDateTime effectiveSince) {
        return v2.entrySet(new PartitionPredicate(key, Predicates.equal("__key.value", key)))
                .stream()
                .filter(e -> effectiveSince == null ? true : effectiveSince.compareTo(e.getKey().getEffectiveSince()) >= 0)
                .sorted(Comparator.<Map.Entry<EntryKeyV2, ConfigData>, ZonedDateTime>comparing(e -> e.getKey().getEffectiveSince()).reversed())
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

}
