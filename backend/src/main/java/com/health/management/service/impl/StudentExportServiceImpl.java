package com.health.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.health.management.entity.*;
import com.health.management.mapper.*;
import com.health.management.service.StudentExportService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学生数据导出服务实现类
 */
@Service
public class StudentExportServiceImpl implements StudentExportService {

    @Autowired
    private DietRecordMapper dietRecordMapper;

    @Autowired
    private ExerciseRecordMapper exerciseRecordMapper;

    @Autowired
    private SleepRecordMapper sleepRecordMapper;

    @Autowired
    private MoodRecordMapper moodRecordMapper;

    @Autowired
    private StudentUserMapper studentUserMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public ByteArrayOutputStream exportBehaviorData(Long studentId, LocalDate startDate, LocalDate endDate) throws Exception {
        // 创建工作簿
        Workbook workbook = new XSSFWorkbook();

        // 创建样式
        Map<String, CellStyle> styles = createStyles(workbook);

        // 获取学生信息
        StudentUser student = studentUserMapper.selectById(studentId);
        String studentName = student != null ? student.getName() : "未知";

        // 导出饮食记录
        exportDietRecords(workbook, styles, studentId, studentName, startDate, endDate);

        // 导出运动记录
        exportExerciseRecords(workbook, styles, studentId, studentName, startDate, endDate);

        // 导出睡眠记录
        exportSleepRecords(workbook, styles, studentId, studentName, startDate, endDate);

        // 导出情绪记录
        exportMoodRecords(workbook, styles, studentId, studentName, startDate, endDate);

        // 写入字节流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream;
    }

    /**
     * 导出饮食记录
     */
    private void exportDietRecords(Workbook workbook, Map<String, CellStyle> styles,
                                   Long studentId, String studentName, LocalDate startDate, LocalDate endDate) {
        Sheet sheet = workbook.createSheet("饮食记录");

        // 创建标题行
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(String.format("%s的饮食记录 (%s 至 %s)",
                studentName,
                startDate.format(DATE_FORMATTER),
                endDate.format(DATE_FORMATTER)));
        titleCell.setCellStyle(styles.get("title"));

        // 创建表头
        Row headerRow = sheet.createRow(2);
        String[] headers = {"记录日期", "餐次", "食物名称", "食物类别", "热量(千卡)", "蛋白质(克)", "碳水(克)", "脂肪(克)", "备注"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(styles.get("header"));
        }

        // 查询数据
        QueryWrapper<DietRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId)
                .ge("record_date", startDate.format(DATE_FORMATTER))
                .le("record_date", endDate.format(DATE_FORMATTER))
                .orderByAsc("record_date", "meal_type");
        List<DietRecord> records = dietRecordMapper.selectList(wrapper);

