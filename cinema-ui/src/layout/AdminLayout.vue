<template>
  <el-container class="admin-layout">
    <el-aside width="250px" class="admin-aside">
      <div class="admin-logo">
        <el-avatar :size="40" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
        <span class="admin-title">影城管理系统</span>
      </div>
      <el-menu :default-active="route.path" class="admin-menu" background-color="#1d2939" text-color="#a0a5ab" active-text-color="#fff" router>
        <el-menu-item index="/admin/movie">🎬 电影管理</el-menu-item>
        <el-menu-item index="/admin/schedule">📅 排片管理</el-menu-item>
        <el-menu-item index="/admin/promotion">🎁 活动管理</el-menu-item>
        <el-menu-item index="/admin/cinema">🏢 影院管理</el-menu-item>
        <el-menu-item index="/admin/vip">👑 会员策略</el-menu-item>
        <el-menu-item index="/admin/refund">🔙 退票策略</el-menu-item>
        <el-menu-item index="/admin/ticket">🎟️ 订单管理</el-menu-item>
        <el-menu-item index="/admin/statistic">📈 影院统计</el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="admin-header">
        <div class="header-breadcrumb">
          <span style="color: #666;">当前位置：</span><strong>{{ pageTitle }}</strong>
        </div>
        <el-button type="danger" plain size="small" @click="handleLogout">退出登录</el-button>
      </el-header>

      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const pageTitle = computed(() => {
  const titles = {
    '/admin/movie': '电影管理', '/admin/schedule': '排片管理', '/admin/promotion': '活动管理',
    '/admin/cinema': '影院/影厅管理', '/admin/vip': '会员充值策略', '/admin/refund': '退票策略配置', 
    '/admin/statistic': '影院数据统计', '/admin/ticket': '订单管理'
  }
  return titles[route.path] || '控制台'
})

const handleLogout = async () => {
  try {
    await axios.post('/api/logout')
  } catch (e) {
    console.error('后端注销失败，但前端依然会退出')
  }
  localStorage.removeItem('user')
  router.push('/login')
}
</script>

<style scoped>
.admin-layout { height: 100vh; background-color: #f5f7fa; }
.admin-aside { background-color: #1d2939; color: white; display: flex; flex-direction: column; box-shadow: 2px 0 6px rgba(0,21,41,0.35); z-index: 10; }
.admin-logo { height: 80px; display: flex; align-items: center; justify-content: center; gap: 15px; border-bottom: 1px solid #111a26; }
.admin-title { font-size: 18px; font-weight: bold; color: #1caf9a; letter-spacing: 1px; }
.admin-menu { border-right: none; flex: 1; }
.el-menu-item.is-active { background-color: #1caf9a !important; color: white !important; }
.admin-header { height: 60px; background-color: white; box-shadow: 0 1px 4px rgba(0,21,41,0.08); display: flex; justify-content: space-between; align-items: center; padding: 0 30px; }
.header-breadcrumb { font-size: 15px; }
.admin-main { padding: 30px; }
</style>