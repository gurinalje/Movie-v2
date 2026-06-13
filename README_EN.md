# Online Movie Ticket Booking System

**Last Updated:** 2026-06-13

---

## Project Overview

This is a full-stack online movie ticket booking platform built with **Spring Boot + Vue 3** architecture. The system serves two types of users: **regular users** (booking movies) and **administrators** (managing halls, schedules, movies, and promotions). Core features include movie browsing, online seat selection and ticket booking, membership card management, coupon distribution, refund policy configuration, and business data analytics.

---

## Features

### User Features

| Feature | Description |
|---------|-------------|
| Account Management | Registration, login, logout, profile view and edit |
| Movie Browsing | View movie list, movie details, keyword search |
| Online Booking | Select showtime → Select seats → Lock seats → Payment (Bank Card / VIP Balance) |
| Wishlist | Mark/unmark movies as wishlist, view wishlist trends |
| Membership Card | Apply for card, recharge (with bonus strategies), balance consumption, transaction history |
| Coupons | View issued coupons, use for discount when booking tickets |
| Transaction History | View all consumption/recharge history records |

### Admin Features

| Feature | Description |
|---------|-------------|
| Movie Management | Add, edit, batch remove movies |
| Schedule Management | Add/edit/batch delete schedules, configure visible schedule days |
| Hall Management | Add, edit, delete halls (with operating status) |
| Promotions | Publish/offline promotional activities, associate movies with coupons |
| Coupon Management | View all coupons, issue coupons to specified users |
| VIP Management | VIP strategy configuration (bonus rules), VIP information overview |
| Refund Policy | Configure refund time limits and handling fees by movie |
| Ticket Management | View all orders, configure refund time limits |
| Data Statistics | Box office ranking, audience consumption analysis, schedule statistics, popular movies, comprehensive dashboard |

---

## Tech Stack

### Backend (`movie/`)

| Technology | Version | Description |
|------------|---------|-------------|
| Java | 1.8 | Programming Language |
| Spring Boot | 2.1.3.RELEASE | Web Framework |
| MyBatis | 1.3.2 | ORM Framework |
| MySQL | 8.0 | Relational Database |
| Thymeleaf | (built-in) | Template Engine (reserved) |
| Lombok | 1.18.30 | Java Code Simplification |
| Spring Security Crypto | (built-in) | BCrypt Password Encryption |
| Commons FileUpload | 1.3.3 | File Upload |
| Spring Boot Actuator | (built-in) | Application Monitoring |

### Frontend (`cinema-ui/`)

| Technology | Version | Description |
|------------|---------|-------------|
| Vue | 3.5.29 | Frontend Framework |
| Vite | 5.4.11 | Build Tool |
| Vue Router | 5.0.3 | Route Management |
| Pinia | 3.0.4 | State Management |
| Element Plus | 2.13.5 | UI Component Library |
| Axios | 1.13.6 | HTTP Client |
| ECharts | 6.0.0 | Data Visualization Charts |

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
│       │   ├── bl/                     # ---- Business Logic Layer ----
│       │   ├── blImpl/                 # ---- Business Logic Implementation ----
│       │   ├── data/                   # ---- Data Access Layer (MyBatis Mapper) ----
│       │   ├── po/                     # ---- Persistent Objects ----
│       │   ├── vo/                     # ---- View Objects ----
│       │   └── exception/              # ---- Custom Exceptions ----
│       └── resources/
│           ├── application.yml         # Application Configuration
│           └── dataImpl/               # MyBatis XML Mapper Files
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
│       │   └── AdminLayout.vue         # Admin Dashboard Layout
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
└── SECURITY_REVIEW_REPORT.md           # Security Review Report
```

---

## Prerequisites

| Dependency | Minimum Version | Description |
|------------|-----------------|-------------|
| JDK | 1.8+ | Java Runtime Environment |
| Maven | 3.x | Backend Build Tool |
| MySQL | 8.0+ | Database |
| Node.js | 20.19+ or 22.12+ | Frontend Runtime |
| npm | Corresponding to Node.js | Frontend Package Manager |

---

## Getting Started

### 1. Initialize Database

```bash
# Login to MySQL and execute the schema script
mysql -u root -p < movie/sql/update.sql
```

This script will automatically create the `cinema` database, all tables, and initial test data.

### 2. Configure Database Connection

Edit the `movie/.env` file (ensure this file is not committed to version control):

```properties
DB_URL=jdbc:mysql://localhost:3306/cinema?serverTimezone=CTT&characterEncoding=UTF-8&useSSL=false
DB_USERNAME=root
DB_PASSWORD=your_database_password
```

### 3. Start Backend

```bash
# Navigate to backend directory
cd movie

# Compile and start
mvn spring-boot:run
```

Backend will run on **http://localhost:8080** by default.

### 4. Start Frontend

```bash
# Navigate to frontend directory
cd cinema-ui

# Install dependencies
npm install

# Start development server
npm run dev
```

Frontend will run on **http://localhost:5173** by default. All `/api` requests will be proxied to backend `http://localhost:8080` (the `/api` prefix will be removed).

### 5. Access the System

| Role | Username | Password |
|------|----------|----------|
| Admin | root | 123456 |
| User | faker | 111111 |
| User | zhangsan | 111222 |

- Visit **http://localhost:5173** to open the system homepage
- Login with admin credentials to access the admin dashboard
- Login with user credentials to access the user frontend

---

## Development Guide

### Frontend Development

```bash
# Start development server (with hot reload)
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview
```

**Frontend Proxy Configuration** (`vite.config.js`):

```javascript
proxy: {
  '/api': {
    target: 'http://localhost:8080',  // Backend address
    changeOrigin: true,
    rewrite: (path) => path.replace(/^\/api/, '')  // Remove /api prefix
  }
}
```

### Backend Development

**Backend Layered Architecture:**

```
Controller (Controller Layer)
    ↓ Calls
Service Interface (Business Logic Layer)
    ↓ Implements
ServiceImpl (Business Logic Implementation)
    ↓ Calls
Mapper (Data Access Layer, MyBatis XML Mapping)
    ↓ Operates
MySQL Database
```

**Key Configurations:**

- `application.yml` - Datasource, MyBatis mapping paths, Jackson timezone
- `InterceptorConfiguration.java` - Login interceptor, excludes login/register/static resources
- `SessionInterceptor.java` - HTTP Session-based login verification, returns 401 JSON if not logged in

---

## Database Schema

| Table | Description |
|-------|-------------|
| `user` | User table (id, username, password, kind: 1=admin/2=user) |
| `movie` | Movie table (name, director, cast, type, poster, etc.) |
| `hall` | Hall table (rows, columns, seats, operating status) |
| `schedule` | Schedule table (links hall and movie, showtime and price) |
| `schedule_view` | Schedule visible days setting |
| `ticket` | Order table (links user and schedule, seat info, order status) |
| `vip_card` | Membership card table (balance and total consumption) |
| `vip_strategy` | VIP recharge strategy (recharge X get Y bonus) |
| `coupon` | Coupon template table (usage threshold and discount amount) |
| `coupon_user` | Coupon distribution record table |
| `activity` | Promotion activity table |
| `activity_movie` | Activity-movie association table |
| `refundpolicy` | Refund policy table (refund time limit and handling fee by movie) |
| `refund_info` | Global refund time limit setting |
| `history` | User consumption/recharge history table |
| `movie_like` | User wishlist table |

---

## License

This project is for learning and research purposes only.
