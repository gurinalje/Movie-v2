## 在线电影购票系统 - 前端

> Vue 3 + Element Plus 前端应用。

### 技术栈

- Vue 3.5 + Vite 5
- Vue Router 5 + Pinia 3
- Element Plus 2.13 + ECharts 6
- Axios

### 快速启动

```bash
# 安装依赖
npm install

# 启动开发服务器（端口 5173）
npm run dev

# 构建生产版本
npm run build
```

### 页面路由

| 路径 | 页面 | 说明 |
|------|------|------|
| `/login` | Login.vue | 登录页 |
| `/home` | Home.vue | 首页（热门电影） |
| `/movie` | Movie.vue | 电影列表 |
| `/movieDetail` | MovieDetail.vue | 电影详情 |
| `/movieBuy` | MovieBuy.vue | 选座购票 |
| `/userBuy` | UserBuy.vue | 我的订单 |
| `/userCost` | UserCost.vue | 消费记录 |
| `/userInfo` | UserInfo.vue | 个人信息 |
| `/userMember` | UserMember.vue | 会员中心 |
| `/admin/movie` | AdminMovie.vue | [管理] 电影管理 |
| `/admin/schedule` | AdminSchedule.vue | [管理] 排片管理 |
| `/admin/cinema` | AdminCinema.vue | [管理] 影厅管理 |
| `/admin/promotion` | AdminPromotion.vue | [管理] 促销活动 |
| `/admin/vip` | AdminVip.vue | [管理] VIP 管理 |
| `/admin/refund` | AdminRefund.vue | [管理] 退票策略 |
| `/admin/statistic` | AdminStatistic.vue | [管理] 数据统计 |
| `/admin/ticket` | AdminTicket.vue | [管理] 票务管理 |

### 项目结构

```
cinema-ui/
├── package.json              # Node 依赖
├── vite.config.js            # Vite 配置（含 API 代理）
├── index.html                # HTML 入口
└── src/
    ├── main.js               # 应用入口
    ├── App.vue               # 根组件
    ├── router/index.js       # 路由配置
    ├── components/
    │   └── NavBar.vue        # 导航栏
    ├── layout/
    │   └── AdminLayout.vue   # 管理后台布局
    └── views/                # 页面组件
```

### API 代理

开发环境下，`/api` 前缀的请求会自动代理到后端 `http://localhost:8080`：

```javascript
// vite.config.js
proxy: {
  '/api': {
    target: 'http://localhost:8080',
    changeOrigin: true,
    rewrite: (path) => path.replace(/^\/api/, '')
  }
}
```

前端请求示例：`axios.get('/api/movie/all')` → 实际发送到 `http://localhost:8080/movie/all`
