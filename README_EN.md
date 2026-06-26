<div align="center">

# Movie-v2 - Online Cinema Ticket Booking System

![Vue](https://img.shields.io/badge/Vue-3.5-42b883?style=flat&logo=vue.js)
![TypeScript](https://img.shields.io/badge/TypeScript-5.0-3178c6?style=flat&logo=typescript)
![Vite](https://img.shields.io/badge/Vite-5.4-646cff?style=flat&logo=vite)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.1-6db33f?style=flat&logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479a1?style=flat&logo=mysql)

English | **[中文](README.md)**

[![GitHub stars](https://img.shields.io/github/stars/gurinalje/Movie-v2?style=social)](https://github.com/gurinalje/Movie-v2/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/gurinalje/Movie-v2?style=social)](https://github.com/gurinalje/Movie-v2/network/members)

</div>

---

## Introduction

A full-stack online cinema ticket booking platform built with Vue 3 and Spring Boot, simulating real-world cinema business scenarios. Features include complete user-side ticket purchasing flow and admin management system.

**Project Duration:** 2 weeks
**Role:** Independent Developer
**Project Type:** Full-stack Development (Frontend + Backend + Database Design)

---

## Core Features

### User Side

| Feature | Implementation |
|---------|----------------|
| Online Seat Selection | Canvas seat algorithm + concurrent locking |
| VIP Membership Card | Recharge strategy engine + balance payment |
| Coupon System | Coupon issuance/redemption + discount rules |
| Movie Search | Fuzzy search + pagination |
| Transaction History | Multi-dimensional statistics + history query |

### Admin Side

| Feature | Implementation |
|---------|----------------|
| Schedule Management | Session management + time conflict detection |
| Data Dashboard | ECharts multi-dimensional visualization |
| Promotions | Activity configuration + coupon association |
| Refund Policy | Per-movie refund time limits and fees |

---

## Technical Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                      Frontend (Vue 3)                       │
├─────────────────────────────────────────────────────────────┤
│  Vue3 Composition API + TypeScript + Vite + Element Plus    │
│  Pinia State Management + Vue Router + ECharts              │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                     Backend (Spring Boot)                   │
├─────────────────────────────────────────────────────────────┤
│  Spring Boot + MyBatis + MySQL 8.0 + BCrypt Encryption      │
│  Layered Architecture: Controller → Service → Mapper → DB   │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                        Database (MySQL)                     │
├─────────────────────────────────────────────────────────────┤
│  15 Tables: user, movie, hall, schedule, ticket, vip_card... │
└─────────────────────────────────────────────────────────────┘
```

---

## Project Highlights

### 1. Complete Seat Selection Flow

```javascript
// Core seat selection algorithm
const selectSeat = (row, col) => {
  // 1. Check seat status
  if (seatStatus[row][col] === 'locked') return

  // 2. Lock seat (prevent concurrency)
  await lockSeat(row, col)

  // 3. Create order
  const order = await createOrder({ seat: `${row}-${col}` })

  // 4. Auto-release after 15 minutes if unpaid
  startCountdown(order.id, 15 * 60)
}
```

**Technical Challenges:**
- Concurrent seat locking mechanism
- Auto-release for expired orders (scheduled tasks)
- Real-time seat status synchronization

### 2. Data Visualization Dashboard

Multi-dimensional data display using ECharts:
- Box office rankings (bar chart)
- Audience consumption trends (line chart)
- Schedule statistics (pie chart)
- Popular movies (radar chart)

### 3. Responsive Design

- Mobile device adaptation
- Adaptive layout
- Cross-browser compatibility (Chrome, Firefox, Safari, Edge)

---

## Tech Stack

### Frontend

| Technology | Version | Purpose |
|------------|---------|---------|
| Vue 3 | 3.5.29 | Frontend Framework |
| TypeScript | 5.0+ | Type-safe Development |
| Vite | 5.4.11 | Build Tool |
| Element Plus | 2.13.5 | UI Component Library |
| Pinia | 3.0.4 | State Management |
| ECharts | 6.0.0 | Data Visualization |
| Axios | 1.13.6 | HTTP Client |

### Backend

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 1.8 | Programming Language |
| Spring Boot | 2.1.3 | Web Framework |
| MyBatis | 1.3.2 | ORM Framework |
| MySQL | 8.0 | Database |

---

## Quick Start

### Prerequisites

- JDK 1.8+
- Maven 3.x
- MySQL 8.0+
- Node.js 20.19+

### Installation

```bash
# 1. Clone the repository
git clone https://github.com/gurinalje/Movie-v2.git
cd Movie-v2

# 2. Initialize database
mysql -u root -p < movie/sql/update.sql

# 3. Configure database connection
# Edit movie/.env file

# 4. Start backend
cd movie
mvn spring-boot:run

# 5. Start frontend
cd cinema-ui
npm install
npm run dev
```

### Access the System

| Role | Username | Password |
|------|----------|----------|
| Admin | root | 123456 |
| User | faker | 111111 |

Access URL: http://localhost:5173

---

## Project Structure

```
Online-Movie-Ticket-System/
├── movie/                              # Backend Spring Boot Project
│   ├── pom.xml                         # Maven Dependencies
│   ├── .env                            # Environment Variables (DB config, not in repo)
│   ├── sql/
│   │   └── update.sql                  # Database Schema + Initial Data
│   └── src/main/
│       ├── java/com/example/cinema/
│       │   ├── CinemaApplication.java  # Application Entry Point
│       │   ├── config/
│       │   │   └── InterceptorConfiguration.java  # Interceptor Config
│       │   ├── interceptor/
│       │   │   └── SessionInterceptor.java        # Login Interceptor
│       │   ├── controller/             # ---- Controller Layer (API Entry) ----
│       │   │   ├── user/
│       │   │   │   └── AccountController.java     # Account (Login/Register/User Management)
│       │   │   ├── management/
│       │   │   │   ├── MovieController.java       # Movie Management
│       │   │   │   ├── ScheduleController.java    # Schedule Management
│       │   │   │   └── HallController.java        # Hall Management
│       │   │   ├── sales/
│       │   │   │   ├── TicketController.java      # Ticket (Purchase/Seat Selection/Refund)
│       │   │   │   └── RefundController.java      # Refund Policy Management
│       │   │   ├── promotion/
│       │   │   │   ├── VIPCardController.java     # VIP Membership Card
│       │   │   │   ├── CouponController.java      # Coupon
│       │   │   │   └── ActivityController.java    # Promotion Activity
│       │   │   └── statistics/
│       │   │       └── StatisticsController.java  # Business Data Statistics
│       │   ├── bl/                     # ---- Business Logic Layer Interface ----
│       │   ├── blImpl/                 # ---- Business Logic Layer Implementation ----
│       │   ├── data/                   # ---- Data Access Layer (MyBatis Mapper) ----
│       │   ├── po/                     # ---- Database Entity Classes ----
│       │   └── vo/                     # ---- View Objects / Form Objects ----
│       └── resources/
│           ├── application.yml         # Application Configuration
│           ├── dataImpl/               # MyBatis XML Mapper Files
│           ├── static/                 # Static Resources
│           └── templates/              # Thymeleaf Templates
│
├── cinema-ui/                          # Frontend Vue 3 Project
│   ├── package.json                    # Node Dependencies
│   ├── vite.config.js                  # Vite Config (with proxy settings)
│   ├── index.html                      # HTML Entry Point
│   └── src/
│       ├── main.js                     # Vue Application Entry
│       ├── App.vue                     # Root Component
│       ├── router/index.js             # Frontend Route Configuration
│       ├── components/
│       │   └── NavBar.vue              # Navigation Bar Component
│       ├── layout/
│       │   └── AdminLayout.vue         # Admin Dashboard Layout (Sidebar + Header)
│       └── views/                      # ---- Page Components ----
│           ├── Login.vue               # Login Page
│           ├── Home.vue                # Home Page (Popular Movies)
│           ├── Movie.vue               # Movie List
│           ├── MovieDetail.vue         # Movie Details
│           ├── MovieBuy.vue            # Seat Selection & Booking
│           ├── UserBuy.vue             # User Orders
│           ├── UserCost.vue            # Transaction History
│           ├── UserInfo.vue            # Personal Information
│           ├── UserMember.vue          # Membership Center
│           ├── AdminMovie.vue          # [Admin] Movie Management
│           ├── AdminSchedule.vue       # [Admin] Schedule Management
│           ├── AdminCinema.vue         # [Admin] Hall Management
│           ├── AdminPromotion.vue      # [Admin] Promotion Management
│           ├── AdminVip.vue            # [Admin] VIP Management
│           ├── AdminRefund.vue         # [Admin] Refund Policy
│           ├── AdminStatistic.vue      # [Admin] Data Statistics
│           └── AdminTicket.vue         # [Admin] Ticket Management
│
├── assets/                              # Project Screenshots
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
└── SECURITY_REVIEW_REPORT.md           # Security Review Report
```

---

## Code Examples

### Axios Request Encapsulation

```typescript
// src/api/request.ts
import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// Request interceptor
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  }
)

// Response interceptor
request.interceptors.response.use(
  response => response.data,
  error => {
    ElMessage.error(error.response?.data?.message || 'Request failed')
    return Promise.reject(error)
  }
)

export default request
```

### Pinia State Management

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

## Security Features

- **Password Encryption:** BCrypt algorithm for secure password storage
- **Access Control:** Session-based login interception
- **Data Protection:** UserVO excludes sensitive information
- **SQL Injection Prevention:** MyBatis parameterized queries

---

## Development Log

- ✅ Completed user-side core features
- ✅ Completed admin panel development
- ✅ Implemented data visualization dashboard
- ✅ Completed security audit and vulnerability fixes
- ✅ Optimized page loading performance

---

## License

This project is for learning and research purposes only.

---

<div align="center">

**If this project helps you, please give it a Star!**

[![Star History](https://api.star-history.com/svg?repos=gurinalje/Movie-v2&type=Date)](https://star-history.com/#gurinalje/Movie-v2&Date)

</div>
