package com.health.management.service;

import com.health.management.common.Result;
import com.health.management.dto.UserInfoRequest;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 获取用户信息
     */
    Result getUserInfo(Long studentId);
    
    /**
     * 更新用户信息
     */
    Result updateUserInfo(Long studentId, UserInfoRequest request);
    
    /**
     * 修改密码
     */
    Result changePassword(Long studentId, String oldPassword, String newPassword);
}
