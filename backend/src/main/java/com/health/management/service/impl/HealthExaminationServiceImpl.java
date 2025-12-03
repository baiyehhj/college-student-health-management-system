package com.health.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.management.common.Result;
import com.health.management.dto.HealthExaminationRequest;
import com.health.management.dto.ImportResultResponse;
import com.health.management.entity.HealthExamination;
import com.health.management.mapper.HealthExaminationMapper;
import com.health.management.service.HealthExaminationService;
import com.health.management.util.ExcelUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HealthExaminationServiceImpl implements HealthExaminationService {
    
    @Autowired
    private HealthExaminationMapper healthExaminationMapper;
    
    @Override
    public Result addExamination(Long studentId, HealthExaminationRequest request) {
        HealthExamination examination = new HealthExamination();
        BeanUtils.copyProperties(request, examination);
        examination.setStudentId(studentId);
        
        // 自动计算BMI
        if (request.getHeight() != null && request.getWeight() != null) {
            BigDecimal bmi = calculateBMI(request.getHeight(), request.getWeight());
            examination.setBmi(bmi);
        }
        
        healthExaminationMapper.insert(examination);
        return Result.success("添加成功", examination);
    }
    
    @Override
    public Result updateExamination(Long id, Long studentId, HealthExaminationRequest request) {
        HealthExamination existExamination = healthExaminationMapper.selectById(id);
        if (existExamination == null) {
            return Result.error(404,"体检报告不存在");
        }
        if (!existExamination.getStudentId().equals(studentId)) {
            return Result.error("无权限操作");
        }
        
        HealthExamination examination = new HealthExamination();
        BeanUtils.copyProperties(request, examination);
        examination.setId(id);
        examination.setStudentId(studentId);
        
        // 重新计算BMI
        if (request.getHeight() != null && request.getWeight() != null) {
            BigDecimal bmi = calculateBMI(request.getHeight(), request.getWeight());
            examination.setBmi(bmi);
        }
        
        healthExaminationMapper.updateById(examination);
        return Result.success("更新成功");
    }
    
    @Override
    public Result deleteExamination(Long id, Long studentId) {
        HealthExamination existExamination = healthExaminationMapper.selectById(id);
        if (existExamination == null) {
            return Result.error(404,"体检报告不存在");
        }
        if (!existExamination.getStudentId().equals(studentId)) {
            return Result.error("无权限操作");
        }
        
        healthExaminationMapper.deleteById(id);
        return Result.success("删除成功");
    }
    
    @Override
    public Result listExaminations(Long studentId, Integer page, Integer size) {
        Page<HealthExamination> pageParam = new Page<>(page, size);
        
        QueryWrapper<HealthExamination> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId)
               .orderByDesc("exam_date");
        
        Page<HealthExamination> result = healthExaminationMapper.selectPage(pageParam, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("page", result.getCurrent());
        data.put("size", result.getSize());
        
        return Result.success(data);
    }
    
    @Override
    public Result getExaminationDetail(Long id, Long studentId) {
        HealthExamination examination = healthExaminationMapper.selectById(id);
        if (examination == null) {
            return Result.error(404,"体检报告不存在");
        }
        if (!examination.getStudentId().equals(studentId)) {
            return Result.error("无权限查看");
        }
        
        // 添加健康指标分析
        Map<String, Object> data = new HashMap<>();
        data.put("examination", examination);
        data.put("analysis", analyzeHealthIndicators(examination));
        
        return Result.success(data);
    }
    
    @Override
    public Result getLatestExamination(Long studentId) {
        QueryWrapper<HealthExamination> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId)
               .orderByDesc("exam_date")
               .last("LIMIT 1");
        
        HealthExamination examination = healthExaminationMapper.selectOne(wrapper);
        
        if (examination == null) {
            return Result.success("暂无体检记录", null);
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("examination", examination);
        data.put("analysis", analyzeHealthIndicators(examination));
        
        return Result.success(data);
    }
    
    /**
     * 计算BMI
     * BMI = 体重(kg) / 身高(m)²
     */
    private BigDecimal calculateBMI(BigDecimal height, BigDecimal weight) {
        if (height == null || weight == null || height.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        
        // 身高从cm转为m
        BigDecimal heightInMeters = height.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        
        // 计算BMI
        BigDecimal bmi = weight.divide(
            heightInMeters.multiply(heightInMeters),
            2,
            RoundingMode.HALF_UP
        );
        
        return bmi;
    }
    
    /**
     * 分析健康指标
     */
    private Map<String, Object> analyzeHealthIndicators(HealthExamination exam) {
        Map<String, Object> analysis = new HashMap<>();
        
        // BMI分析
        if (exam.getBmi() != null) {
            double bmi = exam.getBmi().doubleValue();
            String bmiStatus;
            if (bmi < 18.5) {
                bmiStatus = "偏瘦";
            } else if (bmi < 24) {
                bmiStatus = "正常";
            } else if (bmi < 28) {
                bmiStatus = "超重";
            } else {
                bmiStatus = "肥胖";
            }
            analysis.put("bmiStatus", bmiStatus);
        }
        
        // 血压分析
        if (exam.getBloodPressureHigh() != null && exam.getBloodPressureLow() != null) {
            int high = exam.getBloodPressureHigh();
            int low = exam.getBloodPressureLow();
            String bpStatus;
            if (high < 120 && low < 80) {
                bpStatus = "正常";
            } else if (high < 140 && low < 90) {
                bpStatus = "正常高值";
            } else {
                bpStatus = "高血压";
            }
            analysis.put("bloodPressureStatus", bpStatus);
        }
        
        // 心率分析
        if (exam.getHeartRate() != null) {
            int hr = exam.getHeartRate();
            String hrStatus;
            if (hr >= 60 && hr <= 100) {
                hrStatus = "正常";
            } else if (hr < 60) {
                hrStatus = "心动过缓";
            } else {
                hrStatus = "心动过速";
            }
            analysis.put("heartRateStatus", hrStatus);
        }
        
        // 视力分析
        if (exam.getVisionLeft() != null && exam.getVisionRight() != null) {
            double avgVision = (exam.getVisionLeft().doubleValue() + exam.getVisionRight().doubleValue()) / 2;
            String visionStatus;
            if (avgVision >= 5.0) {
                visionStatus = "正常";
            } else if (avgVision >= 4.0) {
                visionStatus = "轻度近视";
            } else {
                visionStatus = "需要矫正";
            }
            analysis.put("visionStatus", visionStatus);
        }
        
        return analysis;
    }
    
    @Override
    public Result importExcel(Long studentId, MultipartFile file) {
        ImportResultResponse result = new ImportResultResponse();
        
        try {
            // 验证文件
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }
            
            String filename = file.getOriginalFilename();
            if (filename == null || !filename.endsWith(".xlsx")) {
                return Result.error("只支持.xlsx格式的Excel文件");
            }
            
            // 读取Excel
            List<List<Object>> dataList = ExcelUtil.readExcel(file);
            
            if (dataList.isEmpty()) {
                return Result.error("Excel文件中没有数据");
            }
            
            // 逐行处理
            for (int i = 0; i < dataList.size(); i++) {
                List<Object> rowData = dataList.get(i);
                int rowNum = i + 2; // Excel行号从1开始，加上表头行
                
                try {
                    // 验证必填字段
                    if (rowData.size() < 4) {
                        result.addFailed("第" + rowNum + "行：数据不完整");
                        continue;
                    }
                    
                    // 解析数据
                    LocalDate examDate = ExcelUtil.parseDate(rowData.get(0));
                    String examType = ExcelUtil.parseString(rowData.get(1));
                    BigDecimal height = ExcelUtil.parseBigDecimal(rowData.get(2));
                    BigDecimal weight = ExcelUtil.parseBigDecimal(rowData.get(3));
                    
                    // 验证必填字段
                    if (examDate == null) {
                        result.addFailed("第" + rowNum + "行：体检日期格式错误");
                        continue;
                    }
                    if (examType == null || examType.isEmpty()) {
                        result.addFailed("第" + rowNum + "行：体检类型不能为空");
                        continue;
                    }
                    if (height == null) {
                        result.addFailed("第" + rowNum + "行：身高不能为空");
                        continue;
                    }
                    if (weight == null) {
                        result.addFailed("第" + rowNum + "行：体重不能为空");
                        continue;
                    }
                    
                    // 创建体检记录
                    HealthExamination exam = new HealthExamination();
                    exam.setStudentId(studentId);
                    exam.setExamDate(examDate);
                    exam.setExamType(examType);
                    exam.setHeight(height);
                    exam.setWeight(weight);
                    
                    // 计算BMI
                    exam.setBmi(calculateBMI(height, weight));
                    
                    // 解析可选字段
                    if (rowData.size() > 4) exam.setBloodPressureHigh(ExcelUtil.parseInteger(rowData.get(4)));
                    if (rowData.size() > 5) exam.setBloodPressureLow(ExcelUtil.parseInteger(rowData.get(5)));
                    if (rowData.size() > 6) exam.setHeartRate(ExcelUtil.parseInteger(rowData.get(6)));
                    if (rowData.size() > 7) exam.setVisionLeft(ExcelUtil.parseBigDecimal(rowData.get(7)));
                    if (rowData.size() > 8) exam.setVisionRight(ExcelUtil.parseBigDecimal(rowData.get(8)));
                    if (rowData.size() > 9) exam.setBloodSugar(ExcelUtil.parseBigDecimal(rowData.get(9)));
                    if (rowData.size() > 10) exam.setHemoglobin(ExcelUtil.parseBigDecimal(rowData.get(10)));
                    if (rowData.size() > 11) exam.setWhiteBloodCell(ExcelUtil.parseBigDecimal(rowData.get(11)));
                    if (rowData.size() > 12) exam.setPlatelet(ExcelUtil.parseBigDecimal(rowData.get(12)));
                    if (rowData.size() > 13) exam.setOverallConclusion(ExcelUtil.parseString(rowData.get(13)));
                    if (rowData.size() > 14) exam.setDoctorAdvice(ExcelUtil.parseString(rowData.get(14)));
                    
                    // 保存到数据库
                    healthExaminationMapper.insert(exam);
                    result.addSuccess();
                    
                } catch (Exception e) {
                    result.addFailed("第" + rowNum + "行：" + e.getMessage());
                }
            }
            
            return Result.success(result);
            
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("导入失败：" + e.getMessage());
        }
    }
}
