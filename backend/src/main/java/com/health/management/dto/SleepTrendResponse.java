package com.health.management.dto;

import java.util.List;

/**
 * 睡眠趋势响应DTO
 */
public class SleepTrendResponse {
    
    private List<String> dates;          // 日期列表
    private List<Double> durations;      // 睡眠时长列表
    private List<Integer> qualities;     // 睡眠质量列表
    private Double avgDuration;          // 平均睡眠时长
    private Double avgQuality;           // 平均睡眠质量
    private Integer goodSleepDays;       // 优质睡眠天数
    
    // Getters and Setters
    public List<String> getDates() {
        return dates;
    }

    public void setDates(List<String> dates) {
        this.dates = dates;
    }

    public List<Double> getDurations() {
        return durations;
    }

    public void setDurations(List<Double> durations) {
        this.durations = durations;
    }

    public List<Integer> getQualities() {
        return qualities;
    }

    public void setQualities(List<Integer> qualities) {
        this.qualities = qualities;
    }

    public Double getAvgDuration() {
        return avgDuration;
    }

    public void setAvgDuration(Double avgDuration) {
        this.avgDuration = avgDuration;
    }

    public Double getAvgQuality() {
        return avgQuality;
    }

    public void setAvgQuality(Double avgQuality) {
        this.avgQuality = avgQuality;
    }

    public Integer getGoodSleepDays() {
        return goodSleepDays;
    }

    public void setGoodSleepDays(Integer goodSleepDays) {
        this.goodSleepDays = goodSleepDays;
    }
}
