import store from "@/store";
import router from "@/router";
import jwtDecoder from 'vue-jwt-decode';
import {refreshTokenAPI} from "@/api/user.js";

const handleError = (error) => {
    const status = error.response?.status;

    switch (status) {
        case 502:
            window.location.href = '/502.html';
            break;
    }

    return Promise.reject(error);
};

const setInterceptors = function (instance) {

    instance.interceptors.response.use(
        (response) => {
            return response;
        },
        (error) => {
            return handleError(error)
        }
    );

    return instance
}

const setAuthInterceptors = function (instance) {
    instance.interceptors.request.use(
        async (config) => {
            const token = store.state.accessToken
            if (token) {
                const decodedToken = jwtDecoder.decode(token);
                const currentTime = Date.now() / 1000;  // 현재 시간(초 단위)
                if (decodedToken.exp < currentTime) {
                    try {
                        const response = await refreshTokenAPI();
                        const newToken = response.headers.access.split(" ")[1];
                        store.commit('setAccessToken', newToken);
                        config.headers.Authorization = 'Bearer ' + newToken;
                    } catch (error) {
                        store.commit('clearData');
                        alert('세션이 만료되었습니다.');
                        await router.push('/');
                        return Promise.reject('Token expired');
                    }
                } else {
                    // 토큰이 유효하면 Authorization 헤더에 추가
                    config.headers.Authorization = 'Bearer ' + token;
                }
            }

            return config;
        },
        (error) => {
            return Promise.reject(error)
        }
    )

    instance.interceptors.response.use(
        (response) => {
            return response;
        },
        async (error) => {
            const originalRequest = error.config;
            if (error.response.status === 401) {

                try {
                    const response = await refreshTokenAPI();
                    const newToken = response.headers.access.split(" ")[1];
                    store.commit('setAccessToken', newToken);
                    originalRequest.headers.Authorization = 'Bearer ' + newToken;
                    return instance(originalRequest);
                } catch (refreshError) {
                    store.commit('clearData');
                    alert('세션이 만료되었습니다.');
                    await router.push('/');
                    return Promise.reject('Token expired');
                }
            }

            return handleError(error);
        }
    );
    return instance
}


export {setInterceptors, setAuthInterceptors}
