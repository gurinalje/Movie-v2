import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 你的 SpringBoot 后端地址
        changeOrigin: true,
        // 就是这行救命的代码！它会把 /api/login 变成 /login 发给后端
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
})