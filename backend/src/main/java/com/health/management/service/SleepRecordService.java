package com.health.management.service;

import com.health.management.common.Result;
import com.health.management.dto.SleepRecordRequest;

import java.time.LocalDate;

/**
 * 睡眠记录服务接口
 */
public interface SleepRecordService {
    
    Result addRecord(Long studentId, SleepRecordRequest request);
    
    Result updateRecord(Long id, Long studentId, SleepRecordRequest request);
    
    Result deleteRecord(Long id, Long studentId);
    
    Result listRecords(Long studentId, LocalDate startDate, LocalDate endDate, Integer page, Integer size);
    
    Result getDailyStats(Long studentId, LocalDate date);
}
