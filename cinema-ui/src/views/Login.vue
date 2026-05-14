<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <h2>🎬 电影院售票与管理系统</h2>
        </div>
      </template>
      <el-form :model="loginForm" label-width="60px">
        <el-form-item label="账号">
          <el-input v-model="loginForm.username" placeholder="请输入用户名 (如 root / faker)" clearable />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button v-if="!isRegister" type="primary" class="login-btn" @click="handleLogin">登 录</el-button>
          <el-button v-else type="success" class="login-btn" @click="handleRegister">注 册 账 号</el-button>
        </el-form-item>
        <div style="text-align: right; color: #409eff; cursor: pointer; font-size: 14px;" @click="isRegister = !isRegister">
          {{ isRegister ? '已有账号？返回登录' : '没有账号？点击注册' }}
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const isRegister = ref(false) // 新增状态：当前是否处于注册模式

const loginForm = reactive({
  username: '',
  password: ''
})

// 登录逻辑 (保持你原有的逻辑不变)
const handleLogin = async () => {
  if (!loginForm.username || !loginForm.password) return ElMessage.warning('请输入完整！')
  try {
    const res = await axios.post('/api/login', loginForm)
    if (res.data.success) {
      ElMessage.success('登录成功！')
      localStorage.setItem('user', JSON.stringify(res.data.content))
      if (loginForm.username === 'root') router.push('/admin')
      else router.push('/home')
    } else {
      ElMessage.error(res.data.message || '登录失败')
    }
  } catch (e) {
    ElMessage.error('网络错误')
  }
}

// 新增：注册逻辑
const handleRegister = async () => {
  if (!loginForm.username || !loginForm.password) return ElMessage.warning('请输入完整！')
  try {
    // 调用后端存在的注册接口，默认注册的 kind 为 2 (普通观众)
    const res = await axios.post('/api/register', {
      username: loginForm.username,
      password: loginForm.password,
      kind: 2
    })
    if (res.data.success) {
      ElMessage.success('注册成功，请直接登录！')
      isRegister.value = false // 注册成功后切回登录模式
    } else {
      ElMessage.error(res.data.message || '该用户名已被注册')
    }
  } catch (e) {
    ElMessage.error('网络错误')
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #2b3e50;
}
.login-card {
  width: 400px;
}
.card-header {
  text-align: center;
}
.login-btn {
  width: 100%;
  margin-top: 10px;
}
</style>