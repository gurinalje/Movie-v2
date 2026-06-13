# 电影购票系统安全审查报告

**项目名称：** 在线电影购票系统  
**审查日期：** 2026-06-13  
**审查范围：** 后端（Java Spring Boot）+ 前端（Vue 3）  
**风险等级：** 🔴 高风险

---

## 摘要

本次安全审查发现多个严重安全漏洞，包括硬编码敏感信息、密码明文存储、缺乏输入验证、敏感信息泄露等。系统存在被攻击者利用的重大风险，建议立即修复所有发现的问题。

**漏洞统计：**
- 🔴 **严重（Critical）：** 3个
- 🟠 **高危（High）：** 4个
- 🟡 **中危（Medium）：** 5个
- 🟢 **低危（Low）：** 3个

---

## 严重漏洞（Critical）

### 1. 数据库密码硬编码在配置文件中
**严重程度：** 🔴 严重  
**影响范围：** 数据库安全  
**位置：** `movie/src/main/resources/application.yml:5`

**问题描述：**
数据库密码以明文形式硬编码在配置文件中，任何能够访问源代码的人都能获取数据库凭据。

**问题代码：**
```yaml
spring:
  datasource:
     url: jdbc:mysql://localhost:3306/cinema?serverTimezone=CTT&characterEncoding=UTF-8&useSSL=false
     username: root
     password: 123456  # ❌ 硬编码密码
     driver-class-name: com.mysql.cj.jdbc.Driver
```

**修复建议：**
1. 将敏感信息移至环境变量
2. 使用 `.env` 文件（确保添加到 `.gitignore`）
3. 使用 Spring Cloud Config 或 Vault 等密钥管理服务

**修复代码：**
```yaml
spring:
  datasource:
     url: ${DB_URL:jdbc:mysql://localhost:3306/cinema?serverTimezone=CTT&characterEncoding=UTF-8&useSSL=false}
     username: ${DB_USERNAME:root}
     password: ${DB_PASSWORD}
     driver-class-name: com.mysql.cj.jdbc.Driver
```

---

### 2. 用户密码明文存储
**严重程度：** 🔴 严重  
**影响范围：** 用户账户安全  
**位置：** `movie/src/main/java/com/example/cinema/blImpl/user/AccountServiceImpl.java:28,39`

**问题描述：**
用户密码在数据库中以明文形式存储，没有使用任何哈希算法。一旦数据库被泄露，所有用户密码将直接暴露。

**问题代码：**
```java
// 注册时直接存储明文密码
accountMapper.createNewAccount(userForm.getUsername(), userForm.getPassword(), userForm.getKind());

// 登录时明文比较密码
if (null == user || !user.getPassword().equals(userForm.getPassword())) {
    return null;
}
```

**修复建议：**
1. 使用 BCrypt 或 Argon2 算法对密码进行哈希处理
2. 登录时验证哈希后的密码

**修复代码：**
```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class AccountServiceImpl implements AccountService {
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Override
    public ResponseVO registerAccount(UserForm userForm) {
        try {
            // 对密码进行哈希处理
            String hashedPassword = passwordEncoder.encode(userForm.getPassword());
            accountMapper.createNewAccount(userForm.getUsername(), hashedPassword, userForm.getKind());
        } catch (Exception e) {
            return ResponseVO.buildFailure(ACCOUNT_EXIST);
        }
        return ResponseVO.buildSuccess();
    }
    
    @Override
    public UserVO login(UserForm userForm) {
        User user = accountMapper.getAccountByName(userForm.getUsername());
        // 验证哈希密码
        if (null == user || !passwordEncoder.matches(userForm.getPassword(), user.getPassword())) {
            return null;
        }
        return new UserVO(user);
    }
}
```

---

### 3. 敏感信息泄露（密码返回给前端）
**严重程度：** 🔴 严重  
**影响范围：** 用户隐私安全  
**位置：** `movie/src/main/java/com/example/cinema/vo/UserVO.java:12-19`

**问题描述：**
`UserVO` 类包含密码字段，登录成功后将密码返回给前端。这可能导致密码被记录在浏览器历史、网络日志或前端存储中。

**问题代码：**
```java
public class UserVO {
    private Integer id;
    private String username;
    private String password;  // ❌ 密码字段
    private Integer kind;
    
    public UserVO(User user){
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();  // ❌ 返回密码
        this.kind = user.getKind();
    }
}
```

**修复建议：**
1. 从 `UserVO` 中移除密码字段
2. 确保 API 响应中不包含敏感信息

**修复代码：**
```java
public class UserVO {
    private Integer id;
    private String username;
    // 移除 password 字段
    private Integer kind;
    
    public UserVO(User user){
        this.id = user.getId();
        this.username = user.getUsername();
        this.kind = user.getKind();
    }
}
```

