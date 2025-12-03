<template>
  <div class="data-analysis">
    <!-- 时间范围选择和导出按钮 -->
    <el-card class="time-selector">
      <el-row type="flex" justify="space-between" align="middle">
        <el-col :span="16">
          <el-radio-group v-model="selectedDays" @change="handleDaysChange">
            <el-radio-button :label="7">最近7天</el-radio-button>
            <el-radio-button :label="30">最近30天</el-radio-button>
            <el-radio-button :label="90">最近90天</el-radio-button>
            <el-radio-button :label="365">最近1年</el-radio-button>
          </el-radio-group>
        </el-col>
        <el-col :span="8" style="text-align: right">
          <el-button 
            type="primary" 
            icon="el-icon-download" 
            @click="showExportDialog"
          >
            导出数据
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 图表网格 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <!-- 体重趋势 -->
      <el-col :span="12">
        <el-card class="chart-card">
          <div slot="header">
            <i class="el-icon-data-line"></i>
            <span>体重/BMI趋势</span>
          </div>
          <div v-loading="loading.weight">
            <div v-if="weightData.dates && weightData.dates.length > 0">
              <div ref="weightChart" style="width: 100%; height: 350px"></div>
              <el-descriptions :column="3" size="small" style="margin-top: 10px">
                <el-descriptions-item label="平均体重">{{ weightData.avgWeight || 0 }}kg</el-descriptions-item>
                <el-descriptions-item label="平均BMI">{{ weightData.avgBmi || 0 }}</el-descriptions-item>
                <el-descriptions-item label="趋势">
                  <el-tag :type="getTrendType(weightData.trend)" size="small">
                    {{ weightData.trend || '无' }}
                  </el-tag>
                </el-descriptions-item>
              </el-descriptions>
            </div>
            <el-empty v-else description="暂无数据" :image-size="100"></el-empty>
          </div>
        </el-card>
      </el-col>

      <!-- 运动统计 -->
      <el-col :span="12">
        <el-card class="chart-card">
          <div slot="header">
            <i class="el-icon-sunrise"></i>
            <span>运动统计</span>
          </div>
          <div v-loading="loading.exercise">
            <div v-if="exerciseData.dates && exerciseData.dates.length > 0">
              <div ref="exerciseChart" style="width: 100%; height: 350px"></div>
              <el-descriptions :column="3" size="small" style="margin-top: 10px">
                <el-descriptions-item label="总时长">{{ exerciseData.totalDuration || 0 }}分钟</el-descriptions-item>
                <el-descriptions-item label="总热量">{{ exerciseData.totalCalories || 0 }}千卡</el-descriptions-item>
                <el-descriptions-item label="平均时长">{{ exerciseData.avgDuration || 0 }}分钟/天</el-descriptions-item>
              </el-descriptions>
            </div>
            <el-empty v-else description="暂无数据" :image-size="100"></el-empty>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <!-- 睡眠趋势 -->
      <el-col :span="12">
        <el-card class="chart-card">
          <div slot="header">
            <i class="el-icon-moon-night"></i>
            <span>睡眠趋势</span>
          </div>
          <div v-loading="loading.sleep">
            <div v-if="sleepData.dates && sleepData.dates.length > 0">
              <div ref="sleepChart" style="width: 100%; height: 350px"></div>
              <el-descriptions :column="3" size="small" style="margin-top: 10px">
                <el-descriptions-item label="平均时长">{{ sleepData.avgDuration || 0 }}小时</el-descriptions-item>
                <el-descriptions-item label="平均质量">{{ sleepData.avgQuality || 0 }}分</el-descriptions-item>
                <el-descriptions-item label="优质睡眠">{{ sleepData.goodSleepDays || 0 }}天</el-descriptions-item>
              </el-descriptions>
            </div>
            <el-empty v-else description="暂无数据" :image-size="100"></el-empty>
          </div>
        </el-card>
      </el-col>

      <!-- 情绪分布 -->
      <el-col :span="12">
        <el-card class="chart-card">
          <div slot="header">
            <i class="el-icon-sunny"></i>
            <span>情绪分布</span>
          </div>
          <div v-loading="loading.mood">
            <div v-if="moodData.distribution && Object.keys(moodData.distribution).length > 0">
              <div ref="moodChart" style="width: 100%; height: 350px"></div>
              <el-descriptions :column="3" size="small" style="margin-top: 10px">
                <el-descriptions-item label="总记录">{{ moodData.totalRecords || 0 }}次</el-descriptions-item>
                <el-descriptions-item label="最常见">{{ moodData.mostFrequent || '无' }}</el-descriptions-item>
                <el-descriptions-item label="积极占比">{{ moodData.positiveRate || 0 }}%</el-descriptions-item>
              </el-descriptions>
            </div>
            <el-empty v-else description="暂无数据" :image-size="100"></el-empty>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <!-- 营养统计 -->
      <el-col :span="24">
        <el-card class="chart-card">
          <div slot="header">
            <i class="el-icon-food"></i>
            <span>营养摄入统计</span>
          </div>
          <div v-loading="loading.nutrition">
            <div v-if="nutritionData.dates && nutritionData.dates.length > 0">
              <div ref="nutritionChart" style="width: 100%; height: 400px"></div>
              <el-descriptions :column="4" size="small" style="margin-top: 10px">
                <el-descriptions-item label="平均热量">{{ nutritionData.avgCalories || 0 }}千卡</el-descriptions-item>
                <el-descriptions-item label="平均蛋白质">{{ nutritionData.avgProtein || 0 }}克</el-descriptions-item>
                <el-descriptions-item label="平均碳水">{{ nutritionData.avgCarbs || 0 }}克</el-descriptions-item>
                <el-descriptions-item label="平均脂肪">{{ nutritionData.avgFat || 0 }}克</el-descriptions-item>
              </el-descriptions>
            </div>
            <el-empty v-else description="暂无数据" :image-size="100"></el-empty>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 导出数据对话框 -->
    <el-dialog
      title="导出行为数据"
      :visible.sync="exportDialogVisible"
      width="500px"
    >
      <el-form :model="exportForm" label-width="100px">
        <el-form-item label="开始日期">
          <el-date-picker
            v-model="exportForm.startDate"
            type="date"
            placeholder="选择开始日期"
            value-format="yyyy-MM-dd"
            style="width: 100%"
          ></el-date-picker>
        </el-form-item>
        
        <el-form-item label="结束日期">
          <el-date-picker
            v-model="exportForm.endDate"
            type="date"
            placeholder="选择结束日期"
            value-format="yyyy-MM-dd"
            style="width: 100%"
          ></el-date-picker>
        </el-form-item>
        
        <el-alert
          title="导出说明"
          type="info"
          :closable="false"
          show-icon
        >
          <template>
            <p style="margin: 0">导出文件将包含选定时间范围内的:</p>
            <ul style="margin: 5px 0 0 20px; padding: 0">
              <li>饮食记录</li>
              <li>运动记录</li>
              <li>睡眠记录</li>
              <li>情绪记录</li>
            </ul>
          </template>
        </el-alert>
      </el-form>
      
      <div slot="footer">
        <el-button @click="exportDialogVisible = false">取消</el-button>
        <el-button 
          type="primary" 
          :loading="exporting" 
          @click="handleExport"
        >
          确定导出
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import {
  getWeightTrend,
  getExerciseStats,
  getSleepTrend,
  getMoodDistribution,
  getNutritionStats
} from '@/api/analysis'
import { exportBehaviorData } from '@/api/export'

