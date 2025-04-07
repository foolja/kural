import axios from 'axios'
import { Message } from 'element-ui'

// 创建axios实例，将来对创建出来的实例进行自定义配置
// 好处：不会污染原始的axios实例
const instance = axios.create({
    baseURL: 'http://127.0.0.1:8888/api/v1/',
    timeout: 5000,
    withCredentials: true // 关键：允许携带Cookie
});

// 添加请求拦截器
instance.interceptors.request.use(function (config) {
    // 在发送请求之前做些什么
    return config;
}, function (error) {
    // 对请求错误做些什么
    return Promise.reject(error);
});

// 添加响应拦截器
instance.interceptors.response.use(function (response) {
    // 2xx 范围内的状态码都会触发该函数。
    // 对响应数据做点什么
    return response;
}, function (error) {
    // 超出 2xx 范围的状态码都会触发该函数。
    // 对响应错误做点什么
    
    return Promise.reject(error);
});

// 导出配置好的实例
export default instance;
