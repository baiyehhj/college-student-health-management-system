package com.health.management.service.impl;

import com.health.management.common.Result;
import com.health.management.config.PasswordEncoderConfig;
import com.health.management.dto.UserInfoRequest;
import com.health.management.entity.StudentUser;
import com.health.management.mapper.StudentUserMapper;
import com.health.management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务实现类 - 修复版
 * 
 * 修复问题：
 * 1. 添加新密码空校验
 * 2. 添加旧密码空校验
 * 3. 确保BCrypt密码编码正确使用
 */
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private StudentUserMapper studentUserMapper;

    @Autowired
    private PasswordEncoderConfig.PasswordEncoder passwordEncoder;
    
    @Override
    public Result getUserInfo(Long studentId) {
        StudentUser user = studentUserMapper.selectById(studentId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 不返回密码
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("studentNo", user.getStudentNo());
        userInfo.put("name", user.getName());
        userInfo.put("gender", user.getGender());
        userInfo.put("age", user.getAge());
        userInfo.put("phone", user.getPhone());
        userInfo.put("email", user.getEmail());
        userInfo.put("major", user.getMajor());
        userInfo.put("className", user.getClassName());
        userInfo.put("avatar", user.getAvatar());
        userInfo.put("createTime", user.getCreateTime());
        
        return Result.success(userInfo);
    }
    
    @Override
    public Result updateUserInfo(Long studentId, UserInfoRequest request) {
        StudentUser user = studentUserMapper.selectById(studentId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 更新用户信息
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        if (request.getAge() != null) {
            user.setAge(request.getAge());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getMajor() != null) {
            user.setMajor(request.getMajor());
        }
        if (request.getClassName() != null) {
            user.setClassName(request.getClassName());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        
        studentUserMapper.updateById(user);
        return Result.success("更新成功");
    }
    
    @Override
    public Result changePassword(Long studentId, String oldPassword, String newPassword) {
        // 修复1: 添加参数空校验
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            return Result.error("旧密码不能为空");
        }
        
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return Result.error("新密码不能为空");
        }
        
        // 修复2: 验证新密码长度
        if (newPassword.length() < 6) {
            return Result.error("新密码长度不能少于6位");
        }
        
        StudentUser user = studentUserMapper.selectById(studentId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 修复3: 确保使用注入的passwordEncoder进行密码校验
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return Result.error("原密码错误");
        }
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        studentUserMapper.updateById(user);
        
        return Result.success("密码修改成功");
    }
}
