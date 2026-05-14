<template>
  <div class="page-container">
    <NavBar />
    <main class="content-container card">
      <h2 style="margin-bottom: 20px; color: #333;">🎟️ 我的电影票</h2>

      <el-table :data="ticketList" stripe style="width: 100%" empty-text="您还没有买过电影票哦">
        <el-table-column prop="movieName" label="电影名称" min-width="150">
          <template #default="scope">
            <strong>{{ scope.row.movieName }}</strong>
          </template>
        </el-table-column>
        <el-table-column prop="hallName" label="影厅" width="120" />
        <el-table-column label="座位" width="120">
          <template #default="scope">
            <el-tag effect="plain">{{ scope.row.row + 1 }}排{{ scope.row.column + 1 }}座</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="放映时间" width="180">
          <template #default="scope">
            <span style="color: #666;">{{ scope.row.startTime }}</span>
          </template>
        </el-table-column>
        <el-table-column label="订单状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.state === '已完成' ? 'success' : (scope.row.state === '已失效' ? 'info' : 'warning')">
              {{ scope.row.state }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="180" align="center">
          <template #default="scope">
            <div v-if="scope.row.state === '未支付'">
              <el-button type="success" size="small" @click="openPayDialog(scope.row)">付款</el-button>
              <el-button type="danger" size="small" @click="cancelTicket(scope.row)">取消</el-button>
            </div>

            <div v-else-if="scope.row.state === '已完成'">
              <el-button type="warning" size="small" plain @click="refundTicket(scope.row)">申请退票</el-button>
            </div>

            <span v-else style="color:#ccc">--</span>
          </template>
        </el-table-column>
      </el-table>

      <el-dialog v-model="payDialogVisible" title="选择支付方式" width="400px" center>
        <div style="display: flex; flex-direction: column; gap: 20px; align-items: center;">
          <el-button type="success" size="large" @click="triggerBankCardPay" style="width: 200px;">💳 银行卡支付</el-button>
          <el-button color="#f7d393" size="large" @click="executePay(true)" style="width: 200px; color: #333;">👑 VIP 余额支付</el-button>
        </div>
      </el-dialog>

      <el-dialog v-model="bankCardDialogVisible" title="银行卡支付" width="400px">
        <el-form :model="bankCardForm" label-width="80px">
          <el-form-item label="银行卡号">
            <el-input v-model="bankCardForm.account" placeholder="请输入您的银行卡号" clearable></el-input>
          </el-form-item>
          <el-form-item label="支付密码">
            <el-input v-model="bankCardForm.password" type="password" placeholder="请输入6位支付密码" show-password></el-input>
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="bankCardDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="confirmBankCardPay">确认付款</el-button>
          </span>
        </template>
      </el-dialog>

      <el-dialog v-model="bankRefundDialogVisible" title="受理银行卡退款申请" width="550px">
        <div class="simulate-review-panel">
          <p>您正在为《{{ currentRefundTicket.movieName }}》申请银行卡退款。系统已为您自动分配管理员处理。</p>
          <div class="reviewer-info card-shadow">
            <el-avatar
                :size="60"
                :src="coloredAdminAvatar"
                class="reviewer-avatar"
            />
            <div class="text-info">
              <h4>系统审核员: [ {{ currentReviewerName }} ]</h4>
              <p>工号: Admin-{{ (Math.random()*1000).toFixed(0) }} | 正在审核...</p>
            </div>
            <el-tag type="warning" effect="dark" size="small">处理中</el-tag>
          </div>
          <el-result icon="info" title="银行卡原路退回" :sub-title="'预计退还 ￥' + estimatedRefundAmount.toFixed(2)">
            <template #extra>
              <div style="display:flex; flex-direction:column; align-items:center; gap:10px;">
                <p style="font-size:12px; color:#ed5565;">注：手续费将根据系统策略扣除，金额以实际到账为准。</p>
                <el-button type="primary" :loading="refunding" @click="executeBankRefund">确认申请并立即退款</el-button>
              </div>
            </template>
          </el-result>
        </div>
      </el-dialog>

    </main>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import NavBar from '../components/NavBar.vue'

// 🚀 核心修复：定义审核员的彩色默认头像
const coloredAdminAvatar = 'https://img.js.design/assets/static/ca6269f886d38e0797871b12b5443e49.png'

const ticketList = ref([])
const currentUser = JSON.parse(localStorage.getItem('user') || '{}')

const payDialogVisible = ref(false)
const bankCardDialogVisible = ref(false)
const currentPayTicketId = ref(null)
const bankCardForm = reactive({ account: '', password: '' })

// 退款相关状态
const bankRefundDialogVisible = ref(false)
const currentRefundTicket = ref({})
const estimatedRefundAmount = ref(0)
const refunding = ref(false)
const currentReviewerName = ref('陈经理')

const fetchTickets = async () => {
  if (!currentUser.id) return
  try {
    const res = await axios.get(`/api/ticket/get/${currentUser.id}`)
    if (res.data.success) ticketList.value = res.data.content || []
  } catch (e) { ElMessage.error('获取购票记录失败') }
}

const refundTicket = async (row) => {
  try {
    const checkRes = await axios.post(`/api/refund?ticketId=${row.ticketId}`)
    if (checkRes.data.success) {
      if (checkRes.data.content === -1) {
        ElMessage.error('该订单不可退票或已超时')
      } else {
        // 判断原支付方式 (可以通过description里是否有'VIP'判断)
        if (row.movieName.includes('VIP付款') || (row.description && row.description.includes('VIP'))) {
          ElMessageBox.confirm(`确定退掉这半张票吗？预计退还 ￥${checkRes.data.content.toFixed(2)} (原路退回VIP卡)`, 'VIP卡退款提示', {
            confirmButtonText: '确定退票', cancelButtonText: '取消', type: 'warning'
          }).then(async () => {
            await executeActualRefund(row.ticketId, checkRes.data.content, true)
          }).catch(() => {})
        } else {
          // 银行卡支付，拉起模拟面板
          currentRefundTicket.value = row
          estimatedRefundAmount.value = checkRes.data.content
          bankRefundDialogVisible.value = true
        }
      }
    } else { ElMessage.error(checkRes.data.message || '查询退款信息失败') }
  } catch (e) { ElMessage.error('退票请求异常') }
}

const executeBankRefund = async () => {
  refunding.value = true
  await executeActualRefund(currentRefundTicket.value.ticketId, estimatedRefundAmount.value, false)
  refunding.value = false
}

const executeActualRefund = async (ticketId, amount, isVip) => {
  try {
    const res = await axios.post(`/api/refund?ticketId=${ticketId}`)
    if (res.data.success) {
      if(isVip) {
        ElMessage.success(`退款成功！已退还：￥${amount.toFixed(2)} (直接退回余额)`)
      } else {
        ElMessage.success(`🎉 退款申请成功！管理员已受理，￥${amount.toFixed(2)} 已原路退回您的银行卡 (模拟流程完成)`)
      }
      bankRefundDialogVisible.value = false
      fetchTickets()
    } else { ElMessage.error(res.data.message || '退款操作失败') }
  } catch (e) { ElMessage.error('后端退款报错') }
}

// 支付相关代码（保持不变）
const openPayDialog = (row) => {
  currentPayTicketId.value = row.ticketId
  payDialogVisible.value = true
}
const triggerBankCardPay = () => {
  payDialogVisible.value = false
  bankCardDialogVisible.value = true
}
const confirmBankCardPay = async () => {
  if (!bankCardForm.account || !bankCardForm.password) return ElMessage.warning('请完整填写！')
  bankCardDialogVisible.value = false
  await executePay(false)
}
const executePay = async (isVip) => {
  const url = isVip ? '/api/ticket/vip/buy' : '/api/ticket/buy'
  try {
    const searchParams = new URLSearchParams()
    searchParams.append('ticketId', currentPayTicketId.value)
    searchParams.append('couponId', 0)
    if(currentUser.id) searchParams.append('userId', currentUser.id)
    const res = await axios.post(`${url}?${searchParams.toString()}`)
    if (res.data.success) {
      ElMessage.success('🎉 支付成功！')
      payDialogVisible.value = false
      fetchTickets()
    } else { ElMessage.error(res.data.message || '支付失败') }
  } catch (e) { ElMessage.error('支付网络异常') }
}

const cancelTicket = async (row) => {
  ElMessageBox.confirm('确定取消这张未支付票吗？', '取消订单', {
    confirmButtonText: '确定取消', cancelButtonText: '再想想', type: 'warning'
  }).then(async () => {
    try {
      const searchParams = new URLSearchParams()
      searchParams.append('ticketId', row.ticketId)
      const res = await axios.post(`/api/ticket/cancel?${searchParams.toString()}`)
      if (res.data.success) { ElMessage.success('已取消'), fetchTickets() }
      else ElMessage.error(res.data.message || '取消失败')
    } catch (e) { ElMessage.error('取消抛出异常') }
  }).catch(() => {})
}

onMounted(() => fetchTickets())
</script>

<style scoped>
.page-container { min-height: 100vh; background-color: #f5f7fa; padding-bottom: 40px; }
.content-container { max-width: 1000px; margin: 30px auto; }
.card { background: #fff; border-radius: 8px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05); padding: 30px; }
/* 🚀 模拟审核面板样式优化 */
.reviewer-info { display: flex; align-items: center; gap: 15px; margin-top: 15px; margin-bottom: 20px; border-radius: 10px; padding: 15px 20px; background-color: #fcfdfd; }
.reviewer-avatar { border: 3px solid #f0f9ff; }
.text-info h4 { margin: 0; color: #333; }
.text-info p { margin: 5px 0 0 0; color: #999; font-size: 13px; }
.card-shadow { box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05); }
</style>