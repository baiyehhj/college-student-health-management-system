package com.health.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.health.management.common.Result;
import com.health.management.dto.*;
import com.health.management.entity.*;
import com.health.management.mapper.*;
import com.health.management.service.DataAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据分析服务实现类
 */
@Service
public class DataAnalysisServiceImpl implements DataAnalysisService {
    
    @Autowired
    private HealthExaminationMapper healthExaminationMapper;
    
    @Autowired
    private ExerciseRecordMapper exerciseRecordMapper;
    
    @Autowired
    private SleepRecordMapper sleepRecordMapper;
    
    @Autowired
    private MoodRecordMapper moodRecordMapper;
    
    @Autowired
    private DietRecordMapper dietRecordMapper;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @Override
    public Result getWeightTrend(Long studentId, Integer days) {
        try {
            // 计算起始日期
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(days);
            
            // 查询体检记录
            QueryWrapper<HealthExamination> wrapper = new QueryWrapper<>();
            wrapper.eq("student_id", studentId)
                   .ge("exam_date", startDate.format(DATE_FORMATTER))
                   .le("exam_date", endDate.format(DATE_FORMATTER))
                   .orderByAsc("exam_date");
            
            List<HealthExamination> records = healthExaminationMapper.selectList(wrapper);
            
            WeightTrendResponse response = new WeightTrendResponse();
            
            if (records.isEmpty()) {
                response.setDates(new ArrayList<>());
                response.setWeights(new ArrayList<>());
                response.setBmis(new ArrayList<>());
                response.setAvgWeight(0.0);
                response.setAvgBmi(0.0);
                response.setTrend("无数据");
                return Result.success(response);
            }
            
            // 提取数据
            List<String> dates = records.stream()
                    .map(r -> r.getExamDate().toString())
                    .collect(Collectors.toList());
            
            List<Double> weights = records.stream()
                    .map(r -> r.getWeight() != null ? r.getWeight().doubleValue() : 0.0)
                    .collect(Collectors.toList());
            
            List<Double> bmis = records.stream()
                    .map(r -> r.getBmi() != null ? r.getBmi().doubleValue() : 0.0)
                    .collect(Collectors.toList());
            
            // 计算统计数据
            double avgWeight = weights.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);
            
            double avgBmi = bmis.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);
            
            // 判断趋势
            String trend = calculateTrend(weights);
            
            response.setDates(dates);
            response.setWeights(weights);
            response.setBmis(bmis);
            response.setAvgWeight(Math.round(avgWeight * 100.0) / 100.0);
            response.setAvgBmi(Math.round(avgBmi * 100.0) / 100.0);
            response.setTrend(trend);
            
