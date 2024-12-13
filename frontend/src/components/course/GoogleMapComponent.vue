<script setup>
/* global google */
import {defineProps, nextTick, ref, watch} from 'vue';
import {GoogleMap, Marker, Polyline} from 'vue3-google-map';

const props = defineProps({
    places: {
        type: Array,
        required: true
    },
    date: {
        type: String,
        required: true
    },
    apiKey: {
        type: String,
        required: true
    }
});

const mapCenter = ref({ lat: 36.4, lng: 127.8 });
const mapRef = ref(null);
const markers = ref([]);
const mapZoom = ref(7);
const path = ref([]);

const createMarkers = () => {
    markers.value = props.places.map((place) => {
        return {
            position: {
                lat: place.place.latitude || 0,
                lng: place.place.longitude || 0
            },
            title: place.place.name,
            icon: {
                // SVG Path for a map pin
                path: 'M12,0C7.6,0,3,3.4,3,9c0,5.3,8,13.4,8.3,13.7c0.2,0.2,0.4,0.3,0.7,0.3s0.5-0.1,0.7-0.3C13,22.4,21,14.3,21,9 C21,3.4,16.4,0,12,0z',
                fillColor: '#FF385C',
                fillOpacity: 1,
                strokeColor: '#FFFFFF',
                strokeWeight: 2,
                scale: 1.5,
                anchor: new google.maps.Point(12, 24),
                labelOrigin: new google.maps.Point(12, 10)
            },
            label: {
                text: place.sequence + '',
                color: 'white',
                fontSize: '14px',
                fontWeight: 'bold',
                fontFamily: 'Arial'
            }
        };
    });

    // Polyline 경로 업데이트
    path.value = props.places.map(place => ({
        lat: place.place.latitude || 0,
        lng: place.place.longitude || 0
    }));

    adjustMapBounds();
};

const adjustMapBounds = () => {
    if (!mapRef.value || props.places.length === 0) {
        mapZoom.value = 7;
        return;
    }

    const bounds = new google.maps.LatLngBounds();
    path.value.forEach(point => {
        bounds.extend(new google.maps.LatLng(point.lat, point.lng));
    });

    const padding = {
        top: 100,
        right: 100,
        bottom: 100,
        left: 100
    };

    mapRef.value?.map.fitBounds(bounds, padding);

    google.maps.event.addListenerOnce(mapRef.value?.map, 'bounds_changed', () => {
        const currentZoom = mapRef.value?.map.getZoom();
        if (currentZoom > 16) {
            mapRef.value?.map.setZoom(16);
        }
        mapZoom.value = mapRef.value?.map.getZoom();
    });
};

let prevPlaces = [...props.places]
watch(() => props.places, () => {
    nextTick(() => {
        if (!areArraysEqual(prevPlaces, props.places)) {
            console.log("장소 데이터 변경됨:", props.places);
            createMarkers();
            prevPlaces = [...props.places];
        }
    });
}, { deep: true });

// path가 변경될 때마다 bounds 재계산
watch(() => path.value, () => {
    nextTick(() => {
        if (path.value.length > 0) {
            adjustMapBounds();
        }
    });
}, { deep: true });

function areArraysEqual(arr1, arr2) {
    return arr1.length === arr2.length && arr1.every((val, index) => val === arr2[index]);
}

watch(() => mapRef.value?.ready, (ready) => {
    if (!ready) return;
    mapRef.value?.map.setOptions({
        gestureHandling: 'cooperative'
    })
    createMarkers();
});
</script>

<template>
    <div class="map-container">
        <GoogleMap
            ref="mapRef"
            :api-key="apiKey"
            mapId="DEMO_MAP_ID"
            style="width: 100%; height: 100%"
            :center="mapCenter"
            :zoom="mapZoom"
            class="map"
        >
            <Marker
                v-for="marker in markers"
                :key="marker.title"
                :options="marker"
            />
            <Polyline
                v-if="path.length >= 2"
                :options="{
                    path: path,
                    strokeColor: '#FF385C',
                    strokeWeight: 3,
                    strokeOpacity: 1,
                    geodesic: true,

                }"
            />
        </GoogleMap>
    </div>
</template>

<style scoped>
.map-container {
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    margin-bottom: 20px;
}

.map {
    border-radius: 8px 8px 0 0;
}
</style>
