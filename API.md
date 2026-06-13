# API 接口文档

**项目名称：** 在线电影购票系统  
**基础地址：** `http://localhost:8080`  
**最后更新：** 2026-06-13

---

## 目录

- [通用说明](#通用说明)
- [1. 账户模块（Account）](#1-账户模块account)
- [2. 电影模块（Movie）](#2-电影模块movie)
- [3. 排片模块（Schedule）](#3-排片模块schedule)
- [4. 影厅模块（Hall）](#4-影厅模块hall)
- [5. 票务模块（Ticket）](#5-票务模块ticket)
- [6. 退票模块（Refund）](#6-退票模块refund)
- [7. VIP 会员模块（VIP）](#7-vip-会员模块vip)
- [8. 优惠券模块（Coupon）](#8-优惠券模块coupon)
- [9. 促销活动模块（Activity）](#9-促销活动模块activity)
- [10. 数据统计模块（Statistics）](#10-数据统计模块statistics)

---

## 通用说明

### 统一响应格式

所有接口返回统一的 JSON 格式：

```json
{
  "success": true,
  "message": null,
  "content": { ... }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `success` | Boolean | 请求是否成功 |
| `message` | String | 失败时的错误提示信息 |
| `content` | Object | 成功时返回的数据 |

### 认证机制

系统基于 HTTP Session 进行身份认证。登录成功后服务端写入 Session，后续请求需携带 Cookie（浏览器自动处理）。未登录访问受保护接口将返回：

```json
HTTP 401 Unauthorized
{
  "success": false,
  "message": "未登录或登录已过期，请重新登录！"
}
```

### 用户类型（kind）

| 值 | 含义 |
|----|------|
| 1 | 管理员 |
| 2 | 普通用户 |

### 订单状态（state）

| 值 | 含义 |
|----|------|
| 0 | 未支付 |
| 1 | 已支付（已完成） |
| 2 | 已失效 |

### 电影状态（status）

| 值 | 含义 |
|----|------|
| 0 | 上架 |
| 1 | 下架 |

### 影厅状态（status）

| 值 | 含义 |
|----|------|
| 1 | 正常营业 |
| 0 | 设备维护 |

---

## 1. 账户模块（Account）

### 1.1 用户登录

**POST** `/login`

**请求体：**

```json
{
  "username": "root",
  "password": "123456"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |

**成功响应：**

```json
{
  "success": true,
  "message": null,
  "content": {
    "id": 8,
    "username": "root",
    "kind": 1
  }
}
```

**失败响应：**

```json
{
  "success": false,
  "message": "用户名或密码错误",
  "content": null
}
```

---

### 1.2 用户注册

**POST** `/register`

**请求体：**

```json
{
  "username": "newuser",
  "password": "mypassword",
  "kind": 2
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名（不可重复） |
| password | String | 是 | 密码 |
| kind | int | 是 | 用户类型（2=普通用户） |

**成功响应：**

```json
{
  "success": true,
  "message": null,
  "content": null
}
```

**失败响应：**

```json
{
  "success": false,
  "message": "该用户已存在",
  "content": null
}
```

---

### 1.3 用户登出

**POST** `/logout`

无需请求参数。清除服务端 Session。

**响应：**

```json
{
  "success": true,
  "message": null,
  "content": "登出成功"
}
```

---

### 1.4 获取用户信息

**GET** `/get/user?userId={userId}`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | int | 是 | 用户 ID |

**响应：**

```json
{
  "success": true,
  "content": {
    "id": 9,
    "username": "faker",
    "kind": 2
  }
}
```

---

### 1.5 获取所有用户

**GET** `/get/all/user`

**响应：**

```json
{
  "success": true,
  "content": [
    { "id": 8, "username": "root", "kind": 1 },
    { "id": 9, "username": "faker", "kind": 2 }
  ]
}
```

---

### 1.6 更新用户信息

**POST** `/update/user`

**请求体：**

```json
{
  "id": 9,
  "username": "faker",
  "password": "newpassword",
  "kind": 2
}
```

---

### 1.7 删除用户

**POST** `/delete/user?userId={userId}`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | int | 是 | 用户 ID |

---

### 1.8 获取消费历史

**GET** `/get/history?userId={userId}`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | int | 是 | 用户 ID |

**响应：**

```json
{
  "success": true,
  "content": [
    {
      "id": 1,
      "userId": 9,
      "kind": 2,
      "money": -40,
      "time": "2026-03-31T17:10:54",
      "description": "VIP卡购买《满江红》"
    }
  ]
}
```

**history.kind 说明：**

| 值 | 含义 |
|----|------|
| 0 | 购买会员卡 |
| 1 | 会员卡充值 |
| 2 | 购买电影票 |

---

### 1.9 插入消费记录

**POST** `/insert/history`

**请求体：**

```json
{
  "userId": 9,
  "kind": 2,
  "money": -40,
  "description": "VIP卡购买《深海》"
}
```

---

## 2. 电影模块（Movie）

### 2.1 获取所有电影（含已下架）

**GET** `/movie/all`

**响应：**

```json
{
  "success": true,
  "content": [
    {
      "id": 1,
      "name": "阿凡达：水之道",
      "posterUrl": "/images/avatar.jpg",
      "director": "詹姆斯·卡梅隆",
      "screenWriter": "詹姆斯·卡梅隆",
      "starring": "萨姆·沃辛顿 / 佐伊·索尔达娜",
      "type": "科幻/冒险",
      "country": "美国",
      "language": "英语",
      "startDate": "2026-02-01T00:00:00",
      "length": 192,
      "description": "影片设定在《阿凡达》的剧情落幕十余年后...",
      "status": 0,
      "islike": 0,
      "likeCount": 1
    }
  ]
}
```

---

### 2.2 获取所有上架电影

**GET** `/movie/all/exclude/off`

返回结果中不包括已下架的电影。响应格式同 2.1。

---

### 2.3 获取单个电影详情

**GET** `/movie/{id}/{userId}`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | int | 是 | 电影 ID（路径参数） |
| userId | int | 是 | 用户 ID（路径参数） |

**响应：** 返回包含该用户是否标记想看（`islike`）的电影详情对象。

---

### 2.4 搜索电影

**GET** `/movie/search?keyword={keyword}`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | String | 是 | 搜索关键词（按电影名称模糊匹配） |

---

### 2.5 添加电影

**POST** `/movie/add`

**请求体：**

```json
{
  "name": "封神第一部",
  "posterUrl": "/images/fs.jpg",
  "director": "乌尔善",
  "screenWriter": "冉平 / 冉甲男",
  "starring": "费翔 / 李雪健 / 黄渤",
  "type": "动作 / 战争 / 奇幻",
  "country": "中国大陆",
  "language": "汉语普通话",
  "startDate": "2026-07-20T08:00:00",
  "length": 148,
  "description": "商王殷寿与狐妖妲己勾结，暴虐无道..."
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | String | 是 | 电影名称 |
| posterUrl | String | 否 | 海报图片路径 |
| director | String | 否 | 导演 |
| screenWriter | String | 否 | 编剧 |
| starring | String | 否 | 主演 |
| type | String | 否 | 类型 |
| country | String | 否 | 制片国家/地区 |
| language | String | 否 | 语言 |
| startDate | Date | 否 | 上映时间 |
| length | Integer | 是 | 片长（分钟） |
| description | String | 否 | 简介 |

---

### 2.6 更新电影

**POST** `/movie/update`

请求体同 2.5，需额外提供 `id` 字段指定要更新的电影。

```json
{
  "id": 1,
  "name": "阿凡达：水之道",
  "director": "詹姆斯·卡梅隆",
  ...
}
```

---

### 2.7 批量下架电影

**POST** `/movie/off/batch`

**请求体：**

```json
{
  "movieIdList": [4, 7]
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| movieIdList | List\<Integer\> | 是 | 要下架的电影 ID 列表 |

---

### 2.8 标记想看

**POST** `/movie/{movieId}/like?userId={userId}`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| movieId | int | 是 | 电影 ID（路径参数） |
| userId | int | 是 | 用户 ID（查询参数） |

---

### 2.9 取消想看

**POST** `/movie/{movieId}/unlike?userId={userId}`

参数同 2.8。

---

### 2.10 获取想看人数

**GET** `/movie/{movieId}/like/count`

**响应：**

```json
{
  "success": true,
  "content": 5
}
```

---

### 2.11 获取想看人数趋势（按日期）

**GET** `/movie/{movieId}/like/date`

**响应：**

```json
{
  "success": true,
  "content": [
    { "likeNum": 3, "likeTime": "2026-03-01" },
    { "likeNum": 2, "likeTime": "2026-03-05" }
  ]
}
```

---

## 3. 排片模块（Schedule）

### 3.1 添加排片

**POST** `/schedule/add`

**请求体：**

```json
{
  "hallId": 6,
  "movieId": 2,
  "startTime": "2026-04-01T10:00:00",
  "endTime": "2026-04-01T12:53:00",
  "fare": 45.0
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| hallId | Integer | 是 | 影厅 ID |
| movieId | Integer | 是 | 电影 ID |
| startTime | Date | 是 | 开始放映时间 |
| endTime | Date | 是 | 结束放映时间 |
| fare | Double | 是 | 票价（元） |

---

### 3.2 更新排片

**POST** `/schedule/update`

请求体同 3.1，需额外提供 `id` 字段。

---

### 3.3 搜索排片（管理员用）

**GET** `/schedule/search?hallId={hallId}&startDate={startDate}`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| hallId | int | 是 | 影厅 ID |
| startDate | Date | 是 | 起始日期（格式：`yyyy/MM/dd`，如 `2026/04/01`） |

返回该影厅从 startDate 开始 7 天内的排片列表。

**响应：**

```json
{
  "success": true,
  "content": [
    {
      "id": 1,
      "hallId": 6,
      "hallName": "1号激光IMAX厅",
      "movieId": 2,
      "movieName": "流浪地球2",
      "startTime": "2026-04-01T10:00:00",
      "endTime": "2026-04-01T12:53:00",
      "fare": 45.0
    }
  ]
}
```

---

### 3.4 搜索排片（观众用）

**GET** `/schedule/search/audience?movieId={movieId}`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| movieId | int | 是 | 电影 ID |

返回该电影所有未来的排片场次。

---

### 3.5 获取排片详情

**GET** `/schedule/{id}`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | int | 是 | 排片 ID |

---

### 3.6 批量删除排片

**DELETE** `/schedule/delete/batch`

**请求体：**

```json
{
  "scheduleIdList": [1, 2, 3]
}
```

---

### 3.7 设置排片可见天数

**POST** `/schedule/view/set`

**请求体：**

```json
{
  "day": 7
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| day | int | 是 | 允许观众看到未来几天的排片 |

---

### 3.8 获取排片可见天数

**GET** `/schedule/view`

**响应：**

```json
{
  "success": true,
  "content": { "day": 7 }
}
```

---

## 4. 影厅模块（Hall）

### 4.1 获取所有影厅

**GET** `/hall/all`

**响应：**

```json
{
  "success": true,
  "content": [
    {
      "id": 6,
      "name": "1号激光IMAX厅",
      "row": 8,
      "column": 10,
      "status": 1
    },
    {
      "id": 2,
      "name": "2号 VIP 情侣厅",
      "row": 6,
      "column": 8,
      "status": 1
    }
  ]
}
```

---

### 4.2 添加影厅

**POST** `/hall/add`

**请求体：**

```json
{
  "name": "4号杜比影院",
  "row": 10,
  "column": 12,
  "status": 1
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | String | 是 | 影厅名称 |
| row | Integer | 是 | 座位行数 |
| column | Integer | 是 | 座位列数 |
| status | Integer | 否 | 营业状态（1=正常, 0=维护），默认 1 |

---

### 4.3 更新影厅

**POST** `/hall/update`

请求体同 4.2，需提供 `id` 字段。

---

### 4.4 删除影厅

**POST** `/hall/delete?id={id}`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | int | 是 | 影厅 ID |

---

## 5. 票务模块（Ticket）

### 5.1 锁定座位（选座下单）

**POST** `/ticket/lockSeat`

**请求体：**

```json
{
  "userId": 9,
  "scheduleId": 15,
  "seats": [
    { "rowIndex": 0, "columnIndex": 0 },
    { "rowIndex": 0, "columnIndex": 1 }
  ]
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | int | 是 | 用户 ID |
| scheduleId | int | 是 | 排片 ID |
| seats | List | 是 | 座位列表（rowIndex=行号, columnIndex=列号） |

锁定座位后订单状态为 **未支付（state=0）**。

---

### 5.2 购票支付（银行卡）

**POST** `/ticket/buy?ticketId={ticketId}&couponId={couponId}&userId={userId}`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ticketId | List\<Integer\> | 是 | 订单 ID 列表（支持同时支付多张） |
| couponId | int | 是 | 优惠券 ID（不使用则传 0） |
| userId | int | 否 | 用户 ID（默认 0） |

支付成功后订单状态变为 **已支付（state=1）**。

---

### 5.3 VIP 购票

**POST** `/ticket/vip/buy?ticketId={ticketId}&couponId={couponId}&userId={userId}`

参数同 5.2。使用 VIP 会员卡余额支付。

---

### 5.4 获取用户订单

**GET** `/ticket/get/{userId}`

**响应：**

```json
{
  "success": true,
  "content": [
    {
      "id": 47,
      "userId": 9,
      "scheduleId": 15,
      "rowIndex": 0,
      "columnIndex": 7,
      "state": "已完成",
      "time": "2026-03-31T17:10:52"
    }
  ]
}
```

---

### 5.5 获取所有订单

**GET** `/ticket/get/all`

---

### 5.6 获取已占座位

**GET** `/ticket/get/occupiedSeats?scheduleId={scheduleId}`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| scheduleId | int | 是 | 排片 ID |

**响应：** 返回该场次已锁定的座位列表（rowIndex, columnIndex）。

---

### 5.7 取消订单

**POST** `/ticket/cancel?ticketId={ticketId}`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ticketId | List\<Integer\> | 是 | 要取消的订单 ID 列表 |

---

### 5.8 获取退票时限设置

**GET** `/ticket/get/refundInfo`

**响应：**

```json
{
  "success": true,
  "content": { "id": 1, "limitTime": 4 }
}
```

`limitTime` 表示放映前多少小时内允许退票。

---

### 5.9 更新退票时限

**POST** `/ticket/update/refundInfo?limitTime={limitTime}`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| limitTime | int | 是 | 新的退票时限（小时） |

---

## 6. 退票模块（Refund）

### 6.1 退票

**POST** `/refund?ticketId={ticketId}`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ticketId | int | 是 | 要退的订单 ID |

退票成功后订单状态变为 **已失效（state=2）**，退款金额按退票策略的手续费比例计算。

---

### 6.2 获取所有退票策略

**GET** `/get/all/refund`

**响应：**

```json
{
  "success": true,
  "content": [
    {
      "id": 1,
      "movieId": 2,
      "movieName": "流浪地球2",
      "timeBefore": 5,
      "startTime": "2026-03-31T15:17:09",
      "endTime": "2026-04-01T00:00:00",
      "rate": 0.2
    }
  ]
}
```

| 字段 | 说明 |
|------|------|
| movieId | 关联的电影 ID |
| movieName | 电影名称 |
| timeBefore | 允许退票的时间限制（小时） |
| rate | 手续费比例（如 0.2 表示收取 20% 手续费） |

---

### 6.3 添加退票策略

**POST** `/add/refund`

**请求体：**

```json
{
  "movieId": 5,
  "movieName": "满江红",
  "timeBefore": 24,
  "rate": 0.1,
  "startTime": "2026-04-01T00:00:00",
  "endTime": "2026-04-30T23:59:59"
}
```

---

### 6.4 更新退票策略

**POST** `/update/refund`

请求体同 6.3，需提供 `id` 字段。

---

### 6.5 删除退票策略

**POST** `/delete/refund?target_id={target_id}`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| target_id | int | 是 | 退票策略 ID |

---

## 7. VIP 会员模块（VIP）

### 7.1 开通 VIP 会员卡

**POST** `/vip/add?userId={userId}`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | int | 是 | 用户 ID |

办卡费用：25 元。

---

### 7.2 获取用户 VIP 卡信息

**GET** `/vip/{userId}/get`

**响应：**

```json
{
  "success": true,
  "content": {
    "id": 1,
    "userId": 9,
    "balance": 1560.0,
    "joinDate": "2026-03-01T10:00:00",
    "name": "VIP用户",
    "total": 2325.0
  }
}
```

| 字段 | 说明 |
|------|------|
| balance | 当前余额 |
| total | 累计消费金额 |

---

### 7.3 VIP 充值

**POST** `/vip/charge`

**请求体：**

```json
{
  "vipId": 1,
  "amount": 200,
  "vipStrategyId": 6
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| vipId | int | 是 | VIP 卡 ID |
| amount | int | 是 | 充值金额（元） |
| vipStrategyId | int | 是 | 充值策略 ID（对应满赠规则） |

充值到账金额 = 充值金额 + 赠送金额。

---

### 7.4 获取所有 VIP 策略

**GET** `/vip/get/all/strategy`

**响应：**

```json
{
  "success": true,
  "content": [
    { "id": 5, "chargeLimit": 100, "giftAmount": 20 },
    { "id": 6, "chargeLimit": 200, "giftAmount": 50 },
    { "id": 7, "chargeLimit": 300, "giftAmount": 100 }
  ]
}
```

规则说明：充值满 100 赠 20，满 200 赠 50，满 300 赠 100。

---

### 7.5 添加 VIP 策略

**POST** `/vip/add/strategy?chargeLimit={chargeLimit}&giftAmount={giftAmount}`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| chargeLimit | int | 是 | 充值门槛金额 |
| giftAmount | int | 是 | 赠送金额 |

---

### 7.6 更新 VIP 策略

**POST** `/vip/update/strategy?id={id}&chargeLimit={chargeLimit}&giftAmount={giftAmount}`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | int | 是 | 策略 ID |
| chargeLimit | int | 是 | 新的充值门槛 |
| giftAmount | int | 是 | 新的赠送金额 |

---

### 7.7 删除 VIP 策略

**POST** `/vip/delete/strategy?id={id}`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | int | 是 | 策略 ID |

---

### 7.8 按充值金额查询可获得的 VIP 策略

**GET** `/vip/get/by/money?target={target}`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| target | int | 是 | 充值金额 |

返回满足门槛的 VIP 策略列表。

---

### 7.9 VIP 模块插入消费记录

**POST** `/vip/insert/history`

请求体格式同 [1.9 插入消费记录](#19-插入消费记录)。

---

## 8. 优惠券模块（Coupon）

### 8.1 获取用户优惠券

**GET** `/coupon/{userId}/get`

**响应：**

```json
{
  "success": true,
  "content": [
    {
      "id": 18,
      "name": "庆祝blg夺冠专属券",
      "description": "全场五折",
      "targetAmount": 50,
      "discountAmount": 25,
      "startTime": "2026-03-31T19:00:00",
      "endTime": "2026-04-02T00:00:00"
    }
  ]
}
```

| 字段 | 说明 |
|------|------|
| targetAmount | 使用门槛（消费满 X 元可用） |
| discountAmount | 优惠金额（减免 X 元） |

---

### 8.2 获取所有优惠券模板

**GET** `/coupon/get/all`

---

### 8.3 发放优惠券

**POST** `/coupon/send?couponId={couponId}&userId={userId}`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| couponId | int | 是 | 优惠券模板 ID |
| userId | int[] | 是 | 接收优惠券的用户 ID 数组 |

示例请求：

```
POST /coupon/send?couponId=18&userId=9&userId=10
```

---

## 9. 促销活动模块（Activity）

### 9.1 发布活动

**POST** `/activity/publish`

**请求体：**

```json
{
  "name": "庆祝blg夺冠",
  "description": "庆祝blg夺冠",
  "startTime": "2026-03-31T19:00:00",
  "endTime": "2026-04-02T00:00:00",
  "movieList": [1, 2, 3],
  "couponForm": {
    "name": "庆祝blg夺冠专属券",
    "description": "全场五折",
    "targetAmount": 50,
    "discountAmount": 25,
    "startTime": "2026-03-31T19:00:00",
    "endTime": "2026-04-02T00:00:00"
  }
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | String | 是 | 活动名称 |
| description | String | 是 | 活动描述 |
| startTime | Timestamp | 是 | 活动开始时间 |
| endTime | Timestamp | 是 | 活动结束时间 |
| movieList | List\<Integer\> | 否 | 参与活动的电影 ID 列表 |
| couponForm | CouponForm | 否 | 关联的优惠券模板 |

**couponForm 结构：**

| 字段 | 类型 | 说明 |
|------|------|------|
| name | String | 优惠券名称 |
| description | String | 优惠券描述 |
| targetAmount | double | 使用门槛 |
| discountAmount | double | 优惠金额 |
| startTime | Timestamp | 可用开始时间 |
| endTime | Timestamp | 可用结束时间 |

---

### 9.2 获取所有活动

**GET** `/activity/get`

**响应：**

```json
{
  "success": true,
  "content": [
    {
      "id": 20,
      "name": "庆祝blg夺冠",
      "description": "庆祝blg夺冠",
      "startTime": "2026-03-31T19:00:00",
      "endTime": "2026-04-02T00:00:00",
      "movieList": [...],
      "coupon": { ... }
    }
  ]
}
```

---

### 9.3 删除（下线）活动

**POST** `/activity/delete?activityId={activityId}`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| activityId | int | 是 | 活动 ID |

---

## 10. 数据统计模块（Statistics）

### 10.1 票房排行

**GET** `/statistics/boxOffice/total`

**响应：**

```json
{
  "success": true,
  "content": [
    { "movieId": 2, "name": "流浪地球2", "boxOffice": 1200 },
    { "movieId": 1, "name": "阿凡达：水之道", "boxOffice": 800 }
  ]
}
```

---

### 10.2 观众消费统计

**GET** `/statistics/audiencePrice`

**响应：**

```json
{
  "success": true,
  "content": [
    { "userId": 9, "totalPrice": 560.0 },
    { "userId": 12, "totalPrice": 320.0 }
  ]
}
```

---

### 10.3 排片统计

**GET** `/statistics/scheduleTime?date={date}`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| date | String | 是 | 日期（格式：`yyyy-MM-dd`，如 `2026-03-31`） |

**响应：**

```json
{
  "success": true,
  "content": [
    { "movieId": 2, "name": "流浪地球2", "time": 5 },
    { "movieId": 5, "name": "满江红", "time": 3 }
  ]
}
```

`time` 表示该日期的排片次数。

---

### 10.4 热门想看排行

**GET** `/statistics/popular/stars`

**响应：**

```json
{
  "success": true,
  "content": [
    { "movieId": 6, "name": "深海", "likeCount": 2 },
    { "movieId": 2, "name": "流浪地球2", "likeCount": 2 }
  ]
}
```

---

### 10.5 综合统计（大屏数据）

**GET** `/statistics/comprehensive`

返回综合统计数据，用于管理后台数据大屏展示。包含票房、观众消费、排片、想看等多维度汇总数据。

---

## 附录：接口汇总表

| 模块 | 方法 | 路径 | 说明 |
|------|------|------|------|
| **账户** | POST | `/login` | 登录 |
| | POST | `/register` | 注册 |
| | POST | `/logout` | 登出 |
| | GET | `/get/user` | 获取用户信息 |
| | GET | `/get/all/user` | 获取所有用户 |
| | POST | `/update/user` | 更新用户 |
| | POST | `/delete/user` | 删除用户 |
| | GET | `/get/history` | 获取消费历史 |
| | POST | `/insert/history` | 插入消费记录 |
| **电影** | GET | `/movie/all` | 获取所有电影 |
| | GET | `/movie/all/exclude/off` | 获取上架电影 |
| | GET | `/movie/{id}/{userId}` | 电影详情 |
| | GET | `/movie/search` | 搜索电影 |
| | POST | `/movie/add` | 添加电影 |
| | POST | `/movie/update` | 更新电影 |
| | POST | `/movie/off/batch` | 批量下架 |
| | POST | `/movie/{movieId}/like` | 标记想看 |
| | POST | `/movie/{movieId}/unlike` | 取消想看 |
| | GET | `/movie/{movieId}/like/count` | 想看人数 |
| | GET | `/movie/{movieId}/like/date` | 想看趋势 |
| **排片** | POST | `/schedule/add` | 添加排片 |
| | POST | `/schedule/update` | 更新排片 |
| | GET | `/schedule/search` | 搜索排片 |
| | GET | `/schedule/search/audience` | 观众搜索排片 |
| | GET | `/schedule/{id}` | 排片详情 |
| | DELETE | `/schedule/delete/batch` | 批量删除排片 |
| | POST | `/schedule/view/set` | 设置可见天数 |
| | GET | `/schedule/view` | 获取可见天数 |
| **影厅** | GET | `/hall/all` | 获取所有影厅 |
| | POST | `/hall/add` | 添加影厅 |
| | POST | `/hall/update` | 更新影厅 |
| | POST | `/hall/delete` | 删除影厅 |
| **票务** | POST | `/ticket/lockSeat` | 锁定座位 |
| | POST | `/ticket/buy` | 银行卡购票 |
| | POST | `/ticket/vip/buy` | VIP 购票 |
| | GET | `/ticket/get/{userId}` | 用户订单 |
| | GET | `/ticket/get/all` | 所有订单 |
| | GET | `/ticket/get/occupiedSeats` | 已占座位 |
| | POST | `/ticket/cancel` | 取消订单 |
| | GET | `/ticket/get/refundInfo` | 退票时限 |
| | POST | `/ticket/update/refundInfo` | 更新退票时限 |
| **退票** | POST | `/refund` | 退票 |
| | GET | `/get/all/refund` | 所有退票策略 |
| | POST | `/add/refund` | 添加退票策略 |
| | POST | `/update/refund` | 更新退票策略 |
| | POST | `/delete/refund` | 删除退票策略 |
| **VIP** | POST | `/vip/add` | 开通 VIP |
| | GET | `/vip/{userId}/get` | VIP 卡信息 |
| | POST | `/vip/charge` | VIP 充值 |
| | GET | `/vip/get/all/strategy` | 所有 VIP 策略 |
| | POST | `/vip/add/strategy` | 添加 VIP 策略 |
| | POST | `/vip/update/strategy` | 更新 VIP 策略 |
| | POST | `/vip/delete/strategy` | 删除 VIP 策略 |
| | GET | `/vip/get/by/money` | 按金额查询策略 |
| **优惠券** | GET | `/coupon/{userId}/get` | 用户优惠券 |
| | GET | `/coupon/get/all` | 所有优惠券模板 |
| | POST | `/coupon/send` | 发放优惠券 |
| **活动** | POST | `/activity/publish` | 发布活动 |
| | GET | `/activity/get` | 所有活动 |
| | POST | `/activity/delete` | 删除活动 |
| **统计** | GET | `/statistics/boxOffice/total` | 票房排行 |
| | GET | `/statistics/audiencePrice` | 观众消费统计 |
| | GET | `/statistics/scheduleTime` | 排片统计 |
| | GET | `/statistics/popular/stars` | 热门想看排行 |
| | GET | `/statistics/comprehensive` | 综合大屏数据 |
