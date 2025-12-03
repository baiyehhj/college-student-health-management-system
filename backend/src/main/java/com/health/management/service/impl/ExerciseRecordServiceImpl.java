package com.health.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.management.common.Result;
import com.health.management.dto.ExerciseRecordRequest;
import com.health.management.entity.ExerciseRecord;
import com.health.management.mapper.ExerciseRecordMapper;
import com.health.management.service.ExerciseRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExerciseRecordServiceImpl implements ExerciseRecordService {
    
    @Autowired
    private ExerciseRecordMapper exerciseRecordMapper;
    
    @Override
    public Result addRecord(Long studentId, ExerciseRecordRequest request) {
        ExerciseRecord record = new ExerciseRecord();
        BeanUtils.copyProperties(request, record);
        record.setStudentId(studentId);
        exerciseRecordMapper.insert(record);
        return Result.success("添加成功", record);
    }
    
    @Override
    public Result updateRecord(Long id, Long studentId, ExerciseRecordRequest request) {
        ExerciseRecord existRecord = exerciseRecordMapper.selectById(id);
        if (existRecord == null) return Result.error(404,"记录不存在");
        if (!existRecord.getStudentId().equals(studentId)) return Result.error("无权限操作");
        
        ExerciseRecord record = new ExerciseRecord();
        BeanUtils.copyProperties(request, record);
        record.setId(id);
        record.setStudentId(studentId);
        exerciseRecordMapper.updateById(record);
        return Result.success("更新成功");
    }
    
    @Override
    public Result deleteRecord(Long id, Long studentId) {
        ExerciseRecord existRecord = exerciseRecordMapper.selectById(id);
        if (existRecord == null) return Result.error("记录不存在");
        if (!existRecord.getStudentId().equals(studentId)) return Result.error("无权限操作");
        exerciseRecordMapper.deleteById(id);
        return Result.success("删除成功");
    }
    
    @Override
    public Result listRecords(Long studentId, LocalDate startDate, LocalDate endDate, Integer page, Integer size) {
        Page<ExerciseRecord> pageParam = new Page<>(page, size);
        QueryWrapper<ExerciseRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId)
               .between("record_date", startDate, endDate)
               .orderByDesc("record_date", "create_time");
        Page<ExerciseRecord> result = exerciseRecordMapper.selectPage(pageParam, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("page", result.getCurrent());
        data.put("size", result.getSize());
        return Result.success(data);
    }
    
    @Override
    public Result getDailyStats(Long studentId, LocalDate date) {
        QueryWrapper<ExerciseRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId).eq("record_date", date);
        List<ExerciseRecord> records = exerciseRecordMapper.selectList(wrapper);
        
        int totalDuration = 0;
        double totalCalories = 0;
        double totalDistance = 0;
        for (ExerciseRecord record : records) {
            totalDuration += record.getDuration() != null ? record.getDuration() : 0;
            totalCalories += record.getCaloriesBurned() != null ? record.getCaloriesBurned().doubleValue() : 0;
            totalDistance += record.getDistance() != null ? record.getDistance().doubleValue() : 0;
        }
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("date", date);
        stats.put("recordCount", records.size());
        stats.put("totalDuration", totalDuration);
        stats.put("totalCalories", BigDecimal.valueOf(totalCalories));
        stats.put("totalDistance", BigDecimal.valueOf(totalDistance));
        stats.put("records", records);
        return Result.success(stats);
    }
}
