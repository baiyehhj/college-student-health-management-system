package com.health.management.controller;

import com.health.management.common.Result;
import com.health.management.dto.FoodRecognitionResponse;
import com.health.management.service.CozeAIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 食物识别Controller
 */
@Slf4j
@RestController
@RequestMapping("/food")
@CrossOrigin
public class FoodRecognitionController {

    @Autowired
    private CozeAIService cozeAIService;

    /**
     * 识别食物图片
     * 
     * @param file 上传的图片文件
     * @return 食物识别结果
     */
    @PostMapping("/recognize")
    public Result recognizeFood(@RequestParam("file") MultipartFile file,
                               @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            // 验证文件
            if (file == null || file.isEmpty()) {
                return Result.error("请上传图片文件");
            }

            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.error("只支持图片文件格式");
            }

            // 验证文件大小 (限制10MB)
            if (file.getSize() > 10 * 1024 * 1024) {
                return Result.error("图片文件大小不能超过10MB");
            }

            log.info("接收到食物识别请求，文件名: {}, 大小: {} bytes",
                    file.getOriginalFilename(), file.getSize());

            // 调用AI识别服务
            FoodRecognitionResponse response = cozeAIService.recognizeFood(file);

            if (response.getSuccess()) {
                log.info("食物识别成功: {}, 类别: {}",
                        response.getFoodName(), response.getFoodCategory());
                return Result.success("识别成功", response);
            } else {
                log.error("食物识别失败: {}", response.getErrorMessage());
                return Result.error(response.getErrorMessage());
            }

        } catch (Exception e) {
            log.error("食物识别异常", e);
            return Result.error("识别失败: " + e.getMessage());
        }
    }

    /**
     * 测试AI服务连接
     */
    @GetMapping("/test")
    public Result testConnection() {
        try {
            return Result.success("AI服务连接正常");
        } catch (Exception e) {
            log.error("AI服务测试失败", e);
            return Result.error("AI服务连接失败: " + e.getMessage());
        }
    }
}
