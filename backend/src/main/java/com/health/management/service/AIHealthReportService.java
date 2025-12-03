package com.health.management.service;

import com.health.management.common.Result;

import java.time.LocalDate;

/**
 * AI健康报告服务接口
 */
public interface AIHealthReportService {
    
    /**
     * 生成AI健康报告
     */
    Result generateReport(Long studentId, LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取报告列表
     */
    Result listReports(Long studentId, Integer page, Integer size);
    
    /**
     * 获取报告详情
     */
    Result getReportDetail(Long id, Long studentId);
    
    /**
     * 获取最新报告
     */
    Result getLatestReport(Long studentId);
}
