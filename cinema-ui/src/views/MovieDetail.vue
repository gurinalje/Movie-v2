<template>
  <div class="page-container">
    <NavBar />
    <main class="content-container">
      <div class="movie-header card">
        <img :src="movieInfo.posterUrl || movieInfo.poster" alt="海报" class="movie-poster" />
        <div class="movie-basic-info">
          <h1 class="movie-title">{{ movieInfo.name }}</h1>
          <div class="info-line"><span>简 介：</span><span class="gray-text">{{ movieInfo.description }}</span></div>
          <div class="info-grid">
            <div><span>上 映：</span><span>{{ (movieInfo.startDate || '').substring(0, 10) }}</span></div>
            <div><span>片 长：</span><span>{{ movieInfo.length }} 分钟</span></div>
            <div><span>类 型：</span><span>{{ movieInfo.type }}</span></div>
            <div><span>地 区：</span><span>{{ movieInfo.country }}</span></div>
            <div><span>语 言：</span><span>{{ movieInfo.language }}</span></div>
            <div><span>导 演：</span><span>{{ movieInfo.director }}</span></div>
            <div><span>编 剧：</span><span>{{ movieInfo.screenWriter || movieInfo.writer }}</span></div>
          </div>
          <div class="info-line"><span>主 演：</span><span>{{ movieInfo.starring }}</span></div>
          <div class="movie-operations">
            <el-button type="danger" plain @click="handleLike">
              💖 想 看 <span v-if="movieInfo.likeCount">({{ movieInfo.likeCount }})</span>
            </el-button>
          </div>
        </div>
      </div>

      <div class="session-container card" style="margin-top: 20px;">
        <h2 style="margin-bottom: 20px;">📅 选座购票</h2>

        <div v-if="scheduleDates && scheduleDates.length > 0">
          <el-tabs v-model="activeDate">
            <el-tab-pane v-for="item in scheduleDates" :key="item.date" :label="formatDateOnly(item.date)" :name="item.date">
              <el-table :data="item.scheduleItemList" stripe style="width: 100%" empty-text="今日暂未安排排片 🪹">
                <el-table-column label="放映时间" width="150" align="center">
                  <template #default="scope">
                    <strong style="font-size: 18px; color: #333;">{{ formatTimeOnly(scope.row.startTime) }}</strong>
                  </template>
                </el-table-column>
                <el-table-column label="结束时间" width="150" align="center">
                  <template #default="scope">
                    <span style="color: #999;">{{ formatTimeOnly(scope.row.endTime) }} 散场</span>
                  </template>
                </el-table-column>
                <el-table-column prop="hallName" label="放映影厅" align="center" />
                <el-table-column prop="fare" label="票价 (元)" align="center">
                  <template #default="scope">
                    <span style="color: #ed5565; font-weight: bold; font-size: 16px;">￥{{ scope.row.fare }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="150" align="center">
                  <template #default="scope">
                    <el-button type="primary" round @click="goToBuy(scope.row.id)">选座购票</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
          </el-tabs>
        </div>

        <el-empty v-else description="影院老板尚未安排排片，敬请期待！" />
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import NavBar from '../components/NavBar.vue' // 确保你的 NavBar 组件路径是正确的

const route = useRoute()
const router = useRouter()
const movieId = route.query.id
const movieInfo = ref({})
const scheduleDates = ref([])
const activeDate = ref('')

// 🌟 核心：拉取电影详情与排片信息 (已加入防崩溃护甲)
const fetchData = async () => {
  try {
    // 1. 获取电影基本详情
    const userStr = localStorage.getItem('user')
    const userId = userStr ? JSON.parse(userStr).id : 1 // 没登录默认传1，防止后端报错
    const res1 = await axios.get(`/api/movie/${movieId}/${userId}`)
    if (res1.data.success) {
      movieInfo.value = res1.data.content || {}
    }

    // 2. 获取排片列表
    const res2 = await axios.get(`/api/schedule/search/audience?movieId=${movieId}`)
    if (res2.data.success) {
      // 🌟 修复 Bug 的关键：用 || [] 兜底，防止后端返回 null 导致前端卡死
      scheduleDates.value = res2.data.content || []

      if (scheduleDates.value.length > 0) {
        // 默认激活第一天的 Tab
        activeDate.value = scheduleDates.value[0].date
      }
    }
  } catch (error) {
    console.error('获取电影数据失败:', error)
  }
}

// 💖 激活“想看”按钮功能
const handleLike = async () => {
  const userStr = localStorage.getItem('user')
  if (!userStr) return ElMessage.warning('请先登录后再点想看哦！')

  const user = JSON.parse(userStr)
  try {
    const res = await axios.post(`/api/movie/${movieInfo.value.id}/like?userId=${user.id}`)

    if (res.data.success) {
      ElMessage.success('💖 已成功加入想看列表！')
      fetchData() // 重新拉取数据，更新想看人数
    } else {
      ElMessage.info(res.data.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error('网络请求失败')
  }
}

// 跳转到买票选座页
const goToBuy = (scheduleId) => {
  router.push(`/movieBuy?scheduleId=${scheduleId}`)
}

// ✂️ 格式化小工具：只截取日期 (如 2026-04-12)
const formatDateOnly = (dateStr) => {
  if (!dateStr) return ''
  return dateStr.substring(0, 10)
}

// ✂️ 格式化小工具：只截取时间 (如 14:30)
const formatTimeOnly = (timeStr) => {
  if (!timeStr) return ''
  const d = new Date(timeStr)
  const pad = (n) => (n < 10 ? '0' + n : n)
  return `${pad(d.getHours())}:${pad(d.getMinutes())}`
}

onMounted(() => {
  if (!movieId) {
    ElMessage.error('缺少电影参数')
    router.push('/home')
    return
  }
  fetchData()
})
</script>

<style scoped>
.page-container {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding-bottom: 40px;
}

.content-container {
  max-width: 1000px;
  margin: 30px auto;
}

.card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  padding: 30px;
}

.movie-header {
  display: flex;
  gap: 40px;
}

.movie-poster {
  width: 240px;
  height: 336px;
  border-radius: 8px;
  object-fit: cover;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
}

.movie-basic-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.movie-title {
  font-size: 28px;
  margin-top: 0;
  margin-bottom: 20px;
  color: #333;
}

.info-line {
  margin-bottom: 15px;
  line-height: 1.6;
  font-size: 15px;
}

.info-line span:first-child {
  color: #999;
}

.gray-text {
  color: #666;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
  margin-bottom: 15px;
  font-size: 15px;
}

.info-grid div span:first-child {
  color: #999;
}

.movie-operations {
  margin-top: auto;
  padding-top: 20px;
}
</style>