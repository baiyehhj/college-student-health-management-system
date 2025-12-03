// healthExam.js - 体检报告API
import request from '@/utils/request'

export function addHealthExamination(data) {
  return request({
    url: '/health-exam/add',
    method: 'post',
    data
  })
}

export function updateHealthExamination(id, data) {
  return request({
    url: `/health-exam/update/${id}`,
    method: 'put',
    data
  })
}

export function deleteHealthExamination(id) {
  return request({
    url: `/health-exam/delete/${id}`,
    method: 'delete'
  })
}

export function listHealthExaminations(params) {
  return request({
    url: '/health-exam/list',
    method: 'get',
    params
  })
}

export function getHealthExaminationDetail(id) {
  return request({
    url: `/health-exam/detail/${id}`,
    method: 'get'
  })
}

export function getLatestHealthExamination() {
  return request({
    url: '/health-exam/latest',
    method: 'get'
  })
}

/**
 * 下载Excel导入模板
 */
export function downloadTemplate() {
  return request({
    url: '/health-exam/template',
    method: 'get'
  })
}

/**
 * 导入Excel体检报告
 */
export function importExcel(file) {
  const formData = new FormData()
  formData.append('file', file)
  
  return request({
    url: '/health-exam/import',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
