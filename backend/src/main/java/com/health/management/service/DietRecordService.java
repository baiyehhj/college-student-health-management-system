package com.health.management.service;

import com.health.management.common.Result;
import com.health.management.dto.DietRecordRequest;

import java.time.LocalDate;

/**
 * 饮食记录服务接口
 */
public interface DietRecordService {
    
    /**
     * 添加饮食记录
     */
    Result addRecord(Long studentId, DietRecordRequest request);
    
    /**
     * 更新饮食记录
     */
    Result updateRecord(Long id, Long studentId, DietRecordRequest request);
    
    /**
     * 删除饮食记录
     */
    Result deleteRecord(Long id, Long studentId);
    
    /**
     * 查询饮食记录列表
     */
    Result listRecords(Long studentId, LocalDate startDate, LocalDate endDate, Integer page, Integer size);
    
    /**
     * 获取每日饮食统计
     */
    Result getDailyStats(Long studentId, LocalDate date);
}
