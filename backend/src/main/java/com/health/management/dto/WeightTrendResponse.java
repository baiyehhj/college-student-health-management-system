package com.health.management.dto;

import java.util.List;

/**
 * 体重趋势响应DTO
 */
public class WeightTrendResponse {
    
    private List<String> dates;       // 日期列表
    private List<Double> weights;     // 体重列表
    private List<Double> bmis;        // BMI列表
    private Double avgWeight;         // 平均体重
    private Double avgBmi;            // 平均BMI
    private String trend;             // 趋势：上升/下降/稳定
    
    public WeightTrendResponse() {
    }
    
    public WeightTrendResponse(List<String> dates, List<Double> weights, List<Double> bmis) {
        this.dates = dates;
        this.weights = weights;
        this.bmis = bmis;
    }
    
    // Getters and Setters
    public List<String> getDates() {
        return dates;
    }

    public void setDates(List<String> dates) {
        this.dates = dates;
    }

    public List<Double> getWeights() {
        return weights;
    }

    public void setWeights(List<Double> weights) {
        this.weights = weights;
    }

    public List<Double> getBmis() {
        return bmis;
    }

    public void setBmis(List<Double> bmis) {
        this.bmis = bmis;
    }

    public Double getAvgWeight() {
        return avgWeight;
    }

    public void setAvgWeight(Double avgWeight) {
        this.avgWeight = avgWeight;
    }

    public Double getAvgBmi() {
        return avgBmi;
    }

    public void setAvgBmi(Double avgBmi) {
        this.avgBmi = avgBmi;
    }

    public String getTrend() {
        return trend;
    }

    public void setTrend(String trend) {
        this.trend = trend;
    }
}
