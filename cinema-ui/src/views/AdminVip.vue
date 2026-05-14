<template>
  <div class="card">
    <div class="operation-bar" style="margin-bottom: 20px;">
      <el-button type="primary" size="large" @click="openAddModal">
        ➕ 发布充值优惠
      </el-button>
    </div>

    <el-table :data="vipStrategies" stripe border style="width: 100%;">
      <el-table-column label="充值门槛 (满)">
        <template #default="scope">
          <span style="font-weight: bold;">￥ {{ scope.row.chargeLimit }}</span>
        </template>
      </el-table-column>
      <el-table-column label="赠送金额 (送)">
        <template #default="scope">
          <span style="color: #1caf9a; font-weight: bold;">+ ￥ {{ scope.row.giftAmount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center" fixed="right">
        <template #default="scope">
          <el-button size="small" type="primary" plain @click="openEditModal(scope.row)">修改</el-button>
          <el-button size="small" type="danger" plain @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
      <template #empty>
        <el-empty description="暂无充值优惠策略" />
      </template>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="dialogType === 'add' ? '➕ 发布充值优惠' : '✏️ 修改充值优惠'" width="400px" top="15vh">
      <el-form :model="strategyForm" label-width="120px">
        <el-form-item label="充值门槛 (满)" required>
          <el-input-number v-model="strategyForm.chargeLimit" :min="1" :step="50" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="赠送金额 (送)" required>
          <el-input-number v-model="strategyForm.giftAmount" :min="1" :step="10" style="width: 100%;" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitStrategy">确 认 提 交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

const vipStrategies = ref([])
const dialogVisible = ref(false)
const dialogType = ref('add')
const strategyForm = ref({})

// 🌟 核心：拉取所有优惠策略
const fetchStrategies = async () => {
  try {
    const res = await axios.get('/api/vip/get/all/strategy')
    if (res.data.success) {
      vipStrategies.value = res.data.content || []
    }
  } catch (error) {
    console.error('获取策略失败:', error)
  }
}

onMounted(() => {
  fetchStrategies()
})

const openAddModal = () => {
  dialogType.value = 'add'
  strategyForm.value = { chargeLimit: 100, giftAmount: 20 }
  dialogVisible.value = true
}

const openEditModal = (row) => {
  dialogType.value = 'edit'
  strategyForm.value = { ...row } // 回显数据
  dialogVisible.value = true
}

// 🌟 核心：提交新增或修改
const submitStrategy = async () => {
  if (!strategyForm.value.chargeLimit || !strategyForm.value.giftAmount) {
    return ElMessage.warning('请完整填写优惠金额！')
  }

  try {
    // 关键点：后端使用的是 @RequestParam，所以不能发 JSON，必须以 params 形式拼接在 URL 后面发过去！
    const url = dialogType.value === 'add' ? '/api/vip/add/strategy' : '/api/vip/update/strategy'

    // axios post 的第二个参数是 body(设为 null)，第三个参数是 config(把参数放 params 里)
    const res = await axios.post(url, null, {
      params: {
        id: strategyForm.value.id, // 新增时没 id 也没关系，修改时必须带上
        chargeLimit: strategyForm.value.chargeLimit,
        giftAmount: strategyForm.value.giftAmount
      }
    })

    if (res.data.success) {
      ElMessage.success('保存成功！')
      dialogVisible.value = false
      fetchStrategies() // 刷新列表
    } else {
      ElMessage.error(res.data.message || '保存失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('网络请求失败')
  }
}

// 🌟 核心：删除优惠策略
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除“满 ${row.chargeLimit} 送 ${row.giftAmount}”的优惠吗？`, '删除警告', {
    confirmButtonText: '确定删除', cancelButtonText: '取消', type: 'error'
  }).then(async () => {
    try {
      // 同样处理 @RequestParam
      const res = await axios.post('/api/vip/delete/strategy', null, {
        params: {id: row.id}
      })
      if (res.data.success) {
        ElMessage.success('删除成功！')
        fetchStrategies()
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
</script>

<style scoped>
.card {
  background: white;
  border-radius: 8px;
  padding: 25px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
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