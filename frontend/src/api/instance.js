import axios from "axios";
import {setInterceptors} from "@/api/interceptor";

export const instance = axios.create();

const createAuthInstance = function () {
    const instance = axios.create();
    return setInterceptors(instance);
};

export const authInstance = createAuthInstance();
