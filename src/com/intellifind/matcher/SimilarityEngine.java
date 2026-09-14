package com.intellifind.matcher;

import com.intellifind.model.FoundItem;
import com.intellifind.model.LostItem;
import com.intellifind.model.MatchResult;

public class SimilarityEngine {
    private static final double WEIGHT_CATEGORY = 0.20;
    private static final double WEIGHT_BRAND    = 0.15;
    private static final double WEIGHT_MODEL    = 0.15;
    private static final double WEIGHT_COLOR    = 0.10;
    private static final double WEIGHT_LOCATION = 0.15;
    private static final double WEIGHT_DATE     = 0.10;
    private static final double WEIGHT_DESC     = 0.15;

    public static MatchResult calculate(LostItem lost, FoundItem found) {
        double catScore = lost.getCategory().equalsIgnoreCase(found.getCategory()) ? 1.0 : 0.0;

        // Neutral score fallback (0.50) if either party left brand/model unrecorded
        double brandScore = evaluateAttribute(lost.getBrand(), found.getBrand());
        double modelScore = evaluateAttribute(lost.getModel(), found.getModel());
        double colorScore = evaluateAttribute(lost.getColor(), found.getColor());

        double locScore = LocationMatcher.computeLocationScore(lost.getLocation(), found.getLocation());
        double dateScore = DateMatcher.computeDateScore(lost.getDate(), found.getDate());
        double descScore = TextSimilarity.computeJaccardSimilarity(lost.getDescription(), found.getDescription());

        // Hard gate: mismatch in broad category is penalized directly
        if (catScore == 0.0) {
            return new MatchResult(lost, found, 0, brandScore, modelScore, colorScore, locScore, dateScore, descScore, 0.10);
        }

        double total = (catScore * WEIGHT_CATEGORY)
                + (brandScore * WEIGHT_BRAND)
                + (modelScore * WEIGHT_MODEL)
                + (colorScore * WEIGHT_COLOR)
                + (locScore * WEIGHT_LOCATION)
                + (dateScore * WEIGHT_DATE)
                + (descScore * WEIGHT_DESC);

        return new MatchResult(lost, found, catScore, brandScore, modelScore, colorScore, locScore, dateScore, descScore, total);
    }

    private static double evaluateAttribute(String attr1, String attr2) {
        if (attr1 == null || attr2 == null || attr1.trim().isEmpty() || attr2.trim().isEmpty()) {
            return 0.50; // Neutral credit: missing info does not imply mismatch
        }
        return TextSimilarity.computeLevenshteinSimilarity(attr1, attr2);
    }
}