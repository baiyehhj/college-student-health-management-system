package com.health.management.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel工具类
 */
public class ExcelUtil {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * 读取Excel文件
     * @param file 上传的文件
     * @return 数据列表
     */
    public static List<List<Object>> readExcel(MultipartFile file) throws IOException {
        List<List<Object>> dataList = new ArrayList<>();
        
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            
            // 从第二行开始读取（第一行是表头）
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                List<Object> rowData = new ArrayList<>();
                for (int j = 0; j < row.getLastCellNum(); j++) {
                    Cell cell = row.getCell(j);
                    rowData.add(getCellValue(cell));
                }
                
                // 跳过空行
                if (rowData.stream().allMatch(obj -> obj == null || obj.toString().trim().isEmpty())) {
                    continue;
                }
                
                dataList.add(rowData);
            }
        }
        
        return dataList;
    }
    
    /**
     * 创建体检报告Excel模板
     */
    public static byte[] createHealthExamTemplate() throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("体检报告");
            
            // 创建表头样式
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            
            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                "体检日期*", "体检类型*", "身高(cm)*", "体重(kg)*", 
                "收缩压", "舒张压", "心率", 
                "左眼视力", "右眼视力", "血糖", 
                "血红蛋白", "白细胞", "血小板",
                "总体结论", "医生建议"
            };
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 4000);
            }
            
            // 创建示例数据行
            Row exampleRow = sheet.createRow(1);
            Object[] exampleData = {
                "2025-11-19", "入学体检", 175, 65.5,
                120, 80, 72,
                5.0, 5.0, 5.2,
                150, 6.5, 250,
                "健康", "保持良好作息"
            };
            
            for (int i = 0; i < exampleData.length; i++) {
                Cell cell = exampleRow.createCell(i);
                if (exampleData[i] instanceof Number) {
                    cell.setCellValue(((Number) exampleData[i]).doubleValue());
                } else {
                    cell.setCellValue(exampleData[i].toString());
                }
            }
            
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
    
    /**
     * 获取单元格值
     */
    private static Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().format(DATE_FORMATTER);
                } else {
                    return cell.getNumericCellValue();
                }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return null;
            default:
                return null;
        }
    }
    
    /**
     * 解析日期
     */
    public static LocalDate parseDate(Object value) {
        if (value == null) return null;
        
        String dateStr = value.toString().trim();
        if (dateStr.isEmpty()) return null;
        
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 解析BigDecimal
     */
    public static BigDecimal parseBigDecimal(Object value) {
        if (value == null) return null;
        
        try {
            if (value instanceof Number) {
                return BigDecimal.valueOf(((Number) value).doubleValue());
            } else {
                String str = value.toString().trim();
                return str.isEmpty() ? null : new BigDecimal(str);
            }
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 解析Integer
     */
    public static Integer parseInteger(Object value) {
        if (value == null) return null;
        
        try {
            if (value instanceof Number) {
                return ((Number) value).intValue();
            } else {
                String str = value.toString().trim();
                return str.isEmpty() ? null : Integer.parseInt(str);
            }
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 解析String
     */
    public static String parseString(Object value) {
        if (value == null) return null;
        return value.toString().trim();
    }
}
