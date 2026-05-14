<template>
  <el-header class="nav-header">
    <div class="nav-container">
      <div class="logo" @click="router.push('/home')">
        <i class="el-icon-film"></i> lm-明日影院
      </div>
      <el-menu :default-active="activeMenu" mode="horizontal" router class="nav-menu">
        <el-menu-item index="/home">首页</el-menu-item>
        <el-menu-item index="/movie">电影列表</el-menu-item>
        <el-menu-item index="/admin/movie" v-if="isAdmin">电影管理</el-menu-item>
        <el-menu-item index="/admin/cinema" v-if="isAdmin">影院管理</el-menu-item>
        <el-menu-item index="/admin/refund" v-if="isAdmin">退票策略</el-menu-item>
      </el-menu>
      <div class="user-area">
        <el-dropdown v-if="isLoggedIn" @command="handleCommand" trigger="click">
          <div class="user-info">
            <el-avatar
                :size="40"
                :src="coloredDefaultAvatar"
                class="user-avatar"
            />
            <span class="username">{{ username }}</span>
            <i class="el-icon-arrow-down el-icon--right"></i>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="userMember">会员中心</el-dropdown-item>
              <el-dropdown-item command="userBuy">我的电影票</el-dropdown-item>
              <el-dropdown-item command="userCost">消费记录</el-dropdown-item>
              <el-dropdown-item command="logout" divided>安全退出</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <div v-else class="auth-btns">
          <el-button type="primary" link @click="router.push('/login')">登录</el-button>
          <el-button type="primary" @click="router.push('/register')">注册</el-button>
        </div>
      </div>
    </div>
  </el-header>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

// 🚀 核心修复：定义彩色默认头像地址
const coloredDefaultAvatar = 'https://img.js.design/assets/static/f56291a9b1c55bdf994f7d43232149b1.png'

const activeMenu = computed(() => route.path)
const user = ref(null)

const isLoggedIn = computed(() => !!user.value)
const username = computed(() => user.value?.username || '用户')
const isAdmin = computed(() => user.value?.role === 'admin')

const updateUserInfo = () => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    try {
      user.value = JSON.parse(userStr)
    } catch (e) {
      localStorage.removeItem('user')
      user.value = null
    }
  } else {
    user.value = null
  }
}

const handleCommand = (command) => {
  if (command === 'logout') {
    localStorage.removeItem('user')
    sessionStorage.removeItem('id')
    updateUserInfo()
    ElMessage.success('已安全退出')
    router.push('/login')
  } else {
    router.push('/' + command)
  }
}

onMounted(() => {
  updateUserInfo()
  window.addEventListener('storage', (e) => {
    if (e.key === 'user') updateUserInfo()
  })
})
</script>

<style scoped>
.nav-header {
  background: white;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  padding: 0;
  height: 64px !important;
  position: sticky;
  top: 0;
  z-index: 1000;
}
.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  height: 100%;
}
.logo {
  font-size: 24px;
  font-weight: 800;
  color: #1caf9a;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  margin-right: 40px;
}
.nav-menu {
  flex: 1;
  border-bottom: none !important;
  height: 100%;
}
.nav-menu .el-menu-item {
  height: 64px;
  line-height: 64px;
  font-size: 15px;
}
.user-area {
  display: flex;
  align-items: center;
}
.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  gap: 10px;
  outline: none;
}
.user-avatar {
  border: 2px solid #e0f2f1;
}
.username {
  font-size: 14px;
  color: #333;
}
</style>