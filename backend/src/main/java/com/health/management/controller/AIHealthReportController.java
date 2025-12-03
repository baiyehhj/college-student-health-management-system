package com.health.management.controller;

import com.health.management.common.Result;
import com.health.management.service.AIHealthReportService;
import com.health.management.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * AI健康报告控制器
 */
@RestController
@RequestMapping("/ai-report")
@CrossOrigin
public class AIHealthReportController {
    
    @Autowired
    private AIHealthReportService aiHealthReportService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 生成AI健康报告
     * @param startDate 开始日期
     * @param endDate 结束日期
     */
    @PostMapping("/generate")
    public Result generateReport(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                 @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                 @RequestHeader("Authorization") String token) {
        Long studentId = getStudentIdFromToken(token);
        return aiHealthReportService.generateReport(studentId, startDate, endDate);
    }
    
    /**
     * 获取AI健康报告列表
     */
    @GetMapping("/list")
    public Result listReports(@RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer size,
                             @RequestHeader("Authorization") String token) {
        Long studentId = getStudentIdFromToken(token);
        return aiHealthReportService.listReports(studentId, page, size);
    }
    
    /**
     * 获取报告详情
     */
    @GetMapping("/detail/{id}")
    public Result getReportDetail(@PathVariable Long id,
                                  @RequestHeader("Authorization") String token) {
        Long studentId = getStudentIdFromToken(token);
        return aiHealthReportService.getReportDetail(id, studentId);
    }
    
    /**
     * 获取最新报告
     */
    @GetMapping("/latest")
    public Result getLatestReport(@RequestHeader("Authorization") String token) {
        Long studentId = getStudentIdFromToken(token);
        return aiHealthReportService.getLatestReport(studentId);
    }
    
    private Long getStudentIdFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtUtil.getStudentIdFromToken(token);
    }
}
