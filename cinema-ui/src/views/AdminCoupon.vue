<template>
  <div>
    <div class="operation-bar" style="margin-bottom: 20px;">
      <el-button type="primary" size="large" @click="openSendDialog">🎫 向用户发放优惠券</el-button>
    </div>

    <!-- 优惠券列表表格 -->
    <el-table :data="couponList" stripe style="width: 100%;" empty-text="暂无可用优惠券"
      :header-cell-style="{ background: '#f5f7fa', color: '#333', fontWeight: 'bold' }">
      <el-table-column prop="id" label="ID" width="80" align="center" />
      <el-table-column prop="name" label="优惠券名称" min-width="160" />
      <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
      <el-table-column label="使用门槛" width="130" align="center">
        <template #default="{ row }">
          <span style="color: #e6a23c;">≥ ¥{{ row.targetAmount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="优惠金额" width="120" align="center">
        <template #default="{ row }">
          <span style="color: #f56c6c; font-weight: bold;">-¥{{ row.discountAmount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="开始时间" width="170" align="center">
        <template #default="{ row }">
          {{ formatTime(row.startTime) }}
        </template>
      </el-table-column>
      <el-table-column label="失效时间" width="170" align="center">
        <template #default="{ row }">
          {{ formatTime(row.endTime) }}
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="couponList.length === 0" description="暂无可用优惠券" />

    <!-- 发放优惠券对话框 -->
    <el-dialog v-model="sendDialogVisible" title="🎫 发放优惠券" width="500px">
      <el-form :model="sendForm" label-width="100px">
        <el-form-item label="选择优惠券" required>
          <el-select v-model="sendForm.couponId" placeholder="请选择优惠券" style="width: 100%;">
            <el-option v-for="coupon in couponList" :key="coupon.id" :label="coupon.name + ' (满' + coupon.targetAmount + '减' + coupon.discountAmount + ')'" :value="coupon.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="选择用户" required>
          <el-select v-model="sendForm.userIds" multiple placeholder="请选择要发放的用户（可多选）" style="width: 100%;" filterable>
            <el-option v-for="user in userList" :key="user.id" :label="user.username + ' (ID: ' + user.id + ')'" :value="user.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="sendDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleSendCoupon" :loading="sending">确认发放</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const couponList = ref([])
const userList = ref([])

// 获取所有有效优惠券
const fetchCoupons = async () => {
  try {
    const res = await axios.get('/api/coupon/get/all')
    if (res.data.success) {
      couponList.value = res.data.content
    } else {
      ElMessage.error(res.data.message || '获取优惠券列表失败')
    }
  } catch (error) {
    console.error('获取优惠券失败:', error)
  }
}

// 获取所有用户
const fetchUsers = async () => {
  try {
    const res = await axios.get('/api/get/all/user')
    if (res.data.success) {
      userList.value = res.data.content
    } else {
      ElMessage.error(res.data.message || '获取用户列表失败')
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
  }
}

onMounted(() => {
  fetchCoupons()
  fetchUsers()
})

// 发放优惠券相关
const sendDialogVisible = ref(false)
const sending = ref(false)
const sendForm = ref({
  couponId: null,
  userIds: []
})

const openSendDialog = () => {
  sendForm.value = {
    couponId: null,
    userIds: []
  }
  sendDialogVisible.value = true
}

const handleSendCoupon = async () => {
  if (!sendForm.value.couponId) {
    return ElMessage.warning('请选择要发放的优惠券！')
  }
  if (sendForm.value.userIds.length === 0) {
    return ElMessage.warning('请至少选择一个用户！')
  }

  sending.value = true
  try {
    // 后端接口: POST /coupon/send?couponId=X&userId=Y&userId=Z
    // 使用 axios params 序列化，需要手动拼接多个 userId
    const userIdParams = sendForm.value.userIds.map(id => `userId=${id}`).join('&')
    const url = `/api/coupon/send?couponId=${sendForm.value.couponId}&${userIdParams}`
    const res = await axios.post(url)
    if (res.data.success) {
      ElMessage.success(`优惠券已成功发放给 ${sendForm.value.userIds.length} 位用户！`)
      sendDialogVisible.value = false
    } else {
      ElMessage.error(res.data.message || '发放失败')
    }
  } catch (error) {
    console.error('发放优惠券失败:', error)
    ElMessage.error('网络请求失败')
  } finally {
    sending.value = false
  }
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  return timeStr.replace('T', ' ').substring(0, 16)
}
</script>

<style scoped>
.operation-bar {
  display: flex;
  align-items: center;
}

:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

:deep(.el-table th) {
  font-size: 14px;
}

:deep(.el-table td) {
  font-size: 13px;
}
</style>
