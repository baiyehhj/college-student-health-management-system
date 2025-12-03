<template>
  <el-container class="home-container">
    <!-- 侧边栏 -->
    <el-aside :width="sidebarWidth" class="sidebar">
      <div class="logo">
        <i class="el-icon-s-data"></i>
        <span v-if="!collapsed">健康管理系统</span>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        :collapse="collapsed"
        :unique-opened="true"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <el-menu-item index="/dashboard">
          <i class="el-icon-s-home"></i>
          <span slot="title">数据面板</span>
        </el-menu-item>
        
        <el-submenu index="behavior">
          <template slot="title">
            <i class="el-icon-document"></i>
            <span>行为数据</span>
          </template>
          <el-menu-item index="/diet-record">
            <i class="el-icon-apple"></i>
            <span slot="title">饮食记录</span>
          </el-menu-item>
          <el-menu-item index="/exercise-record">
            <i class="el-icon-trophy"></i>
            <span slot="title">运动记录</span>
          </el-menu-item>
          <el-menu-item index="/sleep-record">
            <i class="el-icon-moon-night"></i>
            <span slot="title">作息记录</span>
          </el-menu-item>
          <el-menu-item index="/mood-record">
            <i class="el-icon-sunny"></i>
            <span slot="title">情绪记录</span>
          </el-menu-item>
        </el-submenu>
        
        <el-menu-item index="/health-exam">
          <i class="el-icon-s-order"></i>
          <span slot="title">体检报告</span>
        </el-menu-item>
        
        <el-menu-item index="/data-analysis">
          <i class="el-icon-data-line"></i>
          <span slot="title">数据分析</span>
        </el-menu-item>
        
        <el-menu-item index="/ai-report">
          <i class="el-icon-magic-stick"></i>
          <span slot="title">AI健康报告</span>
        </el-menu-item>
        
        <el-menu-item index="/notification">
          <i class="el-icon-bell"></i>
          <span slot="title">系统通知</span>
        </el-menu-item>
        
        <el-menu-item index="/profile">
          <i class="el-icon-user"></i>
          <span slot="title">个人中心</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <!-- 主体内容 -->
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header class="header">
        <div class="header-left">
          <i
            :class="collapsed ? 'el-icon-s-unfold' : 'el-icon-s-fold'"
            class="toggle-btn"
            @click="toggleSidebar"
          ></i>
        </div>
        
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar
                :size="35"
                :src="userInfo.avatar"
                icon="el-icon-user-solid"
              ></el-avatar>
              <span class="user-name">{{ userInfo.name }}</span>
              <i class="el-icon-arrow-down"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="profile">
                <i class="el-icon-user"></i> 个人中心
              </el-dropdown-item>
              <el-dropdown-item command="logout" divided>
                <i class="el-icon-switch-button"></i> 退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </el-header>
      
      <!-- 内容区域 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'Home',
  data() {
    return {
      collapsed: false
    }
  },
  computed: {
    ...mapGetters(['getUserInfo']),
    userInfo() {
      return this.getUserInfo || {}
    },
    sidebarWidth() {
      return this.collapsed ? '64px' : '200px'
    },
    activeMenu() {
      return this.$route.path
    }
  },
  methods: {
    // 切换侧边栏
    toggleSidebar() {
      this.collapsed = !this.collapsed
      this.$store.commit('TOGGLE_SIDEBAR')
    },
    
    // 处理用户下拉菜单
    handleCommand(command) {
      if (command === 'profile') {
        // 避免重复导航到当前路由
        if (this.$route.path !== '/profile') {
          this.$router.push('/profile')
        }
      } else if (command === 'logout') {
        this.$confirm('确认退出登录吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$store.dispatch('logout')
          this.$message.success('已退出登录')
          this.$router.push('/login')
        })
      }
    }
  }
}
</script>

<style scoped>
.home-container {
  height: 100vh;
}

.sidebar {
  background-color: #304156;
  transition: width 0.28s;
  overflow-x: hidden;
}

.logo {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 60px;
  color: white;
  font-size: 20px;
  font-weight: bold;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.logo i {
  font-size: 28px;
  margin-right: 10px;
}

.el-menu {
  border-right: none;
}

.header {
  background: white;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.toggle-btn {
  font-size: 24px;
  cursor: pointer;
  color: #5a5e66;
  transition: color 0.3s;
}

.toggle-btn:hover {
  color: #409eff;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.user-name {
  margin: 0 10px;
  color: #5a5e66;
}

.main-content {
  background: #f0f2f5;
  overflow-y: auto;
}
</style>