---

## 高危漏洞（High）

### 4. 缺乏 CSRF 保护
**严重程度：** 🟠 高危  
**影响范围：** 跨站请求伪造攻击  
**位置：** 所有 POST 端点

**问题描述：**
系统没有实现 CSRF 保护机制，攻击者可以构造恶意页面，诱使用户在已登录状态下执行非预期操作（如购票、修改信息等）。

**修复建议：**
1. 添加 Spring Security 依赖
2. 启用 CSRF 保护
3. 前端在请求中携带 CSRF Token

---

### 5. 缺乏速率限制
**严重程度：** 🟠 高危  
**影响范围：** 暴力破解、拒绝服务  
**位置：** `/login`、`/register` 等端点

**问题描述：**
登录和注册接口没有速率限制，攻击者可以进行暴力破解攻击或大量注册垃圾账户。

**修复建议：**
1. 实现基于 IP 和用户 的速率限制
2. 对登录失败进行账户锁定机制

**修复代码示例：**
```java
import com.bucket4j.core.Bandwidth;
import com.bucket4j.core.Bucket;
import java.time.Duration;

@RestController
public class AccountController {
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    
    private Bucket createNewBucket() {
        return Bucket.builder()
            .addLimit(Bandwidth.simple(5, Duration.ofMinutes(1))) // 每分钟最多5次请求
            .build();
    }
    
    @PostMapping("/login")
    public ResponseVO login(@RequestBody UserForm userForm, 
                           @RequestParam String clientIp) {
        Bucket bucket = buckets.computeIfAbsent(clientIp, k -> createNewBucket());
        
        if (!bucket.tryConsume(1)) {
            return ResponseVO.buildFailure("请求过于频繁，请稍后再试");
        }
        // ... 登录逻辑
    }
}
```

---

### 6. 会话管理不安全
**严重程度：** 🟠 高危  
**影响范围：** 会话劫持  
**位置：** `movie/src/main/java/com/example/cinema/controller/user/AccountController.java:32`

**问题描述：**
使用简单的 HTTP Session 存储用户信息，没有设置安全属性（如 HttpOnly、Secure、SameSite），容易被 XSS 或中间人攻击窃取。

**问题代码：**
```java
session.setAttribute(InterceptorConfiguration.SESSION_KEY, userForm);
```

**修复建议：**
1. 使用 JWT 令牌替代 Session
2. 如果必须使用 Session，设置安全属性
3. 实现会话超时机制

---

### 7. 前端硬编码管理员判断逻辑
**严重程度：** 🟠 高危  
**影响范围：** 权限绕过  
**位置：** `cinema-ui/src/views/Login.vue:50`

**问题描述：**
前端通过硬编码的用户名 `'root'` 来判断是否跳转到管理员页面，任何人都可以修改前端代码来绕过权限控制。

**问题代码：**
```javascript
if (loginForm.username === 'root') router.push('/admin')
```

**修复建议：**
1. 权限判断应该在后端进行
2. 前端根据后端返回的角色信息决定跳转
3. 前端路由添加导航守卫

---

## 中危漏洞（Medium）

### 8. 缺乏输入验证
**严重程度：** 🟡 中危  
**影响范围：** 数据完整性  
**位置：** 多个控制器

**问题描述：**
用户输入没有经过验证，可能导致数据异常或潜在的注入攻击。虽然 MyBatis 使用参数化查询防止了 SQL 注入，但其他类型的输入问题仍然存在。

**修复建议：**
1. 使用 `@Valid` 注解和 JSR 303 验证
2. 定义验证规则（长度、格式、范围等）

**修复代码示例：**
```java
public class UserForm {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度必须在6-100之间")
    private String password;
}
```

---

### 9. 敏感数据存储在 localStorage
**严重程度：** 🟡 中危  
**影响范围：** XSS 攻击下的数据泄露  
**位置：** `cinema-ui/src/views/Login.vue:49`

**问题描述：**
用户信息存储在 `localStorage` 中，如果存在 XSS 漏洞，攻击者可以窃取这些信息。

**问题代码：**
```javascript
localStorage.setItem('user', JSON.stringify(res.data.content))
```

**修复建议：**
1. 使用 httpOnly Cookie 存储认证信息
2. 如果必须使用 localStorage，确保内容不包含敏感信息

---

### 10. 数据库连接未启用 SSL
**严重程度：** 🟡 中危  
**影响范围：** 数据传输安全  
**位置：** `movie/src/main/resources/application.yml:3`

**问题描述：**
数据库连接使用 `useSSL=false`，数据在传输过程中未加密，可能被中间人窃听。

