# 在线电影购票系统

**最后更新：** 2026-06-13

---

## 项目介绍

本系统是一个前后端分离的在线电影购票平台，采用经典的 **Spring Boot + Vue 3** 架构。系统面向两类用户：**普通用户**（购票观影）和**管理员**（影厅/排片/电影/促销管理）。核心业务涵盖电影浏览、在线选座购票、会员卡充值消费、优惠券发放使用、退票策略配置以及经营数据统计等功能。

---

## 功能说明

### 普通用户功能

| 功能模块 | 说明 |
|---------|------|
| 账户管理 | 注册、登录、登出、个人信息查看与修改 |
| 电影浏览 | 查看电影列表、电影详情、按关键词搜索 |
| 在线购票 | 选择排片场次 → 选座 → 锁座 → 支付（银行卡 / VIP 余额） |
| 想看标记 | 标记/取消想看电影，查看想看人数趋势 |
| 会员卡 | 办卡、充值（满赠策略）、余额消费、消费历史 |
| 优惠券 | 查看已发放的优惠券，购票时使用抵扣 |
| 消费记录 | 查看全部消费/充值历史记录 |

### 管理员功能

| 功能模块 | 说明 |
|---------|------|
| 电影管理 | 添加、编辑、批量下架电影 |
| 排片管理 | 新增/编辑/批量删除排片，配置观众可见排片天数 |
| 影厅管理 | 添加、编辑、删除影厅（含营业状态） |
| 促销活动 | 发布/下线优惠活动，关联电影与优惠券 |
| 优惠券管理 | 查看全部优惠券，向指定用户发放优惠券 |
| 会员管理 | VIP 策略配置（满赠规则）、VIP 信息总览 |
| 退票策略 | 按电影配置退票时间限制与手续费比例 |
| 票务管理 | 查看全部订单、配置退票时限 |
| 数据统计 | 票房排行、观众消费分析、排片统计、热门电影、综合大屏 |

---

## 技术栈

### 后端 (`movie/`)

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 1.8 | 编程语言 |
| Spring Boot | 2.1.3.RELEASE | Web 框架 |
| MyBatis | 1.3.2 | ORM 框架 |
| MySQL | 8.0 | 关系型数据库 |
| Thymeleaf | (内置) | 模板引擎（预留） |
| Lombok | 1.18.30 | 简化 Java 代码 |
| Spring Security Crypto | (内置) | BCrypt 密码加密 |
| Commons FileUpload | 1.3.3 | 文件上传 |
| Spring Boot Actuator | (内置) | 应用监控 |

### 前端 (`cinema-ui/`)

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.5.29 | 前端框架 |
| Vite | 5.4.11 | 构建工具 |
| Vue Router | 5.0.3 | 路由管理 |
| Pinia | 3.0.4 | 状态管理 |
| Element Plus | 2.13.5 | UI 组件库 |
| Axios | 1.13.6 | HTTP 客户端 |
| ECharts | 6.0.0 | 数据可视化图表 |

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
└── SECURITY_REVIEW_REPORT.md           # 安全审查报告
```

---

## 环境要求

| 依赖 | 最低版本 | 说明 |
|------|---------|------|
| JDK | 1.8+ | Java 运行环境 |
| Maven | 3.x | 后端构建工具 |
| MySQL | 8.0+ | 数据库 |
| Node.js | 20.19+ 或 22.12+ | 前端运行环境 |
| npm | 对应 Node.js 版本 | 前端包管理器 |

---

## 安装和运行指南

### 1. 初始化数据库

```bash
# 登录 MySQL 并执行建表脚本
mysql -u root -p < movie/sql/update.sql
```

该脚本会自动创建 `cinema` 数据库、所有数据表以及初始测试数据。

### 2. 配置数据库连接

编辑 `movie/.env` 文件（确保此文件不提交到版本库）：

```properties
DB_URL=jdbc:mysql://localhost:3306/cinema?serverTimezone=CTT&characterEncoding=UTF-8&useSSL=false
DB_USERNAME=root
DB_PASSWORD=你的数据库密码
```

### 3. 启动后端

```bash
# 进入后端目录
cd movie

