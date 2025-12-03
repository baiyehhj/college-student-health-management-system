package com.health.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.health.management.common.Result;
import com.health.management.dto.LoginRequest;
import com.health.management.dto.RegisterRequest;
import com.health.management.entity.AdminEmployeeList;
import com.health.management.entity.AdminUser;
import com.health.management.entity.StudentUser;
import com.health.management.mapper.AdminEmployeeListMapper;
import com.health.management.mapper.AdminUserMapper;
import com.health.management.mapper.StudentUserMapper;
import com.health.management.service.AuthService;
import com.health.management.util.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务实现类
 */
@Service
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private StudentUserMapper studentUserMapper;
    
    @Autowired
    private AdminUserMapper adminUserMapper;
    
    @Autowired
    private AdminEmployeeListMapper adminEmployeeListMapper;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Override
    @Transactional
    public Result register(RegisterRequest request) {
        String role = request.getRole();
        
        if ("ADMIN".equals(role)) {
            return registerAdmin(request);
        } else {
            return registerStudent(request);
        }
    }
    
    /**
     * 学生注册
     */
    private Result registerStudent(RegisterRequest request) {
        // 验证学号
        if (request.getStudentNo() == null || request.getStudentNo().trim().isEmpty()) {
            return Result.error("学号不能为空");
        }
        
        // 检查学号是否已存在
        QueryWrapper<StudentUser> wrapper = new QueryWrapper<>();
        wrapper.eq("student_no", request.getStudentNo());
        StudentUser existUser = studentUserMapper.selectOne(wrapper);
        
        if (existUser != null) {
            return Result.error("该学号已被注册");
        }
        
        // 创建新用户
        StudentUser user = new StudentUser();
        user.setStudentNo(request.getStudentNo());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setGender(request.getGender());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setMajor(request.getMajor());
        user.setClassName(request.getClassName());
        user.setStatus(1);
        
        studentUserMapper.insert(user);
        
        return Result.success("注册成功");
    }
    
    /**
     * 管理员注册
     */
    private Result registerAdmin(RegisterRequest request) {
        // 验证工号
        if (request.getEmployeeNo() == null || request.getEmployeeNo().trim().isEmpty()) {
            return Result.error("工号不能为空");
        }
        
        // 验证工号是否在预设表中
        QueryWrapper<AdminEmployeeList> employeeWrapper = new QueryWrapper<>();
        employeeWrapper.eq("employee_no", request.getEmployeeNo());
        AdminEmployeeList employee = adminEmployeeListMapper.selectOne(employeeWrapper);
        
        if (employee == null) {
            return Result.error("工号无效，请联系管理员");
        }
        
        if (employee.getIsRegistered() == 1) {
            return Result.error("该工号已被注册");
        }
        
        // 检查工号是否在管理员表中已存在
        QueryWrapper<AdminUser> adminWrapper = new QueryWrapper<>();
        adminWrapper.eq("employee_no", request.getEmployeeNo());
        AdminUser existAdmin = adminUserMapper.selectOne(adminWrapper);
        
        if (existAdmin != null) {
            return Result.error("该工号已被注册");
        }
        
        // 创建新管理员
        AdminUser admin = new AdminUser();
        admin.setEmployeeNo(request.getEmployeeNo());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setName(request.getName());
        admin.setGender(request.getGender());
        admin.setPhone(request.getPhone());
        admin.setEmail(request.getEmail());
        admin.setDepartment(employee.getDepartment());
        admin.setStatus(1);
        
        adminUserMapper.insert(admin);
        
        // 标记工号已注册
        employee.setIsRegistered(1);
        adminEmployeeListMapper.updateById(employee);
        
        return Result.success("注册成功");
    }
    
    @Override
    public Result login(LoginRequest request) {
        String role = request.getRole();
        
        if ("ADMIN".equals(role)) {
            return loginAdmin(request);
        } else {
            return loginStudent(request);
        }
    }
    
    /**
     * 学生登录
     */
    private Result loginStudent(LoginRequest request) {
        if (request.getStudentNo() == null || request.getStudentNo().trim().isEmpty()) {
            return Result.error("学号不能为空");
        }
        
        // 查询用户
        QueryWrapper<StudentUser> wrapper = new QueryWrapper<>();
        wrapper.eq("student_no", request.getStudentNo());
        StudentUser user = studentUserMapper.selectOne(wrapper);
        
        if (user == null) {
            return Result.error("学号或密码错误");
        }
        
        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return Result.error("学号或密码错误");
        }
        
        // 检查账号状态
        if (user.getStatus() == 0) {
            return Result.error("账号已被禁用");
        }
        
        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getStudentNo(), "STUDENT");
        
        // 返回用户信息和Token
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userInfo", getStudentUserInfo(user));
        
        return Result.success("登录成功", data);
    }
    
    /**
     * 管理员登录
     */
    private Result loginAdmin(LoginRequest request) {
        if (request.getEmployeeNo() == null || request.getEmployeeNo().trim().isEmpty()) {
            return Result.error("工号不能为空");
        }
        
        // 查询管理员
        QueryWrapper<AdminUser> wrapper = new QueryWrapper<>();
        wrapper.eq("employee_no", request.getEmployeeNo());
        AdminUser admin = adminUserMapper.selectOne(wrapper);
        
        if (admin == null) {
            return Result.error("工号或密码错误");
        }
        
        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            return Result.error("工号或密码错误");
        }
        
        // 检查账号状态
        if (admin.getStatus() == 0) {
            return Result.error("账号已被禁用");
        }
        
        // 生成Token
        String token = jwtUtil.generateToken(admin.getId(), admin.getEmployeeNo(), "ADMIN");
        
        // 返回用户信息和Token
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userInfo", getAdminUserInfo(admin));
        
        return Result.success("登录成功", data);
    }
    
    @Override
    public Result getCurrentUser(String token) {
        // 去除Bearer前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        Long userId = jwtUtil.getUserIdFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);
        
        if (userId == null) {
            return Result.error("Token无效");
        }
        
        if ("ADMIN".equals(role)) {
            AdminUser admin = adminUserMapper.selectById(userId);
            if (admin == null) {
                return Result.error("用户不存在");
            }
            return Result.success(getAdminUserInfo(admin));
        } else {
            StudentUser user = studentUserMapper.selectById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }
            return Result.success(getStudentUserInfo(user));
        }
    }
    
    /**
     * 获取学生用户信息(不包含密码)
     */
    private Map<String, Object> getStudentUserInfo(StudentUser user) {
        Map<String, Object> info = new HashMap<>();
        info.put("id", user.getId());
        info.put("studentNo", user.getStudentNo());
        info.put("name", user.getName());
        info.put("gender", user.getGender());
        info.put("age", user.getAge());
        info.put("phone", user.getPhone());
        info.put("email", user.getEmail());
        info.put("major", user.getMajor());
        info.put("className", user.getClassName());
        info.put("avatar", user.getAvatar());
        info.put("role", "STUDENT");
        return info;
    }
    
    /**
     * 获取管理员用户信息(不包含密码)
     */
    private Map<String, Object> getAdminUserInfo(AdminUser admin) {
        Map<String, Object> info = new HashMap<>();
        info.put("id", admin.getId());
        info.put("employeeNo", admin.getEmployeeNo());
        info.put("name", admin.getName());
        info.put("gender", admin.getGender());
        info.put("phone", admin.getPhone());
        info.put("email", admin.getEmail());
        info.put("department", admin.getDepartment());
        info.put("avatar", admin.getAvatar());
        info.put("role", "ADMIN");
        return info;
    }
}
