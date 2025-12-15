<template>
  <div class="diet-record">
    <el-card>
      <div slot="header" class="clearfix">
        <span>饮食记录管理</span>
        <div style="float: right">
          <el-button
            type="primary"
            icon="el-icon-plus"
            @click="handleAdd"
          >
            添加记录
          </el-button>
          <el-button
            type="info"
            icon="el-icon-remove-outline"
            @click="handleAddNone"
          >
            添加"无"记录
          </el-button>
        </div>
      </div>
      
      <!-- 查询条件 -->
      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="开始日期">
          <el-date-picker
            v-model="queryForm.startDate"
            type="date"
            placeholder="选择日期"
            value-format="yyyy-MM-dd"
          ></el-date-picker>
        </el-form-item>
        
        <el-form-item label="结束日期">
          <el-date-picker
            v-model="queryForm.endDate"
            type="date"
            placeholder="选择日期"
            value-format="yyyy-MM-dd"
          ></el-date-picker>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">
            查询
          </el-button>
          <el-button icon="el-icon-refresh" @click="handleReset">
            重置
          </el-button>
        </el-form-item>
      </el-form>
      
      <!-- 数据表格 -->
      <el-table
        :data="tableData"
        border
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column prop="recordDate" label="记录日期" width="120"></el-table-column>
        <el-table-column prop="mealType" label="餐次" width="100">
          <template slot-scope="scope">
            <el-tag :type="getMealTypeTag(scope.row.mealType)">
              {{ getMealTypeName(scope.row.mealType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="foodName" label="食物名称" width="200"></el-table-column>
        <el-table-column prop="foodCategory" label="类别" width="100"></el-table-column>
        <el-table-column prop="calories" label="热量(千卡)" width="120"></el-table-column>
        <el-table-column prop="protein" label="蛋白质(克)" width="120"></el-table-column>
        <el-table-column prop="carbs" label="碳水(克)" width="110"></el-table-column>
        <el-table-column prop="fat" label="脂肪(克)" width="100"></el-table-column>
        <el-table-column prop="description" label="备注" show-overflow-tooltip></el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template slot-scope="scope">
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
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pagination.size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        style="margin-top: 20px; text-align: right"
      ></el-pagination>
    </el-card>
    
    <!-- 添加/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="650px"
      @close="handleDialogClose"
    >
      <el-form
        ref="dataForm"
        :model="dataForm"
        :rules="dataRules"
        label-width="100px"
      >
        <el-form-item label="记录日期" prop="recordDate">
          <el-date-picker
            v-model="dataForm.recordDate"
            type="date"
            placeholder="选择日期"
            value-format="yyyy-MM-dd"
            style="width: 100%"
          ></el-date-picker>
        </el-form-item>
        
        <el-form-item label="餐次" prop="mealType">
          <el-radio-group v-model="dataForm.mealType">
            <el-radio :label="1">早餐</el-radio>
            <el-radio :label="2">午餐</el-radio>
            <el-radio :label="3">晚餐</el-radio>
            <el-radio :label="4">加餐</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <!-- AI识图功能 -->
        <el-form-item label="AI识图" style="margin-bottom: 20px;">
          <div class="ai-recognition-section">
            <el-upload
              ref="upload"
              class="upload-demo"
              action="#"
              :auto-upload="false"
              :show-file-list="false"
              :on-change="handleImageChange"
              accept="image/*"
            >
              <el-button
                size="medium"
                type="success"
                icon="el-icon-camera"
                :loading="recognizing"
              >
                {{ recognizing ? 'AI识别中...' : '拍照/上传图片识别' }}
              </el-button>
            </el-upload>
            <div v-if="uploadedImage" class="image-preview">
              <img :src="uploadedImageUrl" alt="预览图" />
              <el-button
                type="text"
                icon="el-icon-delete"
                @click="clearImage"
                size="mini"
              >
                清除图片
              </el-button>
            </div>
            <div class="ai-tip">
              <i class="el-icon-info"></i>
              <span>上传食物图片，AI将自动识别并填写营养信息</span>
            </div>
          </div>
        </el-form-item>

        <el-divider></el-divider>
        
        <el-form-item>
          <el-button 
            type="info" 
            size="small" 
            icon="el-icon-close" 
            @click="handleSetNoDiet"
            style="width: 100%"
          >
            该餐次无饮食
          </el-button>
        </el-form-item>
        
        <el-form-item label="食物名称" prop="foodName">
          <el-input v-model="dataForm.foodName" placeholder="请输入食物名称"></el-input>
        </el-form-item>
        
        <el-form-item label="食物类别" prop="foodCategory">
          <el-select v-model="dataForm.foodCategory" placeholder="请选择类别" style="width: 100%">
            <el-option label="主食" value="主食"></el-option>
            <el-option label="蔬菜" value="蔬菜"></el-option>
            <el-option label="肉类" value="肉类"></el-option>
            <el-option label="水果" value="水果"></el-option>
            <el-option label="零食" value="零食"></el-option>
            <el-option label="饮料" value="饮料"></el-option>
            <el-option label="其他" value="其他"></el-option>
          </el-select>
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="热量(千卡)" prop="calories">
              <el-input-number
                v-model="dataForm.calories"
                :min="0"
                :precision="2"
                style="width: 100%"
              ></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="蛋白质(克)" prop="protein">
              <el-input-number
                v-model="dataForm.protein"
                :min="0"
                :precision="2"
                style="width: 100%"
              ></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="碳水(克)" prop="carbs">
              <el-input-number
                v-model="dataForm.carbs"
                :min="0"
                :precision="2"
                style="width: 100%"
              ></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="脂肪(克)" prop="fat">
              <el-input-number
                v-model="dataForm.fat"
                :min="0"
                :precision="2"
                style="width: 100%"
              ></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="备注" prop="description">
          <el-input
            v-model="dataForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
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
  </div>
</template>

<script>
import {
  listDietRecords,
  addDietRecord,
  updateDietRecord,
  deleteDietRecord
} from '@/api/diet'

// 新增：导入AI识图API
import { recognizeFood } from '@/api/foodRecognition'

export default {
  name: 'DietRecord',
  data() {
    return {
      loading: false,
      tableData: [],
      
      // 查询条件
      queryForm: {
        startDate: '',
        endDate: ''
      },
      
      // 分页
      pagination: {
        page: 1,
        size: 10,
        total: 0
      },
      
      // 对话框
      dialogVisible: false,
      dialogTitle: '添加饮食记录',
      isEdit: false,
      submitLoading: false,
      
      // 表单数据
      dataForm: {
        id: null,
        recordDate: '',
        mealType: 1,
        foodName: '',
        foodCategory: '',
        calories: 0,
        protein: 0,
        carbs: 0,
        fat: 0,
        description: ''
      },
      
      // 表单验证规则
      dataRules: {
        recordDate: [
          { required: true, message: '请选择记录日期', trigger: 'change' }
        ],
        mealType: [
          { required: true, message: '请选择餐次', trigger: 'change' }
        ],
        foodName: [
          { required: true, message: '请输入食物名称', trigger: 'blur' }
        ]
      },
      
      // AI识图相关
      recognizing: false,  // 是否正在识别
      uploadedImage: null,  // 上传的图片文件
      uploadedImageUrl: '',  // 图片预览URL
    }
  },
  created() {
    this.initQueryDate()
    this.fetchData()
  },
  methods: {
    // 初始化查询日期(默认查询最近7天)
    initQueryDate() {
      const end = new Date()
      const start = new Date()
      start.setDate(start.getDate() - 7)
      
      this.queryForm.startDate = this.formatDate(start)
      this.queryForm.endDate = this.formatDate(end)
    },
    
    // 格式化日期
    formatDate(date) {
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    
    // 获取数据
    async fetchData() {
      this.loading = true
      try {
        const params = {
          startDate: this.queryForm.startDate,
          endDate: this.queryForm.endDate,
          page: this.pagination.page,
          size: this.pagination.size
        }
        
        const response = await listDietRecords(params)
        this.tableData = response.data.records
        this.pagination.total = response.data.total
      } catch (error) {
        this.$message.error('获取数据失败')
      } finally {
        this.loading = false
      }
    },
    
    // 查询
    handleQuery() {
      this.pagination.page = 1
      this.fetchData()
    },
    
    // 重置
    handleReset() {
      this.initQueryDate()
      this.fetchData()
    },
    
    // 添加记录
    handleAdd() {
      this.dialogTitle = '添加饮食记录'
      this.isEdit = false
      this.dialogVisible = true
      this.dataForm = {
        id: null,
        recordDate: this.formatDate(new Date()),
        mealType: 1,
        foodName: '',
        foodCategory: '',
        calories: 0,
        protein: 0,
        carbs: 0,
        fat: 0,
        description: ''
      }
    },
    
    // 添加"无"记录
    handleAddNone() {
      this.dialogTitle = '添加"无"记录'
      this.isEdit = false
      this.dialogVisible = true
      this.dataForm = {
        id: null,
        recordDate: this.formatDate(new Date()),
        mealType: 1,
        foodName: '无',
        foodCategory: '其他',
        calories: 0,
        protein: 0,
        carbs: 0,
        fat: 0,
        description: '该餐次无饮食'
      }
    },
    
    // 编辑记录
    handleEdit(row) {
      this.dialogTitle = '编辑饮食记录'
      this.isEdit = true
      this.dialogVisible = true
      this.dataForm = { ...row }
    },
    
    // 删除记录
    handleDelete(row) {
      this.$confirm('确定要删除这条记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await deleteDietRecord(row.id)
          this.$message.success('删除成功')
          this.fetchData()
        } catch (error) {
          this.$message.error('删除失败')
        }
      }).catch(() => {})
    },
    
    // 设置无饮食
    handleSetNoDiet() {
      this.dataForm.foodName = '无'
      this.dataForm.foodCategory = '其他'
      this.dataForm.calories = 0
      this.dataForm.protein = 0
      this.dataForm.carbs = 0
      this.dataForm.fat = 0
      this.dataForm.description = '该餐次无饮食'
    },
    
    // 提交表单
    handleSubmit() {
      this.$refs.dataForm.validate(async (valid) => {
        if (!valid) {
          return false
        }
        
        this.submitLoading = true
        try {
          if (this.isEdit) {
            await updateDietRecord(this.dataForm.id, this.dataForm)
            this.$message.success('更新成功')
          } else {
            await addDietRecord(this.dataForm)
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
    
    // AI识图相关方法
    
    /**
     * 处理图片选择
     */
    handleImageChange(file) {
      this.uploadedImage = file.raw
      this.uploadedImageUrl = URL.createObjectURL(file.raw)
      
      // 自动开始识别
      this.recognizeFoodImage()
    },
    
    /**
     * AI识别食物
     */
    async recognizeFoodImage() {
      if (!this.uploadedImage) {
        this.$message.warning('请先上传图片')
        return
      }
      
      this.recognizing = true
      
      try {
        const response = await recognizeFood(this.uploadedImage)
        
        if (response.code === 200 && response.data) {
          const foodInfo = response.data
          
          // 自动填充表单
          this.dataForm.foodName = foodInfo.foodName || ''
          this.dataForm.foodCategory = foodInfo.foodCategory || ''
          this.dataForm.calories = foodInfo.calories || 0
          this.dataForm.protein = foodInfo.protein || 0
          this.dataForm.carbs = foodInfo.carbs || 0
          this.dataForm.fat = foodInfo.fat || 0
          
          // 如果有描述，添加到备注中
          if (foodInfo.description) {
            this.dataForm.description = foodInfo.description
          }
          
          // 显示置信度
          if (foodInfo.confidence) {
            const confidencePercent = (foodInfo.confidence * 100).toFixed(1)
            this.$message.success({
              message: `识别成功！置信度: ${confidencePercent}%`,
              duration: 3000
            })
          } else {
            this.$message.success('识别成功！')
          }
          
        } else {
          this.$message.error(response.message || '识别失败，请重试')
        }
        
      } catch (error) {
        console.error('AI识别失败:', error)
        this.$message.error('识别失败: ' + (error.message || '未知错误'))
      } finally {
        this.recognizing = false
      }
    },
    
    /**
     * 清除图片
     */
    clearImage() {
      this.uploadedImage = null
      this.uploadedImageUrl = ''
      
      // 清空上传组件
      if (this.$refs.upload) {
        this.$refs.upload.clearFiles()
      }
    },
    
    // 对话框关闭
    handleDialogClose() {
      this.clearImage()  // 清除图片
      this.resetForm()
      this.$refs.dataForm.clearValidate()
    },
    
    // 重置表单
    resetForm() {
      this.dataForm = {
        id: null,
        recordDate: '',
        mealType: 1,
        foodName: '',
        foodCategory: '',
        calories: 0,
        protein: 0,
        carbs: 0,
        fat: 0,
        description: ''
      }
    },
    
    // 分页
    handleSizeChange(size) {
      this.pagination.size = size
      this.fetchData()
    },
    
    handleCurrentChange(page) {
      this.pagination.page = page
      this.fetchData()
    },
    
    // 获取餐次名称
    getMealTypeName(type) {
      const types = {
        1: '早餐',
        2: '午餐',
        3: '晚餐',
        4: '加餐'
      }
      return types[type] || '未知'
    },
    
    // 获取餐次标签类型
    getMealTypeTag(type) {
      const tags = {
        1: '',
        2: 'success',
        3: 'warning',
        4: 'info'
      }
      return tags[type] || ''
    }
  }
}
</script>

<style scoped>
.diet-record {
  padding: 20px;
}

.query-form {
  margin-bottom: 20px;
}

/* AI识图样式 */
.ai-recognition-section {
  text-align: center;
  padding: 15px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8f4f8 100%);
  border-radius: 8px;
  border: 2px dashed #409EFF;
}

.image-preview {
  margin-top: 15px;
  text-align: center;
}

.image-preview img {
  max-width: 250px;
  max-height: 250px;
  border-radius: 8px;
  box-shadow: 0 4px 12px 0 rgba(0, 0, 0, 0.15);
  margin-bottom: 10px;
  transition: transform 0.3s ease;
}

.image-preview img:hover {
  transform: scale(1.05);
}

.ai-tip {
  margin-top: 12px;
  color: #606266;
  font-size: 13px;
  line-height: 1.5;
}

.ai-tip i {
  margin-right: 5px;
  color: #409EFF;
  font-size: 16px;
  vertical-align: middle;
}

.upload-demo {
  display: inline-block;
}
</style>
