<template>
  <div class="admin-statistic">
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="6" v-for="item in summaryData" :key="item.label">
        <div class="stat-card">
          <div class="stat-label">{{ item.label }}</div>
          <div class="stat-value" :style="{ color: item.color }">{{ item.value }}</div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="12">
        <div class="chart-card">
          <h3>🔥 电影总票房排行 (TOP 5)</h3>
          <div ref="boxOfficeChart" class="chart-box"></div>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="chart-card">
          <h3>📊 影厅售票分布占比 </h3>
          <div ref="attendanceChart" class="chart-box"></div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <div class="chart-card">
          <h3>📅 近七日票房销售趋势</h3>
          <div ref="salesTrendChart" class="chart-box"></div>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="chart-card">
          <h3>🎭 电影类型偏好雷达图</h3>
          <div ref="typePreferenceChart" class="chart-box"></div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <div class="chart-card">
          <div class="chart-header">
            <h3>🎬 每日排片场次统计</h3>
            <el-date-picker
              v-model="scheduleDate"
              type="date"
              placeholder="选择日期"
              value-format="YYYY-MM-DD"
              :clearable="false"
              @change="fetchScheduleData"
              style="margin-left: auto;"
            />
          </div>
          <div ref="scheduleChart" class="chart-box"></div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, shallowRef } from 'vue'
import * as echarts from 'echarts'
import axios from 'axios'

const summaryData = ref([
  { label: '累计总票房', value: '加载中...', color: '#ed5565' },
  { label: '今日支付订单数', value: '0 笔', color: '#1caf9a' },
  { label: '累计开卡会员数', value: '0 人', color: '#f8ac59' },
  { label: '全站平均客单价', value: '加载中...', color: '#23b7e5' }
])

const boxOfficeChart = ref(null)
const attendanceChart = ref(null)
const salesTrendChart = ref(null)
const typePreferenceChart = ref(null)
const scheduleDate = ref(new Date().toISOString().slice(0, 10))
const scheduleChart = ref(null)
const charts = shallowRef({})

const fetchStatisticsData = async () => {
  try {
    // 1. 获取总票房和排行
    const boxRes = await axios.get('/api/statistics/boxOffice/total')
    if (boxRes.data.success && boxRes.data.content) {
      const boxData = boxRes.data.content
      const totalIncome = boxData.reduce((sum, item) => sum + (item.boxOffice || 0), 0)
      summaryData.value[0].value = `￥${totalIncome.toLocaleString()}`
      const top5 = boxData.slice(0, 5)
      charts.value.boxChart.setOption({
        xAxis: { data: top5.map(item => item.name) },
        series: [{ data: top5.map(item => item.boxOffice || 0) }]
      })
    }

    // 🚀 2. 获取大屏综合真实数据
    const compRes = await axios.get('/api/statistics/comprehensive')
    if (compRes.data.success && compRes.data.content) {
      const data = compRes.data.content

      // 更新顶部数字
      summaryData.value[1].value = `${data.todayOrderCount} 笔`
      summaryData.value[2].value = `${data.vipCount} 人`
      summaryData.value[3].value = `￥${data.averagePrice.toFixed(1)}`

      // 更新影厅分布饼图
      if (data.hallPopularity && data.hallPopularity.length > 0) {
        charts.value.attChart.setOption({ series: [{ data: data.hallPopularity }] })
      }

      // 更新近七日折线趋势图
      if (data.sevenDaysTrend && data.sevenDaysTrend.length > 0) {
        charts.value.trendChart.setOption({
          xAxis: { data: data.sevenDaysTrend.map(item => item.date) },
          series: [{ data: data.sevenDaysTrend.map(item => item.boxOffice) }]
        })
      }

      // 更新电影类型雷达图
      if (data.typePreference && data.typePreference.length > 0) {
        // 先计算一下雷达图的最大边界 (比如最大票数+10)
        let maxVal = Math.max(...data.typePreference.map(i => i.value)) + 10
        let indicatorData = data.typePreference.map(item => ({ name: item.name || '其他', max: maxVal }))
        let valueData = data.typePreference.map(item => item.value)

        charts.value.typeChart.setOption({
          radar: { indicator: indicatorData },
          series: [{ data: [{ value: valueData, name: '观众类型偏好' }] }]
        })
      }
    }
  } catch (error) {
    console.error('获取统计数据失败', error)
  }
}

