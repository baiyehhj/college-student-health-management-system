package com.health.management.service;

import com.health.management.common.Result;
import com.health.management.dto.HealthExaminationRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * 体检报告服务接口
 */
public interface HealthExaminationService {
    
    Result addExamination(Long studentId, HealthExaminationRequest request);
    
    Result updateExamination(Long id, Long studentId, HealthExaminationRequest request);
    
    Result deleteExamination(Long id, Long studentId);
    
    Result listExaminations(Long studentId, Integer page, Integer size);
    
    Result getExaminationDetail(Long id, Long studentId);
    
    Result getLatestExamination(Long studentId);
    
    Result importExcel(Long studentId, MultipartFile file);
}
