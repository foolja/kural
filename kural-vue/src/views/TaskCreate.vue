<template>
  <el-container class="task-create-container">
    <!-- 任务信息表单 -->
    <el-form :model="taskForm" ref="taskForm" label-width="120px" class="task-form">
      <el-row > <!-- 减小gutter值以减少间隙 -->
        <!-- 左侧表单区域 -->
        <el-col :span="16">

          <el-form-item label="任务类型" :rules="[{ required: true, message: '请选择任务类型', trigger: 'change' }]">
            <el-select v-model="taskForm.taskType" placeholder="请选择任务类型" @change="handleTaskTypeChange" class="input-width">
              <el-option label="端口扫描" value="端口扫描" />
              <el-option label="Web漏洞扫描" value="Web漏洞扫描" />
              <el-option label="弱口令扫描" value="弱口令扫描" />
              <el-option label="网站目录扫描" value="网站目录扫描" />
            </el-select>
          </el-form-item>
          
          <!-- 任务名称 -->
          <el-form-item label="任务名称" :rules="[{ required: true, message: '请输入任务名称', trigger: 'blur' }]">
            <el-input v-model="taskForm.taskName" placeholder="请输入任务名称" class="input-width" />
          </el-form-item>

          

          <el-form-item label="扫描目标" :rules="[{ required: true, message: '请输入扫描目标', trigger: 'blur' }]">
            <el-input type="textarea" v-model="taskForm.target" placeholder="请输入扫描目标" rows="3" class="input-width" />
          </el-form-item>

          <el-form-item label="任务描述" :rules="[{ required: false, message: '请输入任务描述', trigger: 'blur' }]">
            <el-input type="textarea" v-model="taskForm.description" placeholder="请输入任务描述" rows="4" class="input-width" />
          </el-form-item>

          <el-form-item label="任务优先级" :rules="[{ required: true, message: '请选择任务优先级', trigger: 'change' }]">
            <el-select v-model="taskForm.priority" placeholder="请选择任务优先级" class="input-width">
              <el-option label="高" value=3 />
              <el-option label="中" value=2 />
              <el-option label="低" value=1 />
            </el-select>
          </el-form-item>
          <el-form-item label="自定义yaml-poc" class="editor-yaml" v-if="taskForm.taskType === 'Web漏洞扫描'">
              <CodeEditor @change="codeChange" v-model="code" language="yaml"></CodeEditor>
          </el-form-item>




          <el-form-item label="扫描范围" v-if="taskForm.taskType === '端口扫描'">
            <el-radio-group v-model="taskForm.isFullScan">
              <el-radio-button :label=true>全端口扫描</el-radio-button>
              <el-radio-button :label=false>自定义端口</el-radio-button>
            </el-radio-group>
            <div class="form-comment">全端口扫描将扫描1-65535所有端口</div>
          </el-form-item>

          <el-form-item 
            label="扫描端口" 
            v-if="taskForm.taskType === '端口扫描' && !taskForm.isFullScan">
            <el-input 
              v-model="taskForm.portList" 
              placeholder="请输入端口（例如：80,443,8000-8080）" 
              class="input-width" />
          </el-form-item>

          <el-form-item label="扫描方法" v-if="taskForm.taskType === '端口扫描'">
            <el-radio-group v-model="taskForm.scanMethod">
              <el-radio label="SYN扫描">SYN扫描 (半连接扫描)</el-radio>
              <el-radio label="TCP连接">TCP连接 (全连接扫描)</el-radio>
              <el-radio label="UDP扫描">UDP扫描</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="服务类型" v-if="taskForm.taskType === '弱口令扫描'">
            <el-checkbox-group v-model="taskForm.serviceTypeList">
              <el-checkbox label="FTP" value="FTP"/>
              <el-checkbox label="SSH" value="SSH"/>
              <el-checkbox label="MySQL" value="MySQL"/>             
              <el-checkbox label="Redis" value="Redis"/>
              <el-checkbox label="Mssql" value="Mssql"/>
              <el-checkbox label="Mongodb" value="Mongodb"/>
            </el-checkbox-group>
          </el-form-item>
          <el-form-item  v-if="taskForm.taskType === '弱口令扫描'"  label="用户名字典路径" :rules="[{ required: false, message: '请输入用户名字典路径', trigger: 'blur' }]">
            <el-input type="textarea" v-model="taskForm.usernameFilePath" placeholder="请输入用户名字典路径" rows="1" class="input-width" />
          </el-form-item>
          <el-form-item  v-if="taskForm.taskType === '弱口令扫描'"  label="密码字典路径" :rules="[{ required: false, message: '请输入密码字典路径', trigger: 'blur' }]">
            <el-input type="textarea" v-model="taskForm.passwordFilePath" placeholder="请输入密码字典路径" rows="1" class="input-width" />
          </el-form-item>
          <!-- 提交按钮 -->
          <el-form-item>
            <el-button type="primary" @click="submitForm">提交任务</el-button>
          </el-form-item>
        </el-col>

        <!-- 右侧注释区域 -->
        <el-col :span="8">
          <div class="comment-area">
            <h3>任务配置说明</h3>
            <ul>
              <li><strong>任务名称：</strong>长度应为 2 到 200 字符。任务名称唯一</li>
              <li><strong>任务类型：</strong>如端口扫描、Web漏洞扫描等</li>
              <ul v-if="taskForm.taskType === 'Web漏洞扫描'">
              
              <li><strong>扫描目标：</strong>输入扫描目标的url地址</li>
              </ul>
              <ul v-if="taskForm.taskType != 'Web漏洞扫描'">
              
              <li><strong>扫描目标：</strong>输入扫描的目标 IP 地址或域名,IP地址支持格式如下：</li>
              <li><strong>&emsp;&emsp;&emsp;&emsp;&ensp; </strong>(1)10.0.0.1</li>
              <li><strong>&emsp;&emsp;&emsp;&emsp;&ensp; </strong>(2)10.0.0.0/24</li>
              <li><strong>&emsp;&emsp;&emsp;&emsp;&ensp; </strong>(3)10.0.0.∗</li>
              <li><strong>&emsp;&emsp;&emsp;&emsp;&ensp; </strong>(4)10.0.0.1-10</li>
              <li><strong>&emsp;&emsp;&emsp;&emsp;&ensp; </strong>(5)10.0.0.1, 10.0.0.5-10, 192.168.1.∗, 192.168.10.0/24</li>
              <li><strong>任务描述：</strong>简短描述任务的目的,选填</li>
              <li><strong>任务优先级：</strong>设置任务的优先级，影响执行顺序</li>
              </ul>
              
            </ul>
            <ul v-if="taskForm.taskType === '端口扫描'">
              
              <li><strong>扫描端口：</strong>输入需要扫描的端口，支持用"-"表示范围，也可以用逗号分隔端口</li>
              <li><strong>扫描方法：</strong>选择适合的扫描方法，如 SYN 扫描、TCP 连接扫描等</li>
            </ul>
            <ul v-if="taskForm.taskType === 'Web漏洞扫描'">
             <li><strong>自定义yaml-poc：</strong>用户可以输入一个自定义poc的yaml文件来针对此扫描任务, 但是不会添加到系统的poc列表中</li>
            </ul>
            <ul v-if="taskForm.taskType === '弱口令扫描'">
             
              <li><strong>服务类型：</strong>选择需要爆破的服务类型，如 SSH、MySQL、FTP</li>
              <li><strong>用户名字典：</strong>输入存在于本主机的用户名字典路径，用于尝试爆破目标服务</li>
              <li><strong>密码字典：</strong>输入存在于本主机的密码字典路径，用于尝试爆破目标服务</li>
            </ul>
            <ul v-if="taskForm.taskType === '网站目录扫描'">
              
            </ul>
          </div>
        </el-col>
      </el-row>
    </el-form>
  </el-container>