const fetchScheduleData = async () => {
  try {
    const res = await axios.get('/api/statistics/scheduleTime', { params: { date: scheduleDate.value } })
    if (res.data.success && res.data.content) {
      const scheduleData = res.data.content
      if (scheduleData.length > 0) {
        charts.value.scheduleChart.setOption({
          xAxis: { data: scheduleData.map(item => item.name) },
          series: [{ data: scheduleData.map(item => item.time || 0) }]
        })
      } else {
        charts.value.scheduleChart.setOption({
          xAxis: { data: [] },
          series: [{ data: [] }]
        })
      }
    }
  } catch (error) {
    console.error('获取排片统计数据失败', error)
  }
}

onMounted(() => {
  // 仅设置空壳骨架，数据来了自动填满
  charts.value.boxChart = echarts.init(boxOfficeChart.value)
  charts.value.boxChart.setOption({ tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } }, xAxis: { type: 'category', data: [] }, yAxis: { type: 'value', name: '票房(元)' }, series: [{ data: [], type: 'bar', itemStyle: { color: '#1caf9a', borderRadius: [4, 4, 0, 0] } }] })

  charts.value.attChart = echarts.init(attendanceChart.value)
  charts.value.attChart.setOption({ tooltip: { trigger: 'item', formatter: '{b}: {c} 票' }, series: [{ type: 'pie', radius: ['40%', '70%'], itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 }, data: [] }] })

  charts.value.trendChart = echarts.init(salesTrendChart.value)
  charts.value.trendChart.setOption({ tooltip: { trigger: 'axis' }, xAxis: { type: 'category', data: [] }, yAxis: { type: 'value', name: '总票房(元)' }, series: [{ data: [], type: 'line', smooth: true, areaStyle: { opacity: 0.3 }, itemStyle: { color: '#ed5565' } }] })

  charts.value.typeChart = echarts.init(typePreferenceChart.value)
  charts.value.typeChart.setOption({ tooltip: {}, radar: { indicator: [{name:'暂无数据', max:100}] }, series: [{ type: 'radar', data: [{value:[], name:'观众类型偏好', areaStyle:{color:'rgba(35,183,229,0.5)'}}] }] })

  charts.value.scheduleChart = echarts.init(scheduleChart.value)
  charts.value.scheduleChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    xAxis: { type: 'category', data: [], axisLabel: { rotate: 30 } },
    yAxis: { type: 'value', name: '场次', minInterval: 1 },
    grid: { left: '3%', right: '4%', bottom: '15%', containLabel: true },
    series: [{
      data: [],
      type: 'bar',
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#f8ac59' },
          { offset: 1, color: '#ed5565' }
        ]),
        borderRadius: [4, 4, 0, 0]
      },
      label: { show: true, position: 'top', color: '#666' }
    }]
  })

  fetchStatisticsData()
  fetchScheduleData()
  window.addEventListener('resize', () => Object.values(charts.value).forEach(chart => chart.resize()))
})
</script>

<style scoped>
.stat-card { background: white; padding: 20px; border-radius: 8px; text-align: center; box-shadow: 0 4px 12px rgba(0,0,0,0.05); transition: transform 0.3s;}
.stat-card:hover { transform: translateY(-5px); }
.stat-label { font-size: 14px; color: #999; margin-bottom: 10px; }
.stat-value { font-size: 24px; font-weight: bold; }
.chart-card { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }
.chart-card h3 { margin: 0 0 20px 0; font-size: 16px; color: #333; border-left: 4px solid #1caf9a; padding-left: 10px; }
.chart-header { display: flex; align-items: center; }
.chart-header h3 { margin: 0 0 0 0; font-size: 16px; color: #333; border-left: 4px solid #1caf9a; padding-left: 10px; }
.chart-box { height: 300px; width: 100%; }
</style>