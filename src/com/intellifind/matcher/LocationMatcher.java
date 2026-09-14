package com.intellifind.matcher;

import java.util.*;

public class LocationMatcher {
    private static final Map<String, String> LOCATION_GRAPH = new HashMap<>();

    static {
        // Hierarchy: Specific Room -> Area/Building -> Main Campus
        LOCATION_GRAPH.put("reading room", "library");
        LOCATION_GRAPH.put("digital library", "library");
        LOCATION_GRAPH.put("library reference desk", "library");
        LOCATION_GRAPH.put("computer lab 1", "main block");
        LOCATION_GRAPH.put("computer lab 2", "main block");
        LOCATION_GRAPH.put("seminar hall", "main block");
        LOCATION_GRAPH.put("cafeteria indoor", "cafeteria");
        LOCATION_GRAPH.put("cafeteria lawn", "cafeteria");
        LOCATION_GRAPH.put("badminton court", "sports complex");
        LOCATION_GRAPH.put("gymnasium", "sports complex");
    }

    public static double computeLocationScore(String loc1, String loc2) {
        if (loc1 == null || loc2 == null) return 0.0;
        String l1 = loc1.trim().toLowerCase();
        String l2 = loc2.trim().toLowerCase();

        if (l1.equals(l2)) return 1.0;

        String parent1 = LOCATION_GRAPH.getOrDefault(l1, l1);
        String parent2 = LOCATION_GRAPH.getOrDefault(l2, l2);

        if (parent1.equals(parent2) || l1.contains(parent2) || l2.contains(parent1)) {
            return 0.85;
        }

        if (l1.contains("campus") || l2.contains("campus")) {
            return 0.50;
        }

        return TextSimilarity.computeJaccardSimilarity(l1, l2) > 0.4 ? 0.60 : 0.20;
    }
}