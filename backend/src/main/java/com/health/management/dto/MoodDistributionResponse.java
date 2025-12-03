package com.health.management.dto;

import java.util.Map;

/**
 * 情绪分布响应DTO
 */
public class MoodDistributionResponse {
    
    private Map<String, Integer> distribution;  // 情绪分布 {情绪:次数}
    private String mostFrequent;                // 最常见情绪
    private Integer totalRecords;               // 总记录数
    private Double positiveRate;                // 积极情绪占比
    
    // Getters and Setters
    public Map<String, Integer> getDistribution() {
        return distribution;
    }

    public void setDistribution(Map<String, Integer> distribution) {
        this.distribution = distribution;
    }

    public String getMostFrequent() {
        return mostFrequent;
    }

    public void setMostFrequent(String mostFrequent) {
        this.mostFrequent = mostFrequent;
    }

    public Integer getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Integer totalRecords) {
        this.totalRecords = totalRecords;
    }

    public Double getPositiveRate() {
        return positiveRate;
    }

    public void setPositiveRate(Double positiveRate) {
        this.positiveRate = positiveRate;
    }
}
