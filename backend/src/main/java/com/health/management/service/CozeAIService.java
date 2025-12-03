package com.health.management.service;

import com.health.management.dto.FoodRecognitionResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * 扣子AI服务接口 - 食物识别
 */
public interface CozeAIService {

    /**
     * 识别食物图片并获取营养信息
     * 
     * @param file 食物图片文件
     * @return 食物识别结果
     */
    FoodRecognitionResponse recognizeFood(MultipartFile file);
}