<template>
  <div class="home-container">
    <NavBar />
    <main class="main-content">
      <section class="banner-section card-shadow">
        <el-carousel height="480px" indicator-position="outside" :interval="6000" arrow="always">
          <el-carousel-item v-for="item in bannerMovies" :key="item.id">
            <div class="banner-wrapper" @click="goToDetail(item.id)">

              <div class="blur-background" :style="{ backgroundImage: `url(${item.posterUrl})` }"></div>

              <div class="banner-mask"></div>

              <div class="banner-content">
                <div class="poster-stage">
                  <img :src="item.posterUrl" class="real-poster card-shadow" :alt="item.name" />
                </div>
                <div class="movie-details">
                  <h1 class="movie-name-big">{{ item.name }}</h1>
                  <div class="movie-meta">
                    <el-tag size="small" effect="plain" color="rgba(255,255,255,0.2)" style="color:white; border:none;">
                      {{ item.type || '未知类型' }}
                    </el-tag>
                    <span class="language">{{ item.language || '国语' }}</span>
                  </div>
                  <p class="movie-desc">{{ item.description || '暂无剧情简介，敬请期待。' }}</p>
                  <el-button type="danger" size="large" class="buy-btn-big" round>
                    🎟️ 立即购票
                  </el-button>
                </div>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
      </section>

      <div class="content-grid">
        <aside class="sidebar">
          <div class="section-card card-shadow">
            <h3 class="section-title">🌟 最受欢迎影星</h3>
            <div class="star-list">
              <div v-for="(star, index) in popularStars" :key="index" class="star-item">
                <span class="rank-num" :class="{'top-three': index < 3}">{{ index + 1 }}</span>
                <span class="star-name">{{ star }}</span>
              </div>
              <el-empty v-if="popularStars.length === 0" description="暂无购票记录" :image-size="60" />
            </div>
          </div>
        </aside>

        <section class="movie-section">
          <div class="section-header">
            <h3 class="section-title">🔥 正在热映</h3>
            <el-button type="primary" link @click="router.push('/movie')">查看全部 ></el-button>
          </div>
          <div class="movie-grid">
            <div v-for="movie in hotMovies" :key="movie.id" class="movie-card card-shadow" @click="goToDetail(movie.id)">
              <div class="poster-wrapper">
                <img :src="movie.posterUrl" :alt="movie.name">
                <div class="play-icon"><i class="el-icon-video-play"></i></div>
              </div>
              <div class="movie-info">
                <div class="movie-name">{{ movie.name }}</div>
                <div class="movie-type">{{ movie.type }}</div>
              </div>
            </div>
          </div>
        </section>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import NavBar from '../components/NavBar.vue'

const router = useRouter()
const bannerMovies = ref([])
const hotMovies = ref([])
const popularStars = ref([])

const fetchData = async () => {
  try {
    const movieRes = await axios.get('/api/movie/all/exclude/off')
    if (movieRes.data.success) {
      const allMovies = movieRes.data.content || []
      // 取前 8 部为热门
      hotMovies.value = allMovies.slice(0, 8)
      // 取前 5 部进入轮播图
      bannerMovies.value = allMovies.slice(0, 5)
    }

    const starRes = await axios.get('/api/statistics/popular/stars')
    if (starRes.data.success) {
      popularStars.value = starRes.data.content || []
    }
  } catch (e) {
    console.error('加载首页数据失败', e)
  }
}

const goToDetail = (id) => {
  router.push({ path: '/movieDetail', query: { id: id } })
}

onMounted(fetchData)
</script>

<style scoped>
/* 🚀 全局统一样式 */
.home-container {
  background-color: #f0f2f5; /* 更柔和的背景色 */
  min-height: 100vh;
  font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", Arial, sans-serif;
}

.main-content {
  max-width: 1260px; /* 略微加宽 */
  margin: 0 auto;
  padding: 30px 20px;
}

.card-shadow {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  border-radius: 12px;
  overflow: hidden;
}

/* 🚀 核心：全新轮播图样式 */
.banner-section {
  margin-bottom: 40px;
}

.banner-wrapper {
  height: 100%;
  width: 100%;
  position: relative;
  cursor: pointer;
  overflow: hidden;
}