</template>

<script>
import { addPortScanTask, addWebVulnScanTask, addWeakPasswordScanTask } from '@/api/task'

import CodeEditor from "@/components/editor.vue";
export default {
  components: {CodeEditor},
  data() {
    return {
      taskForm: {
        taskName: '',
        taskType: '',
        target: '',
        description: '',
        priority: '',
        //用于端口扫描
        portList: '',  
        scanMethod: '',  
        isFullScan: true,  
        //用于弱口令扫描
        serviceTypeList:[],   
        usernameFilePath:'',  
        passwordFilePath:'',
        useDefaultDict:true,
        yamlContent: '', // 用于存储 YAML 文件内容
      },
      code:''
       
    };
  },
  methods: {
    codeChange(e){
      this.taskForm.yamlContent = e
    },
    handleTaskTypeChange(value) {
      if (value === '端口扫描') {
        this.taskForm.portList = '';
        this.taskForm.scanMethod = '';
        this.taskForm.isFullScan = true; // 重置为全端口扫描
      } else if (value === '弱口令扫描'){
        this.taskForm.portList = '';
        this.taskForm.scanMethod = '';
      }else {

      }
    }, 
    async submitForm() {
      this.$refs.taskForm.validate( async(valid) => {
        if (valid) {
          if (this.taskForm.taskType === '端口扫描') {
           const portScanForm = {
              taskName: this.taskForm.taskName,
              taskType: this.taskForm.taskType,
              target: this.taskForm.target,
              description: this.taskForm.description,
              priority: this.taskForm.priority,
              portList: this.taskForm.portList,
              scanMethod: this.taskForm.scanMethod,
              isFullScan: this.taskForm.isFullScan
            };
            const response = await addPortScanTask(portScanForm)
             if (response.data.code === '200' && response.data.msg === '创建成功' && response.data.data != null){
                this.$message.success(response.data.msg);
             }else{
                this.$message.error(response.data.msg);
             }
             //清空表单
            this.formClear();
          } else if (this.taskForm.taskType === 'Web漏洞扫描') {
            const webVulnScanForm = {
              taskName: this.taskForm.taskName,
              taskType: this.taskForm.taskType,
              target: this.taskForm.target,
              description: this.taskForm.description,
              priority: this.taskForm.priority,
              pocContent: this.taskForm.yamlContent  
            };
            const response = await addWebVulnScanTask(webVulnScanForm)
            if (response.data.code === '200' && response.data.msg === '创建成功' && response.data.data != null){
                this.$message.success(response.data.msg);
             }else{
                this.$message.error(response.data.msg);
             }
              //清空表单
            this.formClear();
          } else if (this.taskForm.taskType === '弱口令扫描') {
              if(this.taskForm.serviceTypeList.length == 0){            
                  this.$message.error('至少选择一项服务进行爆破');
                  return false
              }
              if(this.taskForm.usernameFilePath !=  '' || this.taskForm.passwordFilePath !=  ''){
                this.taskForm.useDefaultDict = false
              }
              const portScanForm = {
                taskName: this.taskForm.taskName,
                taskType: this.taskForm.taskType,
                target: this.taskForm.target,
                description: this.taskForm.description,
                priority: this.taskForm.priority,
                serviceTypeList: this.taskForm.serviceTypeList,
                usernameFilePath: this.taskForm.usernameFilePath,
                passwordFilePath: this.taskForm.passwordFilePath,
                useDefaultDict: this.taskForm.useDefaultDict
            };
              const response = await addWeakPasswordScanTask(portScanForm)
              if (response.data.code === '200' && response.data.msg === '创建成功' && response.data.data != null){
                this.$message.success(response.data.msg);
              }else{
                this.$message.error(response.data.msg);
              }
              //清空表单
              this.formClear();
          }
        } else {
          this.$message.error('请填写所有必填项');
          return false;
        }
        
      });
    },
    formClear(){
     this.taskForm.taskName = '';
     this.taskForm.taskType = '';
     this.taskForm.target = '';
     this.taskForm.description = '';
     this.taskForm.priority = '';
     this.taskForm.portList = '';
     this.taskForm.scanMethod = '';
     this.taskForm.isFullScan = true;
     this.taskForm.usernameFilePath = '';
     this.taskForm.passwordFilePath = '';
     this.taskForm.serviceTypeList = [];
     this.code = ''; // 新增这行
     this.taskForm.yamlContent = ''; // 新增这行

    }
  }
};
</script>

