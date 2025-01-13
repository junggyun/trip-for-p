<script setup>
import {computed, onMounted, onUnmounted, ref} from 'vue';
import {getPopularRegionsAPI} from "@/api/course";
import locationImage from '@/assets/location.png'
import router from "@/router";
import {getMagazineListAPI} from "@/api/magazine";

const magazines = ref([]);
const regions = ref([]);
const plans = ref([]);

// 각 섹션별 현재 슬라이드 인덱스 관리
const magazineSlideIndex = ref(0);
const regionSlideIndex = ref(0);
const planSlideIndex = ref(0);

// 각 섹션별 interval 저장
let magazineInterval;
let regionInterval;
let planInterval;

const getMagazineList = async function () {
    try {
        const request = {
            size: 6,
            page: 0,
        }
        const response = await getMagazineListAPI(request);
        magazines.value = response.data.content;

    } catch (error) {
        console.log(error);
    }
}

const getPopularRegionList = async function () {
    try {
        const response = await getPopularRegionsAPI(10);
        regions.value = response.data;
    } catch (error) {
        console.log(error)
    }
}

const getImageSrc = computed(() => (imageUrl) => {
    return imageUrl && imageUrl.trim() !== '' ? imageUrl : locationImage;
});

const goToMagazineDetail = (id) => {
    router.push(`/magazine/${id}`)
};

const goToCourseListByRegion = (city) => {
    router.push(`/course/search?keyword=${city}`)
}

const goToPlanDetail = (id) => {
    router.push(`/plan/${id}`)
};

const moveSlide = (direction, section) => {
    const gridElement = document.querySelector(`.${section}-section .grid`);
    const items = gridElement.querySelectorAll('.item');
    if (!items.length) {
        return;
    }

    const isMobile = window.innerWidth <= 768;

    // 아이템의 전체 너비 (gap 포함)
    const itemWidth = items[0].offsetWidth + (isMobile ? 20 : 32);
    let currentIndex;
    let maxIndex;
    let slideIndex;

    switch (section) {
        case 'magazine':
            currentIndex = magazineSlideIndex.value;
            maxIndex = magazines.value.length - (isMobile ? 0 : 2);
            magazineSlideIndex.value = direction === 'next'
                ? (currentIndex + 1) % maxIndex
                : (currentIndex - 1 + maxIndex) % maxIndex;
            slideIndex = magazineSlideIndex.value;
            break;
        case 'popular-regions':
            currentIndex = regionSlideIndex.value;
            maxIndex = regions.value.length - (isMobile ? 0 : 2);
            regionSlideIndex.value = direction === 'next'
                ? (currentIndex + 1) % maxIndex
                : (currentIndex - 1 + maxIndex) % maxIndex;
            slideIndex = regionSlideIndex.value;
            break;
        case 'popular-plans':
            currentIndex = planSlideIndex.value;
            maxIndex = plans.value.length - (isMobile ? 0 : 2);
            planSlideIndex.value = direction === 'next'
                ? (currentIndex + 1) % maxIndex
                : (currentIndex - 1 + maxIndex) % maxIndex;
            slideIndex = planSlideIndex.value;
            break;
    }

    gridElement.style.transition = 'transform 0.5s ease';
    gridElement.style.transform = `translateX(-${slideIndex * itemWidth}px)`;
};

const startAutoSlides = () => {
    magazineInterval = setInterval(() => moveSlide('next', 'magazine'), 3000);
    regionInterval = setInterval(() => moveSlide('next', 'popular-regions'), 3000);
    planInterval = setInterval(() => moveSlide('next', 'popular-plans'), 3000);
};

const stopAutoSlides = () => {
    if (magazineInterval) {
        clearInterval(magazineInterval);
    }
    if (regionInterval) {
        clearInterval(regionInterval);
    }
    if (planInterval) {
        clearInterval(planInterval);
    }
};

const resumeAutoSlides = () => {
    stopAutoSlides();
    startAutoSlides();
};

const handleResize = () => {
    moveSlide('next', 'magazine');
    moveSlide('next', 'popular-regions');
    moveSlide('next', 'popular-plans');
};

