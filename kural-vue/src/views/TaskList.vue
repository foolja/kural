<template>
  <div class="task-list-container">
    <!-- 任务列表标题 -->
    <div class="header">
      <h2>扫描任务列表</h2>
      <el-button type="primary" @click="fetchTasks" class="refresh-btn">
        <i class="el-icon-refresh"></i> 刷新列表
      </el-button>
    </div>

    <!-- 新增滚动容器 -->
    <div class="task-list-scroll">
      <el-card v-for="task in tasks" :key="task.id" class="task-item">
        <el-row type="flex" align="middle" class="task-content">
          <!-- 左侧任务信息 -->
          <el-col :span="16">
            <div class="task-meta">
              <h3 class="task-title">
                {{ task.taskName }}
                <el-tag
                  :type="statusTypeMap[task.status]"
                  size="small"
                  class="status-tag"
                >
                  {{ task.status }}
                </el-tag>
              </h3>
              <div class="task-info">
                <span
                  ><i class="el-icon-s-operation"></i> 类型：{{
                    task.taskType
                  }}</span
                >
                <span
                  ><i class="el-icon-user"></i> 创建者：{{ task.creator }}</span
                >
                <span
                  ><i class="el-icon-time"></i> 创建时间：{{
                    task.createTime
                  }}</span
                >
              </div>
              <div class="task-target">
                <i class="el-icon-aim"></i> 扫描目标：{{ task.target }}
              </div>
            </div>
          </el-col>

          <!-- 右侧操作和进度 -->
          <el-col :span="8" class="task-actions">
            <div class="progress-container">
              <el-progress
                :percentage="task.progress"
                :color="customProgressColor"
                :stroke-width="12"
              />
              <div class="action-buttons">
                <el-button
                  v-if="task.status === '待执行'"
                  type="success"
                  size="mini"
                  @click="startTask(task)"
                  >启动</el-button
                >
                <el-button
                  v-if="task.status === '已完成' || task.status === '已取消' " 
                  type="info"
                  size="mini"
                  @click="restartTask(task)"
                  >重启</el-button
                >
                <el-button
                  v-if="task.status === '运行中'"
                  type="warning"
                  size="mini"
                  @click="pauseTask(task.taskId)"
                  >暂停</el-button
                >
                <el-button
                  v-if="task.status === '已暂停'"
                  type="success"
                  size="mini"
                  @click="continueTask(task.taskId)"
                  >继续</el-button
                >
                <el-button type="danger" size="mini" @click="updateTask(task)"
                  >更新</el-button
                >
              </div>
            </div>
            <el-link
              type="primary"
              @click="showDetail(task)"
              class="detail-link"
              >任务详情 ></el-link
            >
          </el-col>
        </el-row>
      </el-card>
    </div>
  </div>
</template>

<script>
import { selectBaseScanTaskList } from "@/api/task";
import { startTask } from "@/api/task";
export default {
  name: "TaskList",
  data() {
    return {
      tasks: [], // 将从后端获取的任务数据
      taskSockets: new Map(),
      statusTypeMap: {
        待执行: "info",
        运行中: "primary",
        已完成: "success",
        已取消: "warning",
        已失败: "danger",
      },
      customProgressColor: (percentage) => {
        if (percentage === 100) return "#67c23a";
        return "#409eff";
      },
    };
  },
  mounted() {
    // 在此处调用获取任务列表的API
    this.fetchTasks();
  },
  beforeDestroy() {
    this.closeAllWebSockets()
  },
  methods: {
    async fetchTasks() {
      try {
        const res = await selectBaseScanTaskList();
        if(res.data.code == 400 && res.data.msg == "请先登录")
        {
          this.$message.error(res.data.msg)
          return
        }
        this.tasks = res.data.data;
        // 关闭旧的 WebSocket（防止重复连接）
        this.closeAllWebSockets();

        // 为每个新任务创建 WebSocket
        this.tasks.forEach((task) => {
          if (!this.taskSockets.has(task.taskId)) {
            this.createWebSocketForTask(task);
          }
        });
      } catch (error) {
        console.error("获取任务失败:", error);
      }
    },
    closeAllWebSockets() {
      this.taskSockets.forEach((ws, taskId) => {
        ws.close();
      });
      this.taskSockets.clear();
    },
    createWebSocketForTask(task) {
      const ws = new WebSocket(`ws://127.0.0.1:8888/task-progress/${task.taskId}`);
      // 存储到 Map
      this.taskSockets.set(task.taskId, ws);
      ws.onmessage = (event) => {
      try {
        const progressData = JSON.parse(event.data);
        task.progress = progressData.progress      
        if(progressData.progress == 100){
          task.status = '已完成'
        }else if(progressData.progress > 0){
          task.status = '运行中'
        }
      } catch (e) {
        console.error("[错误] WebSocket 消息解析失败:", e);
      }
    };

    },

    async startTask(task) {    
      const response = await startTask(task.taskId)
      if(response.data.code == 200)
      {
        this.$message.success(response.data.msg);
        task.status = '运行中'
      }


    },
    async restartTask(task) {
      task.progress = 0
      task.status = '运行中'
      const response = await startTask(task.taskId)
      if(response.data.code == 200)
      {
        this.$message.success(response.data.msg);
      }

    },
    pauseTask(id) {
      console.log("暂停任务:", id);
    },
    continueTask(id) {
      console.log("继续任务:", id);
    },
    updateTask(task) {
      console.log("更新任务:", task);
    },
    showDetail(task) {
      this.$router.push({
        name: "TaskDetail",
        params: { taskId: task.taskId },
      });
    },
  },
  // 组件销毁时清理资源
  beforeUnmount() {
    this.closeAllWebSockets();
  },
};
</script>

<style scoped lang="css">
.task-list-container {
  padding: 20px;
  background-color: #ffffff;
  height: calc(100vh - 60px); /* 根据实际布局调整 */
  display: flex;
  flex-direction: column;
}

.task-list-container .header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.task-list-container .header h2 {
  color: #303133;
  font-size: 24px;
}

.task-list-container .task-item {
  margin-bottom: 15px;
  transition: box-shadow 0.3s;
}

.task-list-container .task-item:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.task-list-container .task-content {
  padding: 15px 0;
}

.task-list-container .task-meta .task-title {
  margin: 0 0 10px 0;
  font-size: 16px;
  display: flex;
  align-items: center;
}

.task-list-container .task-meta .task-title .status-tag {
  margin-left: 10px;
}

.task-list-container .task-info {
  color: #909399;
  font-size: 12px;
  margin-bottom: 8px;
}

.task-list-container .task-info span {
  margin-right: 15px;
}

.task-list-container .task-target {
  color: #606266;
  font-size: 14px;
}

.task-list-container .task-actions {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.task-list-container .progress-container {
  width: 100%;
  margin-bottom: 10px;
}

.task-list-container .progress-bar {
  margin-bottom: 8px;
}

.task-list-container .action-buttons {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.task-list-container .detail-link {
  font-size: 12px;
}

/* 添加任务列表滚动区域 */
.task-list-scroll {
  flex: 1;
  overflow-y: auto;
  padding-right: 10px; /* 防止滚动条遮挡内容 */
}
</style> 