<style scoped>
.task-create-container {
  padding: 20px;
  background-color: #ffffff;
  flex: 1;
  display: flex;
  justify-content: flex-start;
  height: calc(100vh - 40px); /* 减去padding */
  overflow-y: auto; /* 启用垂直滚动 */
}

.task-form {
  width: 100%;
  min-height: 100%; /* 确保表单撑满容器 */
}

.input-width {
  width: 100%;
  max-width: 500px;
}

.form-comment {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.comment-area {
  padding-left: 20px;
  font-size: 14px;
  color: #333;
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-top: 20px;
}

.comment-area h3 {
  margin-bottom: 20px;
  font-size: 18px;
  color: #409eff;
}

.comment-area ul {
  list-style-type: none;
  padding-left: 0;
}

.comment-area li {
  margin-bottom: 12px;
  font-size: 14px;
  color: #555;
}

.comment-area li strong {
  color: #333;
}
.editor-yaml {
  height: 400px;
  width: 1100px;
  margin-bottom: 20px;
  position: relative; /* 确保编辑器定位正确 */
}

::v-deep .editor-yaml .el-form-item__content {
  height: 100%;
}

::v-deep .editor-yaml > div {
  height: 100% !important;
  border-radius: 4px;
  overflow: hidden;
}
</style>
