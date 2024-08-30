import {createRouter, createWebHistory} from 'vue-router'
import MapView from "@/views/MapView.vue";
import ChatView from "@/views/ChatView.vue";
import SearchPlaceView from "@/views/SearchPlaceView.vue";
import WritePlanView from "@/views/WritePlanView.vue";
import LoginView from "@/views/LoginView.vue";
import SignupView from "@/views/SignupView.vue";
import PlanDetailView from "@/views/PlanDetailView.vue";
import FreePostListView from "@/views/FreePostListView.vue";

const routes = [
    {
        path: '/',
        name: 'Map',
        component: MapView
    },
    {
        path: '/chat',
        name: 'Chat',
        component: ChatView
    },
    {
        path: '/search',
        name: 'Search',
        component: SearchPlaceView
    },
    {
        path: '/login',
        name: 'Login',
        component: LoginView
    },
    {
        path: '/signup',
        name: 'Signup',
        component: SignupView
    },
    {
        path: '/plan/write',
        name: 'WritePlan',
        component: WritePlanView
    },
    {
        path: '/plan/:planId',
        name: 'PlanDetail',
        component: PlanDetailView
    },
    {
        path: '/free-post',
        name: 'FreePostList',
        component: FreePostListView
    },
]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})

export default router
