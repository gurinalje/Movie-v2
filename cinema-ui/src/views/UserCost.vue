<template>
  <div class="page-container">
    <NavBar />
    <main class="content-container">
      <div class="card">
        <div class="header-title"><h2>💰 充值与消费记录</h2></div>
        <el-tabs v-model="activeTab" class="custom-tabs">
          <el-tab-pane label="💳 消费记录" name="consume">
            <el-table :data="consumeRecords" stripe style="width: 100%" :empty-text="'暂无消费记录'">
              <el-table-column prop="time" label="时间" width="220" />
              <el-table-column prop="amount" label="金额 (元)" width="150">
                <template #default="scope"><span style="color: #ed5565; font-weight: bold; font-size: 16px;">- {{ scope.row.amount.toFixed(2) }}</span></template>
              </el-table-column>
              <el-table-column prop="type" label="类型" />
            </el-table>
          </el-tab-pane>
          <el-tab-pane label="💎 充值记录" name="charge">
            <el-table :data="chargeRecords" stripe style="width: 100%" :empty-text="'暂无充值记录'">
              <el-table-column prop="time" label="时间" width="220" />
              <el-table-column prop="amount" label="金额 (元)" width="150">
                <template #default="scope"><span style="color: #1caf9a; font-weight: bold; font-size: 16px;">+ {{ scope.row.amount.toFixed(2) }}</span></template>
              </el-table-column>
              <el-table-column prop="type" label="类型" />
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import NavBar from '../components/NavBar.vue'

const activeTab = ref('consume')
const consumeRecords = ref([])
const chargeRecords = ref([])

const currentUser = JSON.parse(localStorage.getItem('user') || '{}')
const userId = currentUser.id || sessionStorage.getItem('id')

const fetchHistory = async () => {
  if (!userId) {
    console.error("未找到用户信息，无法加载账单！")
    return
  }

  try {
    const res = await axios.get(`/api/get/history?userId=${userId}`)

    if (res.data.success && res.data.content) {
      const allRecords = res.data.content
      consumeRecords.value = []
      chargeRecords.value = []

      allRecords.forEach(record => {
        // 🚀 终极时间格式化防御：兼容字符串格式、时间戳格式，以及后端没传时间的情况
        let formatTime = '系统处理中...'
        if (record.time) {
          if (typeof record.time === 'string') {
            // 如果后端返回类似 2023-11-20T14:30:00 的格式
            formatTime = record.time.replace('T', ' ').substring(0, 19)
          } else {
            // 如果后端返回的是毫秒时间戳数字
            const d = new Date(record.time)
            if (!isNaN(d.getTime())) {
              formatTime = d.getFullYear() + '-' +
                  String(d.getMonth() + 1).padStart(2, '0') + '-' +
                  String(d.getDate()).padStart(2, '0') + ' ' +
                  String(d.getHours()).padStart(2, '0') + ':' +
                  String(d.getMinutes()).padStart(2, '0') + ':' +
                  String(d.getSeconds()).padStart(2, '0')
            }
          }
        }

        const item = {
          time: formatTime,
          amount: Math.abs(record.money || 0),
          type: record.description || '未知交易'
        }

        if (record.money < 0 || record.kind === 0 || record.kind === 2 || record.kind === 3) {
          consumeRecords.value.push(item)
        } else {
          chargeRecords.value.push(item)
        }
      })
    }
  } catch (error) {
    console.error('获取账单真实接口报错了:', error)
  }
}

onMounted(() => fetchHistory())
</script>

<style scoped>
.page-container {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding-bottom: 40px;
}

.content-container {
  max-width: 1000px;
  margin: 40px auto;
}

.card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  padding: 30px;
}

.header-title h2 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #333;
  border-left: 5px solid #1caf9a;
  padding-left: 10px;
}

.custom-tabs {
  margin-top: 20px;
}
</style>