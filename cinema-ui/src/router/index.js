import { createRouter, createWebHistory } from 'vue-router'

// ================= 1. 引入前台普通用户页面 =================
import Login from '../views/Login.vue'
import Home from '../views/Home.vue'
import Movie from '../views/Movie.vue'
import MovieDetail from '../views/MovieDetail.vue'
import MovieBuy from '../views/MovieBuy.vue'
import UserMember from '../views/UserMember.vue'
import UserBuy from '../views/UserBuy.vue'
import UserCost from '../views/UserCost.vue'
import UserInfo from '../views/UserInfo.vue'

// ================= 2. 引入后台管理员布局组件 =================
import AdminLayout from '../layout/AdminLayout.vue'

// (注意：后台的子页面我们在下面用 () => import() 的懒加载语法，这样不需要在上面 import，也就永远不会报 not defined 的错了！)

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // 默认访问根目录时，重定向到登录页
    {
      path: '/',
      redirect: '/login'
    },

    // ---------------- 前台路由部分 ----------------
    { path: '/login', name: 'login', component: Login },
    { path: '/home', name: 'home', component: Home },
    { path: '/movie', name: 'movie', component: Movie },
    { path: '/movieDetail', name: 'movieDetail', component: MovieDetail },
    { path: '/movieBuy', name: 'movieBuy', component: MovieBuy },
    { path: '/userMember', name: 'userMember', component: UserMember },
    { path: '/userBuy', name: 'userBuy', component: UserBuy },
    { path: '/userCost', name: 'userCost', component: UserCost },
    { path: '/userInfo', name: 'userInfo', component: UserInfo },

    // ---------------- 后台路由部分 (嵌套布局) ----------------
    {
      path: '/admin',
      name: 'admin',
      component: AdminLayout, // 父亲：左侧菜单和顶部导航栏
      redirect: '/admin/movie', // 默认访问 /admin 时跳到电影管理
      children: [
        // 儿子们：右侧的内容区 (使用懒加载，按需加载更快，且不会报错)
        {
          path: 'movie',
          name: 'adminMovie',
          component: () => import('../views/AdminMovie.vue')
        },
        {
          path: 'schedule',
          name: 'adminSchedule',
          component: () => import('../views/AdminSchedule.vue')
        },
        {
          path: 'promotion',
          name: 'adminPromotion',
          component: () => import('../views/AdminPromotion.vue')
        },
        {
          path: 'cinema',
          name: 'adminCinema',
          component: () => import('../views/AdminCinema.vue')
        },
        {
          path: 'vip',
          name: 'adminVip',
          component: () => import('../views/AdminVip.vue')
        },
        {
          path: 'refund',
          name: 'adminRefund',
          component: () => import('../views/AdminRefund.vue')
        },
        {
          path: 'statistic',
          name: 'adminStatistic',
          component: () => import('../views/AdminStatistic.vue')
        },
        {
          path: 'ticket',
          name: 'adminTicket',
          component: () => import('../views/AdminTicket.vue')
        },
        {
          path: 'coupon',
          name: 'adminCoupon',
          component: () => import('../views/AdminCoupon.vue')
        }
      ]
    }
  ]
})

export default router