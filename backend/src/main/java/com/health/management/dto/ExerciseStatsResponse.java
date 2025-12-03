package com.health.management.dto;

import java.util.List;
import java.util.Map;

/**
 * 运动统计响应DTO
 */
public class ExerciseStatsResponse {
    
    private List<String> dates;              // 日期列表
    private List<Integer> durations;         // 时长列表
    private List<Double> calories;           // 热量列表
    private Map<String, Integer> typeStats;  // 运动类型统计
    private Integer totalDuration;           // 总时长
    private Double totalCalories;            // 总热量
    private Double avgDuration;              // 平均时长
    
    // Getters and Setters
    public List<String> getDates() {
        return dates;
    }

    public void setDates(List<String> dates) {
        this.dates = dates;
    }

    public List<Integer> getDurations() {
        return durations;
    }

    public void setDurations(List<Integer> durations) {
        this.durations = durations;
    }

    public List<Double> getCalories() {
        return calories;
    }

    public void setCalories(List<Double> calories) {
        this.calories = calories;
    }

    public Map<String, Integer> getTypeStats() {
        return typeStats;
    }

    public void setTypeStats(Map<String, Integer> typeStats) {
        this.typeStats = typeStats;
    }

    public Integer getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(Integer totalDuration) {
        this.totalDuration = totalDuration;
    }

    public Double getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(Double totalCalories) {
        this.totalCalories = totalCalories;
    }

    public Double getAvgDuration() {
        return avgDuration;
    }

    public void setAvgDuration(Double avgDuration) {
        this.avgDuration = avgDuration;
    }
}
