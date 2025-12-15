package com.health.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.management.common.Result;
import com.health.management.config.PasswordEncoderConfig;
import com.health.management.entity.*;
import com.health.management.mapper.*;
import com.health.management.service.AdminService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 管理员服务实现类
 */
@Service
public class AdminServiceImpl implements AdminService {
    
    @Autowired
    private StudentUserMapper studentUserMapper;
    
    @Autowired
    private DietRecordMapper dietRecordMapper;
    
    @Autowired
    private ExerciseRecordMapper exerciseRecordMapper;
    
    @Autowired
    private SleepRecordMapper sleepRecordMapper;
    
    @Autowired
    private MoodRecordMapper moodRecordMapper;
    
    @Autowired
    private HealthExaminationMapper healthExaminationMapper;
    
    @Autowired
    private AIHealthReportMapper aiHealthReportMapper;
    
    private PasswordEncoderConfig.PasswordEncoder passwordEncoder = new PasswordEncoderConfig.PasswordEncoder();
    
    @Override
    public Result getStudentList(Integer pageNum, Integer pageSize, String keyword, Integer status) {
        Page<StudentUser> page = new Page<>(pageNum, pageSize);
        QueryWrapper<StudentUser> wrapper = new QueryWrapper<>();
        
        // 关键词搜索
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w
                .like("student_no", keyword)
                .or()
                .like("name", keyword)
                .or()
                .like("phone", keyword)
                .or()
                .like("email", keyword)
            );
        }
        
        // 状态筛选
        if (status != null) {
            wrapper.eq("status", status);
        }
        
        wrapper.orderByDesc("create_time");
        
        Page<StudentUser> result = studentUserMapper.selectPage(page, wrapper);
        
        // 转换结果，移除密码
        List<Map<String, Object>> studentList = new ArrayList<>();
        for (StudentUser user : result.getRecords()) {
            Map<String, Object> info = new HashMap<>();
            info.put("id", user.getId());
            info.put("studentNo", user.getStudentNo());
            info.put("name", user.getName());
            info.put("gender", user.getGender());
            info.put("age", user.getAge());
            info.put("phone", user.getPhone());
            info.put("email", user.getEmail());
            info.put("major", user.getMajor());
            info.put("className", user.getClassName());
            info.put("status", user.getStatus());
            info.put("createTime", user.getCreateTime());
            studentList.add(info);
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("list", studentList);
        data.put("total", result.getTotal());
        data.put("pageNum", result.getCurrent());
        data.put("pageSize", result.getSize());
        data.put("pages", result.getPages());
        
        return Result.success(data);
    }
    
    @Override
    public Result getStudentDetail(Long studentId) {
        StudentUser user = studentUserMapper.selectById(studentId);
        if (user == null) {
            return Result.error("学生不存在");
        }
        
        Map<String, Object> detail = new HashMap<>();
        
        // 基本信息
        Map<String, Object> basicInfo = new HashMap<>();
        basicInfo.put("id", user.getId());
        basicInfo.put("studentNo", user.getStudentNo());
        basicInfo.put("name", user.getName());
        basicInfo.put("gender", user.getGender());
        basicInfo.put("age", user.getAge());
        basicInfo.put("phone", user.getPhone());
        basicInfo.put("email", user.getEmail());
        basicInfo.put("major", user.getMajor());
        basicInfo.put("className", user.getClassName());
        basicInfo.put("status", user.getStatus());
        basicInfo.put("createTime", user.getCreateTime());
        detail.put("basicInfo", basicInfo);
        
        // 最近7天的数据统计
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(7);
        
        // 饮食记录统计
        QueryWrapper<DietRecord> dietWrapper = new QueryWrapper<>();
        dietWrapper.eq("student_id", studentId)
            .ge("record_date", startDate)
            .le("record_date", endDate);
        Long dietCount = dietRecordMapper.selectCount(dietWrapper);
        detail.put("dietRecordCount", dietCount);
        
        // 运动记录统计
        QueryWrapper<ExerciseRecord> exerciseWrapper = new QueryWrapper<>();
        exerciseWrapper.eq("student_id", studentId)
            .ge("record_date", startDate)
            .le("record_date", endDate);
        Long exerciseCount = exerciseRecordMapper.selectCount(exerciseWrapper);
        detail.put("exerciseRecordCount", exerciseCount);
        
        // 睡眠记录统计
        QueryWrapper<SleepRecord> sleepWrapper = new QueryWrapper<>();
        sleepWrapper.eq("student_id", studentId)
            .ge("record_date", startDate)
            .le("record_date", endDate);
        Long sleepCount = sleepRecordMapper.selectCount(sleepWrapper);
        detail.put("sleepRecordCount", sleepCount);
        
        // 情绪记录统计
        QueryWrapper<MoodRecord> moodWrapper = new QueryWrapper<>();
        moodWrapper.eq("student_id", studentId)
            .ge("record_date", startDate)
            .le("record_date", endDate);
        Long moodCount = moodRecordMapper.selectCount(moodWrapper);
        detail.put("moodRecordCount", moodCount);
        
        // 最新体检报告
        QueryWrapper<HealthExamination> examWrapper = new QueryWrapper<>();
        examWrapper.eq("student_id", studentId)
            .orderByDesc("exam_date")
            .last("LIMIT 1");
        HealthExamination latestExam = healthExaminationMapper.selectOne(examWrapper);
        detail.put("latestExamination", latestExam);
        
        // 最新AI健康报告
        QueryWrapper<AIHealthReport> reportWrapper = new QueryWrapper<>();
        reportWrapper.eq("student_id", studentId)
            .orderByDesc("report_date")
            .last("LIMIT 1");
        AIHealthReport latestReport = aiHealthReportMapper.selectOne(reportWrapper);
        detail.put("latestAIReport", latestReport);
        
        return Result.success(detail);
    }
    
    @Override
    public Result updateStudentStatus(Long studentId, Integer status) {
        StudentUser user = studentUserMapper.selectById(studentId);
        if (user == null) {
            return Result.error("学生不存在");
        }
        
        user.setStatus(status);
        studentUserMapper.updateById(user);
        
        String statusText = status == 1 ? "启用" : "禁用";
        return Result.success("已" + statusText + "该学生账号");
    }
    
    @Override
    public Result resetStudentPassword(Long studentId) {
        StudentUser user = studentUserMapper.selectById(studentId);
        if (user == null) {
            return Result.error("学生不存在");
        }
        
        // 重置为默认密码: 123456
        String defaultPassword = "123456";
        user.setPassword(passwordEncoder.encode(defaultPassword));
        studentUserMapper.updateById(user);
        
        return Result.success("密码已重置为: " + defaultPassword);
    }
    
    @Override
    public Result getSystemStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 学生总数
        Long totalStudents = studentUserMapper.selectCount(null);
        stats.put("totalStudents", totalStudents);
        
        // 活跃学生数（状态正常）
        QueryWrapper<StudentUser> activeWrapper = new QueryWrapper<>();
        activeWrapper.eq("status", 1);
        Long activeStudents = studentUserMapper.selectCount(activeWrapper);
        stats.put("activeStudents", activeStudents);
        
        // 禁用学生数
        stats.put("disabledStudents", totalStudents - activeStudents);
        
        // 今日新增学生
        QueryWrapper<StudentUser> todayWrapper = new QueryWrapper<>();
        todayWrapper.ge("create_time", LocalDate.now().atStartOfDay());
        Long todayNewStudents = studentUserMapper.selectCount(todayWrapper);
        stats.put("todayNewStudents", todayNewStudents);
        
        // 本周新增学生
        QueryWrapper<StudentUser> weekWrapper = new QueryWrapper<>();
        weekWrapper.ge("create_time", LocalDate.now().minusDays(7).atStartOfDay());
        Long weekNewStudents = studentUserMapper.selectCount(weekWrapper);
        stats.put("weekNewStudents", weekNewStudents);
        
        // 本月新增学生
        QueryWrapper<StudentUser> monthWrapper = new QueryWrapper<>();
        monthWrapper.ge("create_time", LocalDate.now().withDayOfMonth(1).atStartOfDay());
        Long monthNewStudents = studentUserMapper.selectCount(monthWrapper);
        stats.put("monthNewStudents", monthNewStudents);
        
        // 今日各类记录数
        LocalDate today = LocalDate.now();
        
        QueryWrapper<DietRecord> dietWrapper = new QueryWrapper<>();
        dietWrapper.eq("record_date", today);
        stats.put("todayDietRecords", dietRecordMapper.selectCount(dietWrapper));
        
        QueryWrapper<ExerciseRecord> exerciseWrapper = new QueryWrapper<>();
        exerciseWrapper.eq("record_date", today);
        stats.put("todayExerciseRecords", exerciseRecordMapper.selectCount(exerciseWrapper));
        
        QueryWrapper<SleepRecord> sleepWrapper = new QueryWrapper<>();
        sleepWrapper.eq("record_date", today);
        stats.put("todaySleepRecords", sleepRecordMapper.selectCount(sleepWrapper));
        
        QueryWrapper<MoodRecord> moodWrapper = new QueryWrapper<>();
        moodWrapper.eq("record_date", today);
        stats.put("todayMoodRecords", moodRecordMapper.selectCount(moodWrapper));
        
        return Result.success(stats);
    }
    
    @Override
    public Result getHealthTrendStatistics() {
        Map<String, Object> trends = new HashMap<>();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        
        // 过去30天每日记录数趋势
        List<Map<String, Object>> dailyTrends = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", date.toString());
            
            QueryWrapper<DietRecord> dietWrapper = new QueryWrapper<>();
            dietWrapper.eq("record_date", date);
            dayData.put("dietCount", dietRecordMapper.selectCount(dietWrapper));
            
            QueryWrapper<ExerciseRecord> exerciseWrapper = new QueryWrapper<>();
            exerciseWrapper.eq("record_date", date);
            dayData.put("exerciseCount", exerciseRecordMapper.selectCount(exerciseWrapper));
            
            QueryWrapper<SleepRecord> sleepWrapper = new QueryWrapper<>();
            sleepWrapper.eq("record_date", date);
            dayData.put("sleepCount", sleepRecordMapper.selectCount(sleepWrapper));
            
            QueryWrapper<MoodRecord> moodWrapper = new QueryWrapper<>();
            moodWrapper.eq("record_date", date);
            dayData.put("moodCount", moodRecordMapper.selectCount(moodWrapper));
            
            dailyTrends.add(dayData);
        }
        trends.put("dailyTrends", dailyTrends);
        
        // 情绪分布统计
        List<Map<String, Object>> moodDistribution = new ArrayList<>();
        String[] moodTypes = {"开心", "平静", "焦虑", "悲伤", "愤怒", "压力"};
        for (int i = 1; i <= 6; i++) {
            QueryWrapper<MoodRecord> moodWrapper = new QueryWrapper<>();
            moodWrapper.eq("mood_type", i)
                .ge("record_date", startDate);
            Long count = moodRecordMapper.selectCount(moodWrapper);
            
            Map<String, Object> item = new HashMap<>();
            item.put("type", i);
            item.put("name", moodTypes[i - 1]);
            item.put("count", count);
            moodDistribution.add(item);
        }
        trends.put("moodDistribution", moodDistribution);
        
        return Result.success(trends);
    }
    
    @Override
    public Result getHealthAlertStatistics() {
        Map<String, Object> alerts = new HashMap<>();
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(7);
        
        // 睡眠不足预警（近7天平均睡眠少于6小时的学生）
        List<Map<String, Object>> sleepAlerts = new ArrayList<>();
        List<StudentUser> allStudents = studentUserMapper.selectList(null);
        
        for (StudentUser student : allStudents) {
            QueryWrapper<SleepRecord> sleepWrapper = new QueryWrapper<>();
            sleepWrapper.eq("student_id", student.getId())
                .ge("record_date", weekAgo);
            List<SleepRecord> sleepRecords = sleepRecordMapper.selectList(sleepWrapper);
            
            if (!sleepRecords.isEmpty()) {
                double avgDuration = sleepRecords.stream()
                    .filter(r -> r.getDuration() != null)
                    .mapToDouble(r -> r.getDuration().doubleValue())
                    .average()
                    .orElse(0);
                
                if (avgDuration > 0 && avgDuration < 6) {
                    Map<String, Object> alert = new HashMap<>();
                    alert.put("studentId", student.getId());
                    alert.put("studentNo", student.getStudentNo());
                    alert.put("studentName", student.getName());
                    alert.put("avgSleepDuration", Math.round(avgDuration * 100.0) / 100.0);
                    alert.put("alertType", "睡眠不足");
                    sleepAlerts.add(alert);
                }
            }
        }
        alerts.put("sleepAlerts", sleepAlerts);
        
        // 情绪异常预警（近7天负面情绪超过50%的学生）
        List<Map<String, Object>> moodAlerts = new ArrayList<>();
        for (StudentUser student : allStudents) {
            QueryWrapper<MoodRecord> moodWrapper = new QueryWrapper<>();
            moodWrapper.eq("student_id", student.getId())
                .ge("record_date", weekAgo);
            List<MoodRecord> moodRecords = moodRecordMapper.selectList(moodWrapper);
            
            if (moodRecords.size() >= 3) {
                long negativeCount = moodRecords.stream()
                    .filter(r -> r.getMoodType() != null && (r.getMoodType() == 3 || r.getMoodType() == 4 || r.getMoodType() == 5 || r.getMoodType() == 6))
                    .count();
                
                double negativeRatio = (double) negativeCount / moodRecords.size();
                
                if (negativeRatio > 0.5) {
                    Map<String, Object> alert = new HashMap<>();
                    alert.put("studentId", student.getId());
                    alert.put("studentNo", student.getStudentNo());
                    alert.put("studentName", student.getName());
                    alert.put("negativeRatio", Math.round(negativeRatio * 100) + "%");
                    alert.put("alertType", "情绪异常");
                    moodAlerts.add(alert);
                }
            }
        }
        alerts.put("moodAlerts", moodAlerts);
        
        // 运动不足预警（近7天运动少于3次的学生）
        List<Map<String, Object>> exerciseAlerts = new ArrayList<>();
        for (StudentUser student : allStudents) {
            QueryWrapper<ExerciseRecord> exerciseWrapper = new QueryWrapper<>();
            exerciseWrapper.eq("student_id", student.getId())
                .ge("record_date", weekAgo);
            Long exerciseCount = exerciseRecordMapper.selectCount(exerciseWrapper);
            
            if (exerciseCount < 3) {
                Map<String, Object> alert = new HashMap<>();
                alert.put("studentId", student.getId());
                alert.put("studentNo", student.getStudentNo());
                alert.put("studentName", student.getName());
                alert.put("exerciseCount", exerciseCount);
                alert.put("alertType", "运动不足");
                exerciseAlerts.add(alert);
            }
        }
        alerts.put("exerciseAlerts", exerciseAlerts);
        
        // 统计汇总
        Map<String, Object> summary = new HashMap<>();
        summary.put("sleepAlertCount", sleepAlerts.size());
        summary.put("moodAlertCount", moodAlerts.size());
        summary.put("exerciseAlertCount", exerciseAlerts.size());
        summary.put("totalAlertCount", sleepAlerts.size() + moodAlerts.size() + exerciseAlerts.size());
        alerts.put("summary", summary);
        
        return Result.success(alerts);
    }
    
    @Override
    public void exportStudentData(HttpServletResponse response, String type) {
        try {
            List<StudentUser> students = studentUserMapper.selectList(null);
            
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("学生数据");
            
            // 创建标题行
            Row headerRow = sheet.createRow(0);
            String[] headers = {"学号", "姓名", "性别", "年龄", "手机号", "邮箱", "专业", "班级", "状态", "注册时间"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            
            // 填充数据
            int rowNum = 1;
            for (StudentUser student : students) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(student.getStudentNo() != null ? student.getStudentNo() : "");
                row.createCell(1).setCellValue(student.getName() != null ? student.getName() : "");
                row.createCell(2).setCellValue(student.getGender() != null ? (student.getGender() == 1 ? "男" : "女") : "");
                row.createCell(3).setCellValue(student.getAge() != null ? student.getAge() : 0);
                row.createCell(4).setCellValue(student.getPhone() != null ? student.getPhone() : "");
                row.createCell(5).setCellValue(student.getEmail() != null ? student.getEmail() : "");
                row.createCell(6).setCellValue(student.getMajor() != null ? student.getMajor() : "");
                row.createCell(7).setCellValue(student.getClassName() != null ? student.getClassName() : "");
                row.createCell(8).setCellValue(student.getStatus() != null ? (student.getStatus() == 1 ? "正常" : "禁用") : "");
                row.createCell(9).setCellValue(student.getCreateTime() != null ? 
                    student.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "");
            }
            
            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // 设置响应头
            String fileName = "学生数据_" + LocalDate.now().toString() + ".xlsx";
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            
            // 写入响应
            OutputStream out = response.getOutputStream();
            workbook.write(out);
            out.flush();
            out.close();
            workbook.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void exportStudentHealthData(HttpServletResponse response, Long studentId) {
        try {
            StudentUser student = studentUserMapper.selectById(studentId);
            if (student == null) {
                return;
            }
            
            Workbook workbook = new XSSFWorkbook();
            
            // 饮食记录sheet
            createDietSheet(workbook, studentId);
            
            // 运动记录sheet
            createExerciseSheet(workbook, studentId);
            
            // 睡眠记录sheet
            createSleepSheet(workbook, studentId);
            
            // 情绪记录sheet
            createMoodSheet(workbook, studentId);
            
            // 设置响应头
            String fileName = student.getName() + "_健康数据_" + LocalDate.now().toString() + ".xlsx";
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            
            // 写入响应
            OutputStream out = response.getOutputStream();
            workbook.write(out);
            out.flush();
            out.close();
            workbook.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void exportAllHealthData(HttpServletResponse response) {
        try {
            List<StudentUser> students = studentUserMapper.selectList(null);
            
            Workbook workbook = new XSSFWorkbook();
            
            // 创建综合数据sheet
            Sheet summarySheet = workbook.createSheet("综合统计");
            Row headerRow = summarySheet.createRow(0);
            String[] headers = {"学号", "姓名", "饮食记录数", "运动记录数", "睡眠记录数", "情绪记录数", "体检记录数"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }
            
            int rowNum = 1;
            for (StudentUser student : students) {
                Row row = summarySheet.createRow(rowNum++);
                row.createCell(0).setCellValue(student.getStudentNo());
                row.createCell(1).setCellValue(student.getName());
                
                QueryWrapper<DietRecord> dietWrapper = new QueryWrapper<>();
                dietWrapper.eq("student_id", student.getId());
                row.createCell(2).setCellValue(dietRecordMapper.selectCount(dietWrapper));
                
                QueryWrapper<ExerciseRecord> exerciseWrapper = new QueryWrapper<>();
                exerciseWrapper.eq("student_id", student.getId());
                row.createCell(3).setCellValue(exerciseRecordMapper.selectCount(exerciseWrapper));
                
                QueryWrapper<SleepRecord> sleepWrapper = new QueryWrapper<>();
                sleepWrapper.eq("student_id", student.getId());
                row.createCell(4).setCellValue(sleepRecordMapper.selectCount(sleepWrapper));
                
                QueryWrapper<MoodRecord> moodWrapper = new QueryWrapper<>();
                moodWrapper.eq("student_id", student.getId());
                row.createCell(5).setCellValue(moodRecordMapper.selectCount(moodWrapper));
                
                QueryWrapper<HealthExamination> examWrapper = new QueryWrapper<>();
                examWrapper.eq("student_id", student.getId());
                row.createCell(6).setCellValue(healthExaminationMapper.selectCount(examWrapper));
            }
            
            // 设置响应头
            String fileName = "全部学生健康数据统计_" + LocalDate.now().toString() + ".xlsx";
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            
            // 写入响应
            OutputStream out = response.getOutputStream();
            workbook.write(out);
            out.flush();
            out.close();
            workbook.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void createDietSheet(Workbook workbook, Long studentId) {
        Sheet sheet = workbook.createSheet("饮食记录");
        Row headerRow = sheet.createRow(0);
        String[] headers = {"日期", "餐次", "食物名称", "类别", "热量", "蛋白质", "碳水", "脂肪", "备注"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }
        
        QueryWrapper<DietRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId).orderByDesc("record_date");
        List<DietRecord> records = dietRecordMapper.selectList(wrapper);
        
        String[] mealTypes = {"", "早餐", "午餐", "晚餐", "加餐"};
        int rowNum = 1;
        for (DietRecord record : records) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(record.getRecordDate() != null ? record.getRecordDate().toString() : "");
            row.createCell(1).setCellValue(record.getMealType() != null && record.getMealType() < mealTypes.length ? mealTypes[record.getMealType()] : "");
            row.createCell(2).setCellValue(record.getFoodName() != null ? record.getFoodName() : "");
            row.createCell(3).setCellValue(record.getFoodCategory() != null ? record.getFoodCategory() : "");
            row.createCell(4).setCellValue(record.getCalories() != null ? record.getCalories().doubleValue() : 0);
            row.createCell(5).setCellValue(record.getProtein() != null ? record.getProtein().doubleValue() : 0);
            row.createCell(6).setCellValue(record.getCarbs() != null ? record.getCarbs().doubleValue() : 0);
            row.createCell(7).setCellValue(record.getFat() != null ? record.getFat().doubleValue() : 0);
            row.createCell(8).setCellValue(record.getDescription() != null ? record.getDescription() : "");
        }
    }
    
    private void createExerciseSheet(Workbook workbook, Long studentId) {
        Sheet sheet = workbook.createSheet("运动记录");
        Row headerRow = sheet.createRow(0);
        String[] headers = {"日期", "运动类型", "时长(分钟)", "消耗热量", "强度", "距离(公里)", "备注"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }
        
        QueryWrapper<ExerciseRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId).orderByDesc("record_date");
        List<ExerciseRecord> records = exerciseRecordMapper.selectList(wrapper);
        
        String[] intensities = {"", "低", "中", "高"};
        int rowNum = 1;
        for (ExerciseRecord record : records) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(record.getRecordDate() != null ? record.getRecordDate().toString() : "");
            row.createCell(1).setCellValue(record.getExerciseType() != null ? record.getExerciseType() : "");
            row.createCell(2).setCellValue(record.getDuration() != null ? record.getDuration() : 0);
            row.createCell(3).setCellValue(record.getCaloriesBurned() != null ? record.getCaloriesBurned().doubleValue() : 0);
            row.createCell(4).setCellValue(record.getIntensity() != null && record.getIntensity() < intensities.length ? intensities[record.getIntensity()] : "");
            row.createCell(5).setCellValue(record.getDistance() != null ? record.getDistance().doubleValue() : 0);
            row.createCell(6).setCellValue(record.getDescription() != null ? record.getDescription() : "");
        }
    }
    
    private void createSleepSheet(Workbook workbook, Long studentId) {
        Sheet sheet = workbook.createSheet("睡眠记录");
        Row headerRow = sheet.createRow(0);
        String[] headers = {"日期", "入睡时间", "起床时间", "时长(小时)", "质量", "深睡时长", "备注"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }
        
        QueryWrapper<SleepRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId).orderByDesc("record_date");
        List<SleepRecord> records = sleepRecordMapper.selectList(wrapper);
        
        String[] qualities = {"", "差", "一般", "良好", "优秀"};
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        int rowNum = 1;
        for (SleepRecord record : records) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(record.getRecordDate() != null ? record.getRecordDate().toString() : "");
            row.createCell(1).setCellValue(record.getSleepTime() != null ? record.getSleepTime().format(formatter) : "");
            row.createCell(2).setCellValue(record.getWakeTime() != null ? record.getWakeTime().format(formatter) : "");
            row.createCell(3).setCellValue(record.getDuration() != null ? record.getDuration().doubleValue() : 0);
            row.createCell(4).setCellValue(record.getQuality() != null && record.getQuality() < qualities.length ? qualities[record.getQuality()] : "");
            row.createCell(5).setCellValue(record.getDeepSleepDuration() != null ? record.getDeepSleepDuration().doubleValue() : 0);
            row.createCell(6).setCellValue(record.getDescription() != null ? record.getDescription() : "");
        }
    }
    
    private void createMoodSheet(Workbook workbook, Long studentId) {
        Sheet sheet = workbook.createSheet("情绪记录");
        Row headerRow = sheet.createRow(0);
        String[] headers = {"日期", "时间", "情绪类型", "情绪评分", "触发事件", "详细描述"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }
        
        QueryWrapper<MoodRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId).orderByDesc("record_date");
        List<MoodRecord> records = moodRecordMapper.selectList(wrapper);
        
        String[] moodTypes = {"", "开心", "平静", "焦虑", "悲伤", "愤怒", "压力"};
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        int rowNum = 1;
        for (MoodRecord record : records) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(record.getRecordDate() != null ? record.getRecordDate().toString() : "");
            row.createCell(1).setCellValue(record.getRecordTime() != null ? record.getRecordTime().format(formatter) : "");
            row.createCell(2).setCellValue(record.getMoodType() != null && record.getMoodType() < moodTypes.length ? moodTypes[record.getMoodType()] : "");
            row.createCell(3).setCellValue(record.getMoodScore() != null ? record.getMoodScore() : 0);
            row.createCell(4).setCellValue(record.getTriggerEvent() != null ? record.getTriggerEvent() : "");
            row.createCell(5).setCellValue(record.getDescription() != null ? record.getDescription() : "");
        }
    }
}
