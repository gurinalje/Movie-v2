<template>
  <div class="page-container">
    <NavBar />
    <main class="content-container card">
      <el-steps :active="activeStep" finish-status="success" align-center style="margin-bottom: 40px;">
        <el-step title="1. 选择座位" />
        <el-step title="2. 确认订单并支付" />
        <el-step title="3. 购票完成" />
      </el-steps>

      <div v-if="activeStep === 0" class="step-content seat-step">
        <div class="seat-map-container">
          <div class="screen-curve">银幕中央</div>
          <div class="seat-grid">
            <div class="seat-row" v-for="(row, rowIndex) in seats" :key="rowIndex">
              <span class="row-num">{{ rowIndex + 1 }}</span>
              <div
                  v-for="(seat, colIndex) in row"
                  :key="colIndex"
                  class="seat"
                  :class="{ 'sold': seat === 1, 'selected': seat === 2, 'available': seat === 0 }"
                  @click="toggleSeat(rowIndex, colIndex)"
              ></div>
            </div>
          </div>
          <div class="seat-legend">
            <div class="legend-item"><div class="seat available"></div> 可选</div>
            <div class="legend-item"><div class="seat sold"></div> 已售</div>
            <div class="legend-item"><div class="seat selected"></div> 已选</div>
          </div>
        </div>

        <div class="movie-side-panel">
          <div class="movie-info-header">
            <div class="side-text">
              <h3>{{ scheduleInfo.movieName || '加载中...' }}</h3>
              <p>影厅：{{ scheduleInfo.hallName }}</p>
              <p>票价：<span style="color: #ed5565; font-weight: bold;">￥{{ scheduleInfo.fare }}</span> / 张</p>
            </div>
          </div>
          <el-divider />
          <div class="schedule-details">
            <p><strong>已选座位：</strong></p>
            <div style="display: flex; flex-wrap: wrap; gap: 5px; margin-bottom: 15px;">
              <el-tag v-for="seat in selectedSeats" :key="seat.rowIndex + '-' + seat.columnIndex" type="success" effect="dark">
                {{ seat.rowIndex + 1 }}排{{ seat.columnIndex + 1 }}座
              </el-tag>
              <span v-if="selectedSeats.length === 0" style="color: #999; font-size: 13px;">请点击左侧选座</span>
            </div>
          </div>
          <div class="total-price-display">
            总计：<span style="color: #ed5565; font-weight: bold; font-size: 24px;">￥{{ totalFare.toFixed(2) }}</span>
          </div>
          <el-button type="primary" size="large" class="submit-btn" :disabled="selectedSeats.length === 0" @click="confirmSeats">
            🔒 确认选座 (锁定座位)
          </el-button>
        </div>
      </div>

      <div v-if="activeStep === 1" class="step-content pay-step" style="text-align: center; padding: 40px;">
        <h2>请选择支付方式</h2>
        <p style="color: #666; margin-bottom: 20px;">锁定 {{ selectedSeats.length }} 个座位，请尽快付款。</p>

        <!-- 费用明细 -->
        <div class="price-summary">
          <div class="price-row">
            <span>原价（{{ selectedSeats.length }} 张 × ￥{{ (scheduleInfo.fare || 0).toFixed(2) }}）</span>
            <span>￥{{ totalFare.toFixed(2) }}</span>
          </div>
          <div v-if="selectedCoupon" class="price-row discount">
            <span>优惠券抵扣（{{ selectedCoupon.name }}）</span>
            <span style="color: #1caf9a;">-￥{{ selectedCoupon.discountAmount.toFixed(2) }}</span>
          </div>
          <div class="price-row total">
            <span>应付金额</span>
            <span style="color: #ed5565; font-size: 24px; font-weight: bold;">￥{{ finalPrice.toFixed(2) }}</span>
          </div>
        </div>

        <!-- 优惠券选择 -->
        <div v-if="availableCoupons.length > 0" class="coupon-section">
          <h3 style="margin: 20px 0 10px; text-align: left;">🎫 使用优惠券</h3>
          <el-radio-group v-model="selectedCouponId" class="coupon-radio-group">
            <div v-for="coupon in availableCoupons" :key="coupon.id" class="coupon-card" :class="{ 'coupon-active': selectedCouponId === coupon.id, 'coupon-disabled': totalFare < coupon.targetAmount }">
              <el-radio :value="coupon.id" :disabled="totalFare < coupon.targetAmount">
                <div class="coupon-info">
                  <div class="coupon-name">{{ coupon.name }}</div>
                  <div class="coupon-desc">{{ coupon.description }}</div>
                  <div class="coupon-condition">
                    满 ￥{{ coupon.targetAmount.toFixed(0) }} 减 ￥{{ coupon.discountAmount.toFixed(0) }}
                    <el-tag v-if="totalFare < coupon.targetAmount" type="info" size="small" style="margin-left: 8px;">未满门槛</el-tag>
                  </div>
                </div>
              </el-radio>
            </div>
          </el-radio-group>
        </div>
        <div v-else-if="couponsLoaded" style="color: #999; font-size: 14px; margin: 15px 0;">暂无可用优惠券</div>

        <!-- 支付按钮 -->
        <div style="display: flex; justify-content: center; gap: 30px; margin-top: 30px;">
          <el-button type="success" size="large" @click="bankCardDialogVisible = true">💳 银行卡支付</el-button>
          <el-button color="#f7d393" size="large" @click="handlePay(true)" style="color: #333;">👑 VIP 余额支付</el-button>
        </div>
        <el-button type="text" style="margin-top: 20px; color: #999;" @click="activeStep = 0">返回重选</el-button>
      </div>

      <div v-if="activeStep === 2" class="step-content success-step" style="text-align: center; padding: 60px;">
        <el-avatar
            :size="100"
            :src="coloredDefaultAvatar"
            class="success-avatar card-shadow"
        />
        <h2 style="margin-top: 25px; color: #1caf9a;">🎉 购票成功！</h2>
        <p style="color: #666; margin-bottom: 30px;">{{ username }}，您的电影票已出票，祝您观影愉快！</p>
        <div>
          <el-button type="primary" @click="router.push('/userBuy')">查看我的电影票</el-button>
          <el-button @click="router.push('/home')">返回首页</el-button>
        </div>
      </div>

      <el-dialog v-model="bankCardDialogVisible" title="银行卡支付" width="400px">
        <el-form :model="bankCardForm" label-width="80px">
          <el-form-item label="银行卡号"><el-input v-model="bankCardForm.account" placeholder="卡号" clearable></el-input></el-form-item>
          <el-form-item label="密码"><el-input v-model="bankCardForm.password" type="password" placeholder="6位密码" show-password></el-input></el-form-item>
        </el-form>
        <template #footer><span class="dialog-footer"><el-button @click="bankCardDialogVisible = false">取消</el-button><el-button type="primary" @click="confirmBankCardPay">确认付款</el-button></span></template>
      </el-dialog>

    </main>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import NavBar from '../components/NavBar.vue'

