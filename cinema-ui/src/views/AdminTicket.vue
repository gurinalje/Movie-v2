<template>
  <div class="admin-ticket-container">
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <span>🎟️ 订单管理</span>
          <el-button type="primary" size="small" @click="fetchTickets" :loading="loading">刷新数据</el-button>
        </div>
      </template>

      <el-table :data="ticketList" stripe style="width: 100%" v-loading="loading" empty-text="暂无订单数据">
        <el-table-column prop="ticketId" label="票号ID" width="80" align="center" />
        <el-table-column prop="username" label="购票用户" width="120">
          <template #default="scope">
            <el-tag type="info" effect="plain">{{ scope.row.username }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="movieName" label="电影名称" min-width="150">
          <template #default="scope">
            <strong>{{ scope.row.movieName }}</strong>
          </template>
        </el-table-column>
        <el-table-column prop="hallName" label="影厅" width="120" />
        <el-table-column label="座位" width="100">
          <template #default="scope">
            {{ scope.row.row + 1 }}排{{ scope.row.column + 1 }}座
          </template>
        </el-table-column>
        <el-table-column label="放映时间" width="260">
          <template #default="scope">
            <span style="color: #666;">{{ scope.row.startTime }} - {{ scope.row.endTime }}</span>
          </template>
        </el-table-column>
        <el-table-column label="订单状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.state === '已完成' ? 'success' : (scope.row.state === '已失效' ? 'info' : 'warning')">
              {{ scope.row.state }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const ticketList = ref([])
const loading = ref(false)

const fetchTickets = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/ticket/get/all')
    if (res.data.success) {
      ticketList.value = res.data.content || []
    } else {
      ElMessage.error(res.data.message || '获取订单列表失败')
    }
  } catch (e) {
    ElMessage.error('网络请求异常')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchTickets()
})
</script>

<style scoped>
.admin-ticket-container {
  height: 100%;
}
.table-card {
  border-radius: 8px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
  font-size: 16px;
  color: #333;
}
</style>
