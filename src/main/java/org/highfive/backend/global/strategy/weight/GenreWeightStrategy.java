package org.highfive.backend.global.strategy.weight;

import java.util.List;
import java.util.Map;

public interface GenreWeightStrategy {

    Map<String, Double> calcWeight(Map<String, Integer> genreCount);
}
