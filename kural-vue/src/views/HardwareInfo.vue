<template>
<div class="hardware-container">
    <!-- CPU信息 -->
    <div class="row">
      <div class="card gauge-card" >
        <h3>CPU 状态</h3>
        <div class="cpu-group">
          <!-- 使用率仪表盘 -->
          <div class="gauge-chart">
            <div ref="cpuGauge" class="gauge-echart"></div>
          </div>
          <!-- 核心数展示 -->
          <div class="core-info">
            <div class="core-item">
              <div class="core-label">逻辑核心</div>
              <div class="core-value">{{ cpuInfo.cpuLogicalProcessorCount }}</div>
            </div>
            <div class="core-item">
              <div class="core-label">物理核心</div>
              <div class="core-value">{{ cpuInfo.cpuPhysicalProcessorCount }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 内存信息 -->
    <div class="row">
      <div class="card gauge-card">
        <h3>内存使用</h3>
        <div class="gauge-chart">
          <div ref="memoryGauge" class="gauge-echart"></div>
          <div class="gauge-footer">
            <span>总内存: {{ memoryInfo.totalSize }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 磁盘信息 -->
    <div class="row">
      <div class="card gauge-card" v-for="(size, drive) in diskInfo.totalSize" :key="drive">
        <h3>磁盘 {{ drive }}</h3>
        <div class="gauge-chart">
          <div :ref="`diskGauge${drive}`" class="gauge-echart"></div>
          <div class="gauge-footer">
            <span>总空间: {{ size.toFixed(1) }} GB</span>
            <span>可用空间: {{ diskInfo.usableSize[drive].toFixed(1) }} GB</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getSystemHardwareInfo } from '@/api/systemInfo'
export default {
  name: 'Hardware',
  data() {
    return {
      // 示例数据
      cpuInfo: {
        cpuLogicalProcessorCount: 32,
        cpuPhysicalProcessorCount: 16,
        usage: 45 // 新增CPU使用率示例数据
      },
      memoryInfo: {
        totalSize: '15.7 GiB',
        usage: 65 // 示例使用率
      },
      diskInfo: {
        totalSize: {
          "C:\\": 400.00097274780273,
          "D:\\": 551.6445274353027
        },
        usableSize: {
          "C:\\": 272.03118896484375,
          "D:\\": 302.020694732666
        }
      },
      cpuChart: null,
      memoryChart: null,
      diskCharts: {}
    }
  },
  mounted() {
    this.initHardareInfo()
    
  },
  beforeDestroy() {
    // 关闭WebSocket
    if (this.ws) {
      this.ws.close()
    }
    // 销毁图表实例
    if (this.cpuChart) {
      this.cpuChart.dispose()
    }
    if (this.memoryChart) {
      this.memoryChart.dispose()
    }
    Object.values(this.diskCharts).forEach(chart => chart.dispose())
},
  methods: {
    async initHardareInfo(){
      const response = await getSystemHardwareInfo()
      if (response.data.code === '200' && response.data.msg === '查询成功' && response.data.data != null){
          this.cpuInfo = response.data.data.cpuInfo
          this.memoryInfo = response.data.data.memoryInfo
          this.diskInfo = response.data.data.diskInfo
          this.cpuInfo.usage = 0;
          this.memoryInfo.usage = 0;
          this.initGauges()
          this.ws = new WebSocket(`ws://127.0.0.1:8888/system-Info`);
          this.ws.onmessage = (event) => {
            try {
                const systemInfoData = JSON.parse(event.data);
                this.cpuInfo.usage = systemInfoData.cpuUsageRate
                this.memoryInfo.usage = systemInfoData.memoryUsageRate
                // 数据更新后触发图表更新
                this.updateCpuGauge();
                this.updateMemoryGauge();
              } catch (e) {
                console.error("[错误] WebSocket 消息解析失败:", e);
            }
          };
          
          
        }else{
          this.$message.error(response.data.msg);
        }
    },
    initGauges() {
       // 初始化时保存图表实例
      this.cpuChart = echarts.init(this.$refs.cpuGauge)
      this.memoryChart = echarts.init(this.$refs.memoryGauge)
      
      Object.keys(this.diskInfo.totalSize).forEach(drive => {
        this.diskCharts[drive] = echarts.init(this.$refs[`diskGauge${drive}`][0])
      })
      
      // 初始渲染
      this.updateAllGauges()
    },
    // 新增统一更新方法
    updateAllGauges() {
      this.updateCpuGauge()
      this.updateMemoryGauge()
      this.updateDiskGauges()
    },
    updateCpuGauge() {
      const option = this.getGaugeOption(this.cpuInfo.usage, '#e74c3c')
      this.cpuChart.setOption(option)
    },
    updateMemoryGauge() {
      const option = this.getGaugeOption(this.memoryInfo.usage, '#6ab04c')
      this.memoryChart.setOption(option)
    },
    updateDiskGauges() {
      Object.keys(this.diskInfo.totalSize).forEach(drive => {
        const total = this.diskInfo.totalSize[drive]
        const used = total - this.diskInfo.usableSize[drive]
        const usage = (used / total * 100).toFixed(1)
        const option = this.getGaugeOption(usage, '#2980b9')
        this.diskCharts[drive].setOption(option)
      })
    },
    getGaugeOption(value, color, title = '') {
      return {
        title: {
          show: title !== '',
          text: title,
          left: 'center',
          top: '5%',
          textStyle: {
            color: '#7f8c8d',
            fontSize: 14
          }
        },
        series: [{
          type: 'gauge',
          center: ['50%', '60%'],
          startAngle: 200,
          endAngle: -20,
          min: 0,
          max: 100,
          splitNumber: 5,
          itemStyle: {
            color: color
          },
          progress: {
            show: true,
            width: 18
          },
          pointer: {
            show: false
          },
          axisLine: {
            lineStyle: {
              width: 18
            }
          },
          axisTick: {
            distance: -25,
            splitNumber: 5,
            lineStyle: {
              width: 1,
              color: '#999'
            }
          },
          splitLine: {
            distance: -30,
            length: 14,
            lineStyle: {
              width: 2,
              color: '#999'
            }
          },
          axisLabel: {
            distance: -20,
            color: '#999',
            fontSize: 12
          },
          anchor: {
            show: false
          },
          title: {
            show: false
          },
           detail: {
            valueAnimation: true,
            width: '60%',
            lineHeight: 40,
            borderRadius: 8,
            offsetCenter: [0, title ? '25%' : '15%'],
            fontSize: 20,
            fontWeight: 'bolder',
            formatter: '{value}%',
            color: 'auto'
          },
          data: [{
            value: value
          }]
        }]
      }
    }
  }
}
</script>

<style scoped>
.hardware-container {
  background: #f5f6fa;
  height: calc(100vh - 50px);
  overflow: auto;
}

.row {
  display: flex;
}

.card {
  background: white;
  padding: 20px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  flex: 1;
}

.gauge-card h3 {
  margin: 0 0 15px 0;
  color: #2c3e50;
  font-size: 18px;
}

.gauge-group {
  display: flex;
  gap: 20px;
}

.gauge-item {
  flex: 1;
  text-align: center;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.gauge-title {
  color: #7f8c8d;
  font-size: 14px;
  margin-bottom: 8px;
}

.gauge-number {
  font-size: 24px;
  font-weight: bold;
  color: #2c3e50;
}

.gauge-echart {
  height: 220px;
}

.gauge-footer {
  margin-top: 15px;
  padding-top: 10px;
  border-top: 1px solid #eee;
  font-size: 12px;
  color: #7f8c8d;
  display: flex;
  justify-content: space-between;
}
.cpu-group {
  display: flex;
  gap: 20px;
}

.gauge-chart {
  flex: 2;
}

.core-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 15px;
}

.core-item {
  background: #f8f9fa;
  padding: 12px;
  border-radius: 8px;
  text-align: center;
}

.core-label {
  color: #7f8c8d;
  font-size: 12px;
  margin-bottom: 4px;
}

.core-value {
  font-size: 20px;
  font-weight: bold;
  color: #2c3e50;
}
</style>