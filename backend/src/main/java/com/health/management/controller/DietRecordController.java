package com.health.management.controller;

import com.health.management.common.Result;
import com.health.management.dto.DietRecordRequest;
import com.health.management.service.DietRecordService;
import com.health.management.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;

/**
 * 饮食记录控制器
 */
@RestController
@RequestMapping("/diet")
@CrossOrigin
public class DietRecordController {
    
    @Autowired
    private DietRecordService dietRecordService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/add")
    public Result addRecord(@Valid @RequestBody DietRecordRequest request,
                           @RequestHeader("Authorization") String token) {
        Long studentId = getStudentIdFromToken(token);
        return dietRecordService.addRecord(studentId, request);
    }
    
    @PutMapping("/update/{id}")
    public Result updateRecord(@PathVariable Long id,
                              @Valid @RequestBody DietRecordRequest request,
                              @RequestHeader("Authorization") String token) {
        Long studentId = getStudentIdFromToken(token);
        return dietRecordService.updateRecord(id, studentId, request);
    }
    
    @DeleteMapping("/delete/{id}")
    public Result deleteRecord(@PathVariable Long id,
                              @RequestHeader("Authorization") String token) {
        Long studentId = getStudentIdFromToken(token);
        return dietRecordService.deleteRecord(id, studentId);
    }
    
    @GetMapping("/list")
    public Result listRecords(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer size,
                             @RequestHeader("Authorization") String token) {
        Long studentId = getStudentIdFromToken(token);
        return dietRecordService.listRecords(studentId, startDate, endDate, page, size);
    }
    
    /**
     * 修复：接口路径从 /daily-stats 改为 /stats/daily
     * 以匹配测试期望的路径格式
     */
    @GetMapping("/stats/daily")
    public Result getDailyStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                @RequestHeader("Authorization") String token) {
        Long studentId = getStudentIdFromToken(token);
        return dietRecordService.getDailyStats(studentId, date);
    }
    
    private Long getStudentIdFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtUtil.getUserIdFromToken(token);
    }
}
