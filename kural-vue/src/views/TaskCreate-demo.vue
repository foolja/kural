<template>
      <el-form ref="taskForm" :model="taskForm" label-width="120px" class="task-form">
        
        <el-row class="task-form-left-content">     
          <!-- 左侧表单区域 -->
          <el-col :span="16" class="form-col">
            <!-- 任务名称 -->
            <el-form-item label="任务名称" :rules="[{ required: true, message: '请输入任务名称', trigger: 'blur' }]">
              <el-input v-model="taskForm.name" placeholder="请输入任务名称" class="input-width" />
            </el-form-item>

            <el-form-item label="任务类型" :rules="[{ required: true, message: '请选择任务类型', trigger: 'change' }]">
              <el-select v-model="taskForm.type" placeholder="请选择任务类型" @change="handleTaskTypeChange" class="input-width">
                <el-option label="端口扫描" value="port-scan" />
                <el-option label="Web漏洞扫描" value="web-vuln-scan" />
                <el-option label="弱口令扫描" value="weak-password-scan" />
                <el-option label="网站目录扫描" value="web-dir-scan" />
              </el-select>
            </el-form-item>

            <el-form-item label="扫描目标" :rules="[{ required: true, message: '请输入扫描目标', trigger: 'blur' }]">
              <el-input type="textarea" v-model="taskForm.target" placeholder="请输入扫描目标" rows="3" class="input-width" />
            </el-form-item>

            <el-form-item label="任务描述" :rules="[{ required: true, message: '请输入任务描述', trigger: 'blur' }]">
              <el-input type="textarea" v-model="taskForm.description" placeholder="请输入任务描述" rows="4" class="input-width" />
            </el-form-item>

            <el-form-item label="任务优先级" :rules="[{ required: true, message: '请选择任务优先级', trigger: 'change' }]">
              <el-select v-model="taskForm.priority" placeholder="请选择任务优先级" class="input-width">
                <el-option label="1" value="1" />
                <el-option label="2" value="2" />
                <el-option label="3" value="3" />
              </el-select>
            </el-form-item>

            <el-form-item label="扫描端口" v-if="taskForm.type === 'port-scan'">
              <el-input v-model="taskForm.ports" placeholder="请输入端口（例如：80, 443，或选择全端口扫描）" class="input-width" />
            </el-form-item>

            <el-form-item label="扫描方法" v-if="taskForm.type === 'port-scan'">
              <el-radio-group v-model="taskForm.scanMethod">
                <el-radio label="SYN扫描">SYN扫描</el-radio>
                <el-radio label="TCP连接">TCP连接</el-radio>
                <el-radio label="UDP扫描">UDP扫描</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="IP范围">
              <el-input v-model="taskForm.ipRange" placeholder="例如：192.168.1.0-192.168.1.255" class="input-width" />
            </el-form-item>

            <el-form-item label="TCP端口">
              <el-input v-model="taskForm.tcpPorts" placeholder="请输入端口（例如：21, 22, 80）" class="input-width" />
            </el-form-item>

            <el-form-item label="UDP端口">
              <el-input v-model="taskForm.udpPorts" placeholder="请输入端口（例如：53, 161）" class="input-width" />
            </el-form-item>
            <!-- 提交按钮 -->
            <el-form-item>
              <el-button type="primary" @click="submitForm">提交任务</el-button>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row class="task-form-right-content">
          <!-- 右侧说明区域 -->
         <el-col :span="8" class="comment-col">
            
              <h3 class="config-title">任务配置说明</h3>
              
              <div class="config-section">
                <h4 class="task-config-title">任务名称</h4>
                <p class="task-config-content">• 应为2到200字符<br>• 不可以数字开头<br>• 任务名称唯一</p>
              </div>

              <div class="config-section">
                <h4 class="task-config-title">任务类型</h4>
                <p class="task-config-content">• 端口扫描：需指定端口范围<br>• Web漏洞扫描：需指定Web地址<br>• 弱口令扫描：需上传字典文件<br>• 网站目录扫描：需配置字典</p>
              </div>

              <div class="config-section">
                <h4 class="task-config-title">扫描目标</h4>
                <p class="task-config-content">• 支持IP/CIDR格式<br>• 支持域名输入<br>• 多目标用换行分隔<br>• 示例：192.168.1.0/24</p>
              </div>

              <div class="config-section">
                <h4 class="task-config-title">优先级设置</h4>
                <p class="task-config-content">• 1为最高优先级<br>• 3为最低优先级<br>• 影响任务调度顺序</p>
              </div>

              <div class="config-section">
                <h4 class="task-config-title">端口配置</h4>
                <p class="task-config-content">• TCP端口示例：21,22,80-100<br>• UDP端口示例：53,161<br>• 全端口扫描留空</p>
              </div>

              <div class="config-section">
                <h4 class="task-config-title">文件上传</h4>
                <p class="task-config-content">• 支持配置文件<br>• 支持字典文件<br>• 最大文件50MB<br>• 支持.zip/.txt格式</p>
              </div>
          </el-col> 
        </el-row>
      </el-form>
