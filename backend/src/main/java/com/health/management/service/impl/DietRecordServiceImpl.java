package com.health.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.management.common.Result;
import com.health.management.dto.DietRecordRequest;
import com.health.management.entity.DietRecord;
import com.health.management.mapper.DietRecordMapper;
import com.health.management.service.DietRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DietRecordServiceImpl implements DietRecordService {
    
    @Autowired
    private DietRecordMapper dietRecordMapper;
    
    @Override
    public Result addRecord(Long studentId, DietRecordRequest request) {
        DietRecord record = new DietRecord();
        BeanUtils.copyProperties(request, record);
        record.setStudentId(studentId);
        dietRecordMapper.insert(record);
        return Result.success("添加成功", record);
    }
    
    @Override
    public Result updateRecord(Long id, Long studentId, DietRecordRequest request) {
        DietRecord existRecord = dietRecordMapper.selectById(id);
        if (existRecord == null) {
            return Result.error(404,"记录不存在");
        }
        if (!existRecord.getStudentId().equals(studentId)) {
            return Result.error("无权限操作");
        }
        
        DietRecord record = new DietRecord();
        BeanUtils.copyProperties(request, record);
        record.setId(id);
        record.setStudentId(studentId);
        dietRecordMapper.updateById(record);
        return Result.success("更新成功");
    }
    
    @Override
    public Result deleteRecord(Long id, Long studentId) {
        DietRecord existRecord = dietRecordMapper.selectById(id);
        if (existRecord == null) {
            return Result.error("记录不存在");
        }
        if (!existRecord.getStudentId().equals(studentId)) {
            return Result.error("无权限操作");
        }
        dietRecordMapper.deleteById(id);
        return Result.success("删除成功");
    }
    
    @Override
    public Result listRecords(Long studentId, LocalDate startDate, LocalDate endDate, Integer page, Integer size) {
        Page<DietRecord> pageParam = new Page<>(page, size);
        QueryWrapper<DietRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId)
               .between("record_date", startDate, endDate)
               .orderByDesc("record_date", "create_time");
        Page<DietRecord> result = dietRecordMapper.selectPage(pageParam, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("page", result.getCurrent());
        data.put("size", result.getSize());
        return Result.success(data);
    }
    
    @Override
    public Result getDailyStats(Long studentId, LocalDate date) {
        QueryWrapper<DietRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId).eq("record_date", date);
        List<DietRecord> records = dietRecordMapper.selectList(wrapper);
        
        double totalCalories = 0;
        double totalProtein = 0;
        double totalCarbs = 0;
        double totalFat = 0;
        
        for (DietRecord record : records) {
            totalCalories += record.getCalories() != null ? record.getCalories().doubleValue() : 0;
            totalProtein += record.getProtein() != null ? record.getProtein().doubleValue() : 0;
            totalCarbs += record.getCarbs() != null ? record.getCarbs().doubleValue() : 0;
            totalFat += record.getFat() != null ? record.getFat().doubleValue() : 0;
        }
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("date", date);
        stats.put("recordCount", records.size());
        stats.put("totalCalories", BigDecimal.valueOf(totalCalories));
        stats.put("totalProtein", BigDecimal.valueOf(totalProtein));
        stats.put("totalCarbs", BigDecimal.valueOf(totalCarbs));
        stats.put("totalFat", BigDecimal.valueOf(totalFat));
        stats.put("records", records);
        return Result.success(stats);
    }
}
