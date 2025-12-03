// diet.js - 饮食记录API
import request from '@/utils/request'

/**
 * 添加饮食记录
 */
export function addDietRecord(data) {
  return request({
    url: '/diet/add',
    method: 'post',
    data
  })
}

/**
 * 更新饮食记录
 */
export function updateDietRecord(id, data) {
  return request({
    url: `/diet/update/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除饮食记录
 */
export function deleteDietRecord(id) {
  return request({
    url: `/diet/delete/${id}`,
    method: 'delete'
  })
}

/**
 * 查询饮食记录列表
 */
export function listDietRecords(params) {
  return request({
    url: '/diet/list',
    method: 'get',
    params
  })
}

/**
 * 获取每日饮食统计
 */
export function getDailyDietStats(date) {
  return request({
    url: '/diet/daily-stats',
    method: 'get',
    params: { date }
  })
}
