import Vue from 'vue';
import Vuex from 'vuex';
import { Message } from 'element-ui';
import router from '@/router';

Vue.use(Vuex);

export default new Vuex.Store({
    state: {
        isAuthenticated: sessionStorage.getItem("isAuthenticated") || false,
        username: sessionStorage.getItem("username") || null,
        userId: sessionStorage.getItem("userId") || null
    },
    mutations: {
        SET_AUTHENTICATED(state, isAuthenticated) {
            state.isAuthenticated = isAuthenticated;
            sessionStorage.setItem("isAuthenticated",isAuthenticated)
        },
        SET_USERNAME(state, username) {
            state.username = username;
            sessionStorage.setItem("username",username)
        },
        SET_USER_ID(state, userId) {
            state.userId = userId;
            sessionStorage.setItem("userId",userId)
        }
    },
    actions: {
        async login({ commit }, user) {
            try {
                const response = await import('@/api/login').then((module) => module.login(user))
                if (response.data.code === '200' && response.data.msg === '登录成功' && response.data.data != null) {
                    commit('SET_AUTHENTICATED', true)
                    commit('SET_USERNAME', user.username);
                    commit('SET_USER_ID', response.data.data)
                    Message.success('登录成功')
                    // 重定向到首页或其他页面
                    router.push('/main').catch(err => { console.log(err) });
                    return response
                } else {
                    commit('SET_AUTHENTICATED', false)
                    commit('SET_USERNAME', null)
                    commit('SET_USER_ID', null)
                    Message.error('登录失败：' + response.data.msg);
                    return null
                }
            } catch (error) {
                commit('SET_AUTHENTICATED', false);
                commit('SET_USERNAME', null);
                commit('SET_USER_ID', null)
                Message.error('登录失败：' + error.message);
                return null
            }
        },
        async logout({ commit }) {
            try {
                await import('@/api/login').then((module) => module.logout());
                commit('SET_AUTHENTICATED', false);
                commit('SET_USERNAME', null);
                commit('SET_USER_ID', null)
                sessionStorage.clear()
                Message.success('登出成功');
                // 重定向到登录页面
                router.push('/login');
            } catch (error) {
                Message.error('注销失败hhh' + error.message);
            }
        },
        async checkAuth({ commit }) {
            try {
                const response = await import('@/api/login').then((module) => module.checkAuth());
                if (response.isAuthenticated) {
                    commit('SET_AUTHENTICATED', true);
                    commit('SET_USERNAME', null);
                } else {
                    commit('SET_AUTHENTICATED', false);
                    commit('SET_USERNAME', null);
                    commit('SET_USER_ID', null)
                }
            } catch (error) {
                commit('SET_AUTHENTICATED', false);
                commit('SET_USER', null);
                commit('SET_USER_ID', null)
            }
        }
    },
    getters: {
        isAuthenticated: state => state.isAuthenticated,
        username: state => state.username,
        userId: state => state.userId
    }
});
