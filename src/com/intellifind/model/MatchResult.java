package com.intellifind.model;

public class MatchResult implements Comparable<MatchResult> {
    private final LostItem lostItem;
    private final FoundItem foundItem;
    private final double categoryScore;
    private final double brandScore;
    private final double modelScore;
    private final double colorScore;
    private final double locationScore;
    private final double dateScore;
    private final double descScore;
    private final double overallScore;

    public MatchResult(LostItem lostItem, FoundItem foundItem,
                       double categoryScore, double brandScore, double modelScore,
                       double colorScore, double locationScore, double dateScore,
                       double descScore, double overallScore) {
        this.lostItem = lostItem;
        this.foundItem = foundItem;
        this.categoryScore = categoryScore;
        this.brandScore = brandScore;
        this.modelScore = modelScore;
        this.colorScore = colorScore;
        this.locationScore = locationScore;
        this.dateScore = dateScore;
        this.descScore = descScore;
        this.overallScore = overallScore;
    }

    public LostItem getLostItem() { return lostItem; }
    public FoundItem getFoundItem() { return foundItem; }
    public double getOverallScore() { return overallScore; }
    public double getCategoryScore() { return categoryScore; }
    public double getBrandScore() { return brandScore; }
    public double getModelScore() { return modelScore; }
    public double getColorScore() { return colorScore; }
    public double getLocationScore() { return locationScore; }
    public double getDateScore() { return dateScore; }
    public double getDescScore() { return descScore; }

    @Override
    public int compareTo(MatchResult o) {
        return Double.compare(o.overallScore, this.overallScore);
    }
}