**修复建议：**
1. 在生产环境中启用 SSL 连接
2. 配置正确的 SSL 证书

---

### 11. 过时的依赖版本
**严重程度：** 🟡 中危  
**影响范围：** 已知漏洞利用  
**位置：** `movie/pom.xml`

**问题描述：**
使用过时的依赖版本，可能包含已知的安全漏洞：
- Spring Boot 2.1.3.RELEASE（2019年发布）
- commons-fileupload 1.3.3（2017年发布）
- commons-io 2.4（2013年发布）
- commons-codec 1.9（2014年发布）

**修复建议：**
1. 升级到最新稳定版本
2. 使用 Dependabot 自动检查依赖更新

---

### 12. 错误处理暴露内部信息
**严重程度：** 🟡 中危  
**影响范围：** 信息泄露  
**位置：** 多个 Service 实现类

**问题描述：**
大量使用 `e.printStackTrace()`，将堆栈跟踪输出到日志，可能暴露系统内部结构和代码逻辑。

**修复建议：**
1. 使用日志框架（如 SLF4J + Logback）
2. 只记录必要信息，避免敏感数据泄露
3. 为用户返回通用错误信息

---

## 低危漏洞（Low）

### 13. 缺乏安全响应头
**严重程度：** 🟢 低危  
**影响范围：** XSS、点击劫持等  
**位置：** 应用配置

**问题描述：**
没有配置安全响应头，如 Content-Security-Policy、X-Frame-Options、X-Content-Type-Options 等。

**修复建议：**
配置安全响应头：
```java
@Configuration
public class SecurityHeaderConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor((request, response, handler) -> {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setHeader("X-Content-Type-Options", "nosniff");
            httpResponse.setHeader("X-Frame-Options", "DENY");
            httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
            httpResponse.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
            return true;
        });
    }
}
```

---

### 14. 缺乏前端路由守卫
**严重程度：** 🟢 低危  
**影响范围：** 未授权访问  
**位置：** `cinema-ui/src/router/index.js`

**问题描述：**
前端路由没有导航守卫，用户可以直接通过 URL 访问受保护的页面，尽管后端有拦截器，但用户体验不佳。

**修复建议：**
添加路由守卫：
```javascript
router.beforeEach((to, from, next) => {
  const user = JSON.parse(localStorage.getItem('user'))
  
  if (to.meta.requiresAuth && !user) {
    next('/login')
  } else if (to.meta.adminOnly && user?.kind !== 0) {
    next('/home')
  } else {
    next()
  }
})
```

---

### 15. 日志中可能包含敏感信息
**严重程度：** 🟢 低危  
**影响范围：** 数据泄露  
**位置：** 多个 Service 实现类

**问题描述：**
部分注释掉的代码中可以看到曾尝试打印用户信息（如密码），虽然已被注释，但表明开发过程中可能存在敏感信息记录的风险。

**修复建议：**
1. 使用日志脱敏工具
2. 建立代码审查规范，禁止记录敏感信息

---

## 安全修复优先级

### 第一阶段（立即修复）- 1-2天
1. ✅ 将数据库密码移至环境变量
2. ✅ 实现密码哈希存储
3. ✅ 从 UserVO 移除密码字段
4. ✅ 升级过时的依赖

### 第二阶段（一周内）- 安全加固
5. ⬜ 添加 Spring Security 依赖
6. ⬜ 实现 CSRF 保护
7. ⬜ 实现速率限制
8. ⬜ 添加输入验证

### 第三阶段（两周内）- 安全加固
9. ⬜ 实现 JWT 认证
10. ⬜ 配置安全响应头
11. ⬜ 添加前端路由守卫
12. ⬜ 改进错误处理和日志

---

## 附录：快速修复代码

### 修复 application.yml
```yaml
spring:
  datasource:
     url: ${DB_URL:jdbc:mysql://localhost:3306/cinema?serverTimezone=CTT&characterEncoding=UTF-8&useSSL=true}
     username: ${DB_USERNAME:root}
     password: ${DB_PASSWORD:changeme}
     driver-class-name: com.mysql.cj.jdbc.Driver
     max-active: 200
     max-idle: 20
     min-idle: 10
```

### 添加 Spring Security 依赖（pom.xml）
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

### 添加输入验证依赖（pom.xml）
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

---

## 结论

本系统存在多个严重和高危安全漏洞，需要立即进行修复。特别是密码明文存储和敏感信息泄露问题，可能直接导致用户数据被窃取。建议按照修复优先级逐步实施安全加固措施，并在每次修复后进行测试验证。

**建议：** 在完成所有安全修复之前，不要将系统部署到生产环境。

---

*报告生成工具：OpenCode Security Review*  
*审查日期：2026-06-13*