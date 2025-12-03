import request from '@/utils/request'

/**
 * 识别食物图片
 * @param {File} file - 图片文件
 * @returns {Promise}
 */
export function recognizeFood(file) {
  const formData = new FormData()
  formData.append('file', file)
  
  return request({
    url: '/food/recognize',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 测试AI服务连接
 * @returns {Promise}
 */
export function testAIConnection() {
  return request({
    url: '/food/test',
    method: 'get'
  })
}
