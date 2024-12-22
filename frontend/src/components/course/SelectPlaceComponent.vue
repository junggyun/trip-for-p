<script setup>
import {computed, defineEmits, defineProps, onMounted, ref, watch} from "vue";
import ItineraryComponent from "@/components/course/ItineraryComponent.vue";
import SearchPlaceComponent from "@/components/course/SearchPlaceComponent.vue";
import router from "@/router";
import {useRoute} from "vue-router";
import {
    createCourseAPI,
    getCityListByProvinceAPI,
    getProvinceListAPI,
    updateCourseAPI
} from "@/api/course";
import GoogleMapComponent from "@/components/course/GoogleMapComponent.vue";

const route = useRoute();

const props = defineProps({
    title: String,
    province: String,
    city: String,
    startDate: String,
    endDate: String,
    spots: {
        type: Array,
        default: () => []
    },
    mode: {
        type: String,
        default: 'create'
    },
});

const title = ref(props.title || '');
const provinceList = ref([]);
const cityList = ref([]);
const province = ref(props.province || '');
const city = ref(props.city || '');
const selectedPlaces = ref({});  // 객체로 초기화
const currentDateIndex = ref(0);
const deletedPlaces = ref([]);
const GOOGLE_MAP_API_KEY = process.env.VUE_APP_GOOGLE_MAP_API_KEY;
const emit = defineEmits(['back-to-day']);

/**
 * 날짜 목록
 */
const dates = computed(() => {
    const start = new Date(props.startDate);
    const end = new Date(props.endDate);
    const dateArray = [];
    for (let dt = new Date(start); dt <= end; dt.setDate(dt.getDate() + 1)) {
        dateArray.push(new Date(dt));
    }
    return dateArray;
});

/**
 * 현재 날짜
 */
const currentDate = computed(() => {
    if (dates.value.length === 0) {
        return null;
    }
    return dates.value[currentDateIndex.value].toISOString().split('T')[0];
});

const goToNextDate = () => {
    if (currentDateIndex.value < dates.value.length - 1) {
        currentDateIndex.value++;
    }
};

const goToPreviousDate = () => {
    if (currentDateIndex.value > 0) {
        currentDateIndex.value--;
    }
};

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

const updateMemo = (date, index, memo) => {
    selectedPlaces.value[date][index].memo = memo;
};

const reorderPlaces = (date) => {
    selectedPlaces.value[date].forEach((item, index) => {
        item.sequence = index + 1;
    });
};

const createCourse = async () => {
    try {
        const spots = Object.entries(selectedPlaces.value)
        .filter(([, places]) => Array.isArray(places)) // 배열인 것만 필터링
        .flatMap(([date, places]) =>
            places.map(place => ({
                place: {
                    mapPlaceId: place.place.id,
                },
                tripDate: date,
                sequence: place.sequence,
                memo: place.memo
            }))
        );

        const createCourseRequest = {
            startDate: props.startDate,
            endDate: props.endDate,
            title: title.value,
            province: province.value,
            city: city.value,
            spots: spots
        };

        const response = await createCourseAPI(createCourseRequest);
        const courseId = response.data.id;
        await router.push(`/course/${courseId}`);
    } catch (error) {
        console.error(error);
    }
};

const updatePlan = async () => {
    try {
        const spots = [
            ...deletedPlaces.value,
            ...Object.entries(selectedPlaces.value)
            .filter(([, places]) => Array.isArray(places))
            .flatMap(([date, places]) =>
                places.map(place => ({
                    id: place.id || null,
                    action: place.id ? 'update' : 'create',
                    place: {
                        mapPlaceId: place.place.id,
                    },
                    tripDate: date,
                    sequence: place.sequence,
                    memo: place.memo
                }))
            )
        ];

        const updateCourseRequest = {
            startDate: props.startDate,
            endDate: props.endDate,
            title: title.value,
            province: province.value,
            city: city.value,
            spots: spots
        };

        const response = await updateCourseAPI(route.params.courseId, updateCourseRequest);
        const courseId = response.data.id;
        await router.push(`/course/${courseId}`);
    } catch (error) {
        console.error(error);
    }
};

const handleBack = () => {
    if (props.mode === 'create') {
        emit('back-to-day');
    } else {
        router.go(-1);
    }
};

const isSaveButtonEnabled = computed(() => {
    const hasPlaces = Object.values(selectedPlaces.value)
    .filter(Array.isArray)
    .some(places => places.length > 0);

    return title.value.trim() !== '' ||
        (props.mode === 'update' && hasPlaces);
});

