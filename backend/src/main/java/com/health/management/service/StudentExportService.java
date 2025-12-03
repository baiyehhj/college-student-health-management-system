package com.health.management.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;

/**
 * 学生数据导出服务接口
 */
public interface StudentExportService {
    
    /**
     * 导出学生行为数据
     * @param studentId 学生ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return Excel文件的字节流
     */
    ByteArrayOutputStream exportBehaviorData(Long studentId, LocalDate startDate, LocalDate endDate) throws Exception;
}
