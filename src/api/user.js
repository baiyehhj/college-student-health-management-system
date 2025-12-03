// user.js - 用户信息API
import request from '@/utils/request'

/**
 * 获取当前用户信息
 */
export function getUserInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

// 别名导出，兼容Profile.vue
export const getUserProfile = getUserInfo

/**
 * 更新用户信息
 */
export function updateUserInfo(data) {
  return request({
    url: '/user/update',
    method: 'put',
    data
  })
}

// 别名导出，兼容Profile.vue
export const updateUserProfile = updateUserInfo

/**
 * 修改密码
 */
export function changePassword(data) {
  return request({
    url: '/user/change-password',
    method: 'put',
    params: data
  })
}