/* 1️⃣ 高斯模糊背景 */
.blur-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-size: cover;
  background-position: center;
  filter: blur(40px) brightness(0.6); /* 核心：深度模糊并压暗 */
  transform: scale(1.1); /* 防止模糊边缘出现白边 */
  z-index: 1;
}

/* 2️⃣ 黑色遮罩 */
.banner-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3); /* 叠加一层淡淡的黑色，增加层次感 */
  z-index: 2;
}

/* 3️⃣ 内容层 */
.banner-content {
  position: relative;
  z-index: 3;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 80px; /* 左右留白，防止太靠边 */
  gap: 60px; /* 左右间距 */
}

.poster-stage {
  flex-shrink: 0; /* 防止海报被挤压 */
  height: 380px;
}

/* 🚀 解决高糊核心：原图展示，不拉伸 */
.real-poster {
  height: 100%;
  width: auto; /* 宽度自适应，保持原图比例 */
  border: 4px solid rgba(255,255,255,0.6); /* 精美白边 */
  object-fit: contain; /* 核心：确保原图完全显示在框内 */
  transition: transform 0.3s;
}

.banner-wrapper:hover .real-poster {
  transform: scale(1.03) rotate(1deg); /* 悬停交互 */
}

.movie-details {
  flex: 1;
  color: white;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  max-width: 600px;
}

.movie-name-big {
  font-size: 40px;
  margin: 0 0 15px 0;
  font-weight: 800;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.5);
  letter-spacing: 1px;
}

.movie-meta {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 25px;
  opacity: 0.9;
}

.language {
  font-size: 14px;
}

.movie-desc {
  font-size: 16px;
  line-height: 1.8;
  opacity: 0.8;
  margin-bottom: 35px;
  height: 86px; /* 限制高度 */
  display: -webkit-box;
  -webkit-line-clamp: 3; /* 限制3行 */
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-align: justify;
}

.buy-btn-big {
  font-weight: bold;
  padding: 12px 40px;
  font-size: 16px;
  background: linear-gradient(135deg, #ff416c, #ff4b2b); /* 渐变色按钮 */
  border: none;
  box-shadow: 0 4px 15px rgba(255, 75, 43, 0.4);
  transition: 0.3s;
}

.buy-btn-big:hover {
  box-shadow: 0 6px 20px rgba(255, 75, 43, 0.6);
  transform: translateY(-2px);
}

/* 🚀 其他区域优化 */
.content-grid {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 30px;
}

.section-card {
  background: white;
  padding: 25px;
}

.section-title {
  font-size: 20px;
  margin-bottom: 25px;
  color: #333;
  border-left: 5px solid #ff4b2b;
  padding-left: 12px;
  font-weight: 800;
}

.star-list { display: flex; flex-direction: column; gap: 18px; }
.star-item { display: flex; align-items: center; gap: 15px; padding-bottom: 10px; border-bottom: 1px solid #f0f0f0; }
.star-item:last-child { border-bottom: none; }
.rank-num { width: 28px; height: 28px; background: #edeff2; border-radius: 6px; display: flex; align-items: center; justify-content: center; font-size: 14px; color: #7f8c8d; font-weight: bold; }
.rank-num.top-three { background: linear-gradient(135deg, #ffac30, #ff4b2b); color: white; }
.star-name { font-size: 15px; color: #444; font-weight: 500; }

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.movie-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 25px;
}

.movie-card {
  background: white;
  cursor: pointer;
  transition: all 0.3s ease;
}

.movie-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
}

.poster-wrapper {
  width: 100%;
  height: 290px;
  position: relative;
  overflow: hidden;
}

.movie-card img {
  width: 100%;
  height: 100%;
  object-fit: cover; /* 列表页海报依然使用裁剪，保证整齐 */
  transition: transform 0.5s;
}

.movie-card:hover img {
  transform: scale(1.08);
}

/* 播放按钮图标悬停效果 */
.play-icon {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 50px;
  color: rgba(255, 255, 255, 0.8);
  opacity: 0;
  transition: 0.3s;
  z-index: 2;
}

.movie-card:hover .play-icon {
  opacity: 1;
}

.movie-info {
  padding: 15px;
}

.movie-name {
  font-weight: bold;
  font-size: 16px;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: #333;
}

.movie-type {
  font-size: 13px;
  color: #7f8c8d;
}
</style>