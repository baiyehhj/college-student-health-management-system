<template>
  <div class="login-container">
    <el-card class="login-card">
      <div slot="header" class="clearfix">
        <h2 style="text-align: center; margin: 0;">
          <i class="el-icon-s-custom"></i>
          大学生健康管理系统
        </h2>
      </div>
      
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <!-- 登录标签 -->
        <el-tab-pane label="登录" name="login">
          <el-form
            ref="loginForm"
            :model="loginForm"
            :rules="loginRules"
            label-width="80px"
          >
            <!-- 身份选择 -->
            <el-form-item label="身份" prop="role">
              <el-radio-group v-model="loginForm.role" @change="handleLoginRoleChange">
                <el-radio label="STUDENT">学生</el-radio>
                <el-radio label="ADMIN">管理员</el-radio>
              </el-radio-group>
            </el-form-item>
            
            <!-- 学生登录：学号 -->
            <el-form-item v-if="loginForm.role === 'STUDENT'" label="学号" prop="studentNo">
              <el-input
                v-model="loginForm.studentNo"
                placeholder="请输入学号"
                prefix-icon="el-icon-user"
              ></el-input>
            </el-form-item>
            
            <!-- 管理员登录：工号 -->
            <el-form-item v-if="loginForm.role === 'ADMIN'" label="工号" prop="employeeNo">
              <el-input
                v-model="loginForm.employeeNo"
                placeholder="请输入工号"
                prefix-icon="el-icon-s-custom"
              ></el-input>
            </el-form-item>
            
            <el-form-item label="密码" prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="el-icon-lock"
                show-password
              ></el-input>
            </el-form-item>
            
            <el-form-item>
              <el-button
                type="primary"
                style="width: 100%"
                :loading="loading"
                @click="handleLogin"
              >
                登录
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <!-- 注册标签 -->
        <el-tab-pane label="注册" name="register">
          <el-form
            ref="registerForm"
            :model="registerForm"
            :rules="registerRules"
            label-width="80px"
          >
            <!-- 身份选择 -->
            <el-form-item label="身份" prop="role">
              <el-radio-group v-model="registerForm.role" @change="handleRegisterRoleChange">
                <el-radio label="STUDENT">学生</el-radio>
                <el-radio label="ADMIN">管理员</el-radio>
              </el-radio-group>
            </el-form-item>
            
            <!-- 学生注册：学号 -->
            <el-form-item v-if="registerForm.role === 'STUDENT'" label="学号" prop="studentNo">
              <el-input
                v-model="registerForm.studentNo"
                placeholder="请输入学号"
              ></el-input>
            </el-form-item>
            
            <!-- 管理员注册：工号 -->
            <el-form-item v-if="registerForm.role === 'ADMIN'" label="工号" prop="employeeNo">
              <el-input
                v-model="registerForm.employeeNo"
                placeholder="请输入预设工号"
              ></el-input>
              <div class="form-tip">
                <i class="el-icon-info"></i>
                管理员注册需要使用预设的工号
              </div>
            </el-form-item>
            
            <el-form-item label="密码" prop="password">
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="请输入密码(至少6位)"
                show-password
              ></el-input>
            </el-form-item>
            
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                placeholder="请再次输入密码"
                show-password
              ></el-input>
            </el-form-item>
            
            <el-form-item label="姓名" prop="name">
              <el-input
                v-model="registerForm.name"
                placeholder="请输入姓名"
              ></el-input>
            </el-form-item>
            
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="registerForm.gender">
                <el-radio :label="1">男</el-radio>
                <el-radio :label="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
            
            <el-form-item label="手机号" prop="phone">
              <el-input
                v-model="registerForm.phone"
                placeholder="请输入手机号"
              ></el-input>
            </el-form-item>
            
            <el-form-item label="邮箱" prop="email">
              <el-input
                v-model="registerForm.email"
                placeholder="请输入邮箱"
              ></el-input>
            </el-form-item>
            
            <!-- 学生专属字段 -->
            <template v-if="registerForm.role === 'STUDENT'">
              <el-form-item label="专业" prop="major">
                <el-input
                  v-model="registerForm.major"
                  placeholder="请输入专业"
                ></el-input>
              </el-form-item>
              
              <el-form-item label="班级" prop="className">
                <el-input
                  v-model="registerForm.className"
                  placeholder="请输入班级"
                ></el-input>
              </el-form-item>
            </template>
            
            <el-form-item>
              <el-button
                type="success"
                style="width: 100%"
                :loading="loading"
                @click="handleRegister"
              >
                注册
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
import { login, register } from '@/api/auth'

