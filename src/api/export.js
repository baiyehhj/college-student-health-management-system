// export.js - 学生导出相关API
import request from '@/utils/request'

/**
 * 导出学生行为数据
 */
export function exportBehaviorData(startDate, endDate) {
  return request({
    url: '/student/export/behavior-data',
    method: 'get',
    params: { startDate, endDate },
    responseType: 'blob'
  })
}
