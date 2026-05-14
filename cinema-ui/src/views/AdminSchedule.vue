<template>
  <div class="card">
    <div class="operation-bar">
      <el-button type="primary" size="large" @click="openAddModal">
        ➕ 新增排片
      </el-button>
      <div class="filter-area">
        <span style="font-size: 14px; color: #666; margin-right: 10px;">放映日期:</span>
        <el-date-picker
            v-model="filterDate"
            type="date"
            placeholder="选择查看日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            clearable
            style="width: 180px;"
            @change="fetchSchedules"
        />
      </div>
    </div>

    <el-table :data="scheduleList" stripe border style="width: 100%; margin-top: 20px;">
      <el-table-column prop="movieName" label="放映电影" min-width="150" style="font-weight: bold;" />

      <el-table-column prop="hallName" label="放映影厅" width="160" align="center">
        <template #default="scope">
          <span :style="{ color: isHallMaintaining(scope.row.hallId) ? '#F56C6C' : 'inherit' }">
            {{ scope.row.hallName }}
            <span v-if="isHallMaintaining(scope.row.hallId)">(维护中)</span>
          </span>
        </template>
      </el-table-column>

      <el-table-column label="开始时间" width="180" align="center">
        <template #default="scope">{{ formatTime(scope.row.startTime) }}</template>
      </el-table-column>
      <el-table-column label="结束时间" width="180" align="center">
        <template #default="scope">{{ formatTime(scope.row.endTime) }}</template>
      </el-table-column>
      <el-table-column prop="fare" label="票价 (元)" width="100" align="center">
        <template #default="scope">
          <span style="color: #ed5565; font-weight: bold;">￥{{ scope.row.fare }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center" fixed="right">
        <template #default="scope">
          <el-button size="small" type="primary" plain @click="openEditModal(scope.row)">修改</el-button>
          <el-button size="small" type="danger" plain @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
      <template #empty>
        <el-empty description="该日期暂无排片安排" />
      </template>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="dialogType === 'add' ? '➕ 新增排片' : '✏️ 修改排片'" width="500px" top="10vh">
      <el-form :model="scheduleForm" label-width="100px">
        <el-form-item label="选择电影" required>
          <el-select v-model="scheduleForm.movieId" placeholder="请选择要放映的电影" style="width: 100%;">
            <el-option v-for="movie in movieList" :key="movie.id" :label="movie.name" :value="movie.id" />
          </el-select>
        </el-form-item>

        <el-form-item label="选择影厅" required>
          <el-select v-model="scheduleForm.hallId" placeholder="请选择放映影厅" style="width: 100%;">
            <el-option v-for="hall in activeHalls" :key="hall.id" :label="hall.name" :value="hall.id" />
          </el-select>
          <div v-if="activeHalls.length === 0" style="color: #F56C6C; font-size: 12px; line-height: 1.2; margin-top: 4px;">
            无可用的正常影厅，请前往“影院管理”恢复营业。
          </div>
        </el-form-item>

        <el-form-item label="开始时间" required>
          <el-date-picker v-model="scheduleForm.startTime" type="datetime" placeholder="选择开始放映时间" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="结束时间" required>
          <el-date-picker v-model="scheduleForm.endTime" type="datetime" placeholder="选择放映结束时间" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="场次票价" required>
          <el-input-number v-model="scheduleForm.fare" :min="1" :step="5" style="width: 100%;" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="submitSchedule">确 认 提 交</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

const movieList = ref([])
const hallList = ref([])
const scheduleList = ref([])

const today = new Date()
const pad = n => n < 10 ? '0' + n : n
const filterDate = ref(`${today.getFullYear()}-${pad(today.getMonth() + 1)}-${pad(today.getDate())}`)

// 🚀 读取本地缓存的黑名单
const getMaintenanceIds = () => JSON.parse(localStorage.getItem('hall_maintenance_ids') || '[]')

// 🚀 计算属性：过滤出没在维护的影厅
const activeHalls = computed(() => {
  const badIds = getMaintenanceIds()
  return hallList.value.filter(h => !badIds.includes(h.id))
})

// 🚀 判断某个影厅是不是坏的
const isHallMaintaining = (id) => {
  return getMaintenanceIds().includes(id)
}

const fetchBaseData = async () => {
  try {
    const [hallsRes, moviesRes] = await Promise.all([
      axios.get('/api/hall/all'),
      axios.get('/api/movie/all/exclude/off')
    ])
    if (hallsRes.data.success) hallList.value = hallsRes.data.content
    if (moviesRes.data.success) movieList.value = moviesRes.data.content

    fetchSchedules()
  } catch (error) {
    console.error('获取基础数据失败:', error)
  }
}

const fetchSchedules = async () => {
  if (!filterDate.value) {
    scheduleList.value = []
    return
  }

  const formattedDate = filterDate.value.replace(/-/g, '/')

  try {
    let allSchedules = []
    for (const hall of hallList.value) {
      const res = await axios.get(`/api/schedule/search?hallId=${hall.id}&startDate=${formattedDate}`)
      if (res.data.success && res.data.content && res.data.content.length > 0) {
        const todaySchedules = res.data.content[0].scheduleItemList
        if (todaySchedules) {
          allSchedules.push(...todaySchedules)
        }
      }
    }
    scheduleList.value = allSchedules
  } catch (error) {
    console.error('获取排片失败:', error)
  }
}

onMounted(() => {
  fetchBaseData()
})

const dialogVisible = ref(false)
const dialogType = ref('add')
const scheduleForm = ref({})

const openAddModal = () => {
  dialogType.value = 'add';
  scheduleForm.value = {fare: 40};
  dialogVisible.value = true
}

const openEditModal = (row) => {
  dialogType.value = 'edit';
  scheduleForm.value = {...row};
  dialogVisible.value = true
}

const submitSchedule = async () => {
  if (!scheduleForm.value.movieId || !scheduleForm.value.hallId || !scheduleForm.value.startTime || !scheduleForm.value.endTime || !scheduleForm.value.fare) {
    return ElMessage.warning('请完整填写排片信息！')
  }

  try {
    const payload = {
      id: scheduleForm.value.id,
      hallId: scheduleForm.value.hallId,
      movieId: scheduleForm.value.movieId,
      fare: scheduleForm.value.fare,
      startTime: new Date(scheduleForm.value.startTime).toISOString(),
      endTime: new Date(scheduleForm.value.endTime).toISOString()
    }

    const url = dialogType.value === 'add' ? '/api/schedule/add' : '/api/schedule/update'
    const res = await axios.post(url, payload)

    if (res.data.success) {
      ElMessage.success('排片保存成功！')
      dialogVisible.value = false
      fetchSchedules()
    } else {
      ElMessage.error(res.data.message || '保存失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('网络请求失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除《${row.movieName}》在 ${row.hallName} 的排片吗？`, '删除警告', {
    confirmButtonText: '确定删除', cancelButtonText: '取消', type: 'error'
  }).then(async () => {
    try {
      const res = await axios.delete('/api/schedule/delete/batch', {
        data: {scheduleIdList: [row.id]}
      })
      if (res.data.success) {
        ElMessage.success('删除成功！')
        fetchSchedules()
      } else {
        ElMessage.error(res.data.message || '删除失败')
      }
    } catch (error) {
      console.error(error)
      ElMessage.error('网络请求失败')
    }
  }).catch(() => {
  })
}

const formatTime = (timeStr) => {
  if (!timeStr) return '';
  const d = new Date(timeStr);
  const pad = (n) => (n < 10 ? '0' + n : n);
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
}
</script>

<style scoped>
/* 完全保留原版样式 */
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
  margin-bottom: 20px;
}

.filter-area {
  display: flex;
  align-items: center;
}

:deep(.el-table th.el-table__cell) {
  background-color: #f5f7fa !important;
  color: #333;
}
</style>