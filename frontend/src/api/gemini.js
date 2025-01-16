import {authInstance} from "@/api/instance";

const recommendRouteAPI = function (request) {
    return authInstance.post(
        `/api/ai`, request
    )
};

export {
    recommendRouteAPI
}
