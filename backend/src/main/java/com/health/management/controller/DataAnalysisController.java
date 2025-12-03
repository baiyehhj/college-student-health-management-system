package com.health.management.controller;

import com.health.management.common.Result;
import com.health.management.service.DataAnalysisService;
import com.health.management.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 数据分析Controller
 */
@RestController
@RequestMapping("/analysis")
@CrossOrigin
public class DataAnalysisController {
    
    @Autowired
    private DataAnalysisService dataAnalysisService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 获取体重趋势
     */
    @GetMapping("/weight-trend")
    public Result getWeightTrend(@RequestParam(defaultValue = "30") Integer days,
                                  @RequestHeader("Authorization") String token) {
        Long studentId = getStudentIdFromToken(token);
        return dataAnalysisService.getWeightTrend(studentId, days);
    }
    
    /**
     * 获取运动统计
     */
    @GetMapping("/exercise-stats")
    public Result getExerciseStats(@RequestParam(defaultValue = "30") Integer days,
                                    @RequestHeader("Authorization") String token) {
        Long studentId = getStudentIdFromToken(token);
        return dataAnalysisService.getExerciseStats(studentId, days);
    }
    
    /**
     * 获取睡眠趋势
     */
    @GetMapping("/sleep-trend")
    public Result getSleepTrend(@RequestParam(defaultValue = "30") Integer days,
                                 @RequestHeader("Authorization") String token) {
        Long studentId = getStudentIdFromToken(token);
        return dataAnalysisService.getSleepTrend(studentId, days);
    }
    
    /**
     * 获取情绪分布
     */
    @GetMapping("/mood-distribution")
    public Result getMoodDistribution(@RequestParam(defaultValue = "30") Integer days,
                                       @RequestHeader("Authorization") String token) {
        Long studentId = getStudentIdFromToken(token);
        return dataAnalysisService.getMoodDistribution(studentId, days);
    }
    
    /**
     * 获取营养统计
     */
    @GetMapping("/nutrition-stats")
    public Result getNutritionStats(@RequestParam(defaultValue = "30") Integer days,
                                     @RequestHeader("Authorization") String token) {
        Long studentId = getStudentIdFromToken(token);
        return dataAnalysisService.getNutritionStats(studentId, days);
    }
    
    private Long getStudentIdFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtUtil.getStudentIdFromToken(token);
    }
}
