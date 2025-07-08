package org.highfive.backend.global.strategy.weight;

import java.util.Map;
import org.highfive.backend.global.strategy.util.GenreHolder;

public class OnboardingGenreWeightStrategy implements GenreWeightStrategy {

    private final double BASIC_WEIGHT = 0.1;

    @Override
    public Map<String, Double> calcWeight(Map<String, Integer> genreCount) {
        Map<String, Double> weightMap = GenreHolder.toWeightMap();
        for (Map.Entry<String, Integer> entry : genreCount.entrySet()) {
            String genreName = entry.getKey();
            int count = entry.getValue();
            weightMap.put(genreName, count * BASIC_WEIGHT);
        }
        return weightMap;
    }
}