        // 填充数据
        int rowNum = 3;
        Map<Integer, String> mealTypeMap = getMealTypeMap();
        for (DietRecord record : records) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(record.getRecordDate().format(DATE_FORMATTER));
            row.createCell(1).setCellValue(mealTypeMap.getOrDefault(record.getMealType(), "未知"));
            row.createCell(2).setCellValue(record.getFoodName() != null ? record.getFoodName() : "");
            row.createCell(3).setCellValue(record.getFoodCategory() != null ? record.getFoodCategory() : "");
            row.createCell(4).setCellValue(record.getCalories() != null ? record.getCalories().doubleValue() : 0.0);
            row.createCell(5).setCellValue(record.getProtein() != null ? record.getProtein().doubleValue() : 0.0);
            row.createCell(6).setCellValue(record.getCarbs() != null ? record.getCarbs().doubleValue() : 0.0);
            row.createCell(7).setCellValue(record.getFat() != null ? record.getFat().doubleValue() : 0.0);
            row.createCell(8).setCellValue(record.getDescription() != null ? record.getDescription() : "");
        }

        // 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * 导出运动记录
     */
    private void exportExerciseRecords(Workbook workbook, Map<String, CellStyle> styles,
                                       Long studentId, String studentName, LocalDate startDate, LocalDate endDate) {
        Sheet sheet = workbook.createSheet("运动记录");

        // 创建标题行
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(String.format("%s的运动记录 (%s 至 %s)",
                studentName,
                startDate.format(DATE_FORMATTER),
                endDate.format(DATE_FORMATTER)));
        titleCell.setCellStyle(styles.get("title"));

        // 创建表头
        Row headerRow = sheet.createRow(2);
        String[] headers = {"记录日期", "运动类型", "运动时长(分钟)", "消耗热量(千卡)", "运动强度", "运动距离(公里)", "备注"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(styles.get("header"));
        }

        // 查询数据
        QueryWrapper<ExerciseRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId)
                .ge("record_date", startDate.format(DATE_FORMATTER))
                .le("record_date", endDate.format(DATE_FORMATTER))
                .orderByAsc("record_date");
        List<ExerciseRecord> records = exerciseRecordMapper.selectList(wrapper);

        // 填充数据
        int rowNum = 3;
        Map<Integer, String> intensityMap = getIntensityMap();
        for (ExerciseRecord record : records) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(record.getRecordDate().format(DATE_FORMATTER));
            row.createCell(1).setCellValue(record.getExerciseType() != null ? record.getExerciseType() : "");
            row.createCell(2).setCellValue(record.getDuration() != null ? record.getDuration() : 0);
            row.createCell(3).setCellValue(record.getCaloriesBurned() != null ? record.getCaloriesBurned().doubleValue() : 0.0);
            row.createCell(4).setCellValue(intensityMap.getOrDefault(record.getIntensity(), "未知"));
            row.createCell(5).setCellValue(record.getDistance() != null ? record.getDistance().doubleValue() : 0.0);
            row.createCell(6).setCellValue(record.getDescription() != null ? record.getDescription() : "");
        }

        // 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * 导出睡眠记录
     */
    private void exportSleepRecords(Workbook workbook, Map<String, CellStyle> styles,
                                    Long studentId, String studentName, LocalDate startDate, LocalDate endDate) {
        Sheet sheet = workbook.createSheet("睡眠记录");

        // 创建标题行
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(String.format("%s的睡眠记录 (%s 至 %s)",
                studentName,
                startDate.format(DATE_FORMATTER),
                endDate.format(DATE_FORMATTER)));
        titleCell.setCellStyle(styles.get("title"));

        // 创建表头
        Row headerRow = sheet.createRow(2);
        String[] headers = {"记录日期", "入睡时间", "起床时间", "睡眠时长(小时)", "睡眠质量(1-5分)", "备注"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(styles.get("header"));
        }

        // 查询数据
        QueryWrapper<SleepRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId)
                .ge("record_date", startDate.format(DATE_FORMATTER))
                .le("record_date", endDate.format(DATE_FORMATTER))
                .orderByAsc("record_date");
        List<SleepRecord> records = sleepRecordMapper.selectList(wrapper);

        // 填充数据
        int rowNum = 3;
        for (SleepRecord record : records) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(record.getRecordDate().format(DATE_FORMATTER));
            row.createCell(1).setCellValue(record.getSleepTime() != null ? record.getSleepTime().toString() : "");            row.createCell(2).setCellValue(record.getWakeTime() != null ? record.getWakeTime().toString() : "");
            row.createCell(3).setCellValue(record.getDuration() != null ? record.getDuration().doubleValue() : 0.0);
            row.createCell(4).setCellValue(record.getQuality() != null ? record.getQuality() : 0);
            row.createCell(5).setCellValue(record.getDescription() != null ? record.getDescription() : "");
        }

        // 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * 导出情绪记录
     */
    private void exportMoodRecords(Workbook workbook, Map<String, CellStyle> styles,
                                   Long studentId, String studentName, LocalDate startDate, LocalDate endDate) {
        Sheet sheet = workbook.createSheet("情绪记录");

        // 创建标题行
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(String.format("%s的情绪记录 (%s 至 %s)",
                studentName,
                startDate.format(DATE_FORMATTER),
                endDate.format(DATE_FORMATTER)));
        titleCell.setCellStyle(styles.get("title"));

        // 创建表头
        Row headerRow = sheet.createRow(2);
        String[] headers = {"记录日期", "情绪类型", "备注"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(styles.get("header"));
        }

        // 查询数据
        QueryWrapper<MoodRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId)
                .ge("record_date", startDate.format(DATE_FORMATTER))
                .le("record_date", endDate.format(DATE_FORMATTER))
                .orderByAsc("record_date");
        List<MoodRecord> records = moodRecordMapper.selectList(wrapper);

        // 填充数据
        int rowNum = 3;
        Map<String, String> moodTypeMap = getMoodTypeMap();
        for (MoodRecord record : records) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(record.getRecordDate().format(DATE_FORMATTER));
            String moodTypeKey = String.valueOf(record.getMoodType());
            row.createCell(1).setCellValue(moodTypeMap.getOrDefault(moodTypeKey, moodTypeKey));
            row.createCell(2).setCellValue(record.getDescription() != null ? record.getDescription() : "");
        }

        // 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * 创建样式
     */
    private Map<String, CellStyle> createStyles(Workbook workbook) {
        Map<String, CellStyle> styles = new HashMap<>();

        // 标题样式
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        styles.put("title", titleStyle);

        // 表头样式
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        styles.put("header", headerStyle);

        return styles;
    }

    /**
     * 餐次映射
     */
    private Map<Integer, String> getMealTypeMap() {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "早餐");
        map.put(2, "午餐");
        map.put(3, "晚餐");
        map.put(4, "加餐");
        return map;
    }

    /**
     * 运动强度映射
     */
    private Map<Integer, String> getIntensityMap() {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "低强度");
        map.put(2, "中强度");
        map.put(3, "高强度");
        return map;
    }

    /**
     * 情绪类型映射
     */
    private Map<String, String> getMoodTypeMap() {
        Map<String, String> map = new HashMap<>();
        map.put("1", "开心");
        map.put("2", "平静");
        map.put("3", "焦虑");
        map.put("4", "悲伤");
        map.put("5", "愤怒");
        map.put("6", "压力");
        return map;
    }
}