// 🚀 核心修复：定义彩色默认头像地址
const coloredDefaultAvatar = 'https://img.js.design/assets/static/f56291a9b1c55bdf994f7d43232149b1.png'

const route = useRoute()
const router = useRouter()
const scheduleId = route.query.scheduleId

const activeStep = ref(0)
const scheduleInfo = ref({})
const seats = ref([])
const lockedTicketIds = ref([])

const bankCardDialogVisible = ref(false)
const bankCardForm = reactive({ account: '', password: '' })

const currentUser = JSON.parse(localStorage.getItem('user') || '{}')
const username = currentUser.username || '用户'

const coupons = ref([])
const couponsLoaded = ref(false)
const selectedCouponId = ref(0)
const selectedCoupon = computed(() => coupons.value.find(c => c.id === selectedCouponId.value) || null)

// 可用优惠券：未过期 + 满足门槛条件（门槛 <= 总价的显示出来，未达门槛的灰显）
const availableCoupons = computed(() => coupons.value.filter(c => {
  return totalFare.value >= c.targetAmount
}))

const finalPrice = computed(() => {
  const discount = selectedCoupon.value ? selectedCoupon.value.discountAmount : 0
  return Math.max(0, totalFare.value - discount)
})

const fetchCoupons = async () => {
  if (!currentUser.id) return
  try {
    const res = await axios.get(`/api/coupon/${currentUser.id}/get`)
    if (res.data.success) {
      coupons.value = res.data.content || []
    }
  } catch (e) { /* 获取优惠券失败，静默处理 */ }
  couponsLoaded.value = true
}

const fetchSeatData = async () => {
  try {
    const res = await axios.get(`/api/ticket/get/occupiedSeats?scheduleId=${scheduleId}`)
    if (res.data.success) {
      scheduleInfo.value = res.data.content.scheduleItem || {}
      seats.value = res.data.content.seats || []
    }
  } catch (e) { ElMessage.error('加载座位失败') }
}

const toggleSeat = (rowIndex, colIndex) => {
  if (seats.value[rowIndex][colIndex] === 1) return
  if (seats.value[rowIndex][colIndex] === 0) seats.value[rowIndex][colIndex] = 2
  else seats.value[rowIndex][colIndex] = 0
}

const selectedSeats = computed(() => {
  const res = []
  for (let r = 0; r < (seats.value?.length || 0); r++) {
    for (let c = 0; c < (seats.value[r]?.length || 0); c++) {
      if (seats.value[r][c] === 2) res.push({ rowIndex: r, columnIndex: c })
    }
  }
  return res
})
const totalFare = computed(() => selectedSeats.value.length * (scheduleInfo.value.fare || 0))

