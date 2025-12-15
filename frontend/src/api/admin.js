// admin.js - 管理员相关API
import request from '@/utils/request'

/**
 * 获取学生列表
 */
export function getStudentList(params) {
  return request({
    url: '/admin/students',
    method: 'get',
    params
  })
}

/**
 * 获取学生详情
 */
export function getStudentDetail(studentId) {
  return request({
    url: `/admin/students/${studentId}`,
    method: 'get'
  })
}

/**
 * 更新学生状态（启用/禁用）
 */
export function updateStudentStatus(studentId, status) {
  return request({
    url: `/admin/students/${studentId}/status`,
    method: 'put',
    data: { status }
  })
}

/**
 * 重置学生密码
 */
export function resetStudentPassword(studentId) {
  return request({
    url: `/admin/students/${studentId}/reset-password`,
    method: 'post'
  })
}

/**
 * 获取系统统计数据
 */
export function getSystemStatistics() {
  return request({
    url: '/admin/statistics',
    method: 'get'
  })
}

/**
 * 获取健康趋势统计
 */
export function getHealthTrendStatistics() {
  return request({
    url: '/admin/statistics/trends',
    method: 'get'
  })
}

/**
 * 获取健康预警统计
 */
export function getHealthAlertStatistics() {
  return request({
    url: '/admin/statistics/alerts',
    method: 'get'
  })
}

/**
 * 导出学生数据
 */
export function exportStudentData(type = 'all') {
  return request({
    url: '/admin/export/students',
    method: 'get',
    params: { type },
    responseType: 'blob'
  })
}

/**
 * 导出指定学生健康数据
 */
export function exportStudentHealthData(studentId) {
  return request({
    url: `/admin/export/students/${studentId}/health`,
    method: 'get',
    responseType: 'blob'
  })
}

/**
 * 导出全部学生健康数据统计
 */
export function exportAllHealthData() {
  return request({
    url: '/admin/export/all-health',
    method: 'get',
    responseType: 'blob'
  })
}
