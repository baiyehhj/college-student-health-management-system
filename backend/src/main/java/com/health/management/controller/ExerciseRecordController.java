package com.health.management.controller;

import com.health.management.common.Result;
import com.health.management.dto.ExerciseRecordRequest;
import com.health.management.service.ExerciseRecordService;
import com.health.management.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;

@RestController
@RequestMapping("/exercise")
@CrossOrigin
public class ExerciseRecordController {
    
    @Autowired
    private ExerciseRecordService exerciseRecordService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/add")
    public Result addRecord(@Valid @RequestBody ExerciseRecordRequest request,
                           @RequestHeader("Authorization") String token) {
        Long studentId = getStudentIdFromToken(token);
        return exerciseRecordService.addRecord(studentId, request);
    }
    
    @PutMapping("/update/{id}")
    public Result updateRecord(@PathVariable Long id,
                              @Valid @RequestBody ExerciseRecordRequest request,
                              @RequestHeader("Authorization") String token) {
        Long studentId = getStudentIdFromToken(token);
        return exerciseRecordService.updateRecord(id, studentId, request);
    }
    
    @DeleteMapping("/delete/{id}")
    public Result deleteRecord(@PathVariable Long id,
                              @RequestHeader("Authorization") String token) {
        Long studentId = getStudentIdFromToken(token);
        return exerciseRecordService.deleteRecord(id, studentId);
    }
    
    @GetMapping("/list")
    public Result listRecords(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer size,
                             @RequestHeader("Authorization") String token) {
        Long studentId = getStudentIdFromToken(token);
        return exerciseRecordService.listRecords(studentId, startDate, endDate, page, size);
    }
    
    @GetMapping("/daily-stats")
    public Result getDailyStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                @RequestHeader("Authorization") String token) {
        Long studentId = getStudentIdFromToken(token);
        return exerciseRecordService.getDailyStats(studentId, date);
    }
    
    private Long getStudentIdFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtUtil.getStudentIdFromToken(token);
    }
}
