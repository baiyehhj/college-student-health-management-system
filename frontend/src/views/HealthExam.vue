<template>
  <div class="health-exam">
    <el-card>
      <div slot="header" class="clearfix">
        <span>体检报告管理</span>
        <div style="float: right">
          <el-button
            type="success"
            icon="el-icon-download"
            @click="handleDownloadTemplate"
          >
            下载模板
          </el-button>
          <el-button
            type="warning"
            icon="el-icon-upload"
            @click="handleImport"
          >
            批量导入
          </el-button>
          <el-button
            type="primary"
            icon="el-icon-plus"
            @click="handleAdd"
          >
            添加报告
          </el-button>
        </div>
      </div>
      
      <!-- 数据表格 -->
      <el-table
        :data="tableData"
        border
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column prop="examDate" label="体检日期" width="120"></el-table-column>
        <el-table-column prop="examType" label="体检类型" width="120"></el-table-column>
        <el-table-column label="身高体重" width="140">
          <template slot-scope="scope">
            {{ scope.row.height }}cm / {{ scope.row.weight }}kg
          </template>
        </el-table-column>
        <el-table-column prop="bmi" label="BMI" width="80"></el-table-column>
        <el-table-column label="血压" width="120">
          <template slot-scope="scope">
            {{ scope.row.bloodPressureHigh }}/{{ scope.row.bloodPressureLow }}
          </template>
        </el-table-column>
        <el-table-column prop="heartRate" label="心率" width="80"></el-table-column>
        <el-table-column prop="overallConclusion" label="总体结论" show-overflow-tooltip></el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template slot-scope="scope">
            <el-button
              type="text"
              icon="el-icon-view"
              @click="handleView(scope.row)"
            >
              详情
            </el-button>
            <el-button
              type="text"
              icon="el-icon-edit"
              @click="handleEdit(scope.row)"
            >
              编辑
            </el-button>
            <el-button
              type="text"
              icon="el-icon-delete"
              style="color: #f56c6c"
              @click="handleDelete(scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.page"
        :page-sizes="[10, 20, 50]"
        :page-size="pagination.size"
        layout="total, sizes, prev, pager, next"
        :total="pagination.total"
        style="margin-top: 20px; text-align: right"
      ></el-pagination>
    </el-card>
    
    <!-- 添加/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="800px"
      @close="handleDialogClose"
    >
      <el-form
        ref="dataForm"
        :model="dataForm"
        :rules="dataRules"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="体检日期" prop="examDate">
              <el-date-picker
                v-model="dataForm.examDate"
                type="date"
                placeholder="选择日期"
                value-format="yyyy-MM-dd"
                style="width: 100%"
              ></el-date-picker>
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="体检类型" prop="examType">
              <el-input v-model="dataForm.examType" placeholder="如：入学体检"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-divider content-position="left">基本体征</el-divider>
        
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="身高(cm)" prop="height">
              <el-input-number v-model="dataForm.height" :min="0" :precision="1" style="width: 100%"></el-input-number>
            </el-form-item>
          </el-col>
          
          <el-col :span="8">
            <el-form-item label="体重(kg)" prop="weight">
              <el-input-number v-model="dataForm.weight" :min="0" :precision="1" style="width: 100%"></el-input-number>
            </el-form-item>
          </el-col>
          
          <el-col :span="8">
            <el-form-item label="心率(次/分)">
              <el-input-number v-model="dataForm.heartRate" :min="0" style="width: 100%"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="收缩压">
              <el-input-number v-model="dataForm.bloodPressureHigh" :min="0" style="width: 100%"></el-input-number>
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="舒张压">
              <el-input-number v-model="dataForm.bloodPressureLow" :min="0" style="width: 100%"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-divider content-position="left">视力检查</el-divider>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="左眼视力">
              <el-input-number v-model="dataForm.visionLeft" :min="0" :max="5.3" :precision="1" style="width: 100%"></el-input-number>
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="右眼视力">
              <el-input-number v-model="dataForm.visionRight" :min="0" :max="5.3" :precision="1" style="width: 100%"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-divider content-position="left">血液检查</el-divider>
        
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="血糖(mmol/L)">
              <el-input-number v-model="dataForm.bloodSugar" :min="0" :precision="2" style="width: 100%"></el-input-number>
            </el-form-item>
          </el-col>
          
          <el-col :span="8">
            <el-form-item label="血红蛋白(g/L)">
              <el-input-number v-model="dataForm.hemoglobin" :min="0" :precision="1" style="width: 100%"></el-input-number>
            </el-form-item>
          </el-col>
          
          <el-col :span="8">
            <el-form-item label="白细胞">
              <el-input-number v-model="dataForm.whiteBloodCell" :min="0" :precision="2" style="width: 100%"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="血小板">
              <el-input-number v-model="dataForm.platelet" :min="0" :precision="1" style="width: 100%"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-divider content-position="left">体检结论</el-divider>
        
        <el-form-item label="总体结论">
          <el-input
            v-model="dataForm.overallConclusion"
            type="textarea"
            :rows="2"
            placeholder="请输入体检总体结论"
          ></el-input>
        </el-form-item>
        
        <el-form-item label="医生建议">
          <el-input
            v-model="dataForm.doctorAdvice"
            type="textarea"
            :rows="3"
            placeholder="请输入医生建议"
          ></el-input>
        </el-form-item>
      </el-form>
      
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          确定
        </el-button>
      </div>
    </el-dialog>
    
    <!-- 详情对话框 -->
    <el-dialog
      title="体检报告详情"
      :visible.sync="detailVisible"
      width="900px"
    >
      <div v-if="detailData" class="detail-content">
        <el-descriptions :column="3" border>
          <el-descriptions-item label="体检日期">{{ detailData.examination.examDate }}</el-descriptions-item>
          <el-descriptions-item label="体检类型">{{ detailData.examination.examType }}</el-descriptions-item>
          <el-descriptions-item label="BMI">{{ detailData.examination.bmi }}</el-descriptions-item>
          
          <el-descriptions-item label="身高">{{ detailData.examination.height }} cm</el-descriptions-item>
          <el-descriptions-item label="体重">{{ detailData.examination.weight }} kg</el-descriptions-item>
          <el-descriptions-item label="心率">{{ detailData.examination.heartRate }} 次/分</el-descriptions-item>
          
          <el-descriptions-item label="血压">
            {{ detailData.examination.bloodPressureHigh }}/{{ detailData.examination.bloodPressureLow }} mmHg
          </el-descriptions-item>
          <el-descriptions-item label="左眼视力">{{ detailData.examination.visionLeft }}</el-descriptions-item>
          <el-descriptions-item label="右眼视力">{{ detailData.examination.visionRight }}</el-descriptions-item>
          
          <el-descriptions-item label="血糖">{{ detailData.examination.bloodSugar }} mmol/L</el-descriptions-item>
          <el-descriptions-item label="血红蛋白">{{ detailData.examination.hemoglobin }} g/L</el-descriptions-item>
          <el-descriptions-item label="白细胞">{{ detailData.examination.whiteBloodCell }}</el-descriptions-item>
        </el-descriptions>
        
        <el-divider></el-divider>
        
        <div v-if="detailData.analysis" class="analysis-section">
          <h4>健康指标分析</h4>
          <el-row :gutter="20" style="margin-top: 20px">
            <el-col :span="6" v-if="detailData.analysis.bmiStatus">
              <el-card shadow="hover">
                <div class="analysis-item">
                  <div class="analysis-label">BMI状态</div>
                  <div class="analysis-value">{{ detailData.analysis.bmiStatus }}</div>
                </div>
              </el-card>
            </el-col>
            
            <el-col :span="6" v-if="detailData.analysis.bloodPressureStatus">
              <el-card shadow="hover">
                <div class="analysis-item">
                  <div class="analysis-label">血压状态</div>
                  <div class="analysis-value">{{ detailData.analysis.bloodPressureStatus }}</div>
                </div>
              </el-card>
            </el-col>
            
            <el-col :span="6" v-if="detailData.analysis.heartRateStatus">
              <el-card shadow="hover">
                <div class="analysis-item">
                  <div class="analysis-label">心率状态</div>
                  <div class="analysis-value">{{ detailData.analysis.heartRateStatus }}</div>
                </div>
              </el-card>
            </el-col>
            
            <el-col :span="6" v-if="detailData.analysis.visionStatus">
              <el-card shadow="hover">
                <div class="analysis-item">
                  <div class="analysis-label">视力状态</div>
                  <div class="analysis-value">{{ detailData.analysis.visionStatus }}</div>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>
        
        <el-divider></el-divider>
        
        <div class="conclusion-section">
          <h4>总体结论</h4>
          <p>{{ detailData.examination.overallConclusion }}</p>
          
          <h4 style="margin-top: 20px">医生建议</h4>
          <p>{{ detailData.examination.doctorAdvice }}</p>
        </div>
      </div>
      
      <div slot="footer">
        <el-button @click="detailVisible = false">关闭</el-button>
      </div>
    </el-dialog>
    
    <!-- 导入对话框 -->
    <el-dialog
      title="批量导入体检报告"
      :visible.sync="importVisible"
      width="600px"
      @close="handleImportClose"
    >
      <div class="import-container">
        <el-alert
          title="导入说明"
          type="info"
          :closable="false"
          style="margin-bottom: 20px"
        >
          <div slot="default">
            <p>1. 请先下载Excel模板，按模板格式填写数据</p>
            <p>2. 必填字段：体检日期、体检类型、身高、体重</p>
            <p>3. 日期格式：yyyy-MM-dd（如：2025-11-19）</p>
            <p>4. 上传文件格式：.xlsx</p>
          </div>
        </el-alert>
        
        <el-upload
          ref="upload"
          class="upload-demo"
          drag
          action=""
          :auto-upload="false"
          :on-change="handleFileChange"
          :file-list="fileList"
          :limit="1"
          accept=".xlsx"
        >
          <i class="el-icon-upload"></i>
          <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
          <div class="el-upload__tip" slot="tip">只能上传xlsx文件</div>
        </el-upload>
        
        <!-- 导入结果 -->
        <div v-if="importResult" class="import-result">
          <el-divider></el-divider>
          <h4>导入结果</h4>
          <el-descriptions :column="3" border size="small">
            <el-descriptions-item label="总记录数">{{ importResult.total }}</el-descriptions-item>
            <el-descriptions-item label="成功">
              <el-tag type="success">{{ importResult.success }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="失败">
              <el-tag type="danger">{{ importResult.failed }}</el-tag>
            </el-descriptions-item>
          </el-descriptions>
          
          <!-- 错误信息 -->
          <div v-if="importResult.errors && importResult.errors.length > 0" style="margin-top: 15px">
            <h5>错误详情：</h5>
            <el-alert
              v-for="(error, index) in importResult.errors"
              :key="index"
              :title="error"
              type="error"
              :closable="false"
              style="margin-bottom: 5px"
            ></el-alert>
          </div>
        </div>
      </div>
      
      <div slot="footer">
        <el-button @click="importVisible = false">取消</el-button>
        <el-button type="primary" @click="handleUpload" :loading="uploading">
          开始导入
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listHealthExaminations,
  addHealthExamination,
  updateHealthExamination,
  deleteHealthExamination,
  getHealthExaminationDetail,
  downloadTemplate,
  importExcel
} from '@/api/healthExam'

