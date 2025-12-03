package com.health.management.service;

import com.health.management.common.Result;

/**
 * 数据分析服务接口
 */
public interface DataAnalysisService {
    
    /**
     * 获取体重趋势
     * @param studentId 学生ID
     * @param days 天数（7/30/90/365）
     * @return 体重趋势数据
     */
    Result getWeightTrend(Long studentId, Integer days);
    
    /**
     * 获取运动统计
     * @param studentId 学生ID
     * @param days 天数
     * @return 运动统计数据
     */
    Result getExerciseStats(Long studentId, Integer days);
    
    /**
     * 获取睡眠趋势
     * @param studentId 学生ID
     * @param days 天数
     * @return 睡眠趋势数据
     */
    Result getSleepTrend(Long studentId, Integer days);
    
    /**
     * 获取情绪分布
     * @param studentId 学生ID
     * @param days 天数
     * @return 情绪分布数据
     */
    Result getMoodDistribution(Long studentId, Integer days);
    
    /**
     * 获取营养统计
     * @param studentId 学生ID
     * @param days 天数
     * @return 营养统计数据
     */
    Result getNutritionStats(Long studentId, Integer days);
}
