<script setup>
import {computed, onMounted, ref, onUnmounted} from 'vue';
import {getPopularPlaceListAPI} from "@/api/course";
import locationImage from '@/assets/location.png'
import router from "@/router";
import {getMagazineListAPI} from "@/api/magazine";

const magazines = ref([]);
const places = ref([]);
const plans = ref([]);
const isLoadingPlaces = ref(true);

// 각 섹션별 현재 슬라이드 인덱스 관리
const magazineSlideIndex = ref(0);
const placeSlideIndex = ref(0);
const planSlideIndex = ref(0);

// 각 섹션별 interval 저장
let magazineInterval;
let placeInterval;
let planInterval;

const getMagazineList = async function() {
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

const getPopularPlaceList = async function () {
    try {
        isLoadingPlaces.value = true;
        const response = await getPopularPlaceListAPI();
        places.value = response.data;
    } catch (error) {
        console.log(error);
    } finally {
        isLoadingPlaces.value = false;
    }
};

const getImageSrc = computed(() => (imageUrl) => {
    return imageUrl && imageUrl.trim() !== '' ? imageUrl : locationImage;
});

const goToMagazineDetail = (id) => {
    router.push(`/magazine/${id}`)
};

const goToPlanDetail = (id) => {
    router.push(`/plan/${id}`)
};

const goToPlaceUri = (uri) => {
    window.open(uri, '_blank');
};

const moveSlide = (direction, section) => {
    const gridElement = document.querySelector(`.${section}-section .grid`);
    const items = gridElement.querySelectorAll('.item');
    if (!items.length) return;

    // 아이템의 전체 너비 (gap 포함)
    const itemWidth = items[0].offsetWidth + 32; // 32px는 gap 2rem
    let currentIndex;
    let maxIndex;
    let slideIndex;

    switch(section) {
        case 'magazine':
            currentIndex = magazineSlideIndex.value;
            // 3개씩 보이므로 maxIndex는 전체 길이 - 2
            maxIndex = Math.max(0, magazines.value.length - 2);
            magazineSlideIndex.value = direction === 'next'
                ? (currentIndex + 1) % maxIndex
                : (currentIndex - 1 + maxIndex) % maxIndex;
            slideIndex = magazineSlideIndex.value;
            break;
        case 'popular-places':
            currentIndex = placeSlideIndex.value;
            maxIndex = Math.max(0, places.value.length - 2);
            placeSlideIndex.value = direction === 'next'
                ? (currentIndex + 1) % maxIndex
                : (currentIndex - 1 + maxIndex) % maxIndex;
            slideIndex = placeSlideIndex.value;
            break;
        case 'popular-plans':
            currentIndex = planSlideIndex.value;
            maxIndex = Math.max(0, plans.value.length - 2);
            planSlideIndex.value = direction === 'next'
                ? (currentIndex + 1) % maxIndex
                : (currentIndex - 1 + maxIndex) % maxIndex;
            slideIndex = planSlideIndex.value;
            break;
    }

    // 슬라이드 전환 애니메이션 적용
    if (direction === 'next' && slideIndex === 0) {
        // 마지막에서 처음으로 돌아갈 때
        gridElement.style.transition = 'transform 0.5s ease';
        gridElement.style.transform = `translateX(0)`;
    } else if (direction === 'prev' && slideIndex === maxIndex - 1) {
        // 처음에서 마지막으로 갈 때
        gridElement.style.transition = 'transform 0.5s ease';
        gridElement.style.transform = `translateX(-${slideIndex * itemWidth}px)`;
    } else {
        // 일반적인 슬라이드 전환
        gridElement.style.transition = 'transform 0.5s ease';
        gridElement.style.transform = `translateX(-${slideIndex * itemWidth}px)`;
    }
};

const startAutoSlides = () => {
    magazineInterval = setInterval(() => moveSlide('next', 'magazine'), 3000);
    placeInterval = setInterval(() => moveSlide('next', 'popular-places'), 3000);
    planInterval = setInterval(() => moveSlide('next', 'popular-plans'), 3000);
};

const stopAutoSlides = () => {
    if (magazineInterval) clearInterval(magazineInterval);
    if (placeInterval) clearInterval(placeInterval);
    if (planInterval) clearInterval(planInterval);
};

const resumeAutoSlides = () => {
    stopAutoSlides();
    startAutoSlides();
};

onMounted(() => {
    getMagazineList().then(() => {
        if (magazines.value.length) startAutoSlides();
    });
    getPopularPlaceList();
});

onUnmounted(() => {
    stopAutoSlides();
});
</script>

<template>
    <div v-if="isLoadingPlaces" class="loading-container">
        <div class="loading-spinner"></div>
        <p class="loading-text">페이지 불러오는 중...</p>
    </div>
    <div v-else class="container">
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
                                <img :src="magazine.fileUrls[0]" :alt="magazine.title" class="item-image">
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

        <section class="popular-places-section">
            <h1 class="section-title">인기 방문지</h1>
            <div class="slider-container">
                <button class="nav-button prev" @click="moveSlide('prev', 'popular-places')">&lt;</button>
                <div class="grid-wrapper">
                    <div class="grid"
                         @mouseenter="stopAutoSlides"
                         @mouseleave="resumeAutoSlides">
                        <div v-for="place in places" :key="place.place.id"
                             class="item"
                             @click="goToPlaceUri(place.place.placeUri)">
                            <div class="image-container">
                                <img :src="getImageSrc(place.place.photoUri)"
                                     :alt="place.place.name"
                                     class="item-image">
                            </div>
                            <div class="item-content">
                                <h2 class="item-title">{{ place.place.name }}</h2>
                                <p class="item-address">{{ place.place.address }}</p>
                                <p class="item-description">방문 횟수: {{ place.count }}</p>
                            </div>
                        </div>
                    </div>
                </div>
                <button class="nav-button next" @click="moveSlide('next', 'popular-places')">&gt;</button>
            </div>
        </section>

        <section class="popular-plans-section">
            <h1 class="section-title">인기 코스</h1>
            <div class="slider-container">
                <button class="nav-button prev" @click="moveSlide('prev', 'popular-plans')">&lt;</button>
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
                <button class="nav-button next" @click="moveSlide('next', 'popular-plans')">&gt;</button>
            </div>
        </section>
    </div>
</template>

<style scoped>
.container {
    width: 100%;
    max-width: 1200px;
    margin: 0 auto;
    padding: 5rem 1rem 0 1rem;
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
    margin-bottom: 4rem;
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

.item-address {
    font-size: 0.85rem;
    color: #666;
    margin: 0;
    line-height: 1.4;
}

.item-description {
    font-size: 0.9rem;
    color: #444;
    margin: 0.5rem 0 0 0;
}

.loading-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 3rem 0;
}

.loading-spinner {
    width: 40px;
    height: 40px;
    border: 3px solid #f3f3f3;
    border-top: 3px solid #3498db;
    border-radius: 50%;
    animation: spin 1s linear infinite;
}

.loading-text {
    margin-top: 1rem;
    color: #666;
    font-size: 1rem;
}

.popular-plans-section {
    display: none;
}

@keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
}

@media (max-width: 768px) {
    .section-title {
        font-size: 2rem;
    }

    .item {
        min-width: 85%;
        flex: 0 0 85%;
    }
}

@media (max-width: 480px) {
    .section-title {
        font-size: 1.75rem;
    }
}
</style>
