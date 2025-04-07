<template>
  <div class="login-container">
    <div class="login-box">
      <h2 class="title">网络空间测绘系统</h2>
      <el-form :model="loginForm" :rules="rules" ref="loginForm" class="login-form">
        <el-form-item prop="username">
          <el-input 
            v-model="loginForm.username" 
            placeholder="请输入用户名"
            prefix-icon="el-icon-user"
          ></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            type="password"
            v-model="loginForm.password"
            placeholder="请输入密码"
            prefix-icon="el-icon-lock"
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button 
            type="primary" 
            @click="handleLogin"
            class="login-btn"
          >登录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { mapActions } from 'vuex'

export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: '',
        password: ''  
      },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
      },
      isLoggedIn: false
    }
  },
  methods: {
     ...mapActions(['login']), 
    async handleLogin() {
        await this.$refs.loginForm.validate()
        await this.login(this.loginForm)
        this.$refs.loginForm.resetFields()
    }
  }
}
</script>

<style scoped lang="less">
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e6f7ff 0%, #f0faff 100%);
  
  .login-box {
    width: 400px;
    padding: 40px;
    background: rgba(255, 255, 255, 0.95);
    border-radius: 8px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 6px 24px rgba(0, 0, 0, 0.12);
    }

    .title {
      text-align: center;
      margin-bottom: 30px;
      color: #1890ff;
      font-size: 24px;
      letter-spacing: 2px;
    }

    .login-form {
      /deep/ .el-input__inner {
        height: 45px;
        border-radius: 4px;
      }

      .login-btn {
        width: 100%;
        height: 45px;
        font-size: 16px;
        letter-spacing: 2px;
      }
    }
  }
}
</style> 