// mood.js - 情绪记录API
import request from '@/utils/request'

export function addMoodRecord(data) {
  return request({
    url: '/mood/add',
    method: 'post',
    data
  })
}

export function updateMoodRecord(id, data) {
  return request({
    url: `/mood/update/${id}`,
    method: 'put',
    data
  })
}

export function deleteMoodRecord(id) {
  return request({
    url: `/mood/delete/${id}`,
    method: 'delete'
  })
}

export function listMoodRecords(params) {
  return request({
    url: '/mood/list',
    method: 'get',
    params
  })
}

export function getDailyMoodStats(date) {
  return request({
    url: '/mood/daily-stats',
    method: 'get',
    params: { date }
  })
}
