<script setup>
import {defineEmits, onMounted, onUnmounted, ref} from 'vue';
import {searchPlacesAPI} from "@/api/google";

const showResults = ref(false);
const searchQuery = ref('');
const searchedQuery = ref('');
const searchResults = ref([]);
const searchInputRef = ref(null); // [] 대신 null로 수정
const searchComponentRef = ref(null);
const isSearching = ref(false);
const emit = defineEmits(['place-selected']);

/**
 * 장소 검색
 */
const searchPlace = async () => {
    if (!searchQuery.value) return;
    if (searchedQuery.value === searchQuery.value) {
        showResults.value = true;
        return;
    }

    showResults.value = true;
    isSearching.value = true;

    try {
        const request = {
            textQuery: searchQuery.value,
            pageSize: 20,
            pageToken: '',
        }
        const response = await searchPlacesAPI(request);
        searchResults.value = response.data.places;
        searchedQuery.value = searchQuery.value;

    } catch (error) {
        console.error('검색 중 오류 발생:', error);
        searchResults.value = [];
    } finally {
        isSearching.value = false;
    }
};

/**
 * 장소 선택
 */
const selectPlace = (place) => {
    // place 객체 구조 변환
    const formattedPlace = {
        place: {
            id: place.id,
            name: place.name,
            address: place.address,
            category: place.category,
            rating: place.rating,
            reviewCount: place.reviewCount,
            latitude: place.latitude,
            longitude: place.longitude
        }
    };

    emit('place-selected', formattedPlace);
    searchQuery.value = '';
    searchedQuery.value = '';
    searchResults.value = [];
    showResults.value = false;
};

/**
 * 검색창 바깥쪽 클릭
 */
const handleClickOutside = (event) => {
    if (searchComponentRef.value && !searchComponentRef.value.contains(event.target)) {
        showResults.value = false;
    }
};

onMounted(() => {
    document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
    document.removeEventListener('click', handleClickOutside);
});
</script>

<template>
    <div class="search-place" ref="searchComponentRef">
        <div class="search-input-container">
            <input
                ref="searchInputRef"
                v-model="searchQuery"
                @keyup.enter="searchPlace"
                placeholder="장소를 검색하세요"
                class="search-input"
            >
            <button @click="searchPlace" class="search-button">검색</button>
        </div>
        <div v-if="showResults" class="search-results">
            <div v-if="isSearching" class="searching">검색 중...</div>
            <ul v-else-if="searchResults.length > 0">
                <li v-for="place in searchResults" :key="place.id" @click="selectPlace(place)">
                    <div class="place-name">{{ place.name }}</div>
                    <div class="place-category">{{ place.category }}</div>
                    <div class="place-address">{{ place.address }}</div>
                    <div class="place-rating">평점: {{ place.rating }} ({{ place.reviewCount }}개 리뷰)</div>
                </li>
            </ul>
            <div v-else class="no-results">
                검색 결과가 없습니다.
            </div>
        </div>
    </div>
</template>

<style scoped>
.search-place {
    margin-bottom: 20px;
    font-family: Arial, sans-serif;
    position: relative;
}

.search-input-container {
    display: flex;
    margin-bottom: 10px;
}

.search-input {
    flex-grow: 1;
    padding: 10px;
    font-size: 16px;
    border: 2px solid #ddd;
    border-radius: 4px 0 0 4px;
    transition: all 0.3s ease;
}

.search-input:focus {
    outline: none;
    border-color: #5c6ac4;
    box-shadow: 0 0 0 4px rgba(92, 106, 196, 0.1);
}

.search-button {
    padding: 10px 20px;
    font-size: 16px;
    color: white;
    background: linear-gradient(135deg, #5c6ac4 0%, #8794d8 100%);
    border: none;
    border-radius: 0 4px 4px 0;
    cursor: pointer;
    transition: all 0.3s ease;
}

.search-button:hover {
    background: linear-gradient(135deg, #4f5bb4 0%, #7683c7 100%);
    transform: translateY(-1px);
    box-shadow: 0 2px 8px rgba(92, 106, 196, 0.2);
}

.search-results {
    position: absolute;
    width: 100%;
    background-color: white;
    box-shadow: 0 4px 12px rgba(92, 106, 196, 0.15);
    border-radius: 4px;
    z-index: 1001;
    max-height: 300px;
    overflow-y: auto;
}

.searching, .no-results {
    padding: 20px;
    text-align: center;
    color: #666;
    font-style: italic;
}

.search-results li {
    cursor: pointer;
    padding: 12px;
    border-bottom: 1px solid #eee;
    transition: all 0.3s ease;
}

.search-results li:last-child {
    border-bottom: none;
}

.search-results li:hover {
    background-color: rgba(92, 106, 196, 0.05);
}

.place-name {
    font-weight: bold;
    margin-bottom: 3px;
}

.place-category {
    font-size: 0.8em;
    color: #5c6ac4;
    margin-bottom: 2px;
}

.place-address {
    font-size: 0.9em;
    color: #666;
    margin-bottom: 2px;
}

.place-rating {
    font-size: 0.8em;
    color: #888;
}

@media (max-width: 600px) {
    .search-input-container {
        flex-direction: column;
    }

    .search-input, .search-button {
        width: 100%;
        border-radius: 4px;
    }

    .search-button {
        margin-top: 10px;
    }
}
</style>
