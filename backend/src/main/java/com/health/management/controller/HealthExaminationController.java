package com.health.management.controller;

import com.health.management.common.Result;
import com.health.management.dto.HealthExaminationRequest;
import com.health.management.entity.HealthExamination;
import com.health.management.mapper.HealthExaminationMapper;
import com.health.management.service.HealthExaminationService;
import com.health.management.util.ExcelUtil;
import com.health.management.util.JwtUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/health-exam")
@CrossOrigin
public class HealthExaminationController {
    
    @Autowired
    private HealthExaminationService healthExaminationService;
    
    @Autowired
    private HealthExaminationMapper healthExaminationMapper;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/add")
    public Result addExamination(@Valid @RequestBody HealthExaminationRequest request,
                                @RequestHeader("Authorization") String token) {
        Long studentId = getStudentIdFromToken(token);
        return healthExaminationService.addExamination(studentId, request);
    }
    
    @PutMapping("/update/{id}")
    public Result updateExamination(@PathVariable Long id,
                                   @Valid @RequestBody HealthExaminationRequest request,
                                   @RequestHeader("Authorization") String token) {
        Long studentId = getStudentIdFromToken(token);
        return healthExaminationService.updateExamination(id, studentId, request);
    }
    
    @DeleteMapping("/delete/{id}")
    public Result deleteExamination(@PathVariable Long id,
                                   @RequestHeader("Authorization") String token) {
        Long studentId = getStudentIdFromToken(token);
        return healthExaminationService.deleteExamination(id, studentId);
    }
    
    @GetMapping("/list")
    public Result listExaminations(@RequestParam(defaultValue = "1") Integer page,
                                  @RequestParam(defaultValue = "10") Integer size,
                                  @RequestHeader("Authorization") String token) {
        Long studentId = getStudentIdFromToken(token);
        return healthExaminationService.listExaminations(studentId, page, size);
    }
    
    @GetMapping("/latest")
    public Result getLatestExamination(@RequestHeader("Authorization") String token) {
        Long studentId = getStudentIdFromToken(token);
        return healthExaminationService.getLatestExamination(studentId);
    }
    
    @GetMapping("/detail/{id}")
    public Result getExaminationDetail(@PathVariable Long id,
                                      @RequestHeader("Authorization") String token) {
        Long studentId = getStudentIdFromToken(token);
        return healthExaminationService.getExaminationDetail(id, studentId);
    }
    
    /**
     * 下载Excel模板
     */
    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) {
        System.out.println("开始生成Excel模板...");
        try {
            byte[] templateBytes = ExcelUtil.createHealthExamTemplate();
            System.out.println("模板生成成功，大小: " + templateBytes.length + " 字节");
            
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            
            // 设置文件名，兼容中文
            String fileName = "体检报告导入模板.xlsx";
            String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName);
            response.setHeader("Content-Length", String.valueOf(templateBytes.length));
            
            // 禁用缓存
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
            
            // 允许跨域
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            
            try (OutputStream out = response.getOutputStream()) {
                out.write(templateBytes);
                out.flush();
            }
            System.out.println("模板下载完成");
        } catch (Exception e) {
            System.err.println("模板生成失败: " + e.getMessage());
            e.printStackTrace();
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":500,\"message\":\"模板生成失败: " + e.getMessage() + "\"}");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * 导入Excel体检报告
     */
    @PostMapping("/import")
    public Result importExcel(@RequestParam("file") MultipartFile file,
                             @RequestHeader("Authorization") String token) {
        Long studentId = getStudentIdFromToken(token);
        return healthExaminationService.importExcel(studentId, file);
    }
    
    private Long getStudentIdFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtUtil.getStudentIdFromToken(token);
    }
}
