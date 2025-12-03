<template>
  <div class="data-export">
    <el-card>
      <div slot="header">
        <span><i class="el-icon-download"></i> 批量数据导出</span>
      </div>
      
      <el-row :gutter="20">
        <!-- 学生基本信息导出 -->
        <el-col :span="8">
          <el-card class="export-card" shadow="hover">
            <div class="export-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
              <i class="el-icon-user"></i>
            </div>
            <h3>学生基本信息</h3>
            <p class="export-desc">导出所有学生的基本信息，包含学号、姓名、联系方式、专业班级等</p>
            <el-button
              type="primary"
              :loading="exporting.students"
              @click="exportStudents"
            >
              <i class="el-icon-download"></i> 导出Excel
            </el-button>
          </el-card>
        </el-col>
        
        <!-- 健康数据统计导出 -->
        <el-col :span="8">
          <el-card class="export-card" shadow="hover">
            <div class="export-icon" style="background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);">
              <i class="el-icon-s-data"></i>
            </div>
            <h3>健康数据统计</h3>
            <p class="export-desc">导出所有学生的健康数据统计汇总，包含各类记录数量统计</p>
            <el-button
              type="success"
              :loading="exporting.allHealth"
              @click="exportAllHealth"
            >
              <i class="el-icon-download"></i> 导出Excel
            </el-button>
          </el-card>
        </el-col>
        
        <!-- 单个学生数据导出 -->
        <el-col :span="8">
          <el-card class="export-card" shadow="hover">
            <div class="export-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
              <i class="el-icon-document"></i>
            </div>
            <h3>单个学生详细数据</h3>
            <p class="export-desc">导出指定学生的完整健康数据，包含饮食、运动、睡眠、情绪记录</p>
            <el-button
              type="warning"
              @click="showStudentSelect"
            >
              <i class="el-icon-search"></i> 选择学生导出
            </el-button>
          </el-card>
        </el-col>
      </el-row>
      
      <!-- 导出说明 -->
      <el-divider content-position="left">导出说明</el-divider>
      <el-alert
        title="导出提示"
        type="info"
        :closable="false"
        show-icon
      >
        <template slot>
          <ul style="margin: 10px 0; padding-left: 20px; line-height: 2;">
            <li>导出的文件格式为 Excel (.xlsx)，可使用 Microsoft Excel 或 WPS 打开</li>
            <li>学生基本信息导出包含：学号、姓名、性别、年龄、手机号、邮箱、专业、班级、状态、注册时间</li>
            <li>健康数据统计导出包含：每个学生的各类健康记录数量汇总</li>
            <li>单个学生详细数据导出包含：饮食记录、运动记录、睡眠记录、情绪记录的详细数据</li>
          </ul>
        </template>
      </el-alert>
    </el-card>
    
    <!-- 学生选择对话框 -->
    <el-dialog
      title="选择学生"
      :visible.sync="studentSelectVisible"
      width="600px"
    >
      <el-input
        v-model="studentSearchKeyword"
        placeholder="搜索学号或姓名"
        prefix-icon="el-icon-search"
        style="margin-bottom: 15px;"
        @input="searchStudents"
      ></el-input>
      
      <el-table
        v-loading="loadingStudents"
        :data="filteredStudents"
        height="300"
        border
        stripe
        @row-click="selectStudent"
        style="cursor: pointer;"
      >
        <el-table-column prop="studentNo" label="学号" width="120"></el-table-column>
        <el-table-column prop="name" label="姓名" width="100"></el-table-column>
        <el-table-column prop="major" label="专业"></el-table-column>
        <el-table-column prop="className" label="班级" width="120"></el-table-column>
        <el-table-column label="操作" width="100">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click.stop="exportStudentData(scope.row)">
              <i class="el-icon-download"></i> 导出
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        style="margin-top: 15px; text-align: right;"
        small
        background
        layout="total, prev, pager, next"
        :total="studentTotal"
        :page-size="10"
        :current-page="studentPageNum"
        @current-change="handleStudentPageChange"
      ></el-pagination>
    </el-dialog>
  </div>
</template>

<script>
import { getStudentList, exportStudentData, exportAllHealthData, exportStudentHealthData } from '@/api/admin'

export default {
  name: 'DataExport',
  data() {
    return {
      exporting: {
        students: false,
        allHealth: false
      },
      studentSelectVisible: false,
      loadingStudents: false,
      studentList: [],
      studentTotal: 0,
      studentPageNum: 1,
      studentSearchKeyword: ''
    }
  },
  computed: {
    filteredStudents() {
      if (!this.studentSearchKeyword) {
        return this.studentList
      }
      const keyword = this.studentSearchKeyword.toLowerCase()
      return this.studentList.filter(s => 
        s.studentNo.toLowerCase().includes(keyword) || 
        s.name.toLowerCase().includes(keyword)
      )
    }
  },
  methods: {
    async exportStudents() {
      this.exporting.students = true
      try {
        const res = await exportStudentData('all')
        this.downloadFile(res, '学生基本信息.xlsx')
        this.$message.success('导出成功')
      } catch (error) {
        this.$message.error('导出失败')
      } finally {
        this.exporting.students = false
      }
    },
    
    async exportAllHealth() {
      this.exporting.allHealth = true
      try {
        const res = await exportAllHealthData()
        this.downloadFile(res, '全部学生健康数据统计.xlsx')
        this.$message.success('导出成功')
      } catch (error) {
        this.$message.error('导出失败')
      } finally {
        this.exporting.allHealth = false
      }
    },
    
    showStudentSelect() {
      this.studentSelectVisible = true
      this.loadStudents()
    },
    
    async loadStudents() {
      this.loadingStudents = true
      try {
        const res = await getStudentList({
          pageNum: this.studentPageNum,
          pageSize: 10,
          keyword: this.studentSearchKeyword
        })
        this.studentList = res.data.list || []
        this.studentTotal = res.data.total || 0
      } catch (error) {
        this.$message.error('加载学生列表失败')
      } finally {
        this.loadingStudents = false
      }
    },
    
    searchStudents() {
      this.studentPageNum = 1
      this.loadStudents()
    },
    
    handleStudentPageChange(page) {
      this.studentPageNum = page
      this.loadStudents()
    },
    
    selectStudent(row) {
      this.exportStudentData(row)
    },
    
    async exportStudentData(student) {
      try {
        const res = await exportStudentHealthData(student.id)
        this.downloadFile(res, `${student.name}_健康数据.xlsx`)
        this.$message.success('导出成功')
        this.studentSelectVisible = false
      } catch (error) {
        this.$message.error('导出失败')
      }
    },
    
    downloadFile(data, fileName) {
      const blob = new Blob([data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = fileName
      link.click()
      window.URL.revokeObjectURL(url)
    }
  }
}
</script>

<style scoped>
.data-export {
  padding: 10px;
}

.export-card {
  text-align: center;
  padding: 20px 0;
}

.export-card >>> .el-card__body {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.export-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 15px;
}

.export-icon i {
  font-size: 36px;
  color: white;
}

.export-card h3 {
  margin: 10px 0;
  color: #303133;
}

.export-desc {
  color: #909399;
  font-size: 14px;
  margin-bottom: 20px;
  line-height: 1.6;
  min-height: 60px;
}
</style>
