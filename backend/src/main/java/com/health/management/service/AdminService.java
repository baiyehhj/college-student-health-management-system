package com.health.management.service;

import com.health.management.common.Result;

import javax.servlet.http.HttpServletResponse;

/**
 * 管理员服务接口
 */
public interface AdminService {
    
    /**
     * 获取所有学生列表
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param keyword 搜索关键词
     * @param status 状态筛选
     */
    Result getStudentList(Integer pageNum, Integer pageSize, String keyword, Integer status);
    
    /**
     * 获取学生详细信息（包含健康数据）
     * @param studentId 学生ID
     */
    Result getStudentDetail(Long studentId);
    
    /**
     * 启用/禁用学生账号
     * @param studentId 学生ID
     * @param status 状态
     */
    Result updateStudentStatus(Long studentId, Integer status);
    
    /**
     * 重置学生密码
     * @param studentId 学生ID
     */
    Result resetStudentPassword(Long studentId);
    
    /**
     * 获取系统统计数据
     */
    Result getSystemStatistics();
    
    /**
     * 获取健康数据趋势统计
     */
    Result getHealthTrendStatistics();
    
    /**
     * 获取健康预警统计
     */
    Result getHealthAlertStatistics();
    
    /**
     * 导出学生数据到Excel
     * @param response HTTP响应
     * @param type 导出类型：all-全部, basic-基本信息, health-健康数据
     */
    void exportStudentData(HttpServletResponse response, String type);
    
    /**
     * 导出指定学生的健康数据
     * @param response HTTP响应
     * @param studentId 学生ID
     */
    void exportStudentHealthData(HttpServletResponse response, Long studentId);
    
    /**
     * 批量导出所有学生健康数据
     * @param response HTTP响应
     */
    void exportAllHealthData(HttpServletResponse response);
}
