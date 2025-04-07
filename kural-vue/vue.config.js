const { defineConfig } = require('@vue/cli-service')
const MonacoWebpackPlugin = require('monaco-editor-webpack-plugin')

module.exports = defineConfig({
  devServer: {
    host: '127.0.0.1',
    port: 8080,
  },
  transpileDependencies: true,  // 修正拼写错误
  chainWebpack(config) {       // 使用链式配置
    config.plugin('monaco').use(MonacoWebpackPlugin, [{
      languages: ['yaml','go', 'python']
    }])
  }
})