</template>

<script>
export default {
  data() {
    return {
      taskForm: {
        name: '',
        type: '',
        target: '',
        description: '',
        priority: '',
        ports: '',  // 用于端口扫描
        scanMethod: '',  // 用于端口扫描方法
        ipRange: '',  // IP范围
        tcpPorts: '',  // TCP端口
        udpPorts: '',  // UDP端口
      }
    };
  },
  methods: {
    handleTaskTypeChange(value) {
      if (value === 'port-scan') {
        this.taskForm.ports = ''; // 清空端口字段
        this.taskForm.scanMethod = ''; // 清空扫描方法
      } else {
        this.taskForm.ports = ''; // 清空端口字段
        this.taskForm.scanMethod = ''; // 清空扫描方法
      }
    },
    submitForm() {
      this.$refs.taskForm.validate((valid) => {
        if (valid) {
          console.log('提交任务:', this.taskForm);
          this.$message.success('任务提交成功');
        } else {
          this.$message.error('请填写所有必填项');
          return false;
        }
      });
    },
    handleUploadSuccess(response, file, fileList) {
      this.$message.success('文件上传成功');
    },
    handleUploadError(err, file, fileList) {
      this.$message.error('文件上传失败');
    }
  }
};
</script>

<style scoped lang="less">
.task-form {
  flex: 1;
  width: 100%;
  height: 100%;
  display: flex;
  background: #ffffff;
  overflow: hidden; /* 防止内部元素溢出 */
}
.task-form-left-content{
  height: 100%;
  background: #ffffff;
}
.task-form-right-content {
  height: 100%;
  background: #ffffff;
  overflow-y: auto;

  h3 {
    margin: 0 0 20px 0;
    padding-bottom: 12px;
    border-bottom: 1px solid #e4e7ed;
  }
}

.el-row {
  flex: 1;
}
.form-col{
  height: 100%;
  margin-top: 3%;
}

.comment-col {
  margin-top: 3px;
  height: 90%;  
  width: 100%;
  overflow-y: auto;  /* 确保有滑动条 */
  
}
/* 其他样式保持原有配置 */
.config-title {
  margin: 0 0 20px 0;
  position: static;
}

/* 调整表单元素间距 */
.el-form-item {
  margin-bottom: 18px;
}

/* 优化说明区域样式 */
.config-section {
  margin-left: 15px;
  margin-bottom: 10px;
  padding: 15px;
  background: #ede7e7;
  width: 54%;
  height: auto;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0,0,0,.03);
  transition: all 0.3s;
}

.config-section:hover {
  box-shadow: 0 2px 12px rgba(0,0,0,.08);
  transform: translateY(-2px);
}

.task-config-title{
  margin-top: 1px;
}
.task-config-content{
  padding: auto;
}
.input-width {
  width: 100% !important;
  max-width: 100% !important;
}
</style>
