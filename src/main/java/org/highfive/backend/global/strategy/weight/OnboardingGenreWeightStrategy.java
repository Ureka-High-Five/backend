package org.highfive.backend.global.strategy.weight;

import java.util.List;
import java.util.Map;

public class OnboardingGenreWeightStrategy implements GenreWeightStrategy {

    @Override
    public Map<String, Double> calcWeight(Map<String, Integer> genreCount) {
        return Map.of();
    }
}
