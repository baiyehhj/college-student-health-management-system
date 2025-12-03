<template>
  <div class="admin-dashboard">
    <!-- 顶部统计卡片 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
            <i class="el-icon-user"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.totalStudents || 0 }}</div>
            <div class="stat-label">学生总数</div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon" style="background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);">
            <i class="el-icon-check"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.activeStudents || 0 }}</div>
            <div class="stat-label">活跃学生</div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
            <i class="el-icon-plus"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.todayNewStudents || 0 }}</div>
            <div class="stat-label">今日新增</div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);">
            <i class="el-icon-date"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.monthNewStudents || 0 }}</div>
            <div class="stat-label">本月新增</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 今日数据记录统计 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="6">
        <el-card class="record-card">
          <div class="record-header">
            <i class="el-icon-food" style="color: #67c23a;"></i>
            <span>今日饮食记录</span>
          </div>
          <div class="record-value">{{ statistics.todayDietRecords || 0 }}</div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="record-card">
          <div class="record-header">
            <i class="el-icon-trophy" style="color: #409eff;"></i>
            <span>今日运动记录</span>
          </div>
          <div class="record-value">{{ statistics.todayExerciseRecords || 0 }}</div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="record-card">
          <div class="record-header">
            <i class="el-icon-moon-night" style="color: #909399;"></i>
            <span>今日睡眠记录</span>
          </div>
          <div class="record-value">{{ statistics.todaySleepRecords || 0 }}</div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="record-card">
          <div class="record-header">
            <i class="el-icon-sunny" style="color: #e6a23c;"></i>
            <span>今日情绪记录</span>
          </div>
          <div class="record-value">{{ statistics.todayMoodRecords || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 图表区域 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="16">
        <el-card>
          <div slot="header">
            <span>近30天数据记录趋势</span>
          </div>
          <div ref="trendChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card>
          <div slot="header">
            <span>情绪分布统计</span>
          </div>
          <div ref="moodChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getSystemStatistics, getHealthTrendStatistics } from '@/api/admin'
import * as echarts from 'echarts'

export default {
  name: 'AdminDashboard',
  data() {
    return {
      statistics: {},
      trendData: [],
      moodDistribution: [],
      trendChart: null,
      moodChart: null
    }
  },
  mounted() {
    this.loadStatistics()
    this.loadTrendStatistics()
  },
  beforeDestroy() {
    if (this.trendChart) {
      this.trendChart.dispose()
    }
    if (this.moodChart) {
      this.moodChart.dispose()
    }
  },
  methods: {
    async loadStatistics() {
      try {
        const res = await getSystemStatistics()
        this.statistics = res.data || {}
      } catch (error) {
        console.error('加载统计数据失败', error)
      }
    },
    
    async loadTrendStatistics() {
      try {
        const res = await getHealthTrendStatistics()
        this.trendData = res.data.dailyTrends || []
        this.moodDistribution = res.data.moodDistribution || []
        
        this.$nextTick(() => {
          this.initTrendChart()
          this.initMoodChart()
        })
      } catch (error) {
        console.error('加载趋势数据失败', error)
      }
    },
    
    initTrendChart() {
      if (this.trendChart) {
        this.trendChart.dispose()
      }
      
      this.trendChart = echarts.init(this.$refs.trendChart)
      
      const dates = this.trendData.map(item => item.date.substring(5))
      const dietData = this.trendData.map(item => item.dietCount)
      const exerciseData = this.trendData.map(item => item.exerciseCount)
      const sleepData = this.trendData.map(item => item.sleepCount)
      const moodData = this.trendData.map(item => item.moodCount)
      
      const option = {
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['饮食记录', '运动记录', '睡眠记录', '情绪记录']
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
          data: dates
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '饮食记录',
            type: 'line',
            smooth: true,
            data: dietData,
            itemStyle: { color: '#67c23a' }
          },
          {
            name: '运动记录',
            type: 'line',
            smooth: true,
            data: exerciseData,
            itemStyle: { color: '#409eff' }
          },
          {
            name: '睡眠记录',
            type: 'line',
            smooth: true,
            data: sleepData,
            itemStyle: { color: '#909399' }
          },
          {
            name: '情绪记录',
            type: 'line',
            smooth: true,
            data: moodData,
            itemStyle: { color: '#e6a23c' }
          }
        ]
      }
      
      this.trendChart.setOption(option)
    },
    
    initMoodChart() {
      if (this.moodChart) {
        this.moodChart.dispose()
      }
      
      this.moodChart = echarts.init(this.$refs.moodChart)
      
      const data = this.moodDistribution.map(item => ({
        name: item.name,
        value: item.count
      }))
      
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '18',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: data,
            color: ['#67c23a', '#409eff', '#e6a23c', '#909399', '#f56c6c', '#9b59b6']
          }
        ]
      }
      
      this.moodChart.setOption(option)
    }
  }
}
</script>

<style scoped>
.admin-dashboard {
  padding: 10px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 15px;
}

.stat-card >>> .el-card__body {
  display: flex;
  align-items: center;
  width: 100%;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.stat-icon i {
  font-size: 28px;
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.record-card {
  text-align: center;
}

.record-header {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 15px;
}

.record-header i {
  font-size: 20px;
  margin-right: 8px;
}

.record-header span {
  font-size: 14px;
  color: #606266;
}

.record-value {
  font-size: 36px;
  font-weight: bold;
  color: #303133;
}
</style>
