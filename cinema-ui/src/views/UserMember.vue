<template>
  <div class="page-container">
    <NavBar />
    <main class="content-container">
      <div class="vip-card-section">
        <div class="vip-card non-member" v-if="!isMember">
          <div class="card-header">
            <span class="card-title">NJU-Se 影城会员卡</span>
            <el-tag type="info" effect="dark">非会员</el-tag>
          </div>
          <div class="card-body">
            <h2 class="price-text">￥{{ vipConfig.price }} / 张</h2>
            <p class="desc">{{ vipConfig.description }}</p>
          </div>
          <div class="card-footer">
            <el-button type="primary" size="large" class="action-btn" @click="buyModalVisible = true">立 即 购 买</el-button>
          </div>
        </div>

        <div class="vip-card is-member" v-else>
          <div class="card-header">
            <span class="card-title" style="color: #f7d393;">👑 NJU-Se 尊享 VIP</span>
            <el-tag color="#f7d393" style="color: #333; border: none;">已开通</el-tag>
          </div>
          <div class="card-body vip-info">
            <p><span>会员卡号：</span> <strong>{{ memberInfo.id }}</strong></p>
            <p><span>入会日期：</span> <strong>{{ memberInfo.joinDate }}</strong></p>
            <p v-if="strategies.length > 0">
              <span>充值优惠：</span>
              <el-select v-model="selectedStrategy" size="small" style="width: 140px; margin-left: 10px;">
                <el-option
                    v-for="s in strategies"
                    :key="s.id"
                    :label="`满 ${s.target} 送 ${s.gift}`"
                    :value="s.id"
                />
              </el-select>
            </p>
            <div class="balance-display">
              <span>当前余额：</span>
              <span class="balance-num">￥{{ memberInfo.balance.toFixed(2) }}</span>
            </div>
          </div>
          <div class="card-footer">
            <el-button color="#f7d393" style="color: #333; font-weight: bold;" size="large" class="action-btn" @click="chargeModalVisible = true">立 即 充 值</el-button>
          </div>
        </div>
      </div>

      <div class="coupon-section card" style="margin-bottom: 30px;">
        <h3 class="section-title">🎁 影城热门活动</h3>
        <el-empty v-if="activities.length === 0" description="暂无促销活动" />
        <div class="coupon-list" v-else>
          <div class="coupon-item" v-for="activity in activities" :key="activity.id" style="height: auto; min-height: 110px;">
            <div class="coupon-left" style="background: linear-gradient(135deg, #1caf9a, #158c7a);">
              <h2 v-if="activity.coupon">￥<span>{{ activity.coupon.discountAmount }}</span></h2>
              <p v-if="activity.coupon">满 {{ activity.coupon.targetAmount }} 可用</p>
              <h2 v-else>赠券</h2>
            </div>
            <div class="coupon-right" style="padding: 15px 20px;">
              <h4>{{ activity.name }}</h4>
              <p class="coupon-desc" style="margin-bottom: 10px;">{{ activity.description }}</p>

              <div style="display: flex; flex-wrap: wrap; gap: 6px; align-items: center;">
                <template v-if="activity.movieList && activity.movieList.length > 0">
                  <span style="font-size: 12px; color: #999;">指定电影：</span>
                  <el-tag
                      v-for="movie in activity.movieList"
                      :key="movie.id"
                      size="small"
                      type="info"
                      effect="plain"
                  >
                    《{{ movie.name }}》
                  </el-tag>
                </template>
                <el-tag v-else size="small" type="warning" effect="light">全场影片通用</el-tag>
              </div>

            </div>
          </div>
        </div>
      </div>

      <div class="coupon-section card">
        <h3 class="section-title">🎟️ 我的优惠券卡包</h3>
        <el-empty v-if="coupons.length === 0" description="卡包里空空如也~" />
        <div class="coupon-list" v-else>
          <div class="coupon-item" v-for="coupon in coupons" :key="coupon.id">
            <div class="coupon-left">
              <h2>￥<span>{{ coupon.discountAmount }}</span></h2>
              <p>满 {{ coupon.targetAmount }} 元可用</p>
            </div>
            <div class="coupon-right">
              <h4>{{ coupon.name }}</h4>
              <p class="coupon-desc">{{ coupon.description }}</p>
              <el-tag size="small" type="success" effect="plain" style="margin-top: 10px;">
                有效期至: {{ coupon.endTime ? coupon.endTime.substring(0,10) : '永久有效' }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>
    </main>

    <el-dialog v-model="buyModalVisible" title="💎 购买会员卡" width="400px" center>
      <el-form label-position="top">
        <el-form-item label="银行卡号" required><el-input v-model="buyForm.cardNum" placeholder="请输入付款银行卡号" clearable /></el-form-item>
        <el-form-item label="支付密码" required><el-input v-model="buyForm.password" type="password" placeholder="请输入密码" show-password /></el-form-item>
        <div class="pay-amount-display">需支付金额：<span class="price-text">￥{{ vipConfig.price }}</span></div>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="buyModalVisible = false">取 消</el-button>
          <el-button type="primary" @click="handleBuyVIP">确 认 支 付</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog v-model="chargeModalVisible" title="💰 会员卡充值" width="400px" center>
      <el-form label-position="top">
        <el-form-item label="银行卡号" required><el-input v-model="chargeForm.cardNum" placeholder="请输入付款银行卡号" clearable /></el-form-item>
        <el-form-item label="支付密码" required><el-input v-model="chargeForm.password" type="password" placeholder="请输入密码" show-password /></el-form-item>
        <el-form-item label="充值金额 (元)" required><el-input-number v-model="chargeForm.amount" :min="10" :step="50" style="width: 100%;" /></el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="chargeModalVisible = false">取 消</el-button>
          <el-button color="#f7d393" style="color: #333;" @click="handleCharge">确 认 充 值</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import NavBar from '../components/NavBar.vue'

const currentUser = JSON.parse(localStorage.getItem('user') || '{}')
const userId = currentUser.id || sessionStorage.getItem('id')

const isMember = ref(false)
const vipConfig = ref({price: 25, description: '购买会员卡后，可享受充值满减、购票折扣等专属优惠！'})
const memberInfo = ref({id: '', joinDate: '', balance: 0})
const strategies = ref([])
const selectedStrategy = ref('')

const coupons = ref([])
const activities = ref([])

const buyModalVisible = ref(false), buyForm = ref({cardNum: '', password: ''})
const chargeModalVisible = ref(false), chargeForm = ref({cardNum: '', password: '', amount: 100})

const fetchAssets = async () => {
  if (!userId) return

  // 1. 获取 VIP 状态
  try {
    const vipRes = await axios.get(`/api/vip/${userId}/get`)
    if (vipRes.data.success && vipRes.data.content) {
      isMember.value = true
      memberInfo.value = {
        id: vipRes.data.content.id,
        joinDate: vipRes.data.content.joinDate ? vipRes.data.content.joinDate.substring(0, 10) : '近期',
        balance: vipRes.data.content.balance
      }
    }
  } catch (e) { isMember.value = false }

  // 2. 获取充值策略
  try {
    const strategyRes = await axios.get('/api/vip/getVIPInfo')
    if (strategyRes.data.success && strategyRes.data.content) {
      vipConfig.value.price = strategyRes.data.content.price || 25
      const rawStrategies = strategyRes.data.content.strategies || []
      strategies.value = rawStrategies.map(s => ({ id: s.id, target: s.chargeLimit, gift: s.giftAmount }))
      if(strategies.value.length > 0) selectedStrategy.value = strategies.value[0].id
    }
  } catch (e) { console.warn('策略加载失败') }

  // 3. 获取个人优惠券卡包 (保留体验券逻辑，如果你不需要体验券了，可以把中间的 if(fetchedCoupons.length === 0) 块删掉)
  try {
    const couponRes = await axios.get(`/api/coupon/${userId}/get`)
    if (couponRes.data.success) {
      let fetchedCoupons = couponRes.data.content || []
      if (fetchedCoupons.length === 0) {
        fetchedCoupons = [
          { id: 998, name: '新人专享立减券', description: '无门槛全场通用', targetAmount: 0, discountAmount: 15, endTime: '2025-12-31' },
          { id: 999, name: '周末狂欢满减券', description: '周末黄金档专用，满 100 减 30', targetAmount: 100, discountAmount: 30, endTime: '2025-12-31' }
        ]
      }
      coupons.value = fetchedCoupons
    }
  } catch (e) { console.warn('优惠券加载失败') }

  // 4. 获取影城活动，这次包含了 movieList
  try {
    const activityRes = await axios.get('/api/activity/get')
    if (activityRes.data.success) {
      activities.value = activityRes.data.content || []
    }
  } catch (e) { console.warn('活动加载失败') }
}

onMounted(() => fetchAssets())

const handleBuyVIP = async () => {
  try {
    const res = await axios.post(`/api/vip/add?userId=${userId}`)
    if (res.data.success) { ElMessage.success('开卡成功！'), buyModalVisible.value = false, fetchAssets() }
  } catch (e) { ElMessage.error('网络错误') }
}

const handleCharge = async () => {
  try {
    const res = await axios.post('/api/vip/charge', { vipId: memberInfo.value.id, amount: chargeForm.value.amount, vipStrategyID: selectedStrategy.value })
    if (res.data.success) { ElMessage.success('充值成功！'), chargeModalVisible.value = false, fetchAssets() }
  } catch (e) { ElMessage.error('网络错误') }
}
</script>

<style scoped>
.page-container { min-height: 100vh; background-color: #f5f7fa; padding-bottom: 40px; }
.content-container { max-width: 800px; margin: 40px auto; }
.card { background: #fff; border-radius: 8px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05); padding: 30px; }
.section-title { margin-top: 0; margin-bottom: 20px; color: #333; border-left: 5px solid #1caf9a; padding-left: 10px; }
.vip-card-section { margin-bottom: 30px; display: flex; justify-content: center; }
.vip-card { width: 500px; border-radius: 16px; box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1); overflow: hidden; color: white; transition: transform 0.3s; }
.vip-card:hover { transform: translateY(-5px); }
.card-header { padding: 20px 25px; display: flex; justify-content: space-between; align-items: center; font-size: 18px; font-weight: bold; border-bottom: 1px solid rgba(255, 255, 255, 0.1); }
.card-body { padding: 30px 25px; text-align: center; }
.card-footer { padding: 0 25px 25px; }
.action-btn { width: 100%; border-radius: 8px; }
.non-member { background: linear-gradient(135deg, #a0a5ab, #656d78); }
.non-member .price-text { font-size: 32px; margin: 0 0 10px 0; color: #fff; }
.non-member .desc { margin: 0; font-size: 14px; opacity: 0.9; line-height: 1.5; }
.is-member { background: linear-gradient(135deg, #2c3e50, #000000); }
.vip-info { text-align: left; }
.vip-info p { margin: 0 0 15px 0; color: #dcdcdc; display: flex; align-items: center; }
.vip-info strong { color: #fff; font-size: 16px; font-family: monospace; letter-spacing: 1px; }
.balance-display { margin-top: 25px; padding-top: 20px; border-top: 1px dashed rgba(255, 255, 255, 0.2); font-size: 16px; color: #dcdcdc; }
.balance-num { font-size: 32px; font-weight: bold; color: #f7d393; margin-left: 10px; }
.coupon-list { display: flex; flex-direction: column; gap: 15px; }
.coupon-item { display: flex; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05); border: 1px solid #f0f0f0; }
.coupon-left { width: 140px; background: linear-gradient(135deg, #ed5565, #da4453); color: white; display: flex; flex-direction: column; justify-content: center; align-items: center; position: relative; }
.coupon-left::after { content: ''; position: absolute; right: -5px; top: 0; bottom: 0; width: 10px; background-image: radial-gradient(circle at 5px 5px, white 5px, transparent 6px); background-size: 10px 15px; }
.coupon-left h2 { margin: 0; font-size: 18px; }
.coupon-left span { font-size: 36px; font-weight: bold; }
.coupon-left p { margin: 5px 0 0; font-size: 12px; opacity: 0.9; text-align: center; padding: 0 5px; }
.coupon-right { flex: 1; padding: 15px 20px; background: white; display: flex; flex-direction: column; justify-content: center; }
.coupon-right h4 { margin: 0 0 8px 0; font-size: 16px; color: #333; }
.coupon-desc { margin: 0; font-size: 13px; color: #666; }
.pay-amount-display { margin-top: 20px; text-align: right; font-size: 16px; padding-top: 15px; border-top: 1px dashed #ddd; }
.price-text { color: #ed5565; font-weight: bold; }
</style>