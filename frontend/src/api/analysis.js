// analysis.js - 数据分析API
import request from '@/utils/request'

/**
 * 获取体重趋势
 */
export function getWeightTrend(days = 30) {
  return request({
    url: '/analysis/weight-trend',
    method: 'get',
    params: { days }
  })
}

/**
 * 获取运动统计
 */
export function getExerciseStats(days = 30) {
  return request({
    url: '/analysis/exercise-stats',
    method: 'get',
    params: { days }
  })
}

/**
 * 获取睡眠趋势
 */
export function getSleepTrend(days = 30) {
  return request({
    url: '/analysis/sleep-trend',
    method: 'get',
    params: { days }
  })
}

/**
 * 获取情绪分布
 */
export function getMoodDistribution(days = 30) {
  return request({
    url: '/analysis/mood-distribution',
    method: 'get',
    params: { days }
  })
}

/**
 * 获取营养统计
 */
export function getNutritionStats(days = 30) {
  return request({
    url: '/analysis/nutrition-stats',
    method: 'get',
    params: { days }
  })
}
