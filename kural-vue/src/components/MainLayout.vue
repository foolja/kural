<template>
  <el-container class="main-container">
    <!-- 左侧侧边栏 -->
    <el-aside 
      class="vertical-nav"
      :width="isCollapse ? '64px' : '240px'"
    >
      <div class="nav-header">
        <img 
          src="@/assets/logo.png" 
          class="nav-logo"
          :style="{width: isCollapse ? '20px' : '80px'}"
        >
      </div>

      <el-menu
        class="nav-menu"
        background-color="#2c3e50"
        text-color="#b5bdc8"
        active-text-color="#409eff"
        :collapse="isCollapse"
        :collapse-transition="false"
        router
      >
        <el-submenu index="1">
          <template #title>
            <i class="el-icon-s-data"></i>
            <span>系统概述</span>
          </template>
          <el-menu-item index="/main/hardware-info">
            
            <span>硬件信息</span>
          </el-menu-item>
          <el-menu-item index="/main/port-scan">
            
            <span>网络流量</span>
          </el-menu-item>
        </el-submenu>
        <el-submenu index="2">
          <template #title>
            <i class="el-icon-s-promotion"></i>
            <span>任务管理</span>
          </template>

          <el-menu-item index="/main/task-create">          
            <span>新建任务</span>
          </el-menu-item>

          <el-menu-item index="/main/task-list">  
            <span>任务列表</span>
          </el-menu-item>
        </el-submenu>
        <el-submenu index="3">
          <template #title>
            <i class="el-icon-monitor"></i>
            <span>态势感知</span>
          </template>
          <el-menu-item index="/main/threat-analysis">
            
            <span>威胁分析</span>
          </el-menu-item>
          <el-menu-item index="/main/risk-assessment">
            
            <span>风险评估</span>
          </el-menu-item>
        </el-submenu>
      </el-menu>
    </el-aside>

    <!-- 右侧主容器 -->
    <el-container class="main-wrapper">
      <!-- 顶部栏 -->
      <el-header class="main-header">
        <div class="header-content">
          <div class="header-left">
            <el-button
              class="collapse-btn"
              @click="toggleCollapse"
              :icon="isCollapse ? 'el-icon-s-unfold' : 'el-icon-s-fold'"
              circle
            />
            <span class="system-title">网络空间测绘系统</span>
          </div>
          
          <div class="header-right">
            <el-input
              v-model="searchKey"
              placeholder="全局搜索资产"
              prefix-icon="el-icon-search"
              class="search-input"
            />
            
            <el-dropdown class="user-dropdown">
              <div class="user-info">
                <el-avatar 
                  size="small"
                  src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png"
                />
                <span class="user-name">{{ $store.getters.username }}</span>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click.native="$router.push('/profile')">
                    <i class="el-icon-user"></i>个人中心
                  </el-dropdown-item>
                  <el-dropdown-item divided @click.native="handleLogout">
                    <i class="el-icon-switch-button"></i>退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-header>

      <!-- 主体内容 -->
      <div class="main-content">
          <router-view/>
      </div>
    </el-container>
  </el-container>
</template>

<script>
import { mapActions } from 'vuex'
import { getUserInfo } from '../api/userInfo.js'
import { checkLoginStatus } from '../utils/cookie.js'
export default {
  data() {
    return {
      isCollapse: false,
      activeMenu: '',
      searchKey: '',
      chartDate: []
    }
  },
  methods: {
    ...mapActions(['logout']),
    
    async handleLogout() {
      console.log("退出登录")
      try {
        await this.logout()
      } catch (error) {
        this.$message.error('退出登录失败')
      }
    },
    
    toggleCollapse() {
      this.isCollapse = !this.isCollapse
    }
  },
  async mounted(){
      if(checkLoginStatus()){
        const response = await getUserInfo()
     if (response.data.code === '200' &&  response.data.msg != null) {
        this.$store.commit('SET_USERNAME',response.data.data.username)
        this.$store.commit('SET_AUTHENTICATED',true)
        this.$store.commit('SET_USER_ID',response.data.data.userId)
     }else{
        this.$message.error('请先登录')
     }   
    }
     
  }
}
</script>

<style scoped>
/* 主容器 */
.main-container {
  display: flex;
  height: 100vh;
  width: 100vw;
  overflow: hidden;
  position: relative;
}

/* 左侧导航栏 */
.vertical-nav {
  transition: width 0.3s;
  background-color: #2c3e50;
  height: 100vh;
  overflow: hidden;
}

.nav-header {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #1f2937;
  flex-shrink: 0;
}

.nav-logo {
  height: 32px;
  transition: all 0.3s;
}

.nav-menu {
  flex: 1;
  border-right: none;
}

/* 顶部栏 */
.main-header {
  height: 64px !important;
  padding: 0 24px;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  background-color: #ffffff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  z-index: 1;
}

.header-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
}

.collapse-btn {
  background-color: transparent;
  border: none;
  font-size: 20px;
  color: #409eff;
  margin-right: 16px;
}

.system-title {
  font-size: 18px;
  color: #000;
}

.header-right {
  display: flex;
  align-items: center;
}

.search-input {
  width: 250px;
  margin-right: 16px;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-name {
  margin-left: 8px;
  font-size: 16px;
  color: #000;
}


/* 右侧内容区 */
.main-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  position: relative;
}
.main-content{
  background: #ffffff;
  flex: 1;
  height: 100%;
}
</style>

<style>
/* 全局样式重置 */
html, body, #app {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;

}
</style>
