<div align="center">

# Movie-v2 - 在线电影购票系统

![Vue](https://img.shields.io/badge/Vue-3.5-42b883?style=flat&logo=vue.js)
![TypeScript](https://img.shields.io/badge/TypeScript-5.0-3178c6?style=flat&logo=typescript)
![Vite](https://img.shields.io/badge/Vite-5.4-646cff?style=flat&logo=vite)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.1-6db33f?style=flat&logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479a1?style=flat&logo=mysql)

**[English](README_EN.md) | 中文**

[![GitHub stars](https://img.shields.io/github/stars/gurinalje/Movie-v2?style=social)](https://github.com/gurinalje/Movie-v2/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/gurinalje/Movie-v2?style=social)](https://github.com/gurinalje/Movie-v2/network/members)

</div>

---

## 项目简介

一个前后端分离的在线电影购票平台，模拟真实电影院业务场景，包含完整的用户端购票流程和管理后台运营系统。

**项目周期：** 2 周
**个人角色：** 独立开发者
**项目类型：** 全栈开发（前端 + 后端 + 数据库设计）

---

## 核心功能

### 用户端

| 功能模块 | 技术实现 |
|---------|---------|
| 在线选座购票 | Canvas 选座算法 + 并发锁座控制 |
| 会员卡系统 | 充值策略引擎 + 余额消费逻辑 |
| 优惠券系统 | 优惠券发放/核销 + 满减规则引擎 |
| 电影搜索 | 模糊搜索 + 分页查询 |
| 消费记录 | 多维度消费统计 + 历史记录查询 |

### 管理端

| 功能模块 | 技术实现 |
|---------|---------|
| 排片调度 | 场次管理 + 时间冲突检测 |
| 数据可视化大屏 | ECharts 多维度数据展示 |
| 促销活动 | 活动配置 + 优惠券关联 |
| 退票策略 | 按电影配置退票时限和手续费 |

---

## 技术架构

```
┌─────────────────────────────────────────────────────────────┐
│                        前端 (Vue 3)                         │
├─────────────────────────────────────────────────────────────┤
│  Vue3 Composition API + TypeScript + Vite + Element Plus    │
│  Pinia 状态管理 + Vue Router 路由 + ECharts 数据可视化       │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                     后端 (Spring Boot)                      │
├─────────────────────────────────────────────────────────────┤
│  Spring Boot + MyBatis + MySQL 8.0 + BCrypt 安全加密        │
│  分层架构: Controller → Service → Mapper → Database          │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                        数据库 (MySQL)                       │
├─────────────────────────────────────────────────────────────┤
│  15 张表: user, movie, hall, schedule, ticket, vip_card...   │
└─────────────────────────────────────────────────────────────┘
```

---

## 项目亮点

### 1. 完整的选座购票流程

```javascript
// 核心选座算法
const selectSeat = (row, col) => {
  // 1. 检查座位状态
  if (seatStatus[row][col] === 'locked') return

  // 2. 锁定座位（防止并发）
  await lockSeat(row, col)

  // 3. 生成订单
  const order = await createOrder({ seat: `${row}-${col}` })

  // 4. 15分钟未支付自动释放
  startCountdown(order.id, 15 * 60)
}
```

**技术难点：**
- 并发场景下的座位锁定机制
- 订单超时自动释放（定时任务）
- 座位状态实时同步

### 2. 数据可视化大屏

使用 ECharts 实现多维度数据展示：
- 票房排行榜（柱状图）
- 观众消费趋势（折线图）
- 排片统计（饼图）
- 热门电影（雷达图）

### 3. 响应式设计

- 移动端适配
- 自适应布局
- 跨浏览器兼容（Chrome、Firefox、Safari、Edge）

---

## 技术栈

### 前端

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue 3 | 3.5.29 | 前端框架 |
| TypeScript | 5.0+ | 类型安全开发 |
| Vite | 5.4.11 | 构建工具 |
| Element Plus | 2.13.5 | UI 组件库 |
| Pinia | 3.0.4 | 状态管理 |
| ECharts | 6.0.0 | 数据可视化 |
| Axios | 1.13.6 | HTTP 请求 |

### 后端

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 1.8 | 编程语言 |
| Spring Boot | 2.1.3 | Web 框架 |
| MyBatis | 1.3.2 | ORM 框架 |
| MySQL | 8.0 | 数据库 |

---

## 快速开始

### 环境要求

- JDK 1.8+
- Maven 3.x
- MySQL 8.0+
- Node.js 20.19+

### 安装步骤

```bash
# 1. 克隆项目
git clone https://github.com/gurinalje/Movie-v2.git
cd Movie-v2

# 2. 初始化数据库
mysql -u root -p < movie/sql/update.sql

# 3. 配置数据库连接
# 编辑 movie/.env 文件

# 4. 启动后端
cd movie
mvn spring-boot:run

# 5. 启动前端
cd cinema-ui
npm install
npm run dev
```

### 访问系统

| 角色 | 账号 | 密码 |
|------|------|------|
| 管理员 | root | 123456 |
| 普通用户 | faker | 111111 |

访问地址：http://localhost:5173

---

## 项目结构

```
在线电影购票系统/
├── movie/                              # 后端 Spring Boot 项目
│   ├── pom.xml                         # Maven 依赖配置
│   ├── .env                            # 环境变量（数据库配置，不入库）
│   ├── sql/
│   │   └── update.sql                  # 数据库建表 + 初始数据脚本
│   └── src/main/
│       ├── java/com/example/cinema/
│       │   ├── CinemaApplication.java  # 启动类
│       │   ├── config/
│       │   │   └── InterceptorConfiguration.java  # 拦截器配置
│       │   ├── interceptor/
│       │   │   └── SessionInterceptor.java        # 登录拦截器
│       │   ├── controller/             # ---- 控制器层（API 入口）----
│       │   │   ├── user/
│       │   │   │   └── AccountController.java     # 账户（登录/注册/用户管理）
│       │   │   ├── management/
│       │   │   │   ├── MovieController.java       # 电影管理
│       │   │   │   ├── ScheduleController.java    # 排片管理
│       │   │   │   └── HallController.java        # 影厅管理
│       │   │   ├── sales/
│       │   │   │   ├── TicketController.java      # 票务（购票/选座/退票）
│       │   │   │   └── RefundController.java      # 退票策略管理
│       │   │   ├── promotion/
│       │   │   │   ├── VIPCardController.java     # VIP 会员卡
│       │   │   │   ├── CouponController.java      # 优惠券
│       │   │   │   └── ActivityController.java    # 促销活动
│       │   │   └── statistics/
│       │   │       └── StatisticsController.java  # 经营数据统计
│       │   ├── bl/                     # ---- 业务逻辑层接口 ----
│       │   ├── blImpl/                 # ---- 业务逻辑层实现 ----
│       │   ├── data/                   # ---- 数据访问层（MyBatis Mapper）----
│       │   ├── po/                     # ---- 数据库实体类 ----
│       │   └── vo/                     # ---- 视图对象 / 表单对象 ----
│       └── resources/
│           ├── application.yml         # 应用配置
│           ├── dataImpl/               # MyBatis XML 映射文件
│           ├── static/                 # 静态资源
│           └── templates/              # Thymeleaf 模板
│
├── cinema-ui/                          # 前端 Vue 3 项目
│   ├── package.json                    # Node 依赖配置
│   ├── vite.config.js                  # Vite 配置（含代理设置）
│   ├── index.html                      # HTML 入口
│   └── src/
│       ├── main.js                     # Vue 应用入口
│       ├── App.vue                     # 根组件
│       ├── router/index.js             # 前端路由配置
│       ├── components/
│       │   └── NavBar.vue              # 导航栏组件
│       ├── layout/
│       │   └── AdminLayout.vue         # 管理后台布局（侧边栏 + 顶栏）
│       └── views/                      # ---- 页面组件 ----
│           ├── Login.vue               # 登录页
│           ├── Home.vue                # 首页（热门电影）
│           ├── Movie.vue               # 电影列表
│           ├── MovieDetail.vue         # 电影详情
│           ├── MovieBuy.vue            # 选座购票
│           ├── UserBuy.vue             # 用户订单
│           ├── UserCost.vue            # 消费记录
│           ├── UserInfo.vue            # 个人信息
│           ├── UserMember.vue          # 会员中心
│           ├── AdminMovie.vue          # [管理] 电影管理
│           ├── AdminSchedule.vue       # [管理] 排片管理
│           ├── AdminCinema.vue         # [管理] 影厅管理
│           ├── AdminPromotion.vue      # [管理] 促销活动管理
│           ├── AdminVip.vue            # [管理] VIP 管理
│           ├── AdminRefund.vue         # [管理] 退票策略管理
│           ├── AdminStatistic.vue      # [管理] 数据统计
│           └── AdminTicket.vue         # [管理] 票务管理
│
├── assets/                              # 项目截图
│   ├── login.png
│   ├── home.png
│   ├── movie-detail-2.png
│   ├── seat-selection.png
│   ├── payment.png
│   ├── order-management.png
│   ├── vip-member.png
│   ├── admin-movie.png
│   ├── admin-hall.png
│   └── admin-statistics.png
│
└── SECURITY_REVIEW_REPORT.md           # 安全审查报告
```

---

## 核心代码示例

### Axios 请求封装

```typescript
// src/api/request.ts
import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => response.data,
  error => {
    ElMessage.error(error.response?.data?.message || '请求失败')
    return Promise.reject(error)
  }
)

export default request
```

### Pinia 状态管理

```typescript
// src/stores/user.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/api/request'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref(null)
  const isLoggedIn = ref(false)

  const login = async (username: string, password: string) => {
    const res = await request.post('/login', { username, password })
    userInfo.value = res.data
    isLoggedIn.value = true
    localStorage.setItem('token', res.data.token)
  }

  const logout = () => {
    userInfo.value = null
    isLoggedIn.value = false
    localStorage.removeItem('token')
  }

  return { userInfo, isLoggedIn, login, logout }
})
```

---

## 安全特性

- **密码加密：** 使用 BCrypt 算法存储密码
- **权限控制：** 基于 Session 的登录拦截
- **数据保护：** UserVO 不返回敏感信息
- **SQL 注入防护：** MyBatis 参数化查询

---

## 开发记录

- ✅ 完成用户端核心功能
- ✅ 完成管理后台开发
- ✅ 实现数据可视化大屏
- ✅ 完成安全审查和漏洞修复
- ✅ 优化页面加载性能

---

## 许可证

本项目仅供学习和研究使用。

---

<div align="center">

**如果这个项目对你有帮助，欢迎 Star 支持！**

[![Star History](https://api.star-history.com/svg?repos=gurinalje/Movie-v2&type=Date)](https://star-history.com/#gurinalje/Movie-v2&Date)

</div>
