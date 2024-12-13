<script setup>
import {computed, defineEmits, defineProps, nextTick, onMounted, ref} from "vue";
import ItineraryComponent from "@/components/course/ItineraryComponent.vue";
import SearchPlaceComponent from "@/components/course/SearchPlaceComponent.vue";
import router from "@/router";
import {useRoute} from "vue-router";
import {createCourseAPI, updateCourseAPI} from "@/api/course";
import GoogleMapComponent from "@/components/course/GoogleMapComponent.vue";

const route = useRoute();

const props = defineProps({
    startDate: String,
    endDate: String,
    planItems: Array,
    mode: {
        type: String,
        default: 'create'
    },
});

const title = ref('');
const selectedPlaces = ref({});
const currentDateIndex = ref(0);
const deletedPlaces = ref([]);

const GOOGLE_MAPS_API_KEY = process.env.VUE_APP_GOOGLE_MAP_API_KEY;

// 날짜별 선택된 장소 초기화
onMounted(() => {
    const start = new Date(props.startDate);
    const end = new Date(props.endDate);

    for (let dt = new Date(start); dt <= end; dt.setDate(dt.getDate() + 1)) {
        const dateString = dt.toISOString().split('T')[0];
        selectedPlaces.value[dateString] = [];
    }

    if (props.planItems) {
        props.planItems.forEach(item => {
            initPlace(item, new Date(item.tripDate))
        })
    }
});

const goToPreviousDate = () => {
    if (currentDateIndex.value > 0) {
        currentDateIndex.value--;
    }
};

const goToNextDate = () => {
    if (currentDateIndex.value < dates.value.length - 1) {
        currentDateIndex.value++;
    }
};

const dates = computed(() => {
    const start = new Date(props.startDate);
    const end = new Date(props.endDate);
    const dateArray = [];
    for (let dt = new Date(start); dt <= end; dt.setDate(dt.getDate() + 1)) {
        dateArray.push(new Date(dt));
    }
    return dateArray;
});

const addPlace = (place, date) => {
    const dateString = date.toISOString().split('T')[0];

    if (!selectedPlaces.value[dateString]) {
        selectedPlaces.value[dateString] = [];
    }

    // 중복 여부 확인
    const isPlaceAlreadyAdded = selectedPlaces.value[dateString].some(
        selectedPlace => selectedPlace.place.id === place.place.id
    );

    if (!isPlaceAlreadyAdded) {
        selectedPlaces.value[dateString].push({
            place: {
                id: place.place.id,
                name: place.place.name,
                address: place.place.address,
                category: place.place.category,
                rating: place.place.rating,
                reviewCount: place.place.reviewCount,
                latitude: place.place.latitude,
                longitude: place.place.longitude
            },
            memo: '',
            sequence: selectedPlaces.value[dateString].length + 1
        });

        nextTick(() => {
            const mapElement = document.querySelector('.google-map-component');  // GoogleMapComponent에 이 클래스를 추가해야 함
            if (mapElement) {
                mapElement.scrollIntoView({
                    behavior: 'smooth',
                    block: 'center'
                });
            }
        });
    }
};

const initPlace = (item, date) => {
    const dateString = date.toISOString().split('T')[0];

    if (!selectedPlaces.value[dateString]) {
        selectedPlaces.value[dateString] = [];
    }

    const isPlaceAlreadyAdded = selectedPlaces.value[dateString].some(
        selectedPlace => selectedPlace.place.id === item.place.id
    );

    if (!isPlaceAlreadyAdded) {
        selectedPlaces.value[dateString].push({
            id: item.id,
            place: item.place,
            memo: item.memo,
            sequence: selectedPlaces.value[dateString].length + 1
        });
    }
};

const deletePlace = (dateString, index) => {
    const deletedItem = selectedPlaces.value[dateString][index];
    if (deletedItem.id) {
        deletedPlaces.value.push({
            id: deletedItem.id,
            action: 'delete',
            place: {
                mapPlaceId: deletedItem.place.id
            },
            tripDate: dateString,
            sequence: deletedItem.sequence,
            memo: deletedItem.memo
        });
    }
    selectedPlaces.value[dateString].splice(index, 1);
    selectedPlaces.value[dateString].forEach((item, i) => {
        item.sequence = i + 1;
    });
};

const updateMemo = (date, index, memo) => {
    selectedPlaces.value[date][index].memo = memo;
};

const reorderPlaces = (date) => {
    selectedPlaces.value[date].forEach((item, index) => {
        item.sequence = index + 1;
    });
};

const currentDate = computed(() => {
    if (dates.value.length === 0) {
        return null;
    }
    return dates.value[currentDateIndex.value].toISOString().split('T')[0];
});

const generatePlan = async () => {
    try {
        const planItems = Object.entries(selectedPlaces.value).flatMap(([date, places]) =>
            places.map(item => ({
                place: {
                    id: item.place.id,
                    name: item.place.place_name,
                    address: item.place.address_name,
                    category: item.place.category_name,
                    rating: item.place.rating,
                    reviewCount: item.place.reviewCount
                },
                tripDate: date,
                sequence: item.sequence,
                memo: item.memo
            }))
        );

        const createPlanRequest = {
            startDate: props.startDate,
            endDate: props.endDate,
            title: title.value,
            planItems: planItems
        };

        const response = await createCourseAPI(createPlanRequest);
        const planId = response.data.id;
        await router.push(`/plan/${planId}`);
    } catch (error) {
        console.error(error);
    }
};

