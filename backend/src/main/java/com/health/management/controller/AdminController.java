package com.health.management.controller;

import com.health.management.common.Result;
import com.health.management.service.AdminService;
import com.health.management.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping("/admin")
@CrossOrigin
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 验证管理员权限
     */
    private boolean isAdmin(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String role = jwtUtil.getRoleFromToken(token);
        return "ADMIN".equals(role);
    }
    
    /**
     * 获取学生列表
     */
    @GetMapping("/students")
    public Result getStudentList(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        
        if (!isAdmin(token)) {
            return Result.error("无权限访问");
        }
        
        return adminService.getStudentList(pageNum, pageSize, keyword, status);
    }
    
    /**
     * 获取学生详情
     */
    @GetMapping("/students/{studentId}")
    public Result getStudentDetail(
            @RequestHeader("Authorization") String token,
            @PathVariable Long studentId) {
        
        if (!isAdmin(token)) {
            return Result.error("无权限访问");
        }
        
        return adminService.getStudentDetail(studentId);
    }
    
    /**
     * 更新学生状态（启用/禁用）
     */
    @PutMapping("/students/{studentId}/status")
    public Result updateStudentStatus(
            @RequestHeader("Authorization") String token,
            @PathVariable Long studentId,
            @RequestBody Map<String, Integer> request) {
        
        if (!isAdmin(token)) {
            return Result.error("无权限访问");
        }
        
        Integer status = request.get("status");
        if (status == null) {
            return Result.error("状态不能为空");
        }
        
        return adminService.updateStudentStatus(studentId, status);
    }
    
    /**
     * 重置学生密码
     */
    @PostMapping("/students/{studentId}/reset-password")
    public Result resetStudentPassword(
            @RequestHeader("Authorization") String token,
            @PathVariable Long studentId) {
        
        if (!isAdmin(token)) {
            return Result.error("无权限访问");
        }
        
        return adminService.resetStudentPassword(studentId);
    }
    
    /**
     * 获取系统统计数据
     */
    @GetMapping("/statistics")
    public Result getSystemStatistics(@RequestHeader("Authorization") String token) {
        if (!isAdmin(token)) {
            return Result.error("无权限访问");
        }
        
        return adminService.getSystemStatistics();
    }
    
    /**
     * 获取健康趋势统计
     */
    @GetMapping("/statistics/trends")
    public Result getHealthTrendStatistics(@RequestHeader("Authorization") String token) {
        if (!isAdmin(token)) {
            return Result.error("无权限访问");
        }
        
        return adminService.getHealthTrendStatistics();
    }
    
    /**
     * 获取健康预警统计
     */
    @GetMapping("/statistics/alerts")
    public Result getHealthAlertStatistics(@RequestHeader("Authorization") String token) {
        if (!isAdmin(token)) {
            return Result.error("无权限访问");
        }
        
        return adminService.getHealthAlertStatistics();
    }
    
    /**
     * 导出学生数据
     */
    @GetMapping("/export/students")
    public void exportStudentData(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "all") String type,
            HttpServletResponse response) {
        
        if (!isAdmin(token)) {
            return;
        }
        
        adminService.exportStudentData(response, type);
    }
    
    /**
     * 导出指定学生健康数据
     */
    @GetMapping("/export/students/{studentId}/health")
    public void exportStudentHealthData(
            @RequestHeader("Authorization") String token,
            @PathVariable Long studentId,
            HttpServletResponse response) {
        
        if (!isAdmin(token)) {
            return;
        }
        
        adminService.exportStudentHealthData(response, studentId);
    }
    
    /**
     * 导出全部学生健康数据统计
     */
    @GetMapping("/export/all-health")
    public void exportAllHealthData(
            @RequestHeader("Authorization") String token,
            HttpServletResponse response) {
        
        if (!isAdmin(token)) {
            return;
        }
        
        adminService.exportAllHealthData(response);
    }
}
