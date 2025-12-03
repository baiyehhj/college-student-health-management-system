package com.health.management.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 食物识别响应DTO
 */
@Data
public class FoodRecognitionResponse {
    
    /**
     * 食物名称
     */
    private String foodName;
    
    /**
     * 食物类别
     */
    private String foodCategory;
    
    /**
     * 热量(千卡)
     */
    private BigDecimal calories;
    
    /**
     * 蛋白质(克)
     */
    private BigDecimal protein;
    
    /**
     * 碳水化合物(克)
     */
    private BigDecimal carbs;
    
    /**
     * 脂肪(克)
     */
    private BigDecimal fat;
    
    /**
     * 识别置信度
     */
    private Double confidence;
    
    /**
     * AI分析描述
     */
    private String description;
    
    /**
     * 是否识别成功
     */
    private Boolean success;
    
    /**
     * 错误信息
     */
    private String errorMessage;
}
