<template>
    <!-- 主体内容 -->
    <el-card class="detail-card" :body-style="{padding:'10px'}">
      <div class="scroll-container">

          <el-button 
            type="text" 
            icon="el-icon-arrow-left" 
            @click="$router.go(-1)"
            class="back-btn"
          >返回列表</el-button>
          <h2 class="title">任务详情 - {{ taskDetail.taskName || '加载中...' }}</h2>


        <!-- 基本信息 -->
        <section class="info-section">
          <h3 class="section-title"><i class="el-icon-info"></i> 基本信息</h3>
          <el-descriptions :column="2" border  :contentStyle="content_style"  :labelStyle="label_style">
            <el-descriptions-item label="任务ID" label-class-name="desc-label">{{ taskDetail.taskId || '-' }}</el-descriptions-item>
            <el-descriptions-item label="任务名称" label-class-name="desc-label">{{ taskDetail.taskName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="任务类型" label-class-name="desc-label">
              <el-tag effect="dark">{{ taskDetail.taskType || '-' }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="任务状态" label-class-name="desc-label">
              <el-tag :type="tagStatusMap[taskDetail.status]" effect="dark">
                {{ taskDetail.status || '-' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="创建时间" label-class-name="desc-label">{{ taskDetail.createTime || '-' }}</el-descriptions-item>
            <el-descriptions-item label="最后更新" label-class-name="desc-label">{{ taskDetail.updateTime || '-' }}</el-descriptions-item>
            <el-descriptions-item label="创建者" label-class-name="desc-label">{{ taskDetail.creator || '-' }}</el-descriptions-item>
            <el-descriptions-item label="任务优先级" label-class-name="desc-label">
              <el-rate 
                :max="3"
                :value="taskDetail.priority"
                show-text
                :texts="['低', '中', '高']"
                disabled
                class="priority-rate"
              />
            </el-descriptions-item>
          </el-descriptions>
        </section>

        <!-- 扫描配置 -->
        <section class="info-section">
          <h3 class="section-title"><i class="el-icon-setting"></i> 扫描配置</h3>
          <el-descriptions :column="2" border  :contentStyle="content_style"  :labelStyle="label_style">
            <el-descriptions-item label="扫描目标" label-class-name="desc-label">
                <el-tag 
                  v-for="(target, index) in formatTargets" 
                  :key="index"
                  type="info"
                  effect="plain"
                  class="target-tag"
                >{{ target }}</el-tag>             
            </el-descriptions-item>
            <el-descriptions-item label="全端口扫描" label-class-name="desc-label">
              {{ ( taskDetail.isFullScan !== undefined ) ? (taskDetail.isFullScan ? '开启' : '关闭') : '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="扫描端口" label-class-name="desc-label" v-if="!taskDetail.isFullScan">
              {{ taskDetail.portList || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="扫描方法" label-class-name="desc-label">
              {{  taskDetail.scanMethod || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="爆破服务" label-class-name="desc-label">
              <template v-if="formatServices.length">
              <el-tag 
                v-for="(service, index) in formatServices" 
                :key="index"
                type="info"
                effect="plain"
                class="target-tag"
              >{{ service }}</el-tag>
            </template>
            <template v-else>
              -
            </template>

            </el-descriptions-item>
          </el-descriptions>
        </section>

        <!-- 进度 & 结果 -->
        <section class="info-section">
          <h3 class="section-title"><i class="el-icon-data-line"></i> 任务进度</h3>
          <div class="progress-container">
            <el-progress 
              :percentage="taskDetail.progress" 
              :color="customProgressColor"
              :stroke-width="16"
              class="progress-bar"
            />
            <div class="progress-status">
              当前状态：<span :style="{color: customProgressColor()}">{{ taskDetail.status }}</span>
            </div>
          </div>

          <h3 class="section-title result-title"><i class="el-icon-document"></i> 扫描结果</h3>
          <div class="result-container">
            <template v-if="!hasResult">
              <el-empty description="暂无扫描结果" :image-size="80" />
            </template>
            
            <!-- 端口扫描结果 -->
            <template v-if="taskDetail.taskType === '端口扫描' && taskDetail.result">
              <div 
                v-for="(ports, target) in taskDetail.result" 
                :key="target"
                class="ip-result-card"
              >
                <div class="ip-header">
                  <el-tag type="info" size="medium" effect="plain">
                    <i class="el-icon-monitor"></i> {{ target }}
                  </el-tag>
                  <span class="port-count">开放端口：{{ ports.length }} 个</span>
                </div>
                <el-table
                  :data="ports"
                  stripe
                  size="small"
                  class="port-table"
                >
                  <el-table-column prop="port" label="端口" width="200">
                    <template #default="{row}">
                      <span class="port-badge">{{ row.port }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column prop="serviceType" label="服务类型">
                    <template #default="{row}">
                      {{ row.serviceType || '未知' }}
                    </template>
                  </el-table-column>
                  <el-table-column prop="serviceVersion" label="服务版本" />
                </el-table>
              </div>
            </template>

            <template v-else-if="taskDetail.taskType === '弱口令扫描' && taskDetail.result">
              <div 
                v-for="(credentials, target) in taskDetail.result" 
                :key="target + '_weak'"
                class="ip-result-card"
              >
                <div class="ip-header">
                  <el-tag type="danger" size="medium" effect="plain">
                    <i class="el-icon-warning"></i> {{ target }}
                  </el-tag>
                  <span class="port-count">发现弱口令：{{ credentials.length }} 个</span>
                </div>
                <el-table
                  :data="credentials"
                  stripe
                  size="small"
                  class="credential-table"
                >
                  <el-table-column prop="port" label="端口" width="120">
                    <template #default="{row}">
                      <span class="port-badge">{{ row.port }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column prop="service" label="服务" width="180">
                    <template #default="{row}">
                      <el-tag effect="plain" type="info">{{ row.service }}</el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column prop="username" label="用户名" width="180">
                    <template #default="{row}">
                      <span class="credential-text">{{ row.username }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column prop="password" label="密码">
                    <template #default="{row}">
                      <span class="credential-password">{{ row.password }}</span>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </template>
          </div>
        </section>
      </div>
    </el-card>
</template>

<script>
import { selectTaskDetail } from '@/api/task'
import { selectTaskResult } from '@/api/task'

export default {
  name: 'TaskDetail',
  props: {
    taskId: {
      type: [String, Number],
      required: true
    }
  },
  data() {
    return {
      taskDetail: {},
      ws: null,
      tagStatusMap: {
        '待执行': 'info',
        '运行中': 'primary',
        '已完成': 'success',
        '已暂停': 'warning',
        '已失败': 'danger'
      },
      content_style: {
        'text-align': 'center',
        // 设置长度
        'width':"400px",
        // 排列第二行
        'word-break': 'break-all'
      },
      label_style: {
        'text-align': 'center',
        // 解决不对齐的问题
        'width':"80px",
        // 排列第二行
        'word-break': 'break-all'
      },
      
    }
  },
  computed: {
    formatTargets() {
      return this.taskDetail.target?.split(/,\s*/) || []
    },
    formatServices() {
      return this.taskDetail.serviceTypes?.split(/,\s*/) || []
    },
    hasResult() {
      return this.taskDetail.result && 
            Object.keys(this.taskDetail.result).length > 0
    },
    formattedResult() {
      return JSON.stringify(this.taskDetail.result, null, 2)
    },
    customProgressColor() {
      return () => {
        const statusMap = {
          '已完成': '#67c23a',
          '已失败': '#ff4949',
          '已暂停': '#e6a23c',
          '运行中': '#409eff',
          '待执行': '#909399'
        }
        return statusMap[this.taskDetail.status] || '#409eff'
      }
    },

  },  
  async mounted() {
    await this.fetchTaskDetail()
 
  },
  beforeDestroy() {
    this.closeWebSocket()
  },
  methods: {
    async fetchTaskDetail() {
      try {
        const response1 = await selectTaskDetail(this.taskId)
        if(response1.data.code == 400)
        {
          this.$message.error(response1.data.msg)
          return
        }
        const demo = response1.data.data
        const response2 = await selectTaskResult(this.taskId)
        demo.result = response2.data.data.result
        this.taskDetail = demo
        this.createWebSocketForTask(this.taskDetail)
      } catch (error) {
        this.$message.error('获取任务详情失败')
        console.error(error)
      }
    },
    closeWebSocket() {
      this.ws.close()
    },
    createWebSocketForTask(taskDetail) {
      this.ws = new WebSocket(`ws://127.0.0.1:8888/task-progress/${taskDetail.taskId}`);
      this.ws.onmessage = (event) => {
      try {
        const progressData = JSON.parse(event.data);
        taskDetail.progress = progressData.progress      
        if(progressData.progress == 100){
          taskDetail.status = '已完成'
        }else if(progressData.progress > 0){
          taskDetail.status = '运行中'
        }
      } catch (e) {
        console.error("[错误] WebSocket 消息解析失败:", e);
      }
    };

    },
    
  }
}
</script>

<style lang="css" scoped>
.detail-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
}

.scroll-container {
  max-height: calc(100vh - 80px);
  overflow-y: auto;
  padding-left: 20px;
  padding-right: 8px;
  padding-bottom: 0;
  padding-top: 0;
}
.detail-header {
  background: #ffffff;
  padding: 16px 24px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.back-btn {
  padding: 0;
  margin-right: 24px;
}

.title {
  text-align: center;
  margin: 0;
  color: #333;
  font-size: 20px;
}
.info-section {
  margin-bottom: 32px;
  position: relative;
}

.section-title {
  color: #303133;
  font-size: 16px;
  margin: 20px 0 15px;
  padding-left: 8px;
  border-left: 4px solid #409eff;
}

.section-title i {
  margin-right: 8px;
}

.target-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  
}

.target-tag {
  margin: 2px;
  background-color: #67c23a; /* 修改背景颜色 */
  color: #fff; /* 修改文字颜色 */
  padding: 2px 8px;
  border-radius: 4px;
  transition: background-color 0.3s, color 0.3s;
}

.target-tag:hover {
  background-color: #52a22b; /* 悬停时的背景颜色 */
  color: #fff; /* 悬停时的文字颜色 */
}

.progress-container {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 4px;
}

.progress-status {
  margin-top: 12px;
  color: #606266;
  font-size: 14px;
}

.result-title {
  margin-top: 40px;
}

.result-container {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  min-height: 200px;
}

.ip-result-card {
  margin-bottom: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  transition: transform 0.2s;
  overflow: hidden;
}
.ip-result-card:hover {
  transform: translateY(-2px);
}
.ip-header {
  display: flex;
  align-items: center;
  padding: 16px;
  background: #f9f8fa;
  border-bottom: 1px solid #ebeef5;
}
.ip-header .el-tag {
  font-size: 14px;
  padding: 6px 12px;
  padding-bottom: 30px;
  border-radius: 20px;
  background: rgba(64,158,255,0.1);
  border-color: transparent;
  color: #409eff;
  
}

.port-count {
  margin-left: 16px;
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.port-table {
  margin: 0;
  border-top: none;
}
.port-table::before {
  display: none; /* 隐藏默认的表格分隔线 */
}

.port-table .el-table__row:hover .port-badge {
  background: #409eff;
  color: white;
}
.port-badge {
  display: inline-block;
  background: rgba(64,158,255,0.1);
  color: #409eff;
  padding: 4px 10px;
  border-radius: 12px;
  font-family: monospace;
  font-weight: 600;
  transition: all 0.2s;
  border: 1px solid rgba(64,158,255,0.2);
}

.result-pre {
  margin: 0;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 6px;
  font-family: 'SFMono-Regular', Consolas, monospace;
  line-height: 1.6;
  color: #2c3e50;
  border: 1px solid #ebeef5;
}
/* 表格条纹颜色优化 */
.el-table--striped .el-table__body tr.el-table__row--striped td {
  background: rgba(250,251,252,0.8);
}

/* 空状态样式调整 */
.empty-result {
  padding: 40px 0;
}

/* 在原有样式基础上追加 */
.credential-table {
  margin: 0;
  border-top: none;
}

.credential-text {
  color: #ff0000;
  font-family: monospace;
  font-weight: 700;
  letter-spacing: 1px;
}

.credential-password {
  color: #ff0000;
  font-family: monospace;
  font-weight: 700;
  letter-spacing: 1px;
}

.credential-table .el-table__row:hover .port-badge {
  background: #f56c6c;
  color: white;
}

::v-deep .desc-label {
  background: #fafbfc !important;
  width: 100px;
}
</style>