onMounted(() => {
    getMagazineList().then(() => {
        if (magazines.value.length) {
            startAutoSlides();
        }
    });
    getPopularRegionList();
    // 저장된 함수를 이벤트 리스너로 등록
    window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
    stopAutoSlides();
    // 동일한 함수를 이벤트 리스너에서 제거
    window.removeEventListener('resize', handleResize);
});
</script>

<template>
    <div class="container">
        <section class="magazine-section">
            <h1 class="section-title">매거진</h1>
            <div class="slider-container">
                <button class="nav-button prev" @click="moveSlide('prev', 'magazine')">&lt;</button>
                <div class="grid-wrapper">
                    <div class="grid"
                         @mouseenter="stopAutoSlides"
                         @mouseleave="resumeAutoSlides">
                        <div v-for="magazine in magazines" :key="magazine.id"
                             class="item"
                             @click="goToMagazineDetail(magazine.id)">
                            <div class="image-container">
                                <img :src="magazine.fileUrl" :alt="magazine.title"
                                     class="item-image">
                            </div>
                            <div class="item-content">
                                <h2 class="item-title">{{ magazine.title }}</h2>
                                <p class="item-description">{{ magazine.description }}</p>
                            </div>
                        </div>
                    </div>
                </div>
                <button class="nav-button next" @click="moveSlide('next', 'magazine')">&gt;</button>
            </div>
        </section>

        <section class="popular-plans-section">
            <h1 class="section-title">인기 코스</h1>
            <div class="slider-container">
                <button class="nav-button prev" @click="moveSlide('prev', 'popular-plans')">&lt;
                </button>
                <div class="grid-wrapper">
                    <div class="grid"
                         @mouseenter="stopAutoSlides"
                         @mouseleave="resumeAutoSlides">
                        <div v-for="plan in plans" :key="plan.id"
                             class="item"
                             @click="goToPlanDetail(plan.id)">
                            <div class="image-container">
                                <img :src="getImageSrc(plan.imageUrl)"
                                     :alt="plan.title"
                                     class="item-image">
                            </div>
                            <div class="item-content">
                                <h2 class="item-title">{{ plan.title }}</h2>
                                <p class="item-description">좋아요: {{ plan.likeCount }}</p>
                            </div>
                        </div>
                    </div>
                </div>
                <button class="nav-button next" @click="moveSlide('next', 'popular-plans')">&gt;
                </button>
            </div>
        </section>
        <section class="popular-regions-section">
            <h1 class="section-title">인기 여행지</h1>
            <div class="slider-container">
                <button class="nav-button prev" @click="moveSlide('prev', 'popular-regions')">&lt;
                </button>
                <div class="grid-wrapper">
                    <div class="grid"
                         @mouseenter="stopAutoSlides"
                         @mouseleave="resumeAutoSlides">
                        <div v-for="region in regions" :key="region.id"
                             class="item region-item"
                            @click="goToCourseListByRegion(region.city)">
                            <div class="region-content">
                                <div class="region-info">
                                    <h2 class="region-name">{{ region.city }}</h2>
                                    <p class="region-province">{{ region.province }}</p>
                                    <div class="region-stats">
                                        <div class="stats-item">
                                            <span class="stats-icon">📍</span>
                                            <span class="stats-value">{{ region.count }}개의 코스</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="region-gradient"></div>
                        </div>
                    </div>
                </div>
                <button class="nav-button next" @click="moveSlide('next', 'popular-regions')">&gt;
                </button>
            </div>
        </section>
    </div>
</template>

<style scoped>
.container {
    width: 100%;
    max-width: 1200px;
    margin: 0 auto;
    padding: 2rem 1rem 0 1rem;
}

.section-title {
    font-size: 2.5rem;
    font-weight: bold;
    text-align: center;
    margin-bottom: 2rem;
    color: #333;
}

.slider-container {
    position: relative;
    padding: 0.5rem 0;
}

.grid-wrapper {
    overflow: hidden;
    margin: 0 40px;
    padding: 1rem 0;
}

.grid {
    display: flex;
    gap: 2rem;
    transition: transform 0.5s ease;
}

.item {
    min-width: calc((100% - 4rem) / 3);
    flex: 0 0 calc((100% - 4rem) / 3);
    margin-bottom: 0.5rem;
    background-color: #fff;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s ease, box-shadow 0.3s ease;
    cursor: pointer;
}

.item:hover {
    transform: translateY(-5px);
    box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
}

