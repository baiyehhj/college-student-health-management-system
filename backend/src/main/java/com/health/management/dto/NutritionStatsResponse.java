package com.health.management.dto;

import java.util.List;

/**
 * 营养统计响应DTO
 */
public class NutritionStatsResponse {
    
    private List<String> dates;           // 日期列表
    private List<Double> calories;        // 热量列表
    private List<Double> proteins;        // 蛋白质列表
    private List<Double> carbs;           // 碳水列表
    private List<Double> fats;            // 脂肪列表
    private Double avgCalories;           // 平均热量
    private Double avgProtein;            // 平均蛋白质
    private Double avgCarbs;              // 平均碳水
    private Double avgFat;                // 平均脂肪
    
    // Getters and Setters
    public List<String> getDates() {
        return dates;
    }

    public void setDates(List<String> dates) {
        this.dates = dates;
    }

    public List<Double> getCalories() {
        return calories;
    }

    public void setCalories(List<Double> calories) {
        this.calories = calories;
    }

    public List<Double> getProteins() {
        return proteins;
    }

    public void setProteins(List<Double> proteins) {
        this.proteins = proteins;
    }

    public List<Double> getCarbs() {
        return carbs;
    }

    public void setCarbs(List<Double> carbs) {
        this.carbs = carbs;
    }

    public List<Double> getFats() {
        return fats;
    }

    public void setFats(List<Double> fats) {
        this.fats = fats;
    }

    public Double getAvgCalories() {
        return avgCalories;
    }

    public void setAvgCalories(Double avgCalories) {
        this.avgCalories = avgCalories;
    }

    public Double getAvgProtein() {
        return avgProtein;
    }

    public void setAvgProtein(Double avgProtein) {
        this.avgProtein = avgProtein;
    }

    public Double getAvgCarbs() {
        return avgCarbs;
    }

    public void setAvgCarbs(Double avgCarbs) {
        this.avgCarbs = avgCarbs;
    }

    public Double getAvgFat() {
        return avgFat;
    }

    public void setAvgFat(Double avgFat) {
        this.avgFat = avgFat;
    }
}
