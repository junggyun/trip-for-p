import {authInstance} from "@/api/instance";

const searchPlacesAPI = function (request) {
    return authInstance.get(
        `/api/google-maps/search`, {
            params: {
                textQuery: request.textQuery,
                pageSize: request.pageSize,
                pageToken: request.pageToken
            }
        }
    )
}

export {
    searchPlacesAPI
}