export default {
  name: 'Login',
  data() {
    // 确认密码验证
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.registerForm.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    
    // 学号/工号验证
    const validateStudentNo = (rule, value, callback) => {
      if (this.loginForm.role === 'STUDENT' && (!value || !value.trim())) {
        callback(new Error('请输入学号'))
      } else {
        callback()
      }
    }
    
    const validateEmployeeNo = (rule, value, callback) => {
      if (this.loginForm.role === 'ADMIN' && (!value || !value.trim())) {
        callback(new Error('请输入工号'))
      } else {
        callback()
      }
    }
    
    const validateRegisterStudentNo = (rule, value, callback) => {
      if (this.registerForm.role === 'STUDENT' && (!value || !value.trim())) {
        callback(new Error('请输入学号'))
      } else {
        callback()
      }
    }
    
    const validateRegisterEmployeeNo = (rule, value, callback) => {
      if (this.registerForm.role === 'ADMIN' && (!value || !value.trim())) {
        callback(new Error('请输入工号'))
      } else {
        callback()
      }
    }
    
    return {
      activeTab: 'login',
      loading: false,
      
      // 登录表单
      loginForm: {
        role: 'STUDENT',
        studentNo: '',
        employeeNo: '',
        password: ''
      },
      loginRules: {
        role: [
          { required: true, message: '请选择身份', trigger: 'change' }
        ],
        studentNo: [
          { validator: validateStudentNo, trigger: 'blur' }
        ],
        employeeNo: [
          { validator: validateEmployeeNo, trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ]
      },
      
      // 注册表单
      registerForm: {
        role: 'STUDENT',
        studentNo: '',
        employeeNo: '',
        password: '',
        confirmPassword: '',
        name: '',
        gender: 1,
        phone: '',
        email: '',
        major: '',
        className: ''
      },
      registerRules: {
        role: [
          { required: true, message: '请选择身份', trigger: 'change' }
        ],
        studentNo: [
          { validator: validateRegisterStudentNo, trigger: 'blur' }
        ],
        employeeNo: [
          { validator: validateRegisterEmployeeNo, trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码至少6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ],
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        gender: [
          { required: true, message: '请选择性别', trigger: 'change' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    handleTabClick(tab) {
      // 切换标签时重置表单
      if (tab.name === 'login') {
        this.$refs.loginForm && this.$refs.loginForm.resetFields()
      } else {
        this.$refs.registerForm && this.$refs.registerForm.resetFields()
      }
    },
    
    // 登录身份切换
    handleLoginRoleChange() {
      this.loginForm.studentNo = ''
      this.loginForm.employeeNo = ''
      this.$refs.loginForm && this.$refs.loginForm.clearValidate()
    },
    
    // 注册身份切换
    handleRegisterRoleChange() {
      this.registerForm.studentNo = ''
      this.registerForm.employeeNo = ''
      this.$refs.registerForm && this.$refs.registerForm.clearValidate()
    },
    
    // 处理登录
    handleLogin() {
      this.$refs.loginForm.validate(async (valid) => {
        if (!valid) return
        
        this.loading = true
        try {
          const response = await login(this.loginForm)
          
          // 保存token和用户信息
          localStorage.setItem('token', response.data.token)
          localStorage.setItem('userInfo', JSON.stringify(response.data.userInfo))
          
          this.$message.success('登录成功')
          
          // 根据角色跳转不同页面
          const role = response.data.userInfo.role
          if (role === 'ADMIN') {
            this.$router.push('/admin/dashboard')
          } else {
            this.$router.push('/home')
          }
        } catch (error) {
          this.$message.error(error.message || '登录失败')
        } finally {
          this.loading = false
        }
      })
    },
    
    // 处理注册
    handleRegister() {
      this.$refs.registerForm.validate(async (valid) => {
        if (!valid) return
        
        this.loading = true
        try {
          await register(this.registerForm)
          
          this.$message.success('注册成功,请登录')
          this.activeTab = 'login'
          
          // 设置登录表单的角色
          this.loginForm.role = this.registerForm.role
          if (this.registerForm.role === 'ADMIN') {
            this.loginForm.employeeNo = this.registerForm.employeeNo
          } else {
            this.loginForm.studentNo = this.registerForm.studentNo
          }
          
          // 清空注册表单
          this.$refs.registerForm.resetFields()
        } catch (error) {
          this.$message.error(error.message || '注册失败')
        } finally {
          this.loading = false
        }
      })
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 500px;
  border-radius: 10px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.login-card >>> .el-card__header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 10px 10px 0 0;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.form-tip i {
  margin-right: 4px;
}
</style>
