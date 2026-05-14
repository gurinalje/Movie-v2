<template>
  <div class="page-container">
    <NavBar />
    <main class="content-container">
      <div class="card info-card">
        <div class="header-title"><h2>👤 修改个人信息</h2></div>
        <el-form :model="userForm" label-width="100px" class="user-form" style="margin-top: 30px;">
          <el-form-item label="当前账号">
            <el-input v-model="userForm.username" disabled /><div class="form-tip">账号为登录凭证，不可修改</div>
          </el-form-item>
          <el-form-item label="新密码" required>
            <el-input v-model="userForm.newPassword" type="password" placeholder="请输入新密码 (6-12个字符)" show-password />
          </el-form-item>
          <el-form-item label="确认密码" required>
            <el-input v-model="userForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="large" style="width: 100%; margin-top: 20px;" @click="handleUpdate">确认修改</el-button>
          </el-form-item>
        </el-form>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { useRouter } from 'vue-router'
import NavBar from '../components/NavBar.vue' // 确保你的 NavBar 组件路径正确

const router = useRouter()

// 1. 正确地从 localStorage 中读取当前登录的用户信息
const currentUserStr = localStorage.getItem('user')
const currentUser = currentUserStr ? JSON.parse(currentUserStr) : { id: 1, username: '未登录' }

// 2. 初始化表单，绑定当前用户名
const userForm = ref({
  username: currentUser.username,
  newPassword: '',
  confirmPassword: ''
})

// 3. 提交修改密码请求
const handleUpdate = async () => {
  if (!userForm.value.newPassword || !userForm.value.confirmPassword) {
    return ElMessage.warning('请完整填写密码！')
  }
  if (userForm.value.newPassword.length < 6 || userForm.value.newPassword.length > 12) {
    return ElMessage.error('密码长度必须在 6-12 位之间！')
  }
  if (userForm.value.newPassword !== userForm.value.confirmPassword) {
    return ElMessage.error('两次输入的密码不一致！')
  }

  try {
    // 🌟 修复点：修改为后端真实存在的接口路径 /api/update/user
    const res = await axios.post('/api/update/user', {
      id: currentUser.id, // 使用正确的用户 ID
      username: currentUser.username,
      password: userForm.value.newPassword
    })

    if (res.data.success) {
      ElMessage.success('密码修改成功！请重新登录。')
      localStorage.removeItem('user') // 清除登录态
      setTimeout(() => {
        router.push('/login')
      }, 1000)
    } else {
      ElMessage.error(res.data.message || '密码修改失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('请求失败，请检查后端接口')
  }
}
</script>

<style scoped>
.page-container {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding-bottom: 40px;
}

.content-container {
  max-width: 600px;
  margin: 40px auto;
}

.card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  padding: 40px;
}

.header-title h2 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #333;
  border-left: 5px solid #1caf9a;
  padding-left: 10px;
}

.form-tip {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
  line-height: 1.2;
}
</style>