<template>
  <div>
    <div class="operation-bar" style="margin-bottom: 20px;">
      <el-button type="primary" size="large" @click="openAddModal">➕ 发布新活动 (优惠券)</el-button>
    </div>

    <el-row :gutter="20">
      <el-col :span="12" v-for="promo in promotionList" :key="promo.id" style="margin-bottom: 20px;">
        <div class="promo-card">
          <div class="promo-coupon">
            <div class="price">￥<span>{{ promo.coupon ? promo.coupon.discountAmount : 0 }}</span></div>
            <div class="condition">满 {{ promo.coupon ? promo.coupon.targetAmount : 0 }} 元可用</div>
          </div>
          <div class="promo-info">
            <div class="promo-header">
              <h3>{{ promo.name }}</h3>
              <el-tag size="small" type="success" effect="light">进行中</el-tag>
            </div>
            <p class="desc">{{ promo.description }}</p>
            <div class="promo-actions">
              <span class="time">结束时间: {{ formatTime(promo.endTime) }}</span>
              <el-button type="danger" size="small" plain @click="handleDelete(promo.id)">下线活动</el-button>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-empty v-if="promotionList.length === 0" description="暂无正在进行的活动" />

    <el-dialog v-model="dialogVisible" title="🎁 发布新活动" width="550px">
      <el-form :model="promoForm" label-width="100px">
        <el-form-item label="活动名称" required>
          <el-input v-model="promoForm.name" placeholder="如：春节档狂欢满减券" />
        </el-form-item>
        <el-form-item label="活动描述" required>
          <el-input v-model="promoForm.description" type="textarea" :rows="2" />
        </el-form-item>
        <div style="display: flex; gap: 20px;">
          <el-form-item label="需满金额" required style="flex: 1;">
            <el-input-number v-model="promoForm.target" :min="0" :step="10" style="width: 100%;" />
          </el-form-item>
          <el-form-item label="优惠金额" required style="flex: 1;" label-width="90px">
            <el-input-number v-model="promoForm.discount" :min="1" :step="5" style="width: 100%;" />
          </el-form-item>
        </div>
        <el-form-item label="开始时间" required>
          <el-date-picker v-model="promoForm.startTime" type="datetime" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择生效时间" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="结束时间" required>
          <el-date-picker v-model="promoForm.endTime" type="datetime" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择结束时间" style="width: 100%;" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitPromotion">发 布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { ElMessageBox } from 'element-plus'

const promotionList = ref([])

const fetchActivities = async () => {
  try {
    const res = await axios.get('/api/activity/get')
    if (res.data.success) {
      promotionList.value = res.data.content
    } else {
      ElMessage.error(res.data.message || '获取活动列表失败')
    }
  } catch (error) {
    console.error('获取活动失败:', error)
  }
}

onMounted(() => {
  fetchActivities()
})

const dialogVisible = ref(false)
const promoForm = ref({})

// 工具函数：获取当前时间并格式化为 YYYY-MM-DD HH:mm:ss
const getCurrentFormattedTime = () => {
  const d = new Date()
  const pad = (n) => (n < 10 ? '0' + n : n)
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:00`
}

const openAddModal = () => {
  promoForm.value = {
    name: '',
    description: '',
    target: 50,
    discount: 10,
    startTime: getCurrentFormattedTime(), // 赋默认值为标准字符串
    endTime: ''
  }
  dialogVisible.value = true
}

const submitPromotion = async () => {
  if (!promoForm.value.name || !promoForm.value.startTime || !promoForm.value.endTime) {
    return ElMessage.warning('请完整填写活动信息（名称、开始和结束时间）！')
  }
  if (promoForm.value.target <= promoForm.value.discount) {
    return ElMessage.error('优惠金额不能大于等于需满足金额！')
  }
  if (promoForm.value.startTime >= promoForm.value.endTime) {
    return ElMessage.error('结束时间必须晚于开始时间！')
  }

  // 强制转换为标准的 ISO 时间格式，解决 Spring Boot 报 400 的问题
  const formatISO = (dateStr) => new Date(dateStr).toISOString();

  const payload = {
    name: promoForm.value.name,
    description: promoForm.value.description,
    startTime: formatISO(promoForm.value.startTime),
    endTime: formatISO(promoForm.value.endTime),
    movieList: [], // 传空数组代表全场通用
    couponForm: {
      name: promoForm.value.name + '专属券',
      description: promoForm.value.description,
      targetAmount: promoForm.value.target,
      discountAmount: promoForm.value.discount,
      startTime: formatISO(promoForm.value.startTime),
      endTime: formatISO(promoForm.value.endTime)
    }
  }

  try {
    const res = await axios.post('/api/activity/publish', payload)
    if (res.data.success) {
      ElMessage.success('活动发布成功！')
      dialogVisible.value = false
      fetchActivities()
    } else {
      ElMessage.error(res.data.message || '发布失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('网络请求失败')
  }
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要下线该活动吗？', '警告', {
    confirmButtonText: '确定下线',
    cancelButtonText: '取消',
    type: 'error'
  }).then(async () => {
    try {
      // 呼叫我们刚写好的后端 Controller 接口
      const res = await axios.post(`/api/activity/delete?activityId=${id}`)
      if (res.data.success) {
        ElMessage.success('活动已成功下线！')
        fetchActivities() // 刷新列表
      } else {
        ElMessage.error(res.data.message || '下线失败')
      }
    } catch (error) {
      console.error(error)
      ElMessage.error('网络请求失败')
    }
  }).catch(() => {})
}
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  return timeStr.replace('T', ' ').substring(0, 16)
}
</script>

<style scoped>
.promo-card { display: flex; height: 160px; background: #fff; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); overflow: hidden; transition: transform 0.3s; }
.promo-card:hover { transform: translateY(-5px); }
.promo-coupon { width: 140px; background: linear-gradient(135deg, #ed5565, #da4453); color: white; display: flex; flex-direction: column; justify-content: center; align-items: center; position: relative; }
.promo-coupon::after { content: ''; position: absolute; right: -5px; top: 0; bottom: 0; width: 10px; background-image: radial-gradient(circle at 5px 5px, white 5px, transparent 6px); background-size: 10px 15px; }
.promo-coupon .price span { font-size: 36px; font-weight: bold; }
.promo-coupon .condition { font-size: 12px; opacity: 0.9; }
.promo-info { flex: 1; padding: 20px; display: flex; flex-direction: column; }
.promo-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.promo-header h3 { margin: 0; font-size: 18px; color: #333; }
.desc { margin: 0 0 15px 0; font-size: 13px; color: #666; }
.promo-actions { display: flex; justify-content: space-between; align-items: flex-end; margin-top: auto; border-top: 1px dashed #eee; padding-top: 10px; }
.promo-actions .time { font-size: 12px; color: #999; }
</style>