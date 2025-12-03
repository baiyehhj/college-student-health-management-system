<template>
  <div class="mood-record">
    <el-card>
      <div slot="header" class="clearfix">
        <span>情绪记录管理</span>
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
        <el-table-column prop="recordTime" label="记录时间" width="160"></el-table-column>
        <el-table-column prop="moodType" label="情绪类型" width="120">
          <template slot-scope="scope">
            <el-tag :type="getMoodTypeColor(scope.row.moodType)">
              {{ getMoodTypeName(scope.row.moodType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="moodScore" label="情绪评分" width="100">
          <template slot-scope="scope">
            <el-rate
              v-model="scope.row.moodScore"
              disabled
              show-score
              text-color="#ff9900"
            ></el-rate>
          </template>
        </el-table-column>
        <el-table-column prop="triggerEvent" label="触发事件" show-overflow-tooltip></el-table-column>
        <el-table-column prop="description" label="详细描述" show-overflow-tooltip></el-table-column>
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
        
        <el-form-item label="记录时间" prop="recordTime">
          <el-date-picker
            v-model="dataForm.recordTime"
            type="datetime"
            placeholder="选择时间"
            value-format="yyyy-MM-dd HH:mm:ss"
            style="width: 100%"
          ></el-date-picker>
        </el-form-item>
        
        <el-form-item label="情绪类型" prop="moodType">
          <el-select v-model="dataForm.moodType" placeholder="请选择情绪类型" style="width: 100%">
            <el-option label="😊 开心" :value="1"></el-option>
            <el-option label="😌 平静" :value="2"></el-option>
            <el-option label="😰 焦虑" :value="3"></el-option>
            <el-option label="😢 悲伤" :value="4"></el-option>
            <el-option label="😡 愤怒" :value="5"></el-option>
            <el-option label="😫 压力" :value="6"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="情绪评分" prop="moodScore">
          <el-rate
            v-model="dataForm.moodScore"
            show-score
            text-color="#ff9900"
          ></el-rate>
          <span style="margin-left: 10px; color: #999">1-10分，分数越高情绪越好</span>
        </el-form-item>
        
        <el-form-item label="触发事件" prop="triggerEvent">
          <el-input
            v-model="dataForm.triggerEvent"
            placeholder="请输入触发该情绪的事件"
          ></el-input>
        </el-form-item>
        
        <el-form-item label="详细描述" prop="description">
          <el-input
            v-model="dataForm.description"
            type="textarea"
            :rows="4"
            placeholder="请详细描述你的感受和想法"
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
  listMoodRecords,
  addMoodRecord,
  updateMoodRecord,
  deleteMoodRecord
} from '@/api/mood'

export default {
  name: 'MoodRecordPage',
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
      dialogTitle: '添加情绪记录',
      isEdit: false,
      submitLoading: false,
      dataForm: {
        id: null,
        recordDate: '',
        recordTime: '',
        moodType: 1,
        moodScore: 5,
        triggerEvent: '',
        description: ''
      },
      dataRules: {
        recordDate: [
          { required: true, message: '请选择记录日期', trigger: 'change' }
        ],
        recordTime: [
          { required: true, message: '请选择记录时间', trigger: 'change' }
        ],
        moodType: [
          { required: true, message: '请选择情绪类型', trigger: 'change' }
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
        
        const response = await listMoodRecords(params)
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
      this.dialogTitle = '添加情绪记录'
      this.isEdit = false
      this.dialogVisible = true
      const now = new Date()
      this.dataForm.recordDate = this.formatDate(now)
      this.dataForm.recordTime = this.formatDateTime(now)
    },
    
    formatDateTime(date) {
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      const seconds = String(date.getSeconds()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
    },
    
    handleEdit(row) {
      this.dialogTitle = '编辑情绪记录'
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
          await deleteMoodRecord(row.id)
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
            await updateMoodRecord(this.dataForm.id, this.dataForm)
            this.$message.success('更新成功')
          } else {
            await addMoodRecord(this.dataForm)
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
        recordTime: '',
        moodType: 1,
        moodScore: 5,
        triggerEvent: '',
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
    
    getMoodTypeName(type) {
      const map = {
        1: '😊 开心',
        2: '😌 平静',
        3: '😰 焦虑',
        4: '😢 悲伤',
        5: '😡 愤怒',
        6: '😫 压力'
      }
      return map[type] || '未知'
    },
    
    getMoodTypeColor(type) {
      const map = {
        1: 'success',
        2: 'primary',
        3: 'warning',
        4: 'info',
        5: 'danger',
        6: 'warning'
      }
      return map[type] || ''
    }
  }
}
</script>

<style scoped>
.mood-record {
  padding: 20px;
}

.query-form {
  margin-bottom: 20px;
}
</style>