const updatePlan = async () => {
    try {
        const planItems = [
            ...deletedPlaces.value,
            ...Object.entries(selectedPlaces.value).flatMap(([date, places]) =>
                places.map(item => ({
                    id: item.id || null,
                    action: item.id ? 'update' : 'create',
                    place: {
                        id: item.place.id,
                        name: item.place.place_name,
                        address: item.place.address_name,
                        category: item.place.category_name,
                        rating: item.place.rating,
                        reviewCount: item.place.reviewCount
                    },
                    tripDate: date,
                    sequence: item.sequence,
                    memo: item.memo
                }))
            )
        ];

        const updatePlanRequest = {
            startDate: props.startDate,
            endDate: props.endDate,
            title: title.value,
            planItems: planItems
        };

        const response = await updateCourseAPI(route.params.planId, updatePlanRequest);
        const planId = response.data.id;
        await router.push(`/plan/${planId}`);
    } catch (error) {
        console.error(error);
    }
};

const emit = defineEmits(['back-to-day']);

const handleBack = () => {
    if (props.mode === 'create') {
        emit('back-to-day');
    } else {
        router.go(-1);
    }
};

const isSaveButtonEnabled = computed(() => {
    return title.value.trim() !== '' &&
        Object.values(selectedPlaces.value).some(places => places.length > 0);
});
</script>

<template>
    <div class="trip-planner">
        <input v-model="title" placeholder="여행 제목" class="title-input">
        <div class="date-navigation">
            <button @click="goToPreviousDate" :disabled="currentDateIndex === 0"
                    class="nav-button prev-button">&lt; 이전
            </button>
            <h3>{{ dates[currentDateIndex].toLocaleDateString() }}</h3>
            <button @click="goToNextDate" :disabled="currentDateIndex === dates.length - 1"
                    class="nav-button next-button">다음 &gt;
            </button>
        </div>
        <div class="main-content">
            <div class="map-section">
                <GoogleMapComponent
                    class="google-map-component"
                    :places="selectedPlaces[currentDate] || []"
                    :date="currentDate"
                    :api-key="GOOGLE_MAPS_API_KEY"
                />
            </div>
            <div class="info-section">
                <div class="search-component-wrapper">
                    <SearchPlaceComponent
                        @place-selected="place => addPlace(place, dates[currentDateIndex])"/>
                </div>
                <ItineraryComponent
                    :places="selectedPlaces[currentDate] || []"
                    :currentDate="currentDate"
                    @update:memo="(index, memo) => updateMemo(currentDate, index, memo)"
                    @reorder="reorderPlaces(currentDate)"
                    @delete="index => deletePlace(currentDate, index)"
                />
            </div>
        </div>
        <div class="button-container">
            <button @click="handleBack" class="back-button">이전</button>
            <button
                v-if="props.mode==='create'"
                @click="generatePlan"
                class="save-plan-button"
                :disabled="!isSaveButtonEnabled"
            >일정 저장
            </button>
            <button
                v-else-if="props.mode==='update'"
                @click="updatePlan"
                class="save-plan-button"
                :disabled="!isSaveButtonEnabled"
            >일정 수정
            </button>
        </div>
    </div>
</template>

<style scoped>
.trip-planner {
    display: flex;
    flex-direction: column;
    width: 100%;
    max-width: 1600px;
    margin: 0 auto;
    padding: 20px;
}

.main-content {
    display: flex;
    gap: 24px;
    margin-bottom: 20px;
    min-height: 600px;
}

.map-section {
    flex: 1;
    position: sticky;
    top: 20px;
    height: calc(100vh - 250px);
}

.info-section {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 20px;
    max-width: 500px;
    overflow-y: auto;
}

.google-map-component {
    height: 100%;
    width: 100%;
    border-radius: 12px;
    overflow: hidden;
}

.search-component-wrapper {
    position: relative;
    z-index: 1000;
    background: white;
    padding: 16px;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.title-input {
    font-size: 1.5em;
    margin-bottom: 20px;
    padding: 10px;
    border: 2px solid #ddd;
    border-radius: 8px;
    transition: border-color 0.3s ease;
}

.title-input:focus {
    outline: none;
    border-color: #4CAF50;
}

.date-navigation {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding: 12px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.nav-button {
    padding: 10px 20px;
    font-size: 14px;
    font-weight: bold;
    color: #fff;
    background-color: #2196F3;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.3s ease;
}

.nav-button:hover:not(:disabled) {
    background-color: #1E88E5;
    transform: translateY(-1px);
}

.nav-button:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}

.button-container {
    display: flex;
    gap: 12px;
    justify-content: center;
    padding: 20px 0;
}

.save-plan-button, .back-button {
    padding: 12px 24px;
    font-size: 16px;
    font-weight: bold;
    color: white;
    background-color: #4CAF50;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.save-plan-button:hover:not(:disabled),
.back-button:hover {
    background-color: #45a049;
    transform: translateY(-2px);
}

.save-plan-button:disabled {
    background-color: #cccccc;
    cursor: not-allowed;
}

@media (max-width: 1024px) {
    .main-content {
        flex-direction: column;
    }

    .map-section {
        position: relative;
        height: 400px;
        top: 0;
    }

    .info-section {
        max-width: 100%;
    }
}

@media (max-width: 600px) {
    .trip-planner {
        padding: 10px;
    }

    .date-navigation {
        flex-direction: column;
        align-items: stretch;
        gap: 10px;
    }

    .date-navigation h3 {
        order: -1;
        text-align: center;
    }

    .button-container {
        flex-direction: column;
    }

    .save-plan-button, .back-button {
        width: 100%;
    }
}
</style>
