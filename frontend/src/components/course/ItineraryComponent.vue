<script setup>
// script 부분은 동일하게 유지
import { ref, computed, defineProps, defineEmits } from 'vue';
import draggable from 'vuedraggable';

const props = defineProps({
    places: {
        type: Array,
        required: true
    },
    routeInfo: {
        type: Object,
        default: () => ({})
    },
    currentDate: {
        type: String,
        required: true
    }
});

const emit = defineEmits(['update:memo', 'reorder', 'delete']);

const formatDuration = (durationInSeconds) => {
    if (durationInSeconds == null) return 'N/A';

    const hours = Math.floor(durationInSeconds / 3600);
    const minutes = Math.floor((durationInSeconds % 3600) / 60);
    const seconds = durationInSeconds % 60;

    const parts = [];

    if (hours > 0) {
        parts.push(`${hours}시간`);
    }
    if (minutes > 0) {
        parts.push(`${minutes}분`);
    }
    if (seconds > 0 || parts.length === 0) {
        parts.push(`${seconds}초`);
    }

    return parts.join(' ');
};

const dragOptions = ref({
    animation: 200,
    group: "description",
    disabled: false,
    ghostClass: "ghost"
});

const updateMemo = (index, event) => {
    emit('update:memo', index, event.target.value);
};

const deletePlace = (index) => {
    emit('delete', index);
};

const onEnd = (event) => {
    emit('reorder', props.currentDate, event.oldIndex, event.newIndex);
};

const totalDuration = computed(() => {
    return props.routeInfo?.summary?.duration ?? 0;
});
</script>

<template>
    <div class="itinerary">
        <draggable
            :list="places"
            v-bind="dragOptions"
            @end="onEnd"
            item-key="sequence"
            handle=".drag-handle"
        >
            <template #item="{ element, index }">
                <div class="itinerary-item">
                    <div class="place-item">
                        <div class="drag-handle">&#9776;</div>
                        <div class="place-content">
                            <div class="place-header">
                                <div class="place-info">
                                    <span class="sequence">{{ element.sequence }}</span>
                                    <div class="place-main-info">
                                        <h3>{{ element.place.name }}</h3>
                                        <p class="address">{{ element.place.address }}</p>
                                        <div class="place-details">
                                            <span class="category">{{ element.place.category }}</span>
                                            <span class="rating">
                                                <span class="rating-stars">★</span>
                                                {{ element.place.rating }}
                                                <span class="review-count">({{ element.place.reviewCount }})</span>
                                            </span>
                                        </div>
                                    </div>
                                </div>
                                <button @click="deletePlace(index)" class="delete-btn" title="삭제">
                                    ×
                                </button>
                            </div>
                            <div class="memo-section">
                                <textarea
                                    :value="element.memo"
                                    @input="updateMemo(index, $event)"
                                    placeholder="메모를 입력하세요 (예: 예약시간, 주문할 메뉴 등)"
                                    class="memo-input"
                                ></textarea>
                            </div>
                        </div>
                    </div>
                </div>
            </template>
        </draggable>
        <div v-if="totalDuration > 0" class="total-duration">
            총 예상 소요 시간: {{ formatDuration(totalDuration) }}
        </div>
    </div>
</template>

<style scoped>
.itinerary {
    margin-top: 20px;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

.itinerary-item {
    margin-bottom: 16px;
    transition: all 0.2s ease;
}

.place-item {
    display: flex;
    background-color: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(92, 106, 196, 0.1);
    overflow: hidden;
    transition: all 0.3s ease;
}

.place-item:hover {
    box-shadow: 0 4px 12px rgba(92, 106, 196, 0.15);
    transform: translateY(-2px);
}

.drag-handle {
    padding: 16px 12px;
    color: #8794d8;
    cursor: move;
    background-color: rgba(92, 106, 196, 0.05);
    transition: all 0.2s ease;
}

.drag-handle:hover {
    color: #5c6ac4;
    background-color: rgba(92, 106, 196, 0.1);
}

.place-content {
    flex: 1;
    padding: 16px;
}

.place-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 12px;
}

.place-info {
    display: flex;
    align-items: flex-start;
    flex: 1;
}

.sequence {
    font-size: 20px;
    font-weight: 600;
    color: #5c6ac4;
    margin-right: 16px;
    min-width: 24px;
}

.place-main-info {
    flex: 1;
}

.place-main-info h3 {
    font-size: 1.125rem;
    font-weight: 600;
    color: #111827;
    margin: 0 0 4px 0;
}

.address {
    color: #6b7280;
    font-size: 0.925rem;
    margin: 0 0 8px 0;
}

.place-details {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-top: 8px;
}

.category {
    background-color: rgba(92, 106, 196, 0.1);
    color: #5c6ac4;
    padding: 4px 8px;
    border-radius: 4px;
    font-size: 0.875rem;
}

.rating {
    display: flex;
    align-items: center;
    gap: 4px;
    color: #4b5563;
    font-size: 0.875rem;
}

.rating-stars {
    color: #5c6ac4;
}

.review-count {
    color: #9ca3af;
}

.memo-section {
    margin-top: 12px;
}

.memo-input {
    width: 100%;
    min-height: 80px;
    padding: 12px;
    border: 1px solid #e5e7eb;
    border-radius: 6px;
    background-color: #f9fafb;
    font-size: 0.925rem;
    resize: vertical;
    transition: all 0.2s ease;
}

.memo-input:focus {
    outline: none;
    border-color: #5c6ac4;
    background-color: white;
    box-shadow: 0 0 0 3px rgba(92, 106, 196, 0.1);
}

.memo-input::placeholder {
    color: #9ca3af;
}

.delete-btn {
    padding: 4px 8px;
    font-size: 1.25rem;
    line-height: 1;
    color: #8794d8;
    background: transparent;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    transition: all 0.2s ease;
}

.delete-btn:hover {
    color: #5c6ac4;
    background-color: rgba(92, 106, 196, 0.1);
}

.duration-info {
    margin: 12px 0;
    padding: 12px;
    background-color: rgba(92, 106, 196, 0.05);
    border-radius: 6px;
    color: #5c6ac4;
    font-size: 0.925rem;
    display: flex;
    align-items: center;
    gap: 8px;
}

.duration-icon {
    font-size: 1.125rem;
    color: #8794d8;
}

.total-duration {
    margin-top: 24px;
    padding: 16px;
    background-color: rgba(92, 106, 196, 0.05);
    border-radius: 8px;
    font-weight: 600;
    text-align: right;
    color: #5c6ac4;
}

.ghost {
    opacity: 0.5;
    background: rgba(92, 106, 196, 0.1);
}

@media (max-width: 640px) {
    .place-item {
        flex-direction: column;
    }

    .drag-handle {
        padding: 8px;
        text-align: center;
    }

    .place-content {
        padding: 12px;
    }
}
</style>
