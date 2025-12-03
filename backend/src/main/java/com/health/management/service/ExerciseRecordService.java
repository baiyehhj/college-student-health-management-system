package com.health.management.service;

import com.health.management.common.Result;
import com.health.management.dto.ExerciseRecordRequest;

import java.time.LocalDate;

/**
 * 运动记录服务接口
 */
public interface ExerciseRecordService {
    
    /**
     * 添加运动记录
     */
    Result addRecord(Long studentId, ExerciseRecordRequest request);
    
    /**
     * 更新运动记录
     */
    Result updateRecord(Long id, Long studentId, ExerciseRecordRequest request);
    
    /**
     * 删除运动记录
     */
    Result deleteRecord(Long id, Long studentId);
    
    /**
     * 查询运动记录列表
     */
    Result listRecords(Long studentId, LocalDate startDate, LocalDate endDate, Integer page, Integer size);
    
    /**
     * 获取每日运动统计
     */
    Result getDailyStats(Long studentId, LocalDate date);
}