            return Result.success(response);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取体重趋势失败");
        }
    }
    
    @Override
    public Result getExerciseStats(Long studentId, Integer days) {
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(days);
            
            QueryWrapper<ExerciseRecord> wrapper = new QueryWrapper<>();
            wrapper.eq("student_id", studentId)
                   .ge("record_date", startDate.format(DATE_FORMATTER))
                   .le("record_date", endDate.format(DATE_FORMATTER))
                   .orderByAsc("record_date");
            
            List<ExerciseRecord> records = exerciseRecordMapper.selectList(wrapper);
            
            ExerciseStatsResponse response = new ExerciseStatsResponse();
            
            if (records.isEmpty()) {
                response.setDates(new ArrayList<>());
                response.setDurations(new ArrayList<>());
                response.setCalories(new ArrayList<>());
                response.setTypeStats(new HashMap<>());
                response.setTotalDuration(0);
                response.setTotalCalories(0.0);
                response.setAvgDuration(0.0);
                return Result.success(response);
            }
            
            // 按日期聚合数据
            Map<String, List<ExerciseRecord>> dateGrouped = records.stream()
                    .collect(Collectors.groupingBy(r -> r.getRecordDate().toString()));
            
            List<String> dates = new ArrayList<>(dateGrouped.keySet());
            Collections.sort(dates);
            
            List<Integer> durations = new ArrayList<>();
            List<Double> calories = new ArrayList<>();
            
            for (String date : dates) {
                List<ExerciseRecord> dayRecords = dateGrouped.get(date);
                int totalDuration = dayRecords.stream()
                        .mapToInt(ExerciseRecord::getDuration)
                        .sum();
                double totalCalories = dayRecords.stream()
                        .mapToDouble(r -> r.getCaloriesBurned() != null ? r.getCaloriesBurned().doubleValue() : 0.0)
                        .sum();
                
                durations.add(totalDuration);
                calories.add(Math.round(totalCalories * 100.0) / 100.0);
            }
            
            // 运动类型统计
            Map<String, Integer> typeStats = records.stream()
                    .collect(Collectors.groupingBy(
                            ExerciseRecord::getExerciseType,
                            Collectors.summingInt(ExerciseRecord::getDuration)
                    ));
            
            // 总计数据
            int totalDuration = records.stream()
                    .mapToInt(ExerciseRecord::getDuration)
                    .sum();
            
            double totalCalories = records.stream()
                    .mapToDouble(r -> r.getCaloriesBurned() != null ? r.getCaloriesBurned().doubleValue() : 0.0)
                    .sum();
            
            double avgDuration = (double) totalDuration / dates.size();
            
            response.setDates(dates);
            response.setDurations(durations);
            response.setCalories(calories);
            response.setTypeStats(typeStats);
            response.setTotalDuration(totalDuration);
            response.setTotalCalories(Math.round(totalCalories * 100.0) / 100.0);
            response.setAvgDuration(Math.round(avgDuration * 100.0) / 100.0);
            
            return Result.success(response);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取运动统计失败");
        }
    }
    
    @Override
    public Result getSleepTrend(Long studentId, Integer days) {
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(days);
            
            QueryWrapper<SleepRecord> wrapper = new QueryWrapper<>();
            wrapper.eq("student_id", studentId)
                   .ge("record_date", startDate.format(DATE_FORMATTER))
                   .le("record_date", endDate.format(DATE_FORMATTER))
                   .orderByAsc("record_date");
            
            List<SleepRecord> records = sleepRecordMapper.selectList(wrapper);
            
            SleepTrendResponse response = new SleepTrendResponse();
            
            if (records.isEmpty()) {
                response.setDates(new ArrayList<>());
                response.setDurations(new ArrayList<>());
                response.setQualities(new ArrayList<>());
                response.setAvgDuration(0.0);
                response.setAvgQuality(0.0);
                response.setGoodSleepDays(0);
                return Result.success(response);
            }
            
            List<String> dates = records.stream()
                    .map(r -> r.getRecordDate().toString())
                    .collect(Collectors.toList());
            
            List<Double> durations = records.stream()
                    .map(r -> r.getDuration() != null ? r.getDuration().doubleValue() : 0.0)
                    .collect(Collectors.toList());
            
            List<Integer> qualities = records.stream()
                    .map(SleepRecord::getQuality)
                    .collect(Collectors.toList());
            
            double avgDuration = durations.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);
            
            double avgQuality = qualities.stream()
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0.0);
            
            // 优质睡眠天数（质量>=4）
            int goodSleepDays = (int) qualities.stream()
                    .filter(q -> q >= 4)
                    .count();
            
            response.setDates(dates);
            response.setDurations(durations);
            response.setQualities(qualities);
            response.setAvgDuration(Math.round(avgDuration * 100.0) / 100.0);
            response.setAvgQuality(Math.round(avgQuality * 100.0) / 100.0);
            response.setGoodSleepDays(goodSleepDays);
            
            return Result.success(response);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取睡眠趋势失败");
        }
    }
    
    @Override
    public Result getMoodDistribution(Long studentId, Integer days) {
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(days);
            
            QueryWrapper<MoodRecord> wrapper = new QueryWrapper<>();
            wrapper.eq("student_id", studentId)
                   .ge("record_date", startDate.format(DATE_FORMATTER))
                   .le("record_date", endDate.format(DATE_FORMATTER));
            
            List<MoodRecord> records = moodRecordMapper.selectList(wrapper);
            
            MoodDistributionResponse response = new MoodDistributionResponse();
            
            if (records.isEmpty()) {
                response.setDistribution(new HashMap<>());
                response.setMostFrequent("无数据");
                response.setTotalRecords(0);
                response.setPositiveRate(0.0);
                return Result.success(response);
            }
            
            // 情绪类型映射：将数字转换为情绪形容词
            Map<String, String> moodTypeMap = new HashMap<>();
            moodTypeMap.put("1", "开心");
            moodTypeMap.put("2", "平静");
            moodTypeMap.put("3", "焦虑");
            moodTypeMap.put("4", "悲伤");
            moodTypeMap.put("5", "愤怒");
            moodTypeMap.put("6", "压力");
            
            // 情绪分布统计
            Map<String, Integer> distribution = new HashMap<>();
            for (MoodRecord record : records) {
                String moodTypeKey = String.valueOf(record.getMoodType());
                // 将数字转换为情绪形容词
                String moodName = moodTypeMap.getOrDefault(moodTypeKey, moodTypeKey);
                distribution.put(moodName, distribution.getOrDefault(moodName, 0) + 1);
            }
            
            // 最常见情绪
            String mostFrequent = distribution.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("无");
            
            // 积极情绪占比（开心、平静为积极情绪）
            Set<String> positiveMoods = new HashSet<>(Arrays.asList("开心", "平静"));
            long positiveCount = records.stream()
                    .filter(r -> {
                        String moodTypeKey = String.valueOf(r.getMoodType());
                        String moodName = moodTypeMap.getOrDefault(moodTypeKey, moodTypeKey);
                        return positiveMoods.contains(moodName);
                    })
                    .count();
            
            double positiveRate = (double) positiveCount / records.size() * 100;
            
            response.setDistribution(distribution);
            response.setMostFrequent(mostFrequent);
            response.setTotalRecords(records.size());
            response.setPositiveRate(Math.round(positiveRate * 100.0) / 100.0);
            
            return Result.success(response);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取情绪分布失败");
        }
    }
    
    @Override
    public Result getNutritionStats(Long studentId, Integer days) {
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(days);
            
            QueryWrapper<DietRecord> wrapper = new QueryWrapper<>();
            wrapper.eq("student_id", studentId)
                   .ge("record_date", startDate.format(DATE_FORMATTER))
                   .le("record_date", endDate.format(DATE_FORMATTER))
                   .orderByAsc("record_date");
            
            List<DietRecord> records = dietRecordMapper.selectList(wrapper);
            
            NutritionStatsResponse response = new NutritionStatsResponse();
            
            if (records.isEmpty()) {
                response.setDates(new ArrayList<>());
                response.setCalories(new ArrayList<>());
                response.setProteins(new ArrayList<>());
                response.setCarbs(new ArrayList<>());
                response.setFats(new ArrayList<>());
                response.setAvgCalories(0.0);
                response.setAvgProtein(0.0);
                response.setAvgCarbs(0.0);
                response.setAvgFat(0.0);
                return Result.success(response);
            }
            
            // 按日期聚合
            Map<String, List<DietRecord>> dateGrouped = records.stream()
                    .collect(Collectors.groupingBy(r -> r.getRecordDate().toString()));
            
            List<String> dates = new ArrayList<>(dateGrouped.keySet());
            Collections.sort(dates);
            
            List<Double> calories = new ArrayList<>();
            List<Double> proteins = new ArrayList<>();
            List<Double> carbs = new ArrayList<>();
            List<Double> fats = new ArrayList<>();
            
            for (String date : dates) {
                List<DietRecord> dayRecords = dateGrouped.get(date);
                
                double totalCalories = dayRecords.stream()
                        .mapToDouble(r -> r.getCalories() != null ? r.getCalories().doubleValue() : 0.0)
                        .sum();
                double totalProtein = dayRecords.stream()
                        .mapToDouble(r -> r.getProtein() != null ? r.getProtein().doubleValue() : 0.0)
                        .sum();
                double totalCarbs = dayRecords.stream()
                        .mapToDouble(r -> r.getCarbs() != null ? r.getCarbs().doubleValue() : 0.0)
                        .sum();
                double totalFat = dayRecords.stream()
                        .mapToDouble(r -> r.getFat() != null ? r.getFat().doubleValue() : 0.0)
                        .sum();
                
                calories.add(Math.round(totalCalories * 100.0) / 100.0);
                proteins.add(Math.round(totalProtein * 100.0) / 100.0);
                carbs.add(Math.round(totalCarbs * 100.0) / 100.0);
                fats.add(Math.round(totalFat * 100.0) / 100.0);
            }
            
            // 平均值
            double avgCalories = calories.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            double avgProtein = proteins.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            double avgCarbs = carbs.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            double avgFat = fats.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            
            response.setDates(dates);
            response.setCalories(calories);
            response.setProteins(proteins);
            response.setCarbs(carbs);
            response.setFats(fats);
            response.setAvgCalories(Math.round(avgCalories * 100.0) / 100.0);
            response.setAvgProtein(Math.round(avgProtein * 100.0) / 100.0);
            response.setAvgCarbs(Math.round(avgCarbs * 100.0) / 100.0);
            response.setAvgFat(Math.round(avgFat * 100.0) / 100.0);
            
            return Result.success(response);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取营养统计失败");
        }
    }
    
    /**
     * 计算趋势
     */
    private String calculateTrend(List<Double> values) {
        if (values.size() < 2) {
            return "稳定";
        }
        
        double first = values.get(0);
        double last = values.get(values.size() - 1);
        double diff = last - first;
        
        if (Math.abs(diff) < 0.5) {
            return "稳定";
        } else if (diff > 0) {
            return "上升";
        } else {
            return "下降";
        }
    }
}