export default {
  name: 'DataAnalysis',
  data() {
    return {
      selectedDays: 30,
      loading: {
        weight: false,
        exercise: false,
        sleep: false,
        mood: false,
        nutrition: false
      },
      weightData: {},
      exerciseData: {},
      sleepData: {},
      moodData: {},
      nutritionData: {},
      charts: {
        weight: null,
        exercise: null,
        sleep: null,
        mood: null,
        nutrition: null
      },
      // 导出相关
      exportDialogVisible: false,
      exporting: false,
      exportForm: {
        startDate: '',
        endDate: ''
      }
    }
  },
  mounted() {
    this.fetchAllData()
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    this.disposeAllCharts()
    window.removeEventListener('resize', this.handleResize)
  },
  methods: {
    async fetchAllData() {
      await Promise.all([
        this.fetchWeightTrend(),
        this.fetchExerciseStats(),
        this.fetchSleepTrend(),
        this.fetchMoodDistribution(),
        this.fetchNutritionStats()
      ])
    },
    
    async fetchWeightTrend() {
      this.loading.weight = true
      try {
        const response = await getWeightTrend(this.selectedDays)
        this.weightData = response.data
        this.$nextTick(() => {
          this.renderWeightChart()
        })
      } catch (error) {
        this.$message.error('获取体重趋势失败')
      } finally {
        this.loading.weight = false
      }
    },
    
    async fetchExerciseStats() {
      this.loading.exercise = true
      try {
        const response = await getExerciseStats(this.selectedDays)
        this.exerciseData = response.data
        this.$nextTick(() => {
          this.renderExerciseChart()
        })
      } catch (error) {
        this.$message.error('获取运动统计失败')
      } finally {
        this.loading.exercise = false
      }
    },
    
    async fetchSleepTrend() {
      this.loading.sleep = true
      try {
        const response = await getSleepTrend(this.selectedDays)
        this.sleepData = response.data
        this.$nextTick(() => {
          this.renderSleepChart()
        })
      } catch (error) {
        this.$message.error('获取睡眠趋势失败')
      } finally {
        this.loading.sleep = false
      }
    },
    
    async fetchMoodDistribution() {
      this.loading.mood = true
      try {
        const response = await getMoodDistribution(this.selectedDays)
        this.moodData = response.data
        this.$nextTick(() => {
          this.renderMoodChart()
        })
      } catch (error) {
        this.$message.error('获取情绪分布失败')
      } finally {
        this.loading.mood = false
      }
    },
    
    async fetchNutritionStats() {
      this.loading.nutrition = true
      try {
        const response = await getNutritionStats(this.selectedDays)
        this.nutritionData = response.data
        this.$nextTick(() => {
          this.renderNutritionChart()
        })
      } catch (error) {
        this.$message.error('获取营养统计失败')
      } finally {
        this.loading.nutrition = false
      }
    },
    
    renderWeightChart() {
      if (!this.$refs.weightChart || !this.weightData.dates || this.weightData.dates.length === 0) return
      
      if (this.charts.weight) {
        this.charts.weight.dispose()
      }
      
      this.charts.weight = echarts.init(this.$refs.weightChart)
      const option = {
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['体重', 'BMI']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: this.weightData.dates
        },
        yAxis: [
          {
            type: 'value',
            name: '体重(kg)',
            position: 'left'
          },
          {
            type: 'value',
            name: 'BMI',
            position: 'right'
          }
        ],
        series: [
          {
            name: '体重',
            type: 'line',
            data: this.weightData.weights,
            smooth: true,
            itemStyle: { color: '#409EFF' }
          },
          {
            name: 'BMI',
            type: 'line',
            yAxisIndex: 1,
            data: this.weightData.bmis,
            smooth: true,
            itemStyle: { color: '#67C23A' }
          }
        ]
      }
      this.charts.weight.setOption(option)
    },
    
    renderExerciseChart() {
      if (!this.$refs.exerciseChart || !this.exerciseData.dates || this.exerciseData.dates.length === 0) return
      
      if (this.charts.exercise) {
        this.charts.exercise.dispose()
      }
      
      this.charts.exercise = echarts.init(this.$refs.exerciseChart)
      const option = {
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['运动时长', '热量消耗']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: this.exerciseData.dates
        },
        yAxis: [
          {
            type: 'value',
            name: '时长(分钟)',
            position: 'left'
          },
          {
            type: 'value',
            name: '热量(千卡)',
            position: 'right'
          }
        ],
        series: [
          {
            name: '运动时长',
            type: 'bar',
            data: this.exerciseData.durations,
            itemStyle: { color: '#E6A23C' }
          },
          {
            name: '热量消耗',
            type: 'line',
            yAxisIndex: 1,
            data: this.exerciseData.calories,
            smooth: true,
            itemStyle: { color: '#F56C6C' }
          }
        ]
      }
      this.charts.exercise.setOption(option)
    },
    
    renderSleepChart() {
      if (!this.$refs.sleepChart || !this.sleepData.dates || this.sleepData.dates.length === 0) return
      
      if (this.charts.sleep) {
        this.charts.sleep.dispose()
      }
      
      this.charts.sleep = echarts.init(this.$refs.sleepChart)
      const option = {
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['睡眠时长', '睡眠质量']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: this.sleepData.dates
        },
        yAxis: [
          {
            type: 'value',
            name: '时长(小时)',
            position: 'left'
          },
          {
            type: 'value',
            name: '质量(分)',
            position: 'right',
            min: 0,
            max: 5
          }
        ],
        series: [
          {
            name: '睡眠时长',
            type: 'line',
            data: this.sleepData.durations,
            smooth: true,
            areaStyle: {},
            itemStyle: { color: '#909399' }
          },
          {
            name: '睡眠质量',
            type: 'line',
            yAxisIndex: 1,
            data: this.sleepData.qualities,
            smooth: true,
            itemStyle: { color: '#67C23A' }
          }
        ]
      }
      this.charts.sleep.setOption(option)
    },
    
    renderMoodChart() {
      if (!this.$refs.moodChart || !this.moodData.distribution || Object.keys(this.moodData.distribution).length === 0) return
      
      if (this.charts.mood) {
        this.charts.mood.dispose()
      }
      
      this.charts.mood = echarts.init(this.$refs.moodChart)
      const data = Object.entries(this.moodData.distribution).map(([name, value]) => ({
        name,
        value
      }))
      
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c}次 ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            name: '情绪',
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: true,
              formatter: '{b}: {d}%'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: 16,
                fontWeight: 'bold'
              }
            },
            data: data
          }
        ]
      }
      this.charts.mood.setOption(option)
    },
    
    renderNutritionChart() {
      if (!this.$refs.nutritionChart || !this.nutritionData.dates || this.nutritionData.dates.length === 0) return
      
      if (this.charts.nutrition) {
        this.charts.nutrition.dispose()
      }
      
      this.charts.nutrition = echarts.init(this.$refs.nutritionChart)
      const option = {
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['热量', '蛋白质', '碳水', '脂肪']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: this.nutritionData.dates
        },
        yAxis: [
          {
            type: 'value',
            name: '热量(千卡)',
            position: 'left'
          },
          {
            type: 'value',
            name: '营养素(克)',
            position: 'right'
          }
        ],
        series: [
          {
            name: '热量',
            type: 'bar',
            data: this.nutritionData.calories,
            itemStyle: { color: '#E6A23C' }
          },
          {
            name: '蛋白质',
            type: 'line',
            yAxisIndex: 1,
            data: this.nutritionData.proteins,
            smooth: true,
            itemStyle: { color: '#409EFF' }
          },
          {
            name: '碳水',
            type: 'line',
            yAxisIndex: 1,
            data: this.nutritionData.carbs,
            smooth: true,
            itemStyle: { color: '#67C23A' }
          },
          {
            name: '脂肪',
            type: 'line',
            yAxisIndex: 1,
            data: this.nutritionData.fats,
            smooth: true,
            itemStyle: { color: '#F56C6C' }
          }
        ]
      }
      this.charts.nutrition.setOption(option)
    },
    
    handleDaysChange() {
      this.fetchAllData()
    },
    
    handleResize() {
      Object.values(this.charts).forEach(chart => {
        if (chart) {
          chart.resize()
        }
      })
    },
    
    disposeAllCharts() {
      Object.values(this.charts).forEach(chart => {
        if (chart) {
          chart.dispose()
        }
      })
    },
    
    getTrendType(trend) {
      if (trend === '上升') return 'danger'
      if (trend === '下降') return 'success'
      return 'info'
    },
    
    // 显示导出对话框
    showExportDialog() {
      // 初始化默认日期范围为最近30天
      const end = new Date()
      const start = new Date()
      start.setDate(start.getDate() - 30)
      
      this.exportForm.startDate = this.formatDate(start)
      this.exportForm.endDate = this.formatDate(end)
      this.exportDialogVisible = true
    },
    
    // 格式化日期
    formatDate(date) {
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    
    // 处理导出
    async handleExport() {
      if (!this.exportForm.startDate || !this.exportForm.endDate) {
        this.$message.warning('请选择开始和结束日期')
        return
      }
      
      if (new Date(this.exportForm.startDate) > new Date(this.exportForm.endDate)) {
        this.$message.warning('开始日期不能大于结束日期')
        return
      }
      
      this.exporting = true
      try {
        const response = await exportBehaviorData(
          this.exportForm.startDate,
          this.exportForm.endDate
        )
        
        // 创建下载链接
        const blob = new Blob([response], { 
          type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' 
        })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = `行为数据_${this.exportForm.startDate}_${this.exportForm.endDate}.xlsx`
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
        
        this.$message.success('导出成功')
        this.exportDialogVisible = false
      } catch (error) {
        console.error('导出失败:', error)
        this.$message.error('导出失败,请稍后重试')
      } finally {
        this.exporting = false
      }
    }
  }
}
</script>

<style scoped>
.data-analysis {
  padding: 20px;
}

.time-selector {
  text-align: center;
}

.chart-card {
  margin-bottom: 20px;
}

.chart-card >>> .el-card__header {
  background-color: #f5f7fa;
  padding: 15px 20px;
}

.chart-card >>> .el-card__header i {
  margin-right: 5px;
  font-size: 18px;
}

.chart-card >>> .el-card__header span {
  font-size: 16px;
  font-weight: bold;
}
</style>
