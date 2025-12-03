package com.health.management.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SleepRecordRequest {
    @NotNull(message = "记录日期不能为空")
    private LocalDate recordDate;
    @NotNull(message = "入睡时间不能为空")
    private LocalDateTime sleepTime;
    @NotNull(message = "起床时间不能为空")
    private LocalDateTime wakeTime;
    private BigDecimal duration;
    private Integer quality;
    private BigDecimal deepSleepDuration;
    private Integer dreamCount;
    private String description;
}
