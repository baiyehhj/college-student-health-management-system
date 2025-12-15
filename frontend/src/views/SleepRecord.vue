<template>
  <div class="sleep-record">
    <el-card>
      <div slot="header" class="clearfix">
        <span>作息记录管理</span>
        <el-button
          style="float: right"
          type="primary"
          icon="el-icon-plus"
          @click="handleAdd"
        >
          添加记录
        </el-button>
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
        <el-table-column prop="sleepTime" label="入睡时间" width="160"></el-table-column>
        <el-table-column prop="wakeTime" label="起床时间" width="160"></el-table-column>
        <el-table-column prop="duration" label="时长(小时)" width="110"></el-table-column>
        <el-table-column prop="quality" label="睡眠质量" width="100">
          <template slot-scope="scope">
            <el-tag :type="getQualityType(scope.row.quality)">
              {{ getQualityName(scope.row.quality) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="deepSleepDuration" label="深睡(小时)" width="110"></el-table-column>
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
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="dataForm"
        :model="dataForm"
        :rules="dataRules"
        label-width="110px"
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
        
        <el-form-item label="入睡时间" prop="sleepTime">
          <el-date-picker
            v-model="dataForm.sleepTime"
            type="datetime"
            placeholder="选择时间"
            value-format="yyyy-MM-dd HH:mm:ss"
            style="width: 100%"
          ></el-date-picker>
        </el-form-item>
        
        <el-form-item label="起床时间" prop="wakeTime">
          <el-date-picker
            v-model="dataForm.wakeTime"
            type="datetime"
            placeholder="选择时间"
            value-format="yyyy-MM-dd HH:mm:ss"
            style="width: 100%"
          ></el-date-picker>
        </el-form-item>
        
        <el-form-item label="睡眠质量" prop="quality">
          <el-radio-group v-model="dataForm.quality">
            <el-radio :label="1">差</el-radio>
            <el-radio :label="2">一般</el-radio>
            <el-radio :label="3">良好</el-radio>
            <el-radio :label="4">优秀</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="深睡时长" prop="deepSleepDuration">
          <el-input-number
            v-model="dataForm.deepSleepDuration"
            :min="0"
            :precision="1"
            style="width: 100%"
          ></el-input-number>
          <span style="margin-left: 10px">小时</span>
        </el-form-item>
        
        <el-form-item label="做梦次数" prop="dreamCount">
          <el-input-number
            v-model="dataForm.dreamCount"
            :min="0"
            style="width: 100%"
          ></el-input-number>
        </el-form-item>
        
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
  listSleepRecords,
  addSleepRecord,
  updateSleepRecord,
  deleteSleepRecord
} from '@/api/sleep'

export default {
  name: 'SleepRecordPage',
  data() {
    return {
      loading: false,
      tableData: [],
      queryForm: {
        startDate: '',
        endDate: ''
      },
      pagination: {
        page: 1,
        size: 10,
        total: 0
      },
      dialogVisible: false,
      dialogTitle: '添加睡眠记录',
      isEdit: false,
      submitLoading: false,
      dataForm: {
        id: null,
        recordDate: '',
        sleepTime: '',
        wakeTime: '',
        quality: 3,
        deepSleepDuration: 0,
        dreamCount: 0,
        description: ''
      },
      dataRules: {
        recordDate: [
          { required: true, message: '请选择记录日期', trigger: 'change' }
        ],
        sleepTime: [
          { required: true, message: '请选择入睡时间', trigger: 'change' }
        ],
        wakeTime: [
          { required: true, message: '请选择起床时间', trigger: 'change' }
        ]
      }
    }
  },
  created() {
    this.initQueryDate()
    this.fetchData()
  },
  methods: {
    initQueryDate() {
      const end = new Date()
      const start = new Date()
      start.setDate(start.getDate() - 7)
      
      this.queryForm.startDate = this.formatDate(start)
      this.queryForm.endDate = this.formatDate(end)
    },
    
    formatDate(date) {
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    
    async fetchData() {
      this.loading = true
      try {
        const params = {
          startDate: this.queryForm.startDate,
          endDate: this.queryForm.endDate,
          page: this.pagination.page,
          size: this.pagination.size
        }
        
        const response = await listSleepRecords(params)
        this.tableData = response.data.records
        this.pagination.total = response.data.total
      } catch (error) {
        this.$message.error('获取数据失败')
      } finally {
        this.loading = false
      }
    },
    
    handleQuery() {
      this.pagination.page = 1
      this.fetchData()
    },
    
    handleReset() {
      this.initQueryDate()
      this.handleQuery()
    },
    
    handleAdd() {
      this.dialogTitle = '添加睡眠记录'
      this.isEdit = false
      this.dialogVisible = true
      this.dataForm.recordDate = this.formatDate(new Date())
    },
    
    handleEdit(row) {
      this.dialogTitle = '编辑睡眠记录'
      this.isEdit = true
      this.dialogVisible = true
      this.dataForm = { ...row }
    },
    
    handleDelete(row) {
      this.$confirm('确认删除该记录吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await deleteSleepRecord(row.id)
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
            await updateSleepRecord(this.dataForm.id, this.dataForm)
            this.$message.success('更新成功')
          } else {
            await addSleepRecord(this.dataForm)
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
        recordDate: '',
        sleepTime: '',
        wakeTime: '',
        quality: 3,
        deepSleepDuration: 0,
        dreamCount: 0,
        description: ''
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
    
    getQualityName(quality) {
      const map = { 1: '差', 2: '一般', 3: '良好', 4: '优秀' }
      return map[quality] || '未知'
    },
    
    getQualityType(quality) {
      const map = { 1: 'danger', 2: 'warning', 3: 'primary', 4: 'success' }
      return map[quality] || ''
    }
  }
}
</script>

<style scoped>
.sleep-record {
  padding: 20px;
}

.query-form {
  margin-bottom: 20px;
}
</style>
