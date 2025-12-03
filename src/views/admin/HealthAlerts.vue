<template>
  <div class="health-alerts">
    <!-- 预警汇总 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="alert-summary-card" shadow="hover">
          <div class="alert-icon" style="background: #f56c6c;">
            <i class="el-icon-warning"></i>
          </div>
          <div class="alert-info">
            <div class="alert-value">{{ summary.totalAlertCount || 0 }}</div>
            <div class="alert-label">预警总数</div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="alert-summary-card" shadow="hover">
          <div class="alert-icon" style="background: #909399;">
            <i class="el-icon-moon-night"></i>
          </div>
          <div class="alert-info">
            <div class="alert-value">{{ summary.sleepAlertCount || 0 }}</div>
            <div class="alert-label">睡眠不足</div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="alert-summary-card" shadow="hover">
          <div class="alert-icon" style="background: #e6a23c;">
            <i class="el-icon-cloudy"></i>
          </div>
          <div class="alert-info">
            <div class="alert-value">{{ summary.moodAlertCount || 0 }}</div>
            <div class="alert-label">情绪异常</div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="alert-summary-card" shadow="hover">
          <div class="alert-icon" style="background: #409eff;">
            <i class="el-icon-trophy-1"></i>
          </div>
          <div class="alert-info">
            <div class="alert-value">{{ summary.exerciseAlertCount || 0 }}</div>
            <div class="alert-label">运动不足</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 预警详情 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 睡眠不足预警 -->
      <el-col :span="8">
        <el-card>
          <div slot="header" class="card-header">
            <span><i class="el-icon-moon-night"></i> 睡眠不足预警</span>
            <el-tag type="info" size="mini">近7天平均睡眠&lt;6小时</el-tag>
          </div>
          <div v-if="sleepAlerts.length > 0" class="alert-list">
            <div v-for="alert in sleepAlerts" :key="alert.studentId" class="alert-item">
              <div class="alert-item-info">
                <span class="alert-student-name">{{ alert.studentName }}</span>
                <span class="alert-student-no">{{ alert.studentNo }}</span>
              </div>
              <div class="alert-item-detail">
                平均睡眠 <strong style="color: #f56c6c;">{{ alert.avgSleepDuration }}</strong> 小时
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无睡眠预警" :image-size="80"></el-empty>
        </el-card>
      </el-col>
      
      <!-- 情绪异常预警 -->
      <el-col :span="8">
        <el-card>
          <div slot="header" class="card-header">
            <span><i class="el-icon-cloudy"></i> 情绪异常预警</span>
            <el-tag type="warning" size="mini">负面情绪&gt;50%</el-tag>
          </div>
          <div v-if="moodAlerts.length > 0" class="alert-list">
            <div v-for="alert in moodAlerts" :key="alert.studentId" class="alert-item">
              <div class="alert-item-info">
                <span class="alert-student-name">{{ alert.studentName }}</span>
                <span class="alert-student-no">{{ alert.studentNo }}</span>
              </div>
              <div class="alert-item-detail">
                负面情绪占比 <strong style="color: #e6a23c;">{{ alert.negativeRatio }}</strong>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无情绪预警" :image-size="80"></el-empty>
        </el-card>
      </el-col>
      
      <!-- 运动不足预警 -->
      <el-col :span="8">
        <el-card>
          <div slot="header" class="card-header">
            <span><i class="el-icon-trophy"></i> 运动不足预警</span>
            <el-tag type="primary" size="mini">近7天运动&lt;3次</el-tag>
          </div>
          <div v-if="exerciseAlerts.length > 0" class="alert-list">
            <div v-for="alert in exerciseAlerts" :key="alert.studentId" class="alert-item">
              <div class="alert-item-info">
                <span class="alert-student-name">{{ alert.studentName }}</span>
                <span class="alert-student-no">{{ alert.studentNo }}</span>
              </div>
              <div class="alert-item-detail">
                运动次数 <strong style="color: #409eff;">{{ alert.exerciseCount }}</strong> 次
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无运动预警" :image-size="80"></el-empty>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getHealthAlertStatistics } from '@/api/admin'

export default {
  name: 'HealthAlerts',
  data() {
    return {
      summary: {},
      sleepAlerts: [],
      moodAlerts: [],
      exerciseAlerts: []
    }
  },
  created() {
    this.loadAlerts()
  },
  methods: {
    async loadAlerts() {
      try {
        const res = await getHealthAlertStatistics()
        this.summary = res.data.summary || {}
        this.sleepAlerts = res.data.sleepAlerts || []
        this.moodAlerts = res.data.moodAlerts || []
        this.exerciseAlerts = res.data.exerciseAlerts || []
      } catch (error) {
        this.$message.error('加载预警数据失败')
      }
    }
  }
}
</script>

<style scoped>
.health-alerts {
  padding: 10px;
}

.alert-summary-card >>> .el-card__body {
  display: flex;
  align-items: center;
  padding: 20px;
}

.alert-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.alert-icon i {
  font-size: 28px;
  color: white;
}

.alert-info {
  flex: 1;
}

.alert-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.alert-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header i {
  margin-right: 5px;
}

.alert-list {
  max-height: 400px;
  overflow-y: auto;
}

.alert-item {
  padding: 12px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.alert-item:last-child {
  border-bottom: none;
}

.alert-item:hover {
  background: #f5f7fa;
}

.alert-item-info {
  display: flex;
  flex-direction: column;
}

.alert-student-name {
  font-weight: 500;
  color: #303133;
}

.alert-student-no {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.alert-item-detail {
  font-size: 13px;
  color: #606266;
}
</style>
