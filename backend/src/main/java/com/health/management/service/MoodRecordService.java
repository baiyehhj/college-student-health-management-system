package com.health.management.service;

import com.health.management.common.Result;
import com.health.management.dto.MoodRecordRequest;

import java.time.LocalDate;

/**
 * 情绪记录服务接口
 */
public interface MoodRecordService {
    
    Result addRecord(Long studentId, MoodRecordRequest request);
    
    Result updateRecord(Long id, Long studentId, MoodRecordRequest request);
    
    Result deleteRecord(Long id, Long studentId);
    
    Result listRecords(Long studentId, LocalDate startDate, LocalDate endDate, Integer page, Integer size);
    
    Result getDailyStats(Long studentId, LocalDate date);
}
