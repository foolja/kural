import request from '@/utils/request'

export const getSystemHardwareInfo = () => {
    return request.get('/system/hardware/Info')
}