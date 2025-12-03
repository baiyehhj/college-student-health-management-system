package com.health.management.controller;

import com.health.management.common.Result;
import com.health.management.dto.UserInfoRequest;
import com.health.management.entity.StudentUser;
import com.health.management.service.UserService;
import com.health.management.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 用户个人信息管理Controller
 */
@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result getUserInfo(@RequestHeader("Authorization") String token) {
        Long studentId = getStudentIdFromToken(token);
        return userService.getUserInfo(studentId);
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Result updateUserInfo(@Valid @RequestBody UserInfoRequest request,
                                 @RequestHeader("Authorization") String token) {
        Long studentId = getStudentIdFromToken(token);
        return userService.updateUserInfo(studentId, request);
    }
    
    /**
     * 修改密码
     */
    @PutMapping("/change-password")
    public Result changePassword(@RequestParam String oldPassword,
                                @RequestParam String newPassword,
                                @RequestHeader("Authorization") String token) {
        Long studentId = getStudentIdFromToken(token);
        return userService.changePassword(studentId, oldPassword, newPassword);
    }
    
    private Long getStudentIdFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtUtil.getStudentIdFromToken(token);
    }
}
