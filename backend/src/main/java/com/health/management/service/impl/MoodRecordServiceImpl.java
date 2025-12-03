package com.health.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.management.common.Result;
import com.health.management.dto.MoodRecordRequest;
import com.health.management.entity.MoodRecord;
import com.health.management.mapper.MoodRecordMapper;
import com.health.management.service.MoodRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MoodRecordServiceImpl implements MoodRecordService {
    
    @Autowired
    private MoodRecordMapper moodRecordMapper;
    
    @Override
    public Result addRecord(Long studentId, MoodRecordRequest request) {
        MoodRecord record = new MoodRecord();
        BeanUtils.copyProperties(request, record);
        record.setStudentId(studentId);
        moodRecordMapper.insert(record);
        return Result.success("添加成功", record);
    }
    
    @Override
    public Result updateRecord(Long id, Long studentId, MoodRecordRequest request) {
        MoodRecord existRecord = moodRecordMapper.selectById(id);
        if (existRecord == null) return Result.error(404,"记录不存在");
        if (!existRecord.getStudentId().equals(studentId)) return Result.error("无权限操作");
        
        MoodRecord record = new MoodRecord();
        BeanUtils.copyProperties(request, record);
        record.setId(id);
        record.setStudentId(studentId);
        moodRecordMapper.updateById(record);
        return Result.success("更新成功");
    }
    
    @Override
    public Result deleteRecord(Long id, Long studentId) {
        MoodRecord existRecord = moodRecordMapper.selectById(id);
        if (existRecord == null) return Result.error(404,"记录不存在");
        if (!existRecord.getStudentId().equals(studentId)) return Result.error("无权限操作");
        moodRecordMapper.deleteById(id);
        return Result.success("删除成功");
    }
    
    @Override
    public Result listRecords(Long studentId, LocalDate startDate, LocalDate endDate, Integer page, Integer size) {
        Page<MoodRecord> pageParam = new Page<>(page, size);
        QueryWrapper<MoodRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId)
               .between("record_date", startDate, endDate)
               .orderByDesc("record_date", "record_time");
        Page<MoodRecord> result = moodRecordMapper.selectPage(pageParam, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("page", result.getCurrent());
        data.put("size", result.getSize());
        return Result.success(data);
    }
    
    @Override
    public Result getDailyStats(Long studentId, LocalDate date) {
        QueryWrapper<MoodRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId).eq("record_date", date);
        List<MoodRecord> records = moodRecordMapper.selectList(wrapper);
        
        double avgScore = 0;
        Map<Integer, Integer> moodTypeCount = new HashMap<>();
        for (MoodRecord record : records) {
            avgScore += record.getMoodScore() != null ? record.getMoodScore() : 0;
            moodTypeCount.merge(record.getMoodType(), 1, Integer::sum);
        }
        if (!records.isEmpty()) {
            avgScore /= records.size();
        }
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("date", date);
        stats.put("recordCount", records.size());
        stats.put("avgScore", BigDecimal.valueOf(avgScore));
        stats.put("moodTypeCount", moodTypeCount);
        stats.put("records", records);
        return Result.success(stats);
    }
}
