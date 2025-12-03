package com.health.management.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class HealthExaminationRequest {
    @NotNull(message = "体检日期不能为空")
    private LocalDate examDate;
    private String examType;
    private BigDecimal height;
    private BigDecimal weight;
    private Integer bloodPressureHigh;
    private Integer bloodPressureLow;
    private Integer heartRate;
    private BigDecimal visionLeft;
    private BigDecimal visionRight;
    private BigDecimal bloodSugar;
    private BigDecimal hemoglobin;
    private BigDecimal whiteBloodCell;
    private BigDecimal platelet;
    private String reportFile;
    private String overallConclusion;
    private String doctorAdvice;
}
