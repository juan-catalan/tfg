package org.juancatalan.edgepaircoverage.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapUtils {
    public static <K, V> Map<V, List<K>> invertMap(Map<K, V> map) {
        Map<V, List<K>> out = new HashMap<>(map.size());

        for (Map.Entry<K, V> kvEntry : map.entrySet()) {
            out.putIfAbsent(kvEntry.getValue(), new ArrayList<>());
            out.get(kvEntry.getValue()).add(kvEntry.getKey());
        }

        return out;
    }
}