const confirmSeats = async () => {
  if (!currentUser.id) return ElMessage.error('请先登录')
  const payload = { userId: currentUser.id, scheduleId: parseInt(scheduleId), seats: selectedSeats.value }
  try {
    const res = await axios.post('/api/ticket/lockSeat', payload)
    if (res.data.success) {
      const content = res.data.content
      lockedTicketIds.value = content.ticketVOList ? content.ticketVOList.map(t => t.id) : []
      selectedCouponId.value = 0  // 重置优惠券选择
      activeStep.value = 1
    } else { ElMessage.error(res.data.message || '选座失败') }
  } catch (e) { ElMessage.error('请求异常') }
}

const confirmBankCardPay = async () => {
  if (!bankCardForm.account || !bankCardForm.password) return ElMessage.warning('请输入！')
  bankCardDialogVisible.value = false
  await handlePay(false)
}
const handlePay = async (isVip) => {
  const url = isVip ? '/api/ticket/vip/buy' : '/api/ticket/buy'
  try {
    const searchParams = new URLSearchParams()
    lockedTicketIds.value.forEach(id => searchParams.append('ticketId', id))
    searchParams.append('couponId', selectedCouponId.value)
    if(currentUser.id) searchParams.append('userId', currentUser.id)
    const res = await axios.post(`${url}?${searchParams.toString()}`)
    if (res.data.success) activeStep.value = 2
    else ElMessage.error(res.data.message || '支付失败')
  } catch (e) { ElMessage.error('请求异常') }
}
onMounted(() => {
  fetchSeatData()
  fetchCoupons()
})
</script>

<style scoped>
.page-container { min-height: 100vh; background-color: #f5f7fa; padding-bottom: 40px; }
.content-container { max-width: 1000px; margin: 30px auto; }
.card { background: #fff; border-radius: 8px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05); padding: 30px; }
.seat-step { display: flex; gap: 40px; }
.seat-map-container { flex: 1; display: flex; flex-direction: column; align-items: center; }
.screen-curve { width: 80%; height: 30px; background: #e0e0e0; border-radius: 50% / 100% 100% 0 0; text-align: center; line-height: 30px; color: #666; font-size: 14px; margin-bottom: 40px; }
.seat-grid { display: flex; flex-direction: column; gap: 10px; }
.seat-row { display: flex; gap: 10px; }
.row-num { width: 20px; text-align: right; color: #999; margin-right: 10px; }
.seat { width: 30px; height: 30px; border-radius: 6px 6px 2px 2px; cursor: pointer; transition: 0.2s; }
.seat.available { background-color: #e0e0e0; }
.seat.available:hover { background-color: #1caf9a; }
.seat.selected { background-color: #1caf9a; box-shadow: 0 2px 5px rgba(28, 175, 154, 0.4); }
.seat.sold { background-color: #ed5565; cursor: not-allowed; }
.seat-legend { display: flex; gap: 20px; margin-top: 40px; font-size: 14px; color: #666; }
.legend-item { display: flex; align-items: center; gap: 8px; }
.movie-side-panel { width: 320px; background: #fafafa; border: 1px solid #eee; border-radius: 8px; padding: 25px; }
.movie-info-header { display: flex; gap: 15px; }
.side-text h3 { margin: 0 0 10px 0; }
.total-price-display { margin-top: 20px; text-align: right; margin-bottom: 20px; }
.submit-btn { width: 100%; border-radius: 20px; }
/* 🚀 彩色头像样式 */
.success-avatar { border: 4px solid #f0f9ff; box-shadow: 0 4px 12px rgba(28, 175, 154, 0.2); }
.card-shadow { box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05); }
/* 费用明细 */
.price-summary { background: #fafafa; border: 1px solid #eee; border-radius: 8px; padding: 15px 20px; margin-bottom: 20px; text-align: left; }
.price-row { display: flex; justify-content: space-between; align-items: center; padding: 6px 0; font-size: 14px; color: #666; }
.price-row.discount { color: #1caf9a; }
.price-row.total { border-top: 1px dashed #ddd; margin-top: 6px; padding-top: 10px; font-weight: bold; }
/* 优惠券选择 */
.coupon-section { text-align: left; }
.coupon-radio-group { width: 100%; display: flex; flex-direction: column; gap: 10px; }
.coupon-card { border: 1px solid #e4e7ed; border-radius: 8px; padding: 12px 16px; transition: all 0.2s; cursor: pointer; }
.coupon-card:hover { border-color: #1caf9a; box-shadow: 0 2px 8px rgba(28, 175, 154, 0.1); }
.coupon-card.coupon-active { border-color: #1caf9a; background: #f0faf8; }
.coupon-card.coupon-disabled { opacity: 0.5; cursor: not-allowed; }
.coupon-card.coupon-disabled:hover { border-color: #e4e7ed; box-shadow: none; }
.coupon-info { line-height: 1.6; }
.coupon-name { font-weight: bold; font-size: 15px; color: #333; }
.coupon-desc { font-size: 13px; color: #999; margin-top: 2px; }
.coupon-condition { font-size: 13px; color: #ed5565; margin-top: 4px; }
</style>