const getProvinceList = async function () {
    try {
        return await getProvinceListAPI();
    } catch (error) {
        console.error(error);
    }
};

const getCityListByProvinceList = async function () {
    try {
        return await getCityListByProvinceAPI(province.value);
    } catch (error) {
        console.error(error);
    }
}

watch(province, async (newValue) => {
    try {
        city.value = '';
        if (newValue) {
            const response = await getCityListByProvinceList();
            cityList.value = response.data;
        } else {
            cityList.value = [];
        }
    } catch (error) {
        console.error('Failed to fetch cities:', error);
    }
});

onMounted(() => {
    const start = new Date(props.startDate);
    const end = new Date(props.endDate);

    // selectedPlaces 초기화
    selectedPlaces.value = {};

    // 각 날짜별로 빈 배열 초기화
    for (let dt = new Date(start); dt <= end; dt.setDate(dt.getDate() + 1)) {
        const dateString = dt.toISOString().split('T')[0];
        selectedPlaces.value[dateString] = [];
    }

    // 기존 spots가 있다면 초기화
    if (props.spots && props.spots.length > 0) {
        props.spots.forEach(spot => {
            initPlace(spot, new Date(spot.tripDate));
        });
    }
});

onMounted(async () => {
    try {
        const response = await getProvinceList();
        provinceList.value = response.data;

        if (province.value) {
            const cityResponse = await getCityListByProvinceList();
            cityList.value = cityResponse.data;
        }
    } catch (error) {
        console.error('Failed to fetch data:', error);
    }
});

</script>

<template>
    <div class="trip-planner">
        <input v-model="title" placeholder="여행 제목" class="title-input">
        <div class="location-selectors">
            <select
                v-model="province"
                class="location-select"
            >
                <option value="">지역을 선택하세요</option>
                <option
                    v-for="prov in provinceList"
                    :key="prov"
                    :value="prov"
                >
                    {{ prov }}
                </option>
            </select>
            <select
                v-model="city"
                class="location-select"
                :disabled="!province"
            >
                <option value="">도시를 선택하세요</option>
                <option
                    v-for="cty in cityList"
                    :key="cty"
                    :value="cty"
                >
                    {{ cty }}
                </option>
            </select>
        </div>
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
                    :apiKey="GOOGLE_MAP_API_KEY"
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
                @click="createCourse"
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
    z-index: 1001;
    background: white;
    padding: 16px;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(92, 106, 196, 0.1);
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
    border-color: #5c6ac4;
}

.date-navigation {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding: 12px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(92, 106, 196, 0.1);
}

.nav-button {
    padding: 10px 20px;
    font-size: 14px;
    font-weight: bold;
    color: #fff;
    background: linear-gradient(135deg, #5c6ac4 0%, #8794d8 100%);
    border: none;
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.3s ease;
}

.nav-button:hover:not(:disabled) {
    background: linear-gradient(135deg, #4f5bb4 0%, #7683c7 100%);
    transform: translateY(-1px);
    box-shadow: 0 2px 8px rgba(92, 106, 196, 0.2);
}

.nav-button:disabled {
    background: linear-gradient(135deg, rgba(92, 106, 196, 0.5) 0%, rgba(135, 148, 216, 0.5) 100%);
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
    background: linear-gradient(135deg, #5c6ac4 0%, #8794d8 100%);
    border: none;
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 2px 4px rgba(92, 106, 196, 0.2);
}

.save-plan-button:hover:not(:disabled),
.back-button:hover {
    background: linear-gradient(135deg, #4f5bb4 0%, #7683c7 100%);
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(92, 106, 196, 0.3);
}

.save-plan-button:disabled {
    background: linear-gradient(135deg, rgba(92, 106, 196, 0.5) 0%, rgba(135, 148, 216, 0.5) 100%);
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
.location-selectors {
    display: flex;
    gap: 12px;
    margin-bottom: 20px;
}

.location-select {
    flex: 1;
    padding: 10px;
    font-size: 1em;
    border: 2px solid #ddd;
    border-radius: 8px;
    transition: border-color 0.3s ease;
}

.location-select:focus {
    outline: none;
    border-color: #5c6ac4;
}

.location-select:disabled {
    background-color: #f5f5f5;
    cursor: not-allowed;
}

@media (max-width: 600px) {
    .location-selectors {
        flex-direction: column;
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
