package com.health.management.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Excel导入结果DTO
 */
public class ImportResultResponse {
    
    private Integer total;           // 总记录数
    private Integer success;         // 成功数
    private Integer failed;          // 失败数
    private List<String> errors;     // 错误信息列表
    
    public ImportResultResponse() {
        this.total = 0;
        this.success = 0;
        this.failed = 0;
        this.errors = new ArrayList<>();
    }
    
    public void addSuccess() {
        this.total++;
        this.success++;
    }
    
    public void addFailed(String error) {
        this.total++;
        this.failed++;
        this.errors.add(error);
    }
    
    // Getters and Setters
    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public Integer getFailed() {
        return failed;
    }

    public void setFailed(Integer failed) {
        this.failed = failed;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
