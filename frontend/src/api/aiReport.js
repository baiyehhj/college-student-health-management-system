// aiReport.js - AI健康报告API
import request from '@/utils/request'

/**
 * 生成AI健康报告
 */
export function generateAIReport(data) {
  return request({
    url: '/ai-report/generate',
    method: 'post',
    params: data,
    timeout: 30000  // AI生成可能需要更长时间
  })
}

/**
 * 获取报告列表
 */
export function listAIReports(params) {
  return request({
    url: '/ai-report/list',
    method: 'get',
    params
  })
}

/**
 * 获取报告详情
 */
export function getReportDetail(id) {
  return request({
    url: `/ai-report/detail/${id}`,
    method: 'get'
  })
}

/**
 * 获取最新报告
 */
export function getLatestReport() {
  return request({
    url: '/ai-report/latest',
    method: 'get'
  })
}
