<template>
  <div>
    <div class="operation-bar" style="margin-bottom: 20px;">
      <el-button type="primary" size="large" @click="openAddModal">➕ 新增影厅</el-button>
    </div>

    <el-row :gutter="25">
      <el-col :span="8" v-for="hall in cinemaHalls" :key="hall.id" style="margin-bottom: 25px;">
        <div class="hall-card">
          <div class="hall-header">
            <h3>{{ hall.name }}</h3>
            <el-tag :type="(hall.status === 0) ? 'danger' : 'success'" size="small">
              {{ (hall.status === 0) ? '维护中' : '正常营业' }}
            </el-tag>
          </div>
          <div class="hall-info">
            <p>规模：<strong>{{ hall.row }}</strong> 排 × <strong>{{ hall.column }}</strong> 列 (总 {{ hall.row * hall.column }} 座)</p>
          </div>
          <div class="mini-seat-map">
            <div class="screen-line">银幕方向</div>
            <div class="mini-row" v-for="r in hall.row" :key="r">
              <div class="mini-seat" v-for="c in hall.column" :key="c"></div>
            </div>
          </div>
          <div class="hall-actions">
            <el-button type="primary" size="small" plain @click="openEditModal(hall)">修改配置</el-button>
            <el-button type="danger" size="small" plain @click="handleDelete(hall.id)">拆除影厅</el-button>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-dialog v-model="dialogVisible" :title="dialogType === 'add' ? '➕ 新增影厅' : '✏️ 修改影厅'" width="450px">
      <el-form :model="hallForm" label-width="90px">
        <el-form-item label="影厅名称" required>
          <el-input v-model="hallForm.name" placeholder="例如：1号激光厅" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="座位排数" required>
              <el-input-number v-model="hallForm.row" :min="3" :max="20" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="座位列数" required label-width="80px">
              <el-input-number v-model="hallForm.column" :min="3" :max="25" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="营业状态" required>
          <el-switch
              v-model="hallForm.status"
              :active-value="1"
              :inactive-value="0"
              active-text="正常营业"
              inactive-text="设备维护"
              active-color="#1caf9a"
              inactive-color="#ff4949"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitHall">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

const cinemaHalls = ref([])
const dialogVisible = ref(false)
const dialogType = ref('add')
const hallForm = ref({})

// 1. 获取所有影厅
const fetchHalls = async () => {
  try {
    const res = await axios.get('/api/hall/all')
    if (res.data.success) {
      cinemaHalls.value = res.data.content
    } else {
      ElMessage.error(res.data.message || '获取列表失败')
    }
  } catch (error) {
    console.error('获取数据失败:', error)
  }
}

onMounted(() => fetchHalls())

const openAddModal = () => {
  dialogType.value = 'add';
  hallForm.value = { name: '', row: 8, column: 10, status: 1 };
  dialogVisible.value = true
}

const openEditModal = (row) => {
  dialogType.value = 'edit';
  hallForm.value = { ...row };
  if (hallForm.value.status === undefined) hallForm.value.status = 1;
  dialogVisible.value = true
}

// 2. 提交新增或修改
const submitHall = async () => {
  if (!hallForm.value.name) return ElMessage.warning('影厅名称不能为空！')
  try {
    let res;
    if (dialogType.value === 'add') {
      res = await axios.post('/api/hall/add', hallForm.value)
    } else {
      res = await axios.post('/api/hall/update', hallForm.value)
    }

    if (res.data.success) {
      ElMessage.success('保存成功！')
      dialogVisible.value = false
      fetchHalls()
    } else {
      ElMessage.error(res.data.message || '操作失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('保存失败，请检查后端接口')
  }
}

// 3. 删除记录
const handleDelete = (id) => {
  ElMessageBox.confirm('警告：拆除将导致该厅相关数据失效！', '严重警告', { type: 'error' }).then(async () => {
    try {
      const res = await axios.post('/api/hall/delete', null, { params: { id: id } })

      if (res.data.success) {
        ElMessage.success('已拆除！')
        fetchHalls()
      } else {
        ElMessage.error(res.data.message || '删除失败，请检查F12报错')
      }
    } catch (error) {
      console.error(error)
      ElMessage.error('网络请求失败，请打开 F12 查看具体的 URL 是否正确')
    }
  }).catch(() => {})
}
</script>

<style scoped>
.hall-card { background: #fff; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); padding: 20px; border-top: 4px solid #1caf9a; transition: transform 0.3s; }
.hall-card:hover { transform: translateY(-5px); }
.hall-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; border-bottom: 1px dashed #eee; padding-bottom: 10px; }
.hall-header h3 { margin: 0; color: #333; font-size: 18px; }
.hall-info p { margin: 5px 0; color: #666; font-size: 14px; }
.mini-seat-map { background-color: #f8f9fa; border-radius: 6px; padding: 15px 10px; margin: 15px 0; display: flex; flex-direction: column; align-items: center; min-height: 120px; justify-content: center; }
.screen-line { width: 80%; border-top: 2px solid #ccc; border-radius: 50% / 100% 100% 0 0; text-align: center; font-size: 12px; color: #aaa; padding-top: 2px; margin-bottom: 15px; }
.mini-row { display: flex; gap: 3px; margin-bottom: 3px; }
.mini-seat { width: 8px; height: 8px; background-color: #1caf9a; border-radius: 2px; opacity: 0.8; }
.hall-actions { display: flex; justify-content: space-between; margin-top: 15px; }
</style>