.nav-button {
    position: absolute;
    top: 50%;
    transform: translateY(-50%);
    width: 40px;
    height: 40px;
    background-color: rgba(255, 255, 255, 0.8);
    border: none;
    border-radius: 50%;
    cursor: pointer;
    font-size: 1.5rem;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    z-index: 1;
}

.nav-button:hover {
    background-color: rgba(255, 255, 255, 0.9);
}

.nav-button.prev {
    left: 0;
}

.nav-button.next {
    right: 0;
}

.image-container {
    width: 100%;
    height: 0;
    padding-bottom: 66.67%;
    position: relative;
    overflow: hidden;
}

.item-image {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    object-fit: cover;
    object-position: center;
}

.item-content {
    padding: 1.5rem;
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
}

.item-title {
    font-size: 1.25rem;
    font-weight: 600;
    color: #222;
    margin: 0;
}

.item-description {
    font-size: 0.9rem;
    color: #444;
    margin: 0.5rem 0 0 0;
}

.popular-plans-section {
    display: none;
}

.region-item {
    position: relative;
    height: 200px;
    background-color: #f8f9fa;
    border-radius: 12px;
    overflow: hidden;
    background-image: linear-gradient(120deg, #84fab0 0%, #8fd3f4 100%);
}

.region-content {
    position: relative;
    height: 100%;
    padding: 2rem;
    display: flex;
    flex-direction: column;
    justify-content: flex-end;
    z-index: 2;
}

.region-gradient {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 70%;
    background: linear-gradient(to top, rgba(0, 0, 0, 0.6), transparent);
    z-index: 1;
}

.region-info {
    color: white;
}

.region-name {
    font-size: 2rem;
    font-weight: 700;
    margin: 0;
    margin-bottom: 0.25rem;
    text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
}

.region-province {
    font-size: 1rem;
    opacity: 0.9;
    margin: 0;
    margin-bottom: 1rem;
}

.region-stats {
    display: flex;
    gap: 1rem;
}

.stats-item {
    display: flex;
    align-items: center;
    gap: 0.5rem;
}

.stats-icon {
    font-size: 1.2rem;
}

.stats-value {
    font-size: 0.9rem;
    font-weight: 500;
}

/* 각 지역별로 다른 그라데이션 배경을 주기 위한 스타일 */
.region-item:nth-child(3n + 1) {
    background-image: linear-gradient(120deg, #f093fb 0%, #f5576c 100%);
}

.region-item:nth-child(3n + 2) {
    background-image: linear-gradient(120deg, #4facfe 0%, #00f2fe 100%);
}

.region-item:nth-child(3n + 3) {
    background-image: linear-gradient(120deg, #84fab0 0%, #8fd3f4 100%);
}

@keyframes spin {
    0% {
        transform: rotate(0deg);
    }
    100% {
        transform: rotate(360deg);
    }
}

@media (max-width: 768px) {
    .container {
        padding: 1rem 0.5rem 0 0.5rem;
    }

    .section-title {
        font-size: 1.75rem;
        margin-bottom: 0;
    }

    .item {
        min-width: calc(100% - 20px);  /* 한 화면에 하나의 아이템만 */
        flex: 0 0 calc(100% - 20px);
        margin: 0 10px;
    }
    .grid-wrapper {
        margin: 0 40px;  /* 좌우 여백 줄임 */
    }
    .grid {
        gap: 0;
    }
    .nav-button {
        width: 32px;   /* 버튼 크기 줄임 */
        height: 32px;
        font-size: 1.2rem;
    }

    .item-content {
        padding: 1rem;
    }

    .item-title {
        font-size: 1.1rem;
    }

    .item-description {
        font-size: 0.85rem;
    }

    /* 지역 카드 스타일 수정 */
    .region-item {
        height: 150px;  /* 높이 줄임 */
    }

    .region-content {
        padding: 1rem;
    }

    .region-name {
        font-size: 1.3rem;
    }

    .region-province {
        font-size: 0.85rem;
        margin-bottom: 0.5rem;
    }

    .stats-value {
        font-size: 0.8rem;
    }
}

@media (max-width: 480px) {
    .section-title {
        font-size: 1.5rem;
    }

    .nav-button {
        width: 28px;
        height: 28px;
        font-size: 1rem;
    }
}
</style>