# 编译并启动
mvn spring-boot:run
```

后端启动后默认运行在 **http://localhost:8080**

### 4. 启动前端

```bash
# 进入前端目录
cd cinema-ui

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端启动后默认运行在 **http://localhost:5173**，所有 `/api` 开头的请求会自动代理到后端 `http://localhost:8080`（路径中的 `/api` 前缀会被去除）。

### 5. 访问系统

| 角色 | 账号 | 密码 |
|------|------|------|
| 管理员 | root | 123456 |
| 普通用户 | faker | 111111 |
| 普通用户 | zhangsan | 111222 |

- 访问 **http://localhost:5173** 即可打开系统首页
- 使用管理员账号登录后自动跳转到后台管理页面
- 使用普通用户账号登录后进入用户前台页面

---

## 前端开发指南

```bash
# 启动开发服务器（热重载）
npm run dev

# 构建生产版本
npm run build

# 预览生产构建
npm run preview
```

**前端代理配置**（`vite.config.js`）：

```javascript
proxy: {
  '/api': {
    target: 'http://localhost:8080',  // 后端地址
    changeOrigin: true,
    rewrite: (path) => path.replace(/^\/api/, '')  // 去除 /api 前缀
  }
}
```

前端页面请求示例：`axios.get('/api/movie/all')` → 实际发送到 `http://localhost:8080/movie/all`。

---

## 后端开发指南

**后端分层架构：**

```
Controller（控制器层）
    ↓ 调用
Service 接口（业务逻辑层）
    ↓ 实现
ServiceImpl（业务逻辑实现层）
    ↓ 调用
Mapper（数据访问层，MyBatis XML 映射）
    ↓ 操作
MySQL 数据库
```

**关键配置：**

- `application.yml` - 数据源、MyBatis 映射路径、Jackson 时区
- `InterceptorConfiguration.java` - 登录拦截器，排除登录/注册/静态资源等路径
- `SessionInterceptor.java` - 基于 HTTP Session 的登录校验，未登录返回 401 JSON

**启动类注解说明：**
- `@EnableScheduling` - 启用定时任务（用于自动清理超时未支付的订单）
- `@SpringBootApplication` - Spring Boot 自动配置

---

## 数据库表结构

| 表名 | 说明 |
|------|------|
| `user` | 用户表（id, username, password, kind: 1管理员/2普通用户） |
| `movie` | 电影表（含名称、导演、主演、类型、海报等） |
| `hall` | 影厅表（含行列座位数、营业状态） |
| `schedule` | 排片表（关联影厅和电影，含场次时间和票价） |
| `schedule_view` | 排片可见天数设置 |
| `ticket` | 订单表（关联用户和排片，含座位信息、订单状态） |
| `vip_card` | 会员卡表（含余额和累计消费） |
| `vip_strategy` | VIP 充值策略（满 X 赠 Y） |
| `coupon` | 优惠券模板表（含使用门槛和优惠金额） |
| `coupon_user` | 优惠券发放记录表 |
| `activity` | 促销活动表 |
| `activity_movie` | 活动与电影关联表 |
| `refundpolicy` | 退票策略表（按电影配置退票时限和手续费比例） |
| `refund_info` | 全局退票时限设置 |
| `history` | 用户消费/充值历史记录表 |
| `movie_like` | 用户想看标记表 |

---

## 已知问题

详细的系统安全审查报告请参阅 [SECURITY_REVIEW_REPORT.md](./SECURITY_REVIEW_REPORT.md)。

**主要待修复项：**

1. ~~用户密码需使用 BCrypt 哈希存储~~ ✅ 已修复
2. ~~`UserVO` 中不应返回密码字段~~ ✅ 已修复
3. ~~前端管理员判断逻辑应改为根据后端 `kind` 字段判断~~ ✅ 已修复
4. 建议添加 CSRF 保护和请求速率限制

---

## 许可证

本项目仅供学习和研究使用。
