<template>
  <div class="card">
    <!-- 顶部统计卡片 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon" style="background: #ecf5ff; color: #409eff;">
          <el-icon :size="28"><User /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ vipUsers.length }}</div>
          <div class="stat-label">VIP 会员总数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: #fdf6ec; color: #e6a23c;">
          <el-icon :size="28"><Coin /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">¥ {{ totalBalance.toFixed(2) }}</div>
          <div class="stat-label">会员卡总余额</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: #f0f9eb; color: #67c23a;">
          <el-icon :size="28"><TrendCharts /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">¥ {{ totalConsumption.toFixed(2) }}</div>
          <div class="stat-label">累计总消费</div>
        </div>
      </div>
    </div>

    <!-- Tab 切换 -->
    <el-tabs v-model="activeTab" type="border-card" style="margin-top: 20px;">
      <!-- ====== Tab 1: VIP 用户信息总览 ====== -->
      <el-tab-pane label="VIP 会员总览" name="overview">
        <!-- 筛选栏 -->
        <div class="filter-bar">
          <el-input
            v-model="filterMoney"
            placeholder="请输入最低消费金额"
            type="number"
            clearable
            style="width: 240px;"
            @keyup.enter="handleFilter"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" @click="handleFilter" :icon="Search">按消费筛选</el-button>
          <el-button @click="resetFilter" :icon="RefreshRight">重置</el-button>
        </div>

        <!-- VIP 用户表格 -->
        <el-table
          :data="filteredVipUsers"
          stripe
          border
          style="width: 100%;"
          v-loading="loading"
        >
          <el-table-column label="序号" type="index" width="60" align="center" />
          <el-table-column label="用户名" prop="name" min-width="120" show-overflow-tooltip>
            <template #default="scope">
              <div class="user-cell">
                <el-avatar :size="32" style="background: #409eff; margin-right: 8px;">
                  {{ (scope.row.name || 'V').charAt(0) }}
                </el-avatar>
                <span style="font-weight: 500;">{{ scope.row.name || '未知用户' }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="用户ID" prop="userId" width="90" align="center" />
          <el-table-column label="会员卡余额" min-width="120" align="right">
            <template #default="scope">
              <span class="money-cell">¥ {{ scope.row.balance.toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="累计消费" min-width="120" align="right">
            <template #default="scope">
              <span class="money-cell consumption">¥ {{ scope.row.total.toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="办卡时间" min-width="160" align="center">
            <template #default="scope">
              <span>{{ formatDate(scope.row.joinDate) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="会员等级" width="100" align="center">
            <template #default="scope">
              <el-tag :type="getVipLevel(scope.row.total).type" effect="dark" round>
                {{ getVipLevel(scope.row.total).label }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>

        <div class="empty-hint" v-if="!loading && filteredVipUsers.length === 0">
          <el-empty description="暂无符合条件的VIP会员" />
        </div>
      </el-tab-pane>

      <!-- ====== Tab 2: 充值优惠策略管理（原有功能） ====== -->
      <el-tab-pane label="充值优惠策略" name="strategy">
        <div class="operation-bar" style="margin-bottom: 20px;">
          <el-button type="primary" @click="openAddModal">
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
      </el-tab-pane>
    </el-tabs>

    <!-- 新增/修改策略弹窗 -->
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
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Coin, TrendCharts, Search, RefreshRight } from '@element-plus/icons-vue'
import axios from 'axios'

// ====== Tab 状态 ======
const activeTab = ref('overview')

// ====== VIP 用户总览相关 ======
const vipUsers = ref([])
const loading = ref(false)
const filterMoney = ref('')

// 计算统计
const totalBalance = computed(() => {
  return vipUsers.value.reduce((sum, u) => sum + (u.balance || 0), 0)
})
const totalConsumption = computed(() => {
  return vipUsers.value.reduce((sum, u) => sum + (u.total || 0), 0)
})

// 筛选后的 VIP 用户列表
const filteredVipUsers = computed(() => {
  if (!filterMoney.value || filterMoney.value === '') {
    return vipUsers.value
  }
  const minMoney = parseFloat(filterMoney.value)
  if (isNaN(minMoney)) return vipUsers.value
  return vipUsers.value.filter(u => u.total >= minMoney)
})

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '未知'
  const d = new Date(dateStr)
  if (isNaN(d.getTime())) return dateStr
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hour = String(d.getHours()).padStart(2, '0')
  const min = String(d.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${min}`
}

// 根据累计消费判断 VIP 等级
const getVipLevel = (total) => {
  if (total >= 2000) return { label: '钻石', type: 'danger' }
  if (total >= 1000) return { label: '黄金', type: 'warning' }
  if (total >= 500)  return { label: '白银', type: '' }
  return { label: '普通', type: 'info' }
}

// 获取所有 VIP 用户
const fetchVipUsers = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/vip/get/all')
    if (res.data.success) {
      vipUsers.value = res.data.content || []
    }
  } catch (error) {
    console.error('获取VIP用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 按消费金额筛选（调用后端接口）
const handleFilter = async () => {
  if (!filterMoney.value || filterMoney.value === '') {
    fetchVipUsers()
    return
  }
  const target = parseInt(filterMoney.value)
  if (isNaN(target) || target < 0) {
    return ElMessage.warning('请输入有效的金额')
  }
  loading.value = true
  try {
    const res = await axios.get('/api/vip/get/by/money', { params: { target } })
    if (res.data.success) {
      vipUsers.value = res.data.content || []
      ElMessage.success(`已筛选消费 ≥ ¥${target} 的VIP会员`)
    }
  } catch (error) {
    console.error('筛选失败:', error)
    ElMessage.error('筛选请求失败')
  } finally {
    loading.value = false
  }
}

// 重置筛选
const resetFilter = () => {
  filterMoney.value = ''
  fetchVipUsers()
}

// ====== 充值策略管理（保留原有逻辑） ======
const vipStrategies = ref([])
const dialogVisible = ref(false)
const dialogType = ref('add')
const strategyForm = ref({})

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

const openAddModal = () => {
  dialogType.value = 'add'
  strategyForm.value = { chargeLimit: 100, giftAmount: 20 }
  dialogVisible.value = true
}

const openEditModal = (row) => {
  dialogType.value = 'edit'
  strategyForm.value = { ...row }
  dialogVisible.value = true
}

const submitStrategy = async () => {
  if (!strategyForm.value.chargeLimit || !strategyForm.value.giftAmount) {
    return ElMessage.warning('请完整填写优惠金额！')
  }
  try {
    const url = dialogType.value === 'add' ? '/api/vip/add/strategy' : '/api/vip/update/strategy'
    const res = await axios.post(url, null, {
      params: {
        id: strategyForm.value.id,
        chargeLimit: strategyForm.value.chargeLimit,
        giftAmount: strategyForm.value.giftAmount
      }
    })
    if (res.data.success) {
      ElMessage.success('保存成功！')
      dialogVisible.value = false
      fetchStrategies()
    } else {
      ElMessage.error(res.data.message || '保存失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('网络请求失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除"满 ${row.chargeLimit} 送 ${row.giftAmount}"的优惠吗？`, '删除警告', {
    confirmButtonText: '确定删除', cancelButtonText: '取消', type: 'error'
  }).then(async () => {
    try {
      const res = await axios.post('/api/vip/delete/strategy', null, {
        params: { id: row.id }
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
  }).catch(() => {})
}

// ====== 初始化 ======
onMounted(() => {
  fetchVipUsers()
  fetchStrategies()
})
</script>

<style scoped>
.card {
  background: white;
  border-radius: 8px;
  padding: 25px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

/* 顶部统计卡片 */
.stats-row {
  display: flex;
  gap: 20px;
  margin-bottom: 10px;
}

.stat-card {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px 24px;
  border-radius: 10px;
  background: #fafafa;
  border: 1px solid #f0f0f0;
  transition: box-shadow 0.3s;
}

.stat-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 22px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

/* 筛选栏 */
.filter-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  padding: 14px 16px;
  background: #f9fafc;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

/* 表格样式 */
.user-cell {
  display: flex;
  align-items: center;
}

.money-cell {
  font-weight: 600;
  font-family: 'Courier New', monospace;
  color: #303133;
}

.money-cell.consumption {
  color: #e6a23c;
}

.operation-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.empty-hint {
  margin-top: 20px;
}

/* Deep 样式 */
:deep(.el-table th.el-table__cell) {
  background-color: #f5f7fa !important;
  color: #333;
}

:deep(.el-tabs__header) {
  margin-bottom: 0;
}

:deep(.el-tabs__content) {
  padding: 20px;
}
</style>
