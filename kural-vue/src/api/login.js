import request from '@/utils/request'

export const login = (user) => {
    return request.post('/login', user)
}

export const logout = () => {
    return request.post('/logout')
}
export const checkAuth = () => {
    return request.get('/check-login')
} 