## 在线电影购票系统 - 后端

> Spring Boot 后端服务，提供 RESTful API。

### 技术栈

- Java 1.8 + Spring Boot 2.1.3
- MyBatis + MySQL 8.0
- Lombok + Spring Security Crypto (BCrypt)

### 快速启动

```bash
# 1. 初始化数据库
mysql -u root -p < sql/update.sql

# 2. 配置数据库连接（编辑 .env 文件）
DB_URL=jdbc:mysql://localhost:3306/cinema?serverTimezone=CTT&characterEncoding=UTF-8&useSSL=false
DB_USERNAME=root
DB_PASSWORD=你的密码

# 3. 编译并启动
mvn spring-boot:run
```

启动后运行在 http://localhost:8080

### 项目结构

```
movie/
├── pom.xml                          # Maven 配置
├── .env                             # 数据库环境变量
├── sql/
│   └── update.sql                   # 建表脚本 + 初始数据
└── src/main/
    ├── java/com/example/cinema/
    │   ├── CinemaApplication.java   # 启动类
    │   ├── config/                  # 配置（拦截器等）
    │   ├── controller/              # 控制器（API 入口）
    │   │   ├── user/                # 账户模块
    │   │   ├── management/          # 电影/排片/影厅管理
    │   │   ├── sales/               # 票务/退票
    │   │   ├── promotion/           # VIP/优惠券/活动
    │   │   └── statistics/          # 数据统计
    │   ├── bl/                      # 业务逻辑接口
    │   ├── blImpl/                  # 业务逻辑实现
    │   ├── data/                    # MyBatis Mapper 接口
    │   ├── po/                      # 数据库实体类
    │   ├── vo/                      # 视图对象/表单对象
    │   └── interceptor/             # 登录拦截器
    └── resources/
        ├── application.yml          # 应用配置
        ├── dataImpl/                # MyBatis XML 映射
        ├── static/                  # 静态资源
        └── templates/               # Thymeleaf 模板
```

### API 文档

完整的 API 接口文档请参阅项目根目录下的 [API.md](../API.md)
