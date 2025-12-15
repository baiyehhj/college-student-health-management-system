// exercise.js - 运动记录API
import request from '@/utils/request'

export function addExerciseRecord(data) {
  return request({
    url: '/exercise/add',
    method: 'post',
    data
  })
}

export function updateExerciseRecord(id, data) {
  return request({
    url: `/exercise/update/${id}`,
    method: 'put',
    data
  })
}

export function deleteExerciseRecord(id) {
  return request({
    url: `/exercise/delete/${id}`,
    method: 'delete'
  })
}

export function listExerciseRecords(params) {
  return request({
    url: '/exercise/list',
    method: 'get',
    params
  })
}

export function getDailyExerciseStats(date) {
  return request({
    url: '/exercise/daily-stats',
    method: 'get',
    params: { date }
  })
}
