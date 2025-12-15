// sleep.js - 睡眠记录API
import request from '@/utils/request'

export function addSleepRecord(data) {
  return request({
    url: '/sleep/add',
    method: 'post',
    data
  })
}

export function updateSleepRecord(id, data) {
  return request({
    url: `/sleep/update/${id}`,
    method: 'put',
    data
  })
}

export function deleteSleepRecord(id) {
  return request({
    url: `/sleep/delete/${id}`,
    method: 'delete'
  })
}

export function listSleepRecords(params) {
  return request({
    url: '/sleep/list',
    method: 'get',
    params
  })
}

export function getDailySleepStats(date) {
  return request({
    url: '/sleep/daily-stats',
    method: 'get',
    params: { date }
  })
}
