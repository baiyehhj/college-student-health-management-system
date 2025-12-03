package com.health.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.management.common.Result;
import com.health.management.dto.SleepRecordRequest;
import com.health.management.entity.SleepRecord;
import com.health.management.mapper.SleepRecordMapper;
import com.health.management.service.SleepRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SleepRecordServiceImpl implements SleepRecordService {
    
    @Autowired
    private SleepRecordMapper sleepRecordMapper;
    
    @Override
    public Result addRecord(Long studentId, SleepRecordRequest request) {
        SleepRecord record = new SleepRecord();
        BeanUtils.copyProperties(request, record);
        record.setStudentId(studentId);
        
        // 计算睡眠时长
        if (request.getSleepTime() != null && request.getWakeTime() != null) {
            Duration duration = Duration.between(request.getSleepTime(), request.getWakeTime());
            record.setDuration(BigDecimal.valueOf(duration.toMinutes() / 60.0));
        }
        
        sleepRecordMapper.insert(record);
        return Result.success("添加成功", record);
    }
    
    @Override
    public Result updateRecord(Long id, Long studentId, SleepRecordRequest request) {
        SleepRecord existRecord = sleepRecordMapper.selectById(id);
        if (existRecord == null) return Result.error(404,"记录不存在");
        if (!existRecord.getStudentId().equals(studentId)) return Result.error("无权限操作");
        
        SleepRecord record = new SleepRecord();
        BeanUtils.copyProperties(request, record);
        record.setId(id);
        record.setStudentId(studentId);
        
        if (request.getSleepTime() != null && request.getWakeTime() != null) {
            Duration duration = Duration.between(request.getSleepTime(), request.getWakeTime());
            record.setDuration(BigDecimal.valueOf(duration.toMinutes() / 60.0));
        }
        
        sleepRecordMapper.updateById(record);
        return Result.success("更新成功");
    }
    
    @Override
    public Result deleteRecord(Long id, Long studentId) {
        SleepRecord existRecord = sleepRecordMapper.selectById(id);
        if (existRecord == null) return Result.error(404,"记录不存在");
        if (!existRecord.getStudentId().equals(studentId)) return Result.error("无权限操作");
        sleepRecordMapper.deleteById(id);
        return Result.success("删除成功");
    }
    
    @Override
    public Result listRecords(Long studentId, LocalDate startDate, LocalDate endDate, Integer page, Integer size) {
        Page<SleepRecord> pageParam = new Page<>(page, size);
        QueryWrapper<SleepRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId)
               .between("record_date", startDate, endDate)
               .orderByDesc("record_date", "create_time");
        Page<SleepRecord> result = sleepRecordMapper.selectPage(pageParam, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("page", result.getCurrent());
        data.put("size", result.getSize());
        return Result.success(data);
    }
    
    @Override
    public Result getDailyStats(Long studentId, LocalDate date) {
        QueryWrapper<SleepRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId).eq("record_date", date);
        List<SleepRecord> records = sleepRecordMapper.selectList(wrapper);
        
        double totalDuration = 0;
        double avgQuality = 0;
        for (SleepRecord record : records) {
            totalDuration += record.getDuration() != null ? record.getDuration().doubleValue() : 0;
            avgQuality += record.getQuality() != null ? record.getQuality() : 0;
        }
        if (!records.isEmpty()) {
            avgQuality /= records.size();
        }
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("date", date);
        stats.put("recordCount", records.size());
        stats.put("totalDuration", BigDecimal.valueOf(totalDuration));
        stats.put("avgQuality", BigDecimal.valueOf(avgQuality));
        stats.put("records", records);
        return Result.success(stats);
    }
}
