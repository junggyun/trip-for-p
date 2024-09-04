import axios from "axios";
import {setInterceptors} from "@/api/interceptor";

const instance = axios.create();

const createAuthInstance = function () {
    const instance = axios.create();
    return setInterceptors(instance);
};

const authInstance = createAuthInstance();

const getPlanListAPI = function (request) {
    return instance.get(`/api/plans?area=${request.area}&size=${request.size}&page=${request.page}`)
}

const processAlanAPI = function (request) {
    return instance.get(
        `/api/alan?content=${request.content}&client_id=${request.clientId}`);
}

const getPlanAPI = function (id) {
    return instance.get(`/api/plans/${id}`);
};

const createPlanAPI = function (request) {
    return authInstance.post(`/api/plans`, request);
}

const createUserAPI = function (request) {
    return instance.post(`/api/users/registration`, request);
}

const loginAPI = function (formData) {
    return instance.post(`/api/users/signin`, formData,
        {
            headers: {
                "Content-Type": 'multipart/form-data'
            }
        })
};

//[회원가입] 인증코드 이메일 전송
const sendVerificationEmailAPI = function (request) {
    return instance.post(`/api/mails/send-verification`, request);
}

//[회원가입] 인증코드 검증
const verifyEmailAPI = function (request) {
    return instance.post(`/api/mails/verification`, request);
}

export {processAlanAPI, createPlanAPI, createUserAPI, loginAPI, getPlanAPI, getPlanListAPI, sendVerificationEmailAPI, verifyEmailAPI}