export default {
  name: 'HealthExamPage',
  data() {
    return {
      loading: false,
      tableData: [],
      pagination: {
        page: 1,
        size: 10,
        total: 0
      },
      dialogVisible: false,
      dialogTitle: '添加体检报告',
      isEdit: false,
      submitLoading: false,
      detailVisible: false,
      detailData: null,
      importVisible: false,
      uploading: false,
      fileList: [],
      importResult: null,
      dataForm: {
        id: null,
        examDate: '',
        examType: '',
        height: null,
        weight: null,
        bloodPressureHigh: null,
        bloodPressureLow: null,
        heartRate: null,
        visionLeft: null,
        visionRight: null,
        bloodSugar: null,
        hemoglobin: null,
        whiteBloodCell: null,
        platelet: null,
        overallConclusion: '',
        doctorAdvice: ''
      },
      dataRules: {
        examDate: [
          { required: true, message: '请选择体检日期', trigger: 'change' }
        ]
      }
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    async fetchData() {
      this.loading = true
      try {
        const params = {
          page: this.pagination.page,
          size: this.pagination.size
        }
        
        const response = await listHealthExaminations(params)
        this.tableData = response.data.records
        this.pagination.total = response.data.total
      } catch (error) {
        this.$message.error('获取数据失败')
      } finally {
        this.loading = false
      }
    },
    
    handleAdd() {
      this.dialogTitle = '添加体检报告'
      this.isEdit = false
      this.dialogVisible = true
      this.dataForm.examDate = this.formatDate(new Date())
    },
    
    formatDate(date) {
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    
    handleEdit(row) {
      this.dialogTitle = '编辑体检报告'
      this.isEdit = true
      this.dialogVisible = true
      this.dataForm = { ...row }
    },
    
    async handleView(row) {
      try {
        const response = await getHealthExaminationDetail(row.id)
        this.detailData = response.data
        this.detailVisible = true
      } catch (error) {
        this.$message.error('获取详情失败')
      }
    },
    
    handleDelete(row) {
      this.$confirm('确认删除该体检报告吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await deleteHealthExamination(row.id)
          this.$message.success('删除成功')
          this.fetchData()
        } catch (error) {
          this.$message.error('删除失败')
        }
      })
    },
    
    handleSubmit() {
      this.$refs.dataForm.validate(async (valid) => {
        if (!valid) return
        
        this.submitLoading = true
        try {
          if (this.isEdit) {
            await updateHealthExamination(this.dataForm.id, this.dataForm)
            this.$message.success('更新成功')
          } else {
            await addHealthExamination(this.dataForm)
            this.$message.success('添加成功')
          }
          
          this.dialogVisible = false
          this.fetchData()
        } catch (error) {
          this.$message.error(this.isEdit ? '更新失败' : '添加失败')
        } finally {
          this.submitLoading = false
        }
      })
    },
    
    handleDialogClose() {
      this.$refs.dataForm.resetFields()
      this.dataForm = {
        id: null,
        examDate: '',
        examType: '',
        height: null,
        weight: null,
        bloodPressureHigh: null,
        bloodPressureLow: null,
        heartRate: null,
        visionLeft: null,
        visionRight: null,
        bloodSugar: null,
        hemoglobin: null,
        whiteBloodCell: null,
        platelet: null,
        overallConclusion: '',
        doctorAdvice: ''
      }
    },
    
    handleSizeChange(size) {
      this.pagination.size = size
      this.fetchData()
    },
    
    handleCurrentChange(page) {
      this.pagination.page = page
      this.fetchData()
    },
    
    // 下载模板
    async handleDownloadTemplate() {
      try {
        this.$message({
          message: '正在生成模板，请稍候...',
          type: 'info',
          duration: 1000
        })
        
        const response = await downloadTemplate()
        
        // 检查响应是否为空
        if (!response || response.size === 0) {
          throw new Error('模板文件为空')
        }
        
        // response直接就是blob数据
        const blob = new Blob([response], {
          type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        })
        
        // 创建下载链接
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = '体检报告导入模板.xlsx'
        link.style.display = 'none'
        document.body.appendChild(link)
        link.click()
        
        // 延迟清理，确保下载开始
        setTimeout(() => {
          document.body.removeChild(link)
          window.URL.revokeObjectURL(url)
        }, 100)
        
        this.$message.success('模板下载成功')
      } catch (error) {
        console.error('下载失败:', error)
        this.$message.error('模板下载失败，请检查网络连接或联系管理员')
      }
    },
    
    // 打开导入对话框
    handleImport() {
      this.importVisible = true
      this.importResult = null
      this.fileList = []
    },
    
    // 文件选择
    handleFileChange(file, fileList) {
      this.fileList = fileList
    },
    
    // 上传导入
    async handleUpload() {
      if (this.fileList.length === 0) {
        this.$message.warning('请选择要上传的文件')
        return
      }
      
      this.uploading = true
      try {
        const file = this.fileList[0].raw
        const response = await importExcel(file)
        
        if (response.code === 200) {
          this.importResult = response.data
          this.$message.success('导入完成')
          
          // 如果有成功记录，刷新列表
          if (this.importResult.success > 0) {
            this.fetchData()
          }
        } else {
          this.$message.error(response.message || '导入失败')
        }
      } catch (error) {
        this.$message.error('导入失败：' + (error.message || '未知错误'))
      } finally {
        this.uploading = false
      }
    },
    
    // 关闭导入对话框
    handleImportClose() {
      this.fileList = []
      this.importResult = null
    }
  }
}
</script>

<style scoped>
.health-exam {
  padding: 20px;
}

.detail-content {
  padding: 20px;
}

.analysis-section {
  margin: 20px 0;
}

.analysis-item {
  text-align: center;
  padding: 20px;
}

.analysis-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.analysis-value {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
}

.conclusion-section h4 {
  color: #303133;
  margin-bottom: 10px;
}

.import-container {
  padding: 10px 0;
}

.import-result {
  margin-top: 20px;
}

.import-result h4 {
  font-size: 16px;
  color: #303133;
  margin-bottom: 10px;
}

.import-result h5 {
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
}

.upload-demo {
  text-align: center;
}

.conclusion-section p {
  color: #606266;
  line-height: 1.8;
}
</style>
