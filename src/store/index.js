import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    // 用户信息
    userInfo: JSON.parse(localStorage.getItem('userInfo')) || null,
    // Token
    token: localStorage.getItem('token') || '',
    // 侧边栏折叠状态
    sidebarCollapsed: false
  },
  
  mutations: {
    // 设置用户信息
    SET_USER_INFO(state, userInfo) {
      state.userInfo = userInfo
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
    },
    
    // 设置Token
    SET_TOKEN(state, token) {
      state.token = token
      localStorage.setItem('token', token)
    },
    
    // 清除用户信息
    CLEAR_USER_INFO(state) {
      state.userInfo = null
      state.token = ''
      localStorage.removeItem('userInfo')
      localStorage.removeItem('token')
    },
    
    // 切换侧边栏状态
    TOGGLE_SIDEBAR(state) {
      state.sidebarCollapsed = !state.sidebarCollapsed
    }
  },
  
  actions: {
    // 登录
    login({ commit }, data) {
      commit('SET_USER_INFO', data.userInfo)
      commit('SET_TOKEN', data.token)
    },
    
    // 登出
    logout({ commit }) {
      commit('CLEAR_USER_INFO')
    },
    
    // 更新用户信息
    updateUserInfo({ commit }, userInfo) {
      commit('SET_USER_INFO', userInfo)
    }
  },
  
  getters: {
    // 是否已登录
    isLoggedIn: state => !!state.token,
    
    // 获取用户信息
    getUserInfo: state => state.userInfo,
    
    // 获取用户名
    getUserName: state => state.userInfo ? state.userInfo.name : '',
    
    // 获取用户角色
    getUserRole: state => state.userInfo ? state.userInfo.role : '',
    
    // 是否是管理员
    isAdmin: state => state.userInfo && state.userInfo.role === 'ADMIN',
    
    // 是否是学生
    isStudent: state => state.userInfo && state.userInfo.role === 'STUDENT',
    
    // 侧边栏状态
    getSidebarCollapsed: state => state.sidebarCollapsed
  }
})
