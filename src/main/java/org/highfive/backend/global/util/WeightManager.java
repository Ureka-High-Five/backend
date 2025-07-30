package org.highfive.backend.global.util;

import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class WeightManager {

    private final double BASIC_WEIGHT = 0.1;

    public Map<String, Double> calcWeight(Map<String, Integer> metaInfoCountMap) {
        Map<String, Double> weightMap = GenreHolder.toWeightMap();
        for (Map.Entry<String, Integer> entry : metaInfoCountMap.entrySet()) {
            String genreName = entry.getKey();
            int count = entry.getValue();
            weightMap.put(genreName, count * BASIC_WEIGHT);
        }
        return weightMap;
    }
}
