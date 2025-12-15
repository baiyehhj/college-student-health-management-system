<template>
  <div class="ai-report">
    <el-card>
      <div slot="header" class="clearfix">
        <span>AI健康报告</span>
        <el-button
          style="float: right"
          type="primary"
          icon="el-icon-magic-stick"
          @click="showGenerateDialog"
        >
          生成新报告
        </el-button>
      </div>
      
      <!-- 最新报告卡片 -->
      <el-card v-if="latestReport" class="latest-report" shadow="hover">
        <div slot="header">
          <i class="el-icon-document"></i>
          <span style="margin-left: 10px">最新健康报告</span>
          <el-tag
            style="float: right"
            :type="getScoreType(latestReport.overallScore)"
          >
            综合评分: {{ latestReport.overallScore }}
          </el-tag>
        </div>
        
        <el-row :gutter="20">
          <el-col :span="24">
            <div class="report-info">
              <p><strong>分析周期:</strong> {{ latestReport.analysisPeriod }}</p>
              <p><strong>生成日期:</strong> {{ latestReport.reportDate }}</p>
            </div>
          </el-col>
        </el-row>
        
        <el-divider></el-divider>
        
<template>
  <div class="health-report-container">
    <!-- 饮食分析模块 -->
    <div class="report-section">
      <h3 class="section-title">饮食分析</h3>
      <div class="analysis-content">{{ latestReport.dietAnalysis }}</div>
    </div>

    <!-- 运动分析模块 -->
    <div class="report-section">
      <h3 class="section-title">运动分析</h3>
      <div class="analysis-content">{{ latestReport.exerciseAnalysis }}</div>
    </div>

    <!-- 睡眠分析模块 -->
    <div class="report-section">
      <h3 class="section-title">睡眠分析</h3>
      <div class="analysis-content">{{ latestReport.sleepAnalysis }}</div>
    </div>

    <!-- 情绪分析模块 -->
    <div class="report-section">
      <h3 class="section-title">情绪分析</h3>
      <div class="analysis-content">{{ latestReport.moodAnalysis }}</div>
    </div>

    <!-- 健康风险提示模块（保留alert样式） -->
    <div class="report-section">
      <h3 class="section-title">健康风险提示</h3>
      <el-alert
        :title="latestReport.healthRisks"
        type="warning"
        :closable="false"
        show-icon
        class="risk-alert"
      ></el-alert>
    </div>

    <!-- 改善建议模块 -->
    <div class="report-section">
      <h3 class="section-title">改善建议</h3>
      <div class="analysis-content recommendations">
        {{ latestReport.recommendations }}
      </div>
    </div>
  </div>
