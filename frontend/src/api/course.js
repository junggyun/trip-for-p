import {authInstance, instance} from "@/api/instance";

/**
 * Course API
 */
// 코스 등록
const createCourseAPI = function (request) {
    return authInstance.post(`/api/courses`, request);
}
// 코스 수정
const updateCourseAPI = function (id, request) {
    return authInstance.put(`/api/courses/${id}`, request);
};

// 코스 삭제
const deleteCourseAPI = function (id) {
    return authInstance.delete(`/api/courses/${id}`);
};

// 코스 단일 조회
const getCourseAPI = function (id) {
    return instance.get(`/api/courses/${id}`);
};

// 코스 목록 조회
const getCourseListAPI = function (request) {
    return instance.get(
        `/api/courses`, {
            params: {
                keyword: request.keyword,
                size: request.size,
                page: request.page
            }
        })
}
// 나의 코스 목록 조회
const getMyCourseListAPI = function (request) {
    return authInstance.get(
        `/api/courses/me`, {
            params: {
                size: request.size,
                page: request.page
            }
        });
};

// 인기 장소 조회
const getPopularPlaceListAPI = function () {
    return instance.get(`/api/courses/popular-places`);
};

// 코스 좋아요
const likeCourseAPI = function (id) {
    return authInstance.post(`/api/course-likes/courses/${id}`);
};

// 코스 좋아요 유무 체크
const checkCourseLikeAPI = function (courseId) {
    return authInstance.get(`/api/course-likes/check`, {
        params: {
            courseId: courseId
        }
    });
};

// 내가 좋아요한 코스 목록 조회
const getMyLikedCoursesAPI = function (request) {
    return authInstance.get('/api/course-likes/me', {
        params: {
            size: request.size,
            page: request.page
        }
    });
}

/**
 * Region API
 */
// 상위지역 조회
const getProvinceListAPI = function () {
    return authInstance.get('/api/regions/provinces');
}
// 하위지역 조회
const getCityListByProvinceAPI = function (province) {
    return authInstance.get(`/api/regions/cities`, {
        params: {
            province: province
        }
    })
}

export {
    createCourseAPI,
    updateCourseAPI,
    deleteCourseAPI,
    getCourseAPI,
    getCourseListAPI,
    getMyCourseListAPI,
    getPopularPlaceListAPI,
    likeCourseAPI,
    checkCourseLikeAPI,
    getMyLikedCoursesAPI,
    getProvinceListAPI,
    getCityListByProvinceAPI
}
