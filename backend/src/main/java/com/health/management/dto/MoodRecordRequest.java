package com.health.management.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MoodRecordRequest {
    @NotNull(message = "记录日期不能为空")
    private LocalDate recordDate;
    @NotNull(message = "记录时间不能为空")
    private LocalDateTime recordTime;
    @NotNull(message = "情绪类型不能为空")
    private Integer moodType;
    private Integer moodScore;
    private String triggerEvent;
    private String description;
}
