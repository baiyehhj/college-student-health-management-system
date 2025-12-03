<template>
  <div class="student-management">
    <el-card>
      <div slot="header" class="card-header">
        <span>学生管理</span>
        <div class="header-actions">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索学号/姓名/手机号"
            prefix-icon="el-icon-search"
            style="width: 250px; margin-right: 10px;"
            clearable
            @clear="handleSearch"
            @keyup.enter.native="handleSearch"
          ></el-input>
          <el-select
            v-model="statusFilter"
            placeholder="状态筛选"
            clearable
            style="width: 120px; margin-right: 10px;"
            @change="handleSearch"
          >
            <el-option label="正常" :value="1"></el-option>
            <el-option label="禁用" :value="0"></el-option>
          </el-select>
          <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
        </div>
      </div>
      
      <!-- 学生列表表格 -->
      <el-table
        v-loading="loading"
        :data="studentList"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column prop="studentNo" label="学号" width="120"></el-table-column>
        <el-table-column prop="name" label="姓名" width="100"></el-table-column>
        <el-table-column label="性别" width="80">
          <template slot-scope="scope">
            {{ scope.row.gender === 1 ? '男' : '女' }}
          </template>
        </el-table-column>
        <el-table-column prop="age" label="年龄" width="80"></el-table-column>
        <el-table-column prop="phone" label="手机号" width="130"></el-table-column>
        <el-table-column prop="email" label="邮箱" min-width="180"></el-table-column>
        <el-table-column prop="major" label="专业" width="150"></el-table-column>
        <el-table-column prop="className" label="班级" width="120"></el-table-column>
        <el-table-column label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
              {{ scope.row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="viewDetail(scope.row)">
              <i class="el-icon-view"></i> 详情
            </el-button>
            <el-button
              v-if="scope.row.status === 1"
              type="text"
              size="small"
              style="color: #f56c6c;"
              @click="toggleStatus(scope.row, 0)"
            >
              <i class="el-icon-lock"></i> 禁用
            </el-button>
            <el-button
              v-else
              type="text"
              size="small"
              style="color: #67c23a;"
              @click="toggleStatus(scope.row, 1)"
            >
              <i class="el-icon-unlock"></i> 启用
            </el-button>
            <el-button
              type="text"
              size="small"
              style="color: #e6a23c;"
              @click="resetPassword(scope.row)"
            >
              <i class="el-icon-key"></i> 重置密码
            </el-button>
            <el-button
              type="text"
              size="small"
              @click="exportHealth(scope.row)"
            >
              <i class="el-icon-download"></i> 导出
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <el-pagination
        style="margin-top: 20px; text-align: right;"
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        :page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :current-page="pageNum"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      ></el-pagination>
    </el-card>
    
    <!-- 学生详情对话框 -->
    <el-dialog
      title="学生详情"
      :visible.sync="detailDialogVisible"
      width="800px"
    >
      <div v-if="studentDetail">
        <!-- 基本信息 -->
        <el-descriptions title="基本信息" :column="3" border>
          <el-descriptions-item label="学号">{{ studentDetail.basicInfo.studentNo }}</el-descriptions-item>
          <el-descriptions-item label="姓名">{{ studentDetail.basicInfo.name }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ studentDetail.basicInfo.gender === 1 ? '男' : '女' }}</el-descriptions-item>
          <el-descriptions-item label="年龄">{{ studentDetail.basicInfo.age || '-' }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ studentDetail.basicInfo.phone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ studentDetail.basicInfo.email || '-' }}</el-descriptions-item>
          <el-descriptions-item label="专业">{{ studentDetail.basicInfo.major || '-' }}</el-descriptions-item>
          <el-descriptions-item label="班级">{{ studentDetail.basicInfo.className || '-' }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="studentDetail.basicInfo.status === 1 ? 'success' : 'danger'" size="small">
              {{ studentDetail.basicInfo.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
        
        <!-- 近7天数据统计 -->
        <h4 style="margin-top: 20px;">近7天数据记录统计</h4>
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card class="mini-stat-card">
              <div class="mini-stat-icon" style="background: #67c23a;"><i class="el-icon-food"></i></div>
              <div class="mini-stat-info">
                <div class="mini-stat-value">{{ studentDetail.dietRecordCount }}</div>
                <div class="mini-stat-label">饮食记录</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="mini-stat-card">
              <div class="mini-stat-icon" style="background: #409eff;"><i class="el-icon-trophy"></i></div>
              <div class="mini-stat-info">
                <div class="mini-stat-value">{{ studentDetail.exerciseRecordCount }}</div>
                <div class="mini-stat-label">运动记录</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="mini-stat-card">
              <div class="mini-stat-icon" style="background: #909399;"><i class="el-icon-moon-night"></i></div>
              <div class="mini-stat-info">
                <div class="mini-stat-value">{{ studentDetail.sleepRecordCount }}</div>
                <div class="mini-stat-label">睡眠记录</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="mini-stat-card">
              <div class="mini-stat-icon" style="background: #e6a23c;"><i class="el-icon-sunny"></i></div>
              <div class="mini-stat-info">
                <div class="mini-stat-value">{{ studentDetail.moodRecordCount }}</div>
                <div class="mini-stat-label">情绪记录</div>
              </div>
            </el-card>
          </el-col>
        </el-row>
        
        <!-- 最新体检报告 -->
        <h4 style="margin-top: 20px;">最新体检报告</h4>
        <div v-if="studentDetail.latestExamination">
          <el-descriptions :column="4" border size="small">
            <el-descriptions-item label="体检日期">{{ studentDetail.latestExamination.examDate }}</el-descriptions-item>
            <el-descriptions-item label="身高">{{ studentDetail.latestExamination.height }} cm</el-descriptions-item>
            <el-descriptions-item label="体重">{{ studentDetail.latestExamination.weight }} kg</el-descriptions-item>
            <el-descriptions-item label="BMI">{{ studentDetail.latestExamination.bmi }}</el-descriptions-item>
            <el-descriptions-item label="血压">{{ studentDetail.latestExamination.bloodPressureHigh }}/{{ studentDetail.latestExamination.bloodPressureLow }} mmHg</el-descriptions-item>
            <el-descriptions-item label="心率">{{ studentDetail.latestExamination.heartRate }} 次/分</el-descriptions-item>
            <el-descriptions-item label="视力(左/右)">{{ studentDetail.latestExamination.visionLeft }}/{{ studentDetail.latestExamination.visionRight }}</el-descriptions-item>
            <el-descriptions-item label="血糖">{{ studentDetail.latestExamination.bloodSugar }} mmol/L</el-descriptions-item>
          </el-descriptions>
        </div>
        <el-empty v-else description="暂无体检报告" :image-size="60"></el-empty>
        
        <!-- 最新AI健康报告 -->
        <h4 style="margin-top: 20px;">最新AI健康报告</h4>
        <div v-if="studentDetail.latestAIReport">
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="报告日期">{{ studentDetail.latestAIReport.reportDate }}</el-descriptions-item>
            <el-descriptions-item label="健康评分">
              <el-rate :value="studentDetail.latestAIReport.overallScore / 20" disabled></el-rate>
              {{ studentDetail.latestAIReport.overallScore }} 分
            </el-descriptions-item>
          </el-descriptions>
        </div>
        <el-empty v-else description="暂无AI健康报告" :image-size="60"></el-empty>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getStudentList, getStudentDetail, updateStudentStatus, resetStudentPassword, exportStudentHealthData } from '@/api/admin'

export default {
  name: 'StudentManagement',
  data() {
    return {
      loading: false,
      studentList: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      searchKeyword: '',
      statusFilter: null,
      detailDialogVisible: false,
      studentDetail: null
    }
  },
  created() {
    this.loadStudentList()
  },
  methods: {
    async loadStudentList() {
      this.loading = true
      try {
        const res = await getStudentList({
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          keyword: this.searchKeyword,
          status: this.statusFilter
        })
        this.studentList = res.data.list || []
        this.total = res.data.total || 0
      } catch (error) {
        this.$message.error('加载学生列表失败')
      } finally {
        this.loading = false
      }
    },
    
    handleSearch() {
      this.pageNum = 1
      this.loadStudentList()
    },
    
    handleSizeChange(size) {
      this.pageSize = size
      this.pageNum = 1
      this.loadStudentList()
    },
    
    handlePageChange(page) {
      this.pageNum = page
      this.loadStudentList()
    },
    
    async viewDetail(row) {
      try {
        const res = await getStudentDetail(row.id)
        this.studentDetail = res.data
        this.detailDialogVisible = true
      } catch (error) {
        this.$message.error('获取学生详情失败')
      }
    },
    
    toggleStatus(row, status) {
      const action = status === 1 ? '启用' : '禁用'
      this.$confirm(`确认要${action}学生 ${row.name} 的账号吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await updateStudentStatus(row.id, status)
          this.$message.success(`已${action}该学生账号`)
          this.loadStudentList()
        } catch (error) {
          this.$message.error(`${action}失败`)
        }
      })
    },
    
    resetPassword(row) {
      this.$confirm(`确认要重置学生 ${row.name} 的密码吗？密码将被重置为 123456`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const res = await resetStudentPassword(row.id)
          this.$message.success(res.msg || '密码已重置')
        } catch (error) {
          this.$message.error('重置密码失败')
        }
      })
    },
    
    async exportHealth(row) {
      try {
        const res = await exportStudentHealthData(row.id)
        const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = `${row.name}_健康数据.xlsx`
        link.click()
        window.URL.revokeObjectURL(url)
        this.$message.success('导出成功')
      } catch (error) {
        this.$message.error('导出失败')
      }
    }
  }
}
</script>

<style scoped>
.student-management {
  padding: 10px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  align-items: center;
}

.mini-stat-card {
  margin-bottom: 10px;
}

.mini-stat-card >>> .el-card__body {
  display: flex;
  align-items: center;
  padding: 15px;
}

.mini-stat-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
}

.mini-stat-icon i {
  font-size: 20px;
  color: white;
}

.mini-stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.mini-stat-label {
  font-size: 12px;
  color: #909399;
}
</style>
