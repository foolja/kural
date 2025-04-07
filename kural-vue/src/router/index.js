import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from "../views/Login.vue"
import MainLayout from '../components/MainLayout.vue'
import AssetDiscovery from '../views/AssetDiscovery.vue'
import PortScan from '../views/PortScan.vue'
import HardwareInfo from '../views/HardwareInfo.vue'
import ThreatAnalysis from '../views/ThreatAnalysis.vue'
import RiskAssessment from '../views/RiskAssessment.vue'
import TaskCreate from '../views/TaskCreate.vue'
import TaskList from '../views/TaskList.vue'
import { checkLoginStatus } from '../utils/cookie.js'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    redirect: '/login',
  },
  {
    path: '/login',
    component: Login
  },
  {
    path: '/main',
    component: MainLayout,
    children: [
      { path: 'hardware-info', component: HardwareInfo, meta: { title: '硬件信息' } },
      { path: 'port-scan', component: PortScan, meta: { title: '端口扫描' } },
      { 
        path: 'task/detail/:taskId',  // 移动后的路径保持语义完整
        name: 'TaskDetail',
        component: () => import('@/views/TaskDetail.vue'),
        props: true,
        meta: { title: '任务详情' }   // 添加meta信息确保统一
      },
      { path: 'task-create', component: TaskCreate, meta: { title: '新建任务' } },
      { path: 'task-list', component: TaskList, meta: { title: '任务列表' } },
    ]
  }
]


const router = new VueRouter({
  mode: 'hash',
  base: process.env.BASE_URL,
  routes
})



router.beforeEach((to, from, next) => {
  const isLoggedIn = checkLoginStatus();
  if (to.path === '/login') {
    if (isLoggedIn) {
      // 如果用户已登录，访问 login 页面时直接跳转到 main 页面
      next('/main');
    } else {
      // 如果用户未登录，允许访问 login 页面
      next();
    }
  } else {
    if (isLoggedIn) {
      // 如果用户已登录，允许访问其他页面
      next();
    } else {
      // 如果用户未登录，重定向到 login 页面
      next('/login');
    }
  }
});


export default router
