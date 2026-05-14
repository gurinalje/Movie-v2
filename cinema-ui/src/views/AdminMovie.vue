<template>
  <div class="card">
    <div class="operation-bar">
      <el-button type="primary" size="large" @click="openAddModal">
        ➕ 上架新电影
      </el-button>
      <el-input
          v-model="searchKeyword"
          placeholder="搜索电影名称..."
          style="width: 250px;"
          clearable
      />
    </div>

    <el-table :data="filteredMovies" stripe border style="width: 100%; margin-top: 20px;">
      <el-table-column prop="posterUrl" label="海报" width="100" align="center">
        <template #default="scope">
          <el-image
              :src="scope.row.posterUrl"
              style="width: 60px; height: 84px; border-radius: 4px;"
              fit="cover"
          />
        </template>
      </el-table-column>
      <el-table-column prop="name" label="电影名称" min-width="150" style="font-weight: bold;" />
      <el-table-column prop="type" label="类型" min-width="120" />
      <el-table-column prop="startDate" label="上映时间" min-width="120">
        <template #default="scope">
          {{ scope.row.startDate ? scope.row.startDate.substring(0, 10) : '' }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.status === 0 ? 'success' : 'info'">
            {{ scope.row.status === 0 ? '已上架' : '已下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" align="center" fixed="right">
        <template #default="scope">
          <el-button size="small" type="primary" plain @click="openEditModal(scope.row)">修改</el-button>
          <el-button
              size="small"
              :type="scope.row.status === 0 ? 'danger' : 'success'"
              plain
              @click="toggleStatus(scope.row)"
          >
            {{ scope.row.status === 0 ? '下架' : '重新上架' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="dialogType === 'add' ? '➕ 上架新电影' : '✏️ 修改电影信息'" width="600px" top="5vh">
      <el-form :model="movieForm" label-width="100px">
        <el-form-item label="电影名称" required>
          <el-input v-model="movieForm.name" placeholder="请输入电影名称" />
        </el-form-item>
        <el-form-item label="上映时间" required>
          <el-date-picker v-model="movieForm.startDate" type="date" placeholder="选择上映日期" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="海报URL" required>
          <el-input v-model="movieForm.posterUrl" placeholder="请输入海报图片链接" />
        </el-form-item>
        <div style="display: flex; gap: 20px;">
          <el-form-item label="电影类型" required style="flex: 1;">
            <el-input v-model="movieForm.type" placeholder="如：动画/奇幻" />
          </el-form-item>
          <el-form-item label="片长(分钟)" required style="flex: 1;" label-width="90px">
            <el-input-number v-model="movieForm.length" :min="1" style="width: 100%;" />
          </el-form-item>
        </div>
        <div style="display: flex; gap: 20px;">
          <el-form-item label="国家/地区" style="flex: 1;">
            <el-input v-model="movieForm.country" placeholder="如：中国大陆" />
          </el-form-item>
          <el-form-item label="语言" style="flex: 1;" label-width="90px">
            <el-input v-model="movieForm.language" placeholder="如：国语" />
          </el-form-item>
        </div>
        <el-form-item label="导演">
          <el-input v-model="movieForm.director" placeholder="请输入导演姓名" />
        </el-form-item>
        <el-form-item label="主演">
          <el-input v-model="movieForm.starring" placeholder="请输入主演姓名" />
        </el-form-item>
        <el-form-item label="电影简介">
          <el-input v-model="movieForm.description" type="textarea" :rows="3" placeholder="请输入电影简介..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="submitMovie">确 认 提 交</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, computed, onMounted} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import axios from 'axios'

// 1. 初始化空数组，准备接收后端真实数据
const movieList = ref([])

// 🌟 核心方法：去后端拉取所有电影数据
const fetchMovies = async () => {
  try {
    const res = await axios.get('/api/movie/all')
    if (res.data.success) {
      movieList.value = res.data.content
    } else {
      ElMessage.error(res.data.message || '获取电影列表失败')
    }
  } catch (error) {
    console.error('获取数据失败:', error)
  }
}

// 页面加载时自动调用
onMounted(() => {
  fetchMovies()
})

// ===== 搜索过滤逻辑 (前端搜索，不发网络请求) =====
const searchKeyword = ref('')
const filteredMovies = computed(() => {
  if (!searchKeyword.value) return movieList.value
  return movieList.value.filter(m => m.name.includes(searchKeyword.value))
})

// ===== 弹窗与表单控制 =====
const dialogVisible = ref(false)
const dialogType = ref('add')
const movieForm = ref({})

const openAddModal = () => {
  dialogType.value = 'add'
  // 新增时默认状态为 0 (后端设定的上架状态)
  movieForm.value = {status: 0, length: 120}
  dialogVisible.value = true
}

const openEditModal = (row) => {
  dialogType.value = 'edit'
  movieForm.value = {...row}
  // 如果日期的时分秒多余，也可以截取一下防报错
  if (movieForm.value.startDate) {
    movieForm.value.startDate = movieForm.value.startDate.substring(0, 10)
  }
  dialogVisible.value = true
}

// 🌟 核心方法：提交新增或修改到数据库
const submitMovie = async () => {
  if (!movieForm.value.name || !movieForm.value.startDate || !movieForm.value.posterUrl) {
    ElMessage.warning('请填写必填项（名称、日期、海报）！')
    return
  }

  try {
    let res;
    if (dialogType.value === 'add') {
      res = await axios.post('/api/movie/add', movieForm.value)
    } else {
      res = await axios.post('/api/movie/update', movieForm.value)
    }

    if (res.data.success) {
      ElMessage.success('保存成功！')
      dialogVisible.value = false
      fetchMovies() // 刷新列表
    } else {
      ElMessage.error(res.data.message || '操作失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('网络请求失败')
  }
}

// 🌟 核心方法：控制上架/下架
const toggleStatus = (row) => {
  const isCurrentlyOnShelf = row.status === 0 // 0代表上架
  const actionText = isCurrentlyOnShelf ? '下架' : '重新上架'

  ElMessageBox.confirm(`确定要 ${actionText} 电影《${row.name}》吗？`, '状态变更', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
  }).then(async () => {
    try {
      let res;
      if (isCurrentlyOnShelf) {
        // 调用后端专门的批量下架接口
        res = await axios.post('/api/movie/off/batch', {movieIdList: [row.id]})
      } else {
        // 后端没有专门的单独上架接口，通过更新状态为 0 来实现上架
        const updatedMovie = {...row, status: 0}
        res = await axios.post('/api/movie/update', updatedMovie)
      }

      if (res.data.success) {
        ElMessage.success(`已成功 ${actionText}！`)
        fetchMovies() // 刷新列表看最新状态
      } else {
        // 如果该电影已有排片，下架时后端可能会报错拦截，这里能展示后端的拦截信息
        ElMessage.error(res.data.message || `${actionText}失败`)
      }
    } catch (error) {
      console.error(error)
      ElMessage.error('网络请求失败')
    }
  }).catch(() => {
  })
}
</script>

<style scoped>
.card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  padding: 25px;
}

.operation-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

:deep(.el-table th.el-table__cell) {
  background-color: #f5f7fa !important;
  color: #333;
}
</style>