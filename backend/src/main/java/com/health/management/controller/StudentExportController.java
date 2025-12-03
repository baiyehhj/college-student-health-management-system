package com.health.management.controller;

import com.health.management.service.StudentExportService;
import com.health.management.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 学生数据导出Controller
 */
@RestController
@RequestMapping("/student/export")
public class StudentExportController {

    @Autowired
    private StudentExportService studentExportService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 导出学生行为数据
     */
    @GetMapping("/behavior-data")
    public ResponseEntity<byte[]> exportBehaviorData(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            HttpServletRequest request) {
        try {
            // 从token获取学生ID
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            Long studentId = getStudentIdFromToken(token);

            // 导出Excel
            ByteArrayOutputStream outputStream = studentExportService.exportBehaviorData(
                    studentId, startDate, endDate);

            // 生成文件名
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String fileName = String.format("行为数据_%s_%s.xlsx",
                    startDate.format(formatter),
                    endDate.format(formatter));
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString())
                    .replace("+", "%20");

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", encodedFileName);
            headers.setContentLength(outputStream.size());

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 从Token中获取学生ID
     */
    private Long getStudentIdFromToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtUtil.getStudentIdFromToken(token);
    }
}
