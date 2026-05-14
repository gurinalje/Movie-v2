<template>
  <div class="page-container">
    <NavBar />
    <main class="content-container">
      <div class="filter-container card">
        <div class="search-box">
          <el-input
              v-model="searchKeyword"
              placeholder="请输入电影名称关键字进行搜索..."
              class="input-with-select"
              size="large"
              clearable
              @keyup.enter="handleSearch"
          >
            <template #append><el-button type="primary" @click="handleSearch">搜 索</el-button></template>
          </el-input>
        </div>
      </div>

      <div class="movie-grid">
        <div class="movie-item card" v-for="movie in displayMovies" :key="movie.id">
          <div class="movie-poster" @click="goToDetail(movie.id)">
            <img :src="movie.posterUrl || movie.poster" alt="poster" />

            <div class="movie-status" v-if="movie.isNowPlaying">正在热映</div>
            <div class="movie-status upcoming" v-else>即将上映</div>
          </div>
          <div class="movie-info">
            <h3 class="movie-title" :title="movie.name">{{ movie.name }}</h3>
            <p class="movie-type">类型: {{ movie.type }}</p>

            <el-button
                :type="movie.isNowPlaying ? 'primary' : 'warning'"
                plain
                class="action-btn"
                @click="goToDetail(movie.id)"
            >
              {{ movie.isNowPlaying ? '购 票' : '预 览' }}
            </el-button>
          </div>
        </div>

        <el-empty v-if="displayMovies.length === 0" description="没有找到相关的电影哦~" style="width: 100%;" />
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import NavBar from '../components/NavBar.vue'

const router = useRouter()
const goToDetail = (id) => router.push(`/movieDetail?id=${id}`)

// 搜索交互反馈
const searchKeyword = ref('')
const handleSearch = () => {
  if(searchKeyword.value) {
    ElMessage.success(`为您找到与 "${searchKeyword.value}" 相关的结果`)
  }
}

// 存放真实的电影列表
const allMovies = ref([])

// ===== 核心：从后端获取真实电影数据并智能分类 =====
const fetchMovies = async () => {
  try {
    const res = await axios.get('/api/movie/all/exclude/off')
    if (res.data.success) {
      const now = new Date()

      // 遍历后端传来的电影，动态计算它到底是“热映”还是“即将上映”
      allMovies.value = res.data.content.map(m => {
        const releaseDate = new Date(m.startDate || m.start_date)
        return {
          ...m,
          isNowPlaying: releaseDate <= now // 新增一个专属属性，方便模板里做判断
        }
      })
    }
  } catch (error) {
    console.error('获取电影列表失败', error)
  }
}

// 页面加载时拉取数据
onMounted(() => {
  fetchMovies()
})

// ===== 极速搜索过滤逻辑 =====
const displayMovies = computed(() => {
  if (!searchKeyword.value) return allMovies.value

  // 忽略大小写进行模糊匹配
  const keyword = searchKeyword.value.toLowerCase()
  return allMovies.value.filter(m => m.name.toLowerCase().includes(keyword))
})
</script>

<style scoped>
/* (下方的 CSS 代码完全没动，保留你原本完美的设计) */
.page-container { min-height: 100vh; background-color: #f5f7fa; }
.content-container { max-width: 1200px; margin: 30px auto; padding: 0 20px; }
.card { background: #fff; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }
.filter-container { padding: 30px; margin-bottom: 30px; display: flex; justify-content: center; }
.search-box { width: 600px; }
.movie-grid { display: flex; flex-wrap: wrap; gap: 30px; }
.movie-item { width: 210px; overflow: hidden; transition: transform 0.3s, box-shadow 0.3s; }
.movie-item:hover { transform: translateY(-8px); box-shadow: 0 12px 24px rgba(0,0,0,0.1); }
.movie-poster { width: 100%; height: 294px; position: relative; cursor: pointer; }
.movie-poster img { width: 100%; height: 100%; object-fit: cover; }
.movie-status { position: absolute; top: 10px; right: -25px; background-color: #ed5565; color: white; font-size: 12px; padding: 4px 30px; transform: rotate(45deg); font-weight: bold; }
.movie-status.upcoming { background-color: #f8ac59; }
.movie-info { padding: 15px; text-align: center; }
.movie-title { font-size: 18px; margin: 0 0 10px 0; color: #333; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.movie-type { font-size: 13px; color: #999; margin: 0 0 15px 0; }
.action-btn { width: 100%; }
</style>