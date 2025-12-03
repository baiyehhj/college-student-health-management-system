<template>
  <el-container class="admin-container">
    <!-- 侧边栏 -->
    <el-aside :width="sidebarWidth" class="sidebar">
      <div class="logo">
        <i class="el-icon-s-platform"></i>
        <span v-if="!collapsed">管理后台</span>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        :collapse="collapsed"
        :unique-opened="true"
        router
        background-color="#1f2d3d"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <el-menu-item index="/admin/dashboard">
          <i class="el-icon-s-data"></i>
          <span slot="title">数据统计</span>
        </el-menu-item>
        
        <el-menu-item index="/admin/students">
          <i class="el-icon-user"></i>
          <span slot="title">学生管理</span>
        </el-menu-item>
        
        <el-menu-item index="/admin/alerts">
          <i class="el-icon-warning"></i>
          <span slot="title">健康预警</span>
        </el-menu-item>
        
        <el-submenu index="export">
          <template slot="title">
            <i class="el-icon-download"></i>
            <span>数据导出</span>
          </template>
          <el-menu-item index="/admin/export">
            <i class="el-icon-document"></i>
            <span slot="title">批量导出</span>
          </el-menu-item>
        </el-submenu>
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
          <el-breadcrumb separator="/">
            <el-breadcrumb-item>管理后台</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentPageTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar
                :size="35"
                :src="userInfo.avatar"
                icon="el-icon-s-custom"
              ></el-avatar>
              <span class="user-name">{{ userInfo.name }}</span>
              <el-tag size="mini" type="danger" style="margin-left: 8px;">管理员</el-tag>
              <i class="el-icon-arrow-down"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="logout">
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
export default {
  name: 'AdminHome',
  data() {
    return {
      collapsed: false
    }
  },
  computed: {
    userInfo() {
      const info = localStorage.getItem('userInfo')
      return info ? JSON.parse(info) : {}
    },
    sidebarWidth() {
      return this.collapsed ? '64px' : '200px'
    },
    activeMenu() {
      return this.$route.path
    },
    currentPageTitle() {
      const titleMap = {
        '/admin/dashboard': '数据统计',
        '/admin/students': '学生管理',
        '/admin/alerts': '健康预警',
        '/admin/export': '批量导出'
      }
      return titleMap[this.$route.path] || '数据统计'
    }
  },
  created() {
    // 检查是否是管理员
    if (this.userInfo.role !== 'ADMIN') {
      this.$message.error('无权限访问管理后台')
      this.$router.push('/login')
    }
  },
  methods: {
    toggleSidebar() {
      this.collapsed = !this.collapsed
    },
    
    handleCommand(command) {
      if (command === 'logout') {
        this.$confirm('确认退出登录吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          this.$message.success('已退出登录')
          this.$router.push('/login')
        })
      }
    }
  }
}
</script>

<style scoped>
.admin-container {
  height: 100vh;
}

.sidebar {
  background-color: #1f2d3d;
  transition: width 0.28s;
  overflow-x: hidden;
}

.logo {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 60px;
  color: white;
  font-size: 18px;
  font-weight: bold;
  background: linear-gradient(135deg, #f56c6c 0%, #e74c3c 100%);
}

.logo i {
  font-size: 24px;
  margin-right: 8px;
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

.header-left {
  display: flex;
  align-items: center;
}

.toggle-btn {
  font-size: 24px;
  cursor: pointer;
  color: #5a5e66;
  transition: color 0.3s;
  margin-right: 15px;
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