</template>
      </el-card>
      
      <!-- 历史报告列表 -->
      <el-divider></el-divider>
      
      <h3>历史报告</h3>
      <el-table
        :data="reportList"
        border
        style="width: 100%; margin-top: 20px"
        v-loading="loading"
      >
        <el-table-column prop="reportDate" label="生成日期" width="120"></el-table-column>
        <el-table-column prop="analysisPeriod" label="分析周期" width="250"></el-table-column>
        <el-table-column prop="overallScore" label="综合评分" width="120">
          <template slot-scope="scope">
            <el-tag :type="getScoreType(scope.row.overallScore)">
              {{ scope.row.overallScore }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="aiModelVersion" label="AI版本" width="120"></el-table-column>
        <el-table-column label="操作" width="150">
          <template slot-scope="scope">
            <el-button
              type="text"
              icon="el-icon-view"
              @click="viewReport(scope.row)"
            >
              查看详情
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
    
    <!-- 生成报告对话框 -->
    <el-dialog
      title="生成AI健康报告"
      :visible.sync="generateDialogVisible"
      width="500px"
    >
      <el-form :model="generateForm" label-width="100px">
        <el-form-item label="分析周期">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
            style="width: 100%"
          ></el-date-picker>
        </el-form-item>
        
        <el-alert
          title="提示"
          type="info"
          :closable="false"
          description="AI将分析您在所选周期内的健康数据，生成个性化的健康报告和建议。报告生成可能需要10-30秒，请耐心等待。"
          show-icon
        ></el-alert>
      </el-form>
      
      <div slot="footer">
        <el-button @click="generateDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          @click="handleGenerate"
          :loading="generating"
        >
          {{ generating ? '生成中...' : '开始生成' }}
        </el-button>
      </div>
    </el-dialog>
    
    <!-- 报告详情对话框 -->
    <el-dialog
      title="报告详情"
      :visible.sync="detailDialogVisible"
      width="800px"
    >
      <div v-if="currentReport">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="生成日期">
            {{ currentReport.reportDate }}
          </el-descriptions-item>
          <el-descriptions-item label="分析周期">
            {{ currentReport.analysisPeriod }}
          </el-descriptions-item>
          <el-descriptions-item label="综合评分">
            <el-tag :type="getScoreType(currentReport.overallScore)">
              {{ currentReport.overallScore }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="AI版本">
            {{ currentReport.aiModelVersion }}
          </el-descriptions-item>
        </el-descriptions>
        
        <el-divider></el-divider>
        
        <div class="detail-section">
          <h4><i class="el-icon-apple"></i> 饮食分析</h4>
          <p>{{ currentReport.dietAnalysis }}</p>
        </div>
        
        <div class="detail-section">
          <h4><i class="el-icon-trophy"></i> 运动分析</h4>
          <p>{{ currentReport.exerciseAnalysis }}</p>
        </div>
        
        <div class="detail-section">
          <h4><i class="el-icon-moon-night"></i> 睡眠分析</h4>
          <p>{{ currentReport.sleepAnalysis }}</p>
        </div>
        
        <div class="detail-section">
          <h4><i class="el-icon-sunny"></i> 情绪分析</h4>
          <p>{{ currentReport.moodAnalysis }}</p>
        </div>
        
        <div class="detail-section">
          <h4><i class="el-icon-warning"></i> 健康风险</h4>
          <el-alert
            :title="currentReport.healthRisks"
            type="warning"
            :closable="false"
          ></el-alert>
        </div>
        
        <div class="detail-section">
          <h4><i class="el-icon-guide"></i> 改善建议</h4>
          <p class="recommendations">{{ currentReport.recommendations }}</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  generateAIReport,
  listAIReports,
  getLatestReport,
  getReportDetail
} from '@/api/aiReport'

export default {
  name: 'AIReport',
  data() {
    return {
      loading: false,
      latestReport: null,
      reportList: [],
      
      // 分页
      pagination: {
        page: 1,
        size: 10,
        total: 0
      },
      
      // 生成对话框
      generateDialogVisible: false,
      generating: false,
      dateRange: [],
      generateForm: {},
      
      // 详情对话框
      detailDialogVisible: false,
      currentReport: null
    }
  },
  created() {
    this.loadLatestReport()
    this.loadReportList()
  },
  methods: {
    // 加载最新报告
    async loadLatestReport() {
      try {
        const response = await getLatestReport()
        this.latestReport = response.data
      } catch (error) {
        console.error('加载最新报告失败', error)
      }
    },
    
    // 加载报告列表
    async loadReportList() {
      this.loading = true
      try {
        const response = await listAIReports({
          page: this.pagination.page,
          size: this.pagination.size
        })
        this.reportList = response.data.records
        this.pagination.total = response.data.total
      } catch (error) {
        this.$message.error('加载报告列表失败')
      } finally {
        this.loading = false
      }
    },
    
    // 显示生成对话框
    showGenerateDialog() {
      // 默认分析最近7天
      const end = new Date()
      const start = new Date()
      start.setDate(start.getDate() - 7)
      
      this.dateRange = [
        this.formatDate(start),
        this.formatDate(end)
      ]
      
      this.generateDialogVisible = true
    },
    
    // 生成报告
    async handleGenerate() {
      if (!this.dateRange || this.dateRange.length !== 2) {
        this.$message.warning('请选择分析周期')
        return
      }
      
      this.generating = true
      try {
        await generateAIReport({
          startDate: this.dateRange[0],
          endDate: this.dateRange[1]
        })
        
        this.$message.success('报告生成成功')
        this.generateDialogVisible = false
        
        // 重新加载数据
        this.loadLatestReport()
        this.loadReportList()
      } catch (error) {
        this.$message.error('报告生成失败: ' + (error.message || '未知错误'))
      } finally {
        this.generating = false
      }
    },
    
    // 查看报告详情
    async viewReport(report) {
      try {
        const response = await getReportDetail(report.id)
        this.currentReport = response.data
        this.detailDialogVisible = true
      } catch (error) {
        this.$message.error('加载报告详情失败')
      }
    },
    
    // 分页
    handleSizeChange(size) {
      this.pagination.size = size
      this.loadReportList()
    },
    
    handleCurrentChange(page) {
      this.pagination.page = page
      this.loadReportList()
    },
    
    // 根据评分获取标签类型
    getScoreType(score) {
      if (score >= 90) return 'success'
      if (score >= 80) return 'primary'
      if (score >= 70) return 'warning'
      return 'danger'
    },
    
    // 格式化日期
    formatDate(date) {
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    }
  }
}
</script>

<style scoped>
.ai-report {
  padding: 20px;
}

.latest-report {
  margin-bottom: 30px;
}

.report-info p {
  margin: 10px 0;
  font-size: 14px;
}

.analysis-content {
  padding: 15px;
  line-height: 1.8;
  white-space: pre-wrap;
  background: #f5f7fa;
  border-radius: 4px;
}

.recommendations {
  color: #409eff;
  font-weight: 500;
}

.detail-section {
  margin: 20px 0;
}

.detail-section h4 {
  color: #409eff;
  margin-bottom: 10px;
}

.detail-section p {
  line-height: 1.8;
  white-space: pre-wrap;
}

/* 整体容器样式 */
.health-report-container {
  width: 100%;
  max-width: 1200px;
  margin: 20px auto;
  padding: 0 20px;
  box-sizing: border-box;
}

/* 每个模块的容器 */
.report-section {
  margin-bottom: 24px;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.04);
}

/* 模块标题样式 */
.section-title {
  margin: 0 0 12px 0;
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  border-left: 4px solid #409eff;
  padding-left: 8px;
}

/* 分析内容样式 */
.analysis-content {
  font-size: 14px;
  line-height: 1.6;
  color: #4b5563;
  padding: 8px 0;
}

/* 改善建议特殊样式（可选） */
.recommendations {
  background: #f9fafb;
  padding: 12px;
  border-radius: 4px;
  border-left: 4px solid #67c23a;
}

/* 风险提示alert样式调整 */
.risk-alert {
  margin: 0;
  --el-alert-padding: 12px 16px;
}
</style>
