<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <div slot="header">
        <span>个人信息</span>
        <el-button
          v-if="!isEditing"
          style="float: right"
          type="primary"
          size="small"
          icon="el-icon-edit"
          @click="handleEdit"
        >
          编辑资料
        </el-button>
      </div>

      <!-- 查看模式 -->
      <div v-if="!isEditing" class="profile-view">
        <el-row :gutter="20">
          <el-col :span="24" class="avatar-section">
            <el-avatar :size="100" :src="userInfo.avatar || defaultAvatar" />
            <h2>{{ userInfo.name || '未设置' }}</h2>
            <el-tag v-if="userInfo.gender === 1" type="primary">男</el-tag>
            <el-tag v-else-if="userInfo.gender === 2" type="danger">女</el-tag>
          </el-col>
        </el-row>

        <el-divider></el-divider>

        <el-descriptions :column="2" border>
          <el-descriptions-item label="学号">
            <el-tag>{{ userInfo.studentNo || '-' }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="姓名">
            {{ userInfo.name || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="性别">
            <span v-if="userInfo.gender === 1">男</span>
            <span v-else-if="userInfo.gender === 2">女</span>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="年龄">
            {{ userInfo.age || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="手机号">
            {{ userInfo.phone || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="邮箱">
            {{ userInfo.email || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="专业">
            {{ userInfo.major || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="班级">
            {{ userInfo.className || '-' }}
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 编辑模式 -->
      <div v-else class="profile-edit">
        <el-form
          ref="profileForm"
          :model="editForm"
          :rules="formRules"
          label-width="100px"
        >
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="姓名" prop="name">
                <el-input v-model="editForm.name" placeholder="请输入姓名"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="性别" prop="gender">
                <el-radio-group v-model="editForm.gender">
                  <el-radio :label="1">男</el-radio>
                  <el-radio :label="2">女</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="年龄" prop="age">
                <el-input-number
                  v-model="editForm.age"
                  :min="16"
                  :max="100"
                  style="width: 100%"
                ></el-input-number>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="editForm.phone" placeholder="请输入手机号"></el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="邮箱" prop="email">
                <el-input v-model="editForm.email" placeholder="请输入邮箱"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="专业" prop="major">
                <el-input v-model="editForm.major" placeholder="请输入专业"></el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="班级" prop="className">
            <el-input v-model="editForm.className" placeholder="请输入班级"></el-input>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleSave" :loading="saving">
              保存
            </el-button>
            <el-button @click="handleCancel">取消</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <!-- 修改密码卡片 -->
    <el-card class="password-card" style="margin-top: 20px">
      <div slot="header">
        <span>修改密码</span>
      </div>
      <el-form
        ref="passwordForm"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="100px"
        style="max-width: 500px"
      >
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            placeholder="请输入旧密码"
          ></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            placeholder="请输入新密码"
          ></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handlePasswordChange" :loading="changingPassword">
            修改密码
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { getUserProfile, updateUserProfile, changePassword } from '@/api/user'

export default {
  name: 'Profile',
  data() {
    const validatePhone = (rule, value, callback) => {
      if (value && !/^1[3-9]\d{9}$/.test(value)) {
        callback(new Error('请输入正确的手机号'))
      } else {
        callback()
      }
    }

    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }

    return {
      isEditing: false,
      saving: false,
      changingPassword: false,
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      
      userInfo: {},
      editForm: {},
      
      formRules: {
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        phone: [
          { validator: validatePhone, trigger: 'blur' }
        ],
        email: [
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ]
      },
      
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      
      passwordRules: {
        oldPassword: [
          { required: true, message: '请输入旧密码', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, message: '密码长度至少6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入新密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.fetchUserInfo()
  },
  methods: {
    async fetchUserInfo() {
      try {
        const response = await getUserProfile()
        this.userInfo = response.data
      } catch (error) {
        this.$message.error('获取用户信息失败')
      }
    },
    
    handleEdit() {
      this.isEditing = true
      this.editForm = { ...this.userInfo }
    },
    
    handleCancel() {
      this.isEditing = false
      this.editForm = {}
    },
    
    handleSave() {
      this.$refs.profileForm.validate(async (valid) => {
        if (!valid) return
        
        this.saving = true
        try {
          await updateUserProfile(this.editForm)
          this.$message.success('保存成功')
          this.isEditing = false
          this.fetchUserInfo()
        } catch (error) {
          this.$message.error('保存失败')
        } finally {
          this.saving = false
        }
      })
    },
    
    handlePasswordChange() {
      this.$refs.passwordForm.validate(async (valid) => {
        if (!valid) return
        
        this.changingPassword = true
        try {
          await changePassword({
            oldPassword: this.passwordForm.oldPassword,
            newPassword: this.passwordForm.newPassword
          })
          this.$message.success('密码修改成功，请重新登录')
          this.$refs.passwordForm.resetFields()
          // 可以选择跳转到登录页
          setTimeout(() => {
            this.$router.push('/login')
          }, 1500)
        } catch (error) {
          this.$message.error(error.message || '密码修改失败')
        } finally {
          this.changingPassword = false
        }
      })
    }
  }
}
</script>

<style scoped>
.profile-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.avatar-section {
  text-align: center;
  padding: 20px 0;
}

.avatar-section h2 {
  margin: 15px 0 10px 0;
}

.profile-view {
  padding: 20px;
}

.profile-edit {
  padding: 20px;
}
</style>
