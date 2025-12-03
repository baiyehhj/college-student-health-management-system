package com.health.management.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 运动记录请求DTO
 */
@Data
public class ExerciseRecordRequest {
    
    @NotNull(message = "记录日期不能为空")
    private LocalDate recordDate;
    
    @NotBlank(message = "运动类型不能为空")
    private String exerciseType;
    
    @NotNull(message = "运动时长不能为空")
    private Integer duration;
    
    private BigDecimal caloriesBurned;
    
    private Integer intensity;
    
    private BigDecimal distance;
    
    private String description;
}
