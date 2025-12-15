<template>
  <div class="notification">
    <el-card>
      <div slot="header">
        <span>系统通知</span>
        <el-button
          style="float: right"
          type="text"
          @click="handleMarkAllRead"
        >
          全部已读
        </el-button>
      </div>
      
      <el-tabs v-model="activeTab">
        <el-tab-pane label="未读消息" name="unread">
          <el-empty description="暂无未读消息"></el-empty>
        </el-tab-pane>
        
        <el-tab-pane label="已读消息" name="read">
          <el-timeline>
            <el-timeline-item
              timestamp="2024-01-15 10:30"
              placement="top"
            >
              <el-card>
                <h4>欢迎使用健康管理系统</h4>
                <p>祝您使用愉快！</p>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'NotificationPage',
  data() {
    return {
      activeTab: 'unread'
    }
  },
  methods: {
    handleMarkAllRead() {
      this.$message.success('已全部标记为已读')
    }
  }
}
</script>

<style scoped>
.notification {
  padding: 20px;
}
</style>
