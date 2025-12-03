<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <i class="el-icon-food stat-icon" style="color: #67C23A"></i>
            <div class="stat-content">
              <div class="stat-value">{{ dietCount }}</div>
              <div class="stat-label">饮食记录</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <i class="el-icon-trophy stat-icon" style="color: #409EFF"></i>
            <div class="stat-content">
              <div class="stat-value">{{ exerciseCount }}</div>
              <div class="stat-label">运动记录</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <i class="el-icon-moon-night stat-icon" style="color: #E6A23C"></i>
            <div class="stat-content">
              <div class="stat-value">{{ sleepCount }}</div>
              <div class="stat-label">睡眠记录</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <i class="el-icon-sunny stat-icon" style="color: #F56C6C"></i>
            <div class="stat-content">
              <div class="stat-value">{{ moodCount }}</div>
              <div class="stat-label">情绪记录</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <div slot="header">
            <span>最近7天健康数据</span>
          </div>
          <div style="height: 300px; display: flex; align-items: center; justify-content: center; color: #909399">
            图表功能开发中...
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card>
          <div slot="header">
            <span>健康建议</span>
          </div>
          <div class="suggestions">
            <el-alert
              title="温馨提示"
              type="info"
              description="建议每天保持7-8小时睡眠，规律饮食，适量运动。"
              :closable="false"
              show-icon
            ></el-alert>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: 'DashboardPage',
  data() {
    return {
      dietCount: 0,
      exerciseCount: 0,
      sleepCount: 0,
      moodCount: 0
    }
  },
  created() {
    this.loadStatistics()
  },
  methods: {
    loadStatistics() {
      // 加载统计数据
      this.dietCount = 12
      this.exerciseCount = 8
      this.sleepCount = 7
      this.moodCount = 5
    }
  }
}
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
}

.stat-icon {
  font-size: 48px;
  margin-right: 20px;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.suggestions {
  padding: 20px 0;
}
</style>
