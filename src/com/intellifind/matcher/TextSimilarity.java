package com.intellifind.matcher;

import java.util.*;

public class TextSimilarity {
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "a", "an", "and", "are", "as", "at", "be", "by", "for", "from",
            "has", "he", "in", "is", "it", "its", "of", "on", "that", "the",
            "to", "was", "were", "will", "with"
    ));

    public static double computeJaccardSimilarity(String s1, String s2) {
        if (s1 == null || s2 == null) return 0.0;
        Set<String> tokens1 = tokenize(s1);
        Set<String> tokens2 = tokenize(s2);

        if (tokens1.isEmpty() && tokens2.isEmpty()) return 1.0;
        if (tokens1.isEmpty() || tokens2.isEmpty()) return 0.0;

        Set<String> intersection = new HashSet<>(tokens1);
        intersection.retainAll(tokens2);

        Set<String> union = new HashSet<>(tokens1);
        union.addAll(tokens2);

        return (double) intersection.size() / union.size();
    }

    private static Set<String> tokenize(String input) {
        Set<String> words = new HashSet<>();
        String[] rawWords = input.toLowerCase().replaceAll("[^a-zA-Z0-9 ]", " ").split("\\s+");
        for (String word : rawWords) {
            String clean = word.trim();
            if (!clean.isEmpty() && !STOP_WORDS.contains(clean)) {
                words.add(clean);
            }
        }
        return words;
    }

    public static double computeLevenshteinSimilarity(String s1, String s2) {
        if (s1 == null || s2 == null) return 0.0;
        String str1 = s1.trim().toLowerCase();
        String str2 = s2.trim().toLowerCase();
        if (str1.equals(str2)) return 1.0;
        int len1 = str1.length();
        int len2 = str2.length();
        if (len1 == 0 || len2 == 0) return 0.0;

        int[][] dp = new int[len1 + 1][len2 + 1];
        for (int i = 0; i <= len1; i++) dp[i][0] = i;
        for (int j = 0; j <= len2; j++) dp[0][j] = j;

        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                int cost = (str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }
        int distance = dp[len1][len2];
        return 1.0 - ((double) distance / Math.max(len1, len2));
    }
}