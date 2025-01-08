<script setup>
import HeaderComponent from "@/components/HeaderComponent.vue";
import FooterComponent from "@/components/FooterComponent.vue";
import {useRoute} from "vue-router";
import {ref, watch} from "vue";

const route = useRoute();
const isSubmenuVisible = ref(false);

const checkSubmenu = (visible) => {
    isSubmenuVisible.value = visible
}

watch(() => route.path, () => {
    isSubmenuVisible.value = false;
});

</script>

<template>
    <div class="app-wrapper">
        <div v-if="route.path !== '/signup' && route.path !== '/login' && route.path !== '/resetpassword'" class="app-header" :key="$route.path">
            <HeaderComponent @submenu-expanded="checkSubmenu"/>
        </div>
        <div class="app-content" :class="{ 'no-padding': route.path === '/signup' || route.path === '/login' || route.path === '/resetpassword', 'submenu-expanded': isSubmenuVisible }">
            <RouterView :key="$router.path"/>
        </div>
        <div v-if="route.path !== '/signup' && route.path !== '/login'" class="app-footer">
            <FooterComponent/>
        </div>
    </div>
</template>

<style scoped>
.app-wrapper {
    display: flex;
    flex-direction: column;
    width: 100vw;
    min-height: 100vh;
}

.app-header {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    z-index: 1002;
}

.app-content {
    flex: 1;
    width: 60%;
    max-width: 1200px;
    margin: 0 auto;
    padding: calc(6rem + 5.5rem) 0 1rem 0;
    display: flex;
    justify-content: center;
}

/* login, signup 페이지용 no-padding 클래스 추가 */
.app-content.no-padding {
    padding: 0;
}

@media (max-width: 1024px) {
    .app-content {
        width: 80%;
    }
}

@media (max-width: 768px) {
    .app-content {
        width: 90%;
        padding: calc(5rem + 5rem) 0 1rem 0;
    }
    .app-content.submenu-expanded {
        padding: calc(4rem + 5rem + 228px) 10px 1rem 10px;
    }
    .app-content.no-padding {
        padding: 0;
    }
}

@media (max-width: 480px) {
    .app-content {
        width: 95%;
        padding: calc(4rem + 5rem) 10px 1rem 10px;
    }
    .app-content.no-padding {
        padding: 0;
    }
}
</style>
