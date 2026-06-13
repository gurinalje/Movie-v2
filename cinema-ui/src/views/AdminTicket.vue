<template>
  <div class="admin-ticket-container">
    <!-- 退票时限配置区域 -->
    <el-card shadow="never" class="refund-config-card">
      <template #header>
        <div class="card-header">
          <span>⏱️ 退票时限配置</span>
        </div>
      </template>

      <div class="refund-config-body">
        <div class="config-info">
          <span class="config-label">当前退票时限：</span>
          <span class="config-value" v-if="!isEditing">{{ refundLimitTime }} 小时</span>
          <div v-else class="config-edit">
            <el-input-number
              v-model="refundLimitTimeEdit"
              :min="1"
              :max="72"
              :step="1"
              controls-position="right"
              size="default"
              style="width: 160px;"
            />
            <span class="config-unit">小时（放映前）</span>
          </div>
        </div>
        <div class="config-actions">
          <template v-if="!isEditing">
            <el-button type="primary" size="default" @click="startEdit">修改时限</el-button>
          </template>
          <template v-else>
            <el-button type="success" size="default" @click="saveRefundConfig" :loading="configSaving">保存</el-button>
            <el-button size="default" @click="cancelEdit">取消</el-button>
          </template>
        </div>
      </div>

      <div class="config-tip">
        <el-icon><InfoFilled /></el-icon>
        <span>设置为 {{ refundLimitTime }} 小时，即用户需在电影放映前 {{ refundLimitTime }} 小时以上才可申请退票。</span>
      </div>
    </el-card>

    <!-- 订单管理区域 -->
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
import { InfoFilled } from '@element-plus/icons-vue'

// ==================== 订单列表 ====================
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

// ==================== 退票时限配置 ====================
const refundLimitTime = ref(0)
const refundLimitTimeEdit = ref(0)
const isEditing = ref(false)
const configSaving = ref(false)

/** 获取当前退票时限配置 */
const fetchRefundConfig = async () => {
  try {
    const res = await axios.get('/api/ticket/get/refundInfo')
    if (res.data.success && res.data.content) {
      refundLimitTime.value = res.data.content.limitTime || 0
    } else {
      ElMessage.error(res.data.message || '获取退票时限配置失败')
    }
  } catch (e) {
    ElMessage.error('获取退票时限配置失败')
  }
}

/** 进入编辑模式 */
const startEdit = () => {
  refundLimitTimeEdit.value = refundLimitTime.value
  isEditing.value = true
}

/** 取消编辑 */
const cancelEdit = () => {
  isEditing.value = false
}

/** 保存退票时限配置 */
const saveRefundConfig = async () => {
  if (refundLimitTimeEdit.value <= 0) {
    ElMessage.warning('退票时限必须大于 0 小时')
    return
  }
  configSaving.value = true
  try {
    const res = await axios.post('/api/ticket/update/refundInfo', null, {
      params: { limitTime: refundLimitTimeEdit.value }
    })
    if (res.data.success) {
      refundLimitTime.value = refundLimitTimeEdit.value
      isEditing.value = false
      ElMessage.success('退票时限更新成功！')
    } else {
      ElMessage.error(res.data.message || '保存失败')
    }
  } catch (e) {
    ElMessage.error('保存退票时限配置失败')
  } finally {
    configSaving.value = false
  }
}

// ==================== 页面初始化 ====================
onMounted(() => {
  fetchTickets()
  fetchRefundConfig()
})
</script>

<style scoped>
.admin-ticket-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.refund-config-card,
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
.refund-config-body {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.config-info {
  display: flex;
  align-items: center;
  gap: 8px;
}
.config-label {
  font-size: 14px;
  color: #606266;
}
.config-value {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
}
.config-edit {
  display: flex;
  align-items: center;
  gap: 10px;
}
.config-unit {
  font-size: 13px;
  color: #909399;
}
.config-actions {
  display: flex;
  gap: 8px;
}
.config-tip {
  margin-top: 12px;
  padding: 8px 12px;
  background-color: #f4f4f5;
  border-radius: 4px;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #909399;
}
</style>
