# Movie-v2 安全修复文档

## 已修复的安全问题

### 1. RBAC权限控制 (P0 - 已修复)

**问题描述**：
- 拦截器仅验证用户是否登录，不验证角色权限
- 普通用户可以调用所有管理员接口

**修复方案**：
- 修改 `SessionInterceptor.java`，添加角色校验逻辑
- 管理员接口（`/management/`、`/admin/`、`/delete/`、`/insert/`、`/update/`）需要 `kind=1`
- 普通用户接口（`/user/`、`/ticket/`、`/payment/`、`/vip/`、`/coupon/`）需要 `kind=2`

**修改文件**：
- `movie/src/main/java/com/example/cinema/interceptor/SessionInterceptor.java`

---

### 2. 注册接口提权漏洞 (P0 - 已修复)

**问题描述**：
- 注册接口直接接受 `kind` 参数，恶意用户可传 `kind=1` 注册为管理员

**修复方案**：
- 在 `AccountController.registerAccount()` 中强制设置 `kind=2`
- 在 `AccountServiceImpl.registerAccount()` 中也强制设置 `kind=2`

**修改文件**：
- `movie/src/main/java/com/example/cinema/controller/user/AccountController.java`
- `movie/src/main/java/com/example/cinema/blImpl/user/AccountServiceImpl.java`

---

### 3. 密码哈希泄露 (P0 - 已修复)

**问题描述**：
- `/get/user` 和 `/get/all/user` 接口返回 `User PO` 对象，包含密码哈希字段

**修复方案**：
- 新增 `getUserVOById()` 和 `getAllUserVO()` 方法，返回 `UserVO` 对象
- `UserVO` 类不包含密码字段

**修改文件**：
- `movie/src/main/java/com/example/cinema/bl/user/AccountService.java` (新增接口方法)
- `movie/src/main/java/com/example/cinema/blImpl/user/AccountServiceImpl.java` (实现新方法)
- `movie/src/main/java/com/example/cinema/controller/user/AccountController.java` (使用新方法)

---

### 4. Session存储明文密码 (P0 - 已修复)

**问题描述**：
- 登录成功后将 `UserForm`（含明文密码）存入 Session

**修复方案**：
- 改为存储 `UserVO` 对象（不包含密码）

**修改文件**：
- `movie/src/main/java/com/example/cinema/controller/user/AccountController.java`

---

### 5. 缺少输入验证 (P1 - 已修复)

**问题描述**：
- 所有 Controller 均未使用 `@Valid` 注解，用户输入未验证

**修复方案**：
- 添加 `spring-boot-starter-validation` 依赖
- 在 `UserForm` 和 `HistoryItem` 上添加验证注解
- 在 Controller 方法参数上添加 `@Valid` 注解

**修改文件**：
- `movie/pom.xml` (添加依赖)
- `movie/src/main/java/com/example/cinema/vo/UserForm.java` (添加验证注解)
- `movie/src/main/java/com/example/cinema/controller/user/AccountController.java` (添加 @Valid)

---

### 6. 异常处理改进 (P2 - 已修复)

**问题描述**：
- 多处使用 `e.printStackTrace()`，应使用 Logger

**修复方案**：
- 将所有 `e.printStackTrace()` 替换为 `logger.error()`

**修改文件**：
- `movie/src/main/java/com/example/cinema/blImpl/user/AccountServiceImpl.java`

---

## 新增功能

### 1. 单元测试

**新增文件**：
- `movie/src/test/java/com/example/cinema/AccountServiceTest.java`

**测试内容**：
- 测试注册时强制设置 `kind=2`
- 测试获取用户VO不包含密码
- 测试登录成功和失败场景

---

## 配置说明

### 环境变量

项目使用 `.env` 文件配置数据库连接：

```properties
DB_URL=jdbc:mysql://localhost:3306/cinema?serverTimezone=CTT&characterEncoding=UTF-8&useSSL=false
DB_USERNAME=root
DB_PASSWORD=你的数据库密码
```

### 安全最佳实践

1. **不要将 `.env` 文件提交到版本库**
2. **生产环境使用强密码**
3. **定期更新依赖版本**
4. **启用 HTTPS**
5. **配置 CORS 白名单**

---

## 待修复问题（P2优先级）

1. **Spring Boot 版本升级**：当前使用 2.1.3，建议升级到 3.x
2. **依赖版本更新**：commons-fileupload、commons-io、commons-codec 版本较旧
3. **BigDecimal 金额计算**：当前使用 `double`，建议改为 `BigDecimal`
4. **N+1 查询优化**：`TicketServiceImpl` 中存在 N+1 查询问题
5. **分页支持**：部分接口缺少分页功能

---

## 测试指南

### 运行单元测试

```bash
cd movie
mvn test
```

### 测试安全修复

1. 注册普通用户，验证 `kind` 是否为 2
2. 尝试用普通用户调用管理员接口，验证是否返回 403
3. 调用 `/get/user` 接口，验证返回的 `UserVO` 不包含密码字段

---

## 联系方式

如有安全问题，请提交 Issue 或联系项目维护者。