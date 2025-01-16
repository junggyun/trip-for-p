import {authInstance} from "@/api/instance";

const recommendRouteAPI = function (request) {
    return authInstance.post(
        `/api/ai`, request
    )
};

const getChanceAPI = function () {
    return authInstance.get(
        `/api/ai/chance`
    )
};

export {
    recommendRouteAPI,
    getChanceAPI
}
