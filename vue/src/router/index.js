import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [{
        path: '/login',
        name: 'Login',
        component: () =>
            import ('../components/Login.vue')
    },
    {
        path: '/home',
        name: 'Home',
        redirect: '/welcome',
        component: () =>
            import ('../views/Home'),
        children: [{
                path: '/welcome',
                component: () =>
                    import ('../components/Welcome')
            },
            {
                path: '/goods',
                component: () =>
                    import ('../components/goods/List.vue')
            },
            {
                path: '/goods/add',
                component: () =>
                    import ('../components/goods/Add.vue')
            },
            {
                path: '/goods/show',
                component: () =>
                    import ('../components/goods/Show.vue')
            },
            {
                path: '/goods/detail',
                component: () =>
                    import ('../components/goods/Detail.vue')
            },
            {
                path: '/orders',
                component: () =>
                    import ('../components/orders/Order.vue')
            },
            {
                path: '/orders/list',
                component: () =>
                    import ('../components/orders/List.vue')
            },
            {
                path: '/user',
                component: () =>
                    import ('../components/users/User.vue')
            },
            {
                path: '/users/list',
                component: () =>
                    import ('../components/users/List.vue')
            },
            {
                path: '/promos',
                component: () =>
                    import ('../components/promos/Promo.vue')
            },
        ],
    },
    {
        path: '/about',
        name: 'About',
        // route level code-splitting
        // this generates a separate chunk (about.[hash].js) for this route
        // which is lazy-loaded when the route is visited.
        component: () =>
            import ('../views/About.vue')
    }
]

const router = new VueRouter({
    routes
})


//路由导航守卫
router.beforeEach((to, from, next) => {
    if (to.path === '/login') return next()
    const tokenStr = window.sessionStorage.getItem('token')
    if (!tokenStr) return next('/login')
    next()
})
export default router