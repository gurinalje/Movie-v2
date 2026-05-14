<template>
  <div class="card">
    <div class="header">
      <h2>🎟️ 电影退票策略管理</h2>
      <el-button type="primary" size="large" @click="openAddDialog">
        ➕ 新增退票策略
      </el-button>
    </div>

    <el-alert
        title="提示：系统会在用户申请退票时，自动匹配对应电影且在生效期内的策略。如果找不到策略，或者距离开场时间小于规定的时限，将拒绝退票。"
        type="info"
        show-icon
        :closable="false"
        style="margin-bottom: 20px;"
    />

    <el-table :data="policyList" stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="策略ID" width="80" />
      <el-table-column prop="movieName" label="适用电影" min-width="150">
        <template #default="scope">
          <strong>{{ scope.row.movieName }}</strong>
        </template>
      </el-table-column>
      <el-table-column label="手续费比例" width="120">
        <template #default="scope">
          <el-tag type="danger" effect="plain">{{ (scope.row.rate * 100).toFixed(0) }}%</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="退票时限" width="180">
        <template #default="scope">
          开场前 <strong style="color: #ed5565;">{{ scope.row.timeBefore }}</strong> 小时
        </template>
      </el-table-column>
      <el-table-column label="策略生效期" min-width="320">
        <template #default="scope">
          <span style="font-size: 13px; color: #666;">
            {{ formatDate(scope.row.startTime) }} 至 {{ formatDate(scope.row.endTime) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center" fixed="right">
        <template #default="scope">
          <el-button type="primary" size="small" plain @click="openEditDialog(scope.row)">编辑</el-button>
          <el-button type="danger" size="small" @click="deletePolicy(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="isEdit ? '📝 修改退票策略' : '➕ 新增退票策略'" v-model="dialogVisible" width="550px">
      <el-form :model="form" label-width="130px">
        <el-form-item label="电影 ID" required>
          <el-input-number v-model="form.movieId" :min="1" placeholder="请输入绑定的电影ID" style="width: 100%;"></el-input-number>
        </el-form-item>
        <el-form-item label="退款手续费比例" required>
          <el-input-number v-model="form.rate" :min="0" :max="1" :step="0.1" placeholder="例如 0.2 代表扣除 20%" style="width: 100%;"></el-input-number>
          <div style="font-size: 12px; color: #999; line-height: 1.5; margin-top: 5px;">
            注：0表示全额退款，0.2表示扣除20%的票价作为手续费。
          </div>
        </el-form-item>
        <el-form-item label="退票时限 (小时)" required>
          <el-input-number v-model="form.timeBefore" :min="0" placeholder="开场前多少小时允许退票" style="width: 100%;"></el-input-number>
          <div style="font-size: 12px; color: #999; line-height: 1.5; margin-top: 5px;">
            注：如果填 24，则距离电影开场不足 24 小时将不允许退票。
          </div>
        </el-form-item>
        <el-form-item label="策略生效时间" required>
          <el-date-picker v-model="form.startTime" type="datetime" placeholder="选择开始时间" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%;"></el-date-picker>
        </el-form-item>
        <el-form-item label="策略失效时间" required>
          <el-date-picker v-model="form.endTime" type="datetime" placeholder="选择结束时间" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%;"></el-date-picker>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="savePolicy">确 认 保 存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

const policyList = ref([])
const loading = ref(false)

const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({
  id: null,
  movieId: 1,
  rate: 0.2,
  timeBefore: 24,
  startTime: '',
  endTime: ''
})

const formatDate = (dateStr) => {
  if (!dateStr) return '永久'
  return dateStr.replace('T', ' ').substring(0, 19)
}

// 1. 获取所有退票策略
const fetchPolicies = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/get/all/refund')
    if (res.data.success) {
      policyList.value = res.data.content || []
    }
  } catch (error) {
    ElMessage.error('获取策略列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchPolicies()
})

const openAddDialog = () => {
  isEdit.value = false
  form.value = { id: null, movieId: 1, rate: 0.2, timeBefore: 24, startTime: '', endTime: '' }
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

// 2. 保存/更新策略
const savePolicy = async () => {
  if (!form.value.startTime || !form.value.endTime) {
    return ElMessage.warning('请完整选择生效和失效时间！')
  }

  const url = isEdit.value ? '/api/update/refund' : '/api/add/refund'
  try {
    const res = await axios.post(url, form.value)
    if (res.data.success) {
      ElMessage.success(isEdit.value ? '修改成功！' : '新增成功！')
      dialogVisible.value = false
      fetchPolicies()
    } else {
      ElMessage.error(res.data.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error('网络异常，保存失败')
  }
}

// 3. 删除策略
const deletePolicy = (id) => {
  ElMessageBox.confirm('确定要删除这条退票策略吗？', '警告', {
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await axios.post(`/api/delete/refund?target_id=${id}`)
      if (res.data.success) {
        ElMessage.success('删除成功！')
        fetchPolicies()
      } else {
        ElMessage.error(res.data.message || '删除失败')
      }
    } catch (e) {
      ElMessage.error('请求异常')
    }
  }).catch(() => {})
}
</script>

<style scoped>
.card {
  background: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  border-bottom: 2px solid #f0f2f5;
  padding-bottom: 15px;
}
.header h2 {
  margin: 0;
  color: #333;
}
</style>