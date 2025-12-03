<template>
  <div class="exercise-record">
    <el-card>
      <div slot="header" class="clearfix">
        <span>运动记录管理</span>
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
            添加"未运动"记录
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
        <el-table-column prop="exerciseType" label="运动类型" width="120"></el-table-column>
        <el-table-column prop="duration" label="时长(分钟)" width="110"></el-table-column>
        <el-table-column prop="caloriesBurned" label="消耗热量(千卡)" width="130"></el-table-column>
        <el-table-column prop="intensity" label="强度" width="100">
          <template slot-scope="scope">
            <el-tag :type="getIntensityTag(scope.row.intensity)">
              {{ getIntensityName(scope.row.intensity) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="distance" label="距离(公里)" width="110"></el-table-column>
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
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="dataForm"
        :model="dataForm"
        :rules="dataRules"
        label-width="120px"
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
        
        <el-form-item>
          <el-button 
            type="info" 
            size="small" 
            icon="el-icon-close" 
            @click="handleSetNoExercise"
            style="width: 100%"
          >
            当天无运动
          </el-button>
        </el-form-item>
        
        <el-form-item label="运动类型" prop="exerciseType">
          <el-select v-model="dataForm.exerciseType" placeholder="请选择运动类型" style="width: 100%">
            <el-option label="跑步" value="跑步"></el-option>
            <el-option label="游泳" value="游泳"></el-option>
            <el-option label="骑行" value="骑行"></el-option>
            <el-option label="健身" value="健身"></el-option>
            <el-option label="瑜伽" value="瑜伽"></el-option>
            <el-option label="篮球" value="篮球"></el-option>
            <el-option label="足球" value="足球"></el-option>
            <el-option label="羽毛球" value="羽毛球"></el-option>
            <el-option label="乒乓球" value="乒乓球"></el-option>
            <el-option label="爬山" value="爬山"></el-option>
            <el-option label="其他" value="其他"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="运动时长" prop="duration">
          <el-input-number
            v-model="dataForm.duration"
            :min="1"
            :max="999"
            placeholder="请输入运动时长"
            style="width: 100%"
          ></el-input-number>
          <span style="margin-left: 10px; color: #909399">分钟</span>
        </el-form-item>
        
        <el-form-item label="消耗热量" prop="caloriesBurned">
          <el-input-number
            v-model="dataForm.caloriesBurned"
            :min="0"
            :max="9999"
            :precision="2"
            placeholder="请输入消耗热量"
            style="width: 100%"
          ></el-input-number>
          <span style="margin-left: 10px; color: #909399">千卡</span>
        </el-form-item>
        
        <el-form-item label="运动强度" prop="intensity">
          <el-radio-group v-model="dataForm.intensity">
            <el-radio :label="1">低强度</el-radio>
            <el-radio :label="2">中强度</el-radio>
            <el-radio :label="3">高强度</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="运动距离" prop="distance">
          <el-input-number
            v-model="dataForm.distance"
            :min="0"
            :max="999"
            :precision="2"
            placeholder="请输入运动距离"
            style="width: 100%"
          ></el-input-number>
          <span style="margin-left: 10px; color: #909399">公里</span>
        </el-form-item>
        
        <el-form-item label="备注">
          <el-input
            v-model="dataForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          ></el-input>
        </el-form-item>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listExerciseRecords,
  addExerciseRecord,
  updateExerciseRecord,
  deleteExerciseRecord
} from '@/api/exercise'

export default {
  name: 'ExerciseRecord',
  data() {
    return {
      loading: false,
      tableData: [],
      
      // 查询表单
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
      dialogTitle: '',
      isEdit: false,
      submitting: false,
      
      // 表单数据
      dataForm: {
        id: null,
        recordDate: '',
        exerciseType: '',
        duration: null,
        caloriesBurned: null,
        intensity: 2,
        distance: null,
        description: ''
      },
      
      // 表单验证
      dataRules: {
        recordDate: [
          { required: true, message: '请选择记录日期', trigger: 'change' }
        ],
        exerciseType: [
          { required: true, message: '请选择运动类型', trigger: 'change' }
        ],
        duration: [
          { required: true, message: '请输入运动时长', trigger: 'blur' }
        ]
      }
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
        
        const response = await listExerciseRecords(params)
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
      this.pagination.page = 1
      this.fetchData()
    },
    
    // 添加
    handleAdd() {
      this.isEdit = false
      this.dialogTitle = '添加运动记录'
      this.dataForm = {
        id: null,
        recordDate: this.formatDate(new Date()),
        exerciseType: '',
        duration: null,
        caloriesBurned: null,
        intensity: 2,
        distance: null,
        description: ''
      }
      this.dialogVisible = true
    },
    
    // 快速添加"未运动"记录
    async handleAddNone() {
      this.$confirm('确认添加今日"无运动"记录吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(async () => {
        try {
          await addExerciseRecord({
            recordDate: this.formatDate(new Date()),
            exerciseType: '无',
            duration: 0,
            caloriesBurned: 0,
            intensity: 1,
            distance: 0,
            description: '今日无运动'
          })
          this.$message.success('添加成功')
          this.fetchData()
        } catch (error) {
          this.$message.error('添加失败')
        }
      }).catch(() => {})
    },
    
    // 快速设置"无运动"
    handleSetNoExercise() {
      this.dataForm.exerciseType = '无'
      this.dataForm.duration = 0
      this.dataForm.caloriesBurned = 0
      this.dataForm.intensity = 1
      this.dataForm.distance = 0
      this.dataForm.description = '当天无运动'
    },
    
    // 编辑
    handleEdit(row) {
      this.isEdit = true
      this.dialogTitle = '编辑运动记录'
      this.dataForm = {
        ...row
      }
      this.dialogVisible = true
    },
    
    // 删除
    handleDelete(row) {
      this.$confirm('确定要删除这条记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await deleteExerciseRecord(row.id)
          this.$message.success('删除成功')
          this.fetchData()
        } catch (error) {
          this.$message.error('删除失败')
        }
      })
    },
    
    // 提交
    handleSubmit() {
      this.$refs.dataForm.validate(async (valid) => {
        if (!valid) return
        
        this.submitting = true
        try {
          if (this.isEdit) {
            await updateExerciseRecord(this.dataForm.id, this.dataForm)
            this.$message.success('更新成功')
          } else {
            await addExerciseRecord(this.dataForm)
            this.$message.success('添加成功')
          }
          this.dialogVisible = false
          this.fetchData()
        } catch (error) {
          this.$message.error(this.isEdit ? '更新失败' : '添加失败')
        } finally {
          this.submitting = false
        }
      })
    },
    
    // 关闭对话框
    handleDialogClose() {
      this.$refs.dataForm.resetFields()
    },
    
    // 分页
    handleSizeChange(size) {
      this.pagination.size = size
      this.pagination.page = 1
      this.fetchData()
    },
    
    handleCurrentChange(page) {
      this.pagination.page = page
      this.fetchData()
    },
    
    // 获取强度标签类型
    getIntensityTag(intensity) {
      const map = {
        1: 'success',
        2: 'warning',
        3: 'danger'
      }
      return map[intensity] || 'info'
    },
    
    // 获取强度名称
    getIntensityName(intensity) {
      const map = {
        1: '低强度',
        2: '中强度',
        3: '高强度'
      }
      return map[intensity] || '未知'
    }
  }
}
</script>

<style scoped>
.exercise-record {
  padding: 20px;
}

.query-form {
  margin-bottom: 20px;
}
</style>
