import request from '@/utils/request'

export const addPortScanTask = (data) => {
    return request.post('/task/portScanTask', data)
}
export const addWeakPasswordScanTask = (data) => {
    return request.post('/task/weakPasswordScanTask', data)
}
export const addWebVulnScanTask = (data) => {
    return request.post('/task/webVulnScanTask', data)
}


export const selectBaseScanTaskList = () => {
    return request.get('/task/baseScanTask/list')
}  
export const selectTaskDetail = (taskId) => {
    return request.get(`/task/${taskId}/detail`)
}   
export const selectTaskResult = (taskId) => {
    return request.get(`/task/${taskId}/result`)
}  



export const startTask = (taskId) => {
    return request.get(`/task/${taskId}/start`)
}   



