package com.health.management.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.health.management.dto.FoodRecognitionResponse;
import com.health.management.service.CozeAIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;

/**
 * 扣子AI服务实现类 - 食物识别
 */
@Slf4j
@Service
public class CozeAIServiceImpl implements CozeAIService {

    @Value("${coze.food.api.token}")
    private String apiKey;

    @Value("${coze.food.api.bot-id}")
    private String botId;

    @Value("${coze.food.api.url:https://api.coze.cn/v3/chat}")
    private String apiUrl;

    @Value("${coze.food.api.file-upload-url:https://api.coze.cn/v1/files/upload}")
    private String fileUploadUrl;

    private final RestTemplate restTemplate;

    public CozeAIServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * 识别食物图片并获取营养信息
     */
    @Override
    public FoodRecognitionResponse recognizeFood(MultipartFile file) {
        FoodRecognitionResponse response = new FoodRecognitionResponse();
        response.setSuccess(false);

        try {
            log.info("开始识别食物图片，文件名: {}, 大小: {} bytes",
                    file.getOriginalFilename(), file.getSize());

            // 验证配置
            validateConfig();

            // 验证文件
            validateFile(file);

            // 步骤1: 上传图片获取file_id
            String fileId = uploadImage(file);
            log.info("图片上传成功，file_id: {}", fileId);

            // 步骤2: 使用file_id调用对话API
            String aiResponse = callCozeAPIWithImage(fileId);
            log.info("AI识别完成，响应长度: {}", aiResponse != null ? aiResponse.length() : 0);

            // 步骤3: 解析食物信息
            parseFoodInfo(aiResponse, response);

            response.setSuccess(true);
            log.info("食物识别成功: {}", response.getFoodName());

        } catch (IllegalArgumentException e) {
            log.error("参数错误: {}", e.getMessage());
            response.setSuccess(false);
            response.setErrorMessage("参数错误: " + e.getMessage());
        } catch (Exception e) {
            log.error("食物识别失败", e);
            response.setSuccess(false);
            response.setErrorMessage("识别失败: " + e.getMessage());
        }

        return response;
    }

    /**
     * 验证配置
     */
    private void validateConfig() {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("扣子API Token未配置");
        }
        if (botId == null || botId.trim().isEmpty()) {
            throw new IllegalArgumentException("扣子Bot ID未配置");
        }
        if (apiUrl == null || apiUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("扣子API URL未配置");
        }
    }

    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("图片文件为空");
        }

        // 检查文件大小（限制10MB）
        long maxSize = 10 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("图片文件过大，最大支持10MB");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("文件类型必须是图片");
        }
    }

    /**
     * 上传图片到Coze并获取file_id
     */
    private String uploadImage(MultipartFile file) {
        try {
            log.info("开始上传图片到Coze");

            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.setBearerAuth(apiKey);

            // 构建multipart请求体
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            // 添加文件
            ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            body.add("file", fileResource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // 发送请求
            ResponseEntity<String> response = restTemplate.postForEntity(
                    fileUploadUrl,
                    requestEntity,
                    String.class
            );

            // 解析响应
            String responseBody = response.getBody();
            log.debug("文件上传响应: {}", responseBody);

            if (responseBody == null || responseBody.trim().isEmpty()) {
                throw new RuntimeException("文件上传返回空响应");
            }

            JSONObject jsonResponse = JSON.parseObject(responseBody);
            Integer code = jsonResponse.getInteger("code");

            if (code == null || code != 0) {
                String errorMsg = jsonResponse.getString("msg");
                throw new RuntimeException("文件上传失败: " + errorMsg);
            }

            // 获取file_id
            JSONObject data = jsonResponse.getJSONObject("data");
            if (data == null) {
                throw new RuntimeException("文件上传响应缺少data字段");
            }

            String fileId = data.getString("id");
            if (fileId == null || fileId.trim().isEmpty()) {
                throw new RuntimeException("文件上传未返回file_id");
            }

            return fileId;

        } catch (Exception e) {
            log.error("上传图片失败", e);
            throw new RuntimeException("上传图片失败: " + e.getMessage(), e);
        }
    }

    /**
     * 使用图片file_id调用Coze API
     */
    private String callCozeAPIWithImage(String fileId) {
        try {
            log.info("准备调用扣子API，使用file_id: {}", fileId);

            // 构建请求
            HttpHeaders headers = buildHeaders();
            Map<String, Object> requestBody = buildRequestBodyWithImage(fileId);

            log.debug("请求URL: {}", apiUrl);
            log.debug("请求体: {}", JSON.toJSONString(requestBody));

            // 发送请求
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // 处理响应
            return handleCozeResponse(response, headers);

        } catch (Exception e) {
            log.error("调用扣子API失败", e);
            throw new RuntimeException("调用AI服务失败: " + e.getMessage(), e);
        }
    }

    /**
     * 构建包含图片的请求体（标准格式）
     */
    private Map<String, Object> buildRequestBodyWithImage(String fileId) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("bot_id", botId);
        requestBody.put("user_id", "food_recognition_" + System.currentTimeMillis());
        requestBody.put("stream", false);
        requestBody.put("auto_save_history", true);

        // 构建消息列表
        List<Map<String, Object>> messages = new ArrayList<>();

        // 单条消息，包含多个内容块
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("type", "question");
        message.put("content_type", "object_string");

        // 构建内容对象列表
        List<Map<String, Object>> contentList = new ArrayList<>();

        // 添加图片内容
        Map<String, Object> imageContent = new HashMap<>();
        imageContent.put("type", "image");
        imageContent.put("file_id", fileId);
        contentList.add(imageContent);

        // 添加文本提示
        Map<String, Object> textContent = new HashMap<>();
        textContent.put("type", "text");
        textContent.put("text", "请识别这张图片中的食物，并返回食物名称和营养信息（JSON格式）");
        contentList.add(textContent);

        message.put("content", JSON.toJSONString(contentList));
        messages.add(message);

        requestBody.put("additional_messages", messages);

        return requestBody;
    }

    /**
     * 构建请求头
     */
    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        return headers;
    }

    /**
     * 处理Coze API响应
     */
    private String handleCozeResponse(ResponseEntity<String> response, HttpHeaders headers) {
        String responseBody = response.getBody();
        log.debug("API响应状态码: {}", response.getStatusCode());
        log.debug("API响应内容: {}", responseBody);

        if (responseBody == null || responseBody.trim().isEmpty()) {
            throw new RuntimeException("API返回空响应");
        }

        JSONObject jsonResponse = JSON.parseObject(responseBody);
        Integer code = jsonResponse.getInteger("code");

        if (code == null || code != 0) {
            String errorMsg = jsonResponse.getString("msg");
            throw new RuntimeException("API调用失败: " + errorMsg);
        }

        JSONObject data = jsonResponse.getJSONObject("data");
        if (data == null) {
            throw new RuntimeException("响应缺少data字段");
        }

        String status = data.getString("status");
        log.info("对话状态: {}", status);

        String chatId = data.getString("id");
        String conversationId = data.getString("conversation_id");

        if ("completed".equals(status)) {
            // 直接从响应提取内容
            String content = extractContentFromResponse(data);
            if (content != null && !content.trim().isEmpty()) {
                log.info("成功从响应中提取内容，长度: {}", content.length());
                return content;
            }

            // 如果响应中没有内容，尝试从message/list获取
            log.info("响应中无直接内容，尝试从message/list获取");
            content = fetchMessagesFromAPI(chatId, conversationId);
            if (content != null && !content.trim().isEmpty()) {
                return content;
            }

            throw new RuntimeException("AI未返回有效内容");

        } else if ("created".equals(status) || "in_progress".equals(status)) {
            // 需要轮询等待结果
            log.info("对话创建成功，开始轮询结果");
            return pollForCompletion(chatId, conversationId, headers);

        } else if ("failed".equals(status)) {
            JSONObject lastError = data.getJSONObject("last_error");
            String errorMsg = lastError != null ? lastError.getString("msg") : "未知错误";
            throw new RuntimeException("AI处理失败: " + errorMsg);

        } else {
            throw new RuntimeException("未知的对话状态: " + status);
        }
    }

    /**
     * 从响应数据中提取内容
     */
    private String extractContentFromResponse(JSONObject data) {
        try {
            String message = data.getString("message");
            if (message != null && !message.trim().isEmpty()) {
                log.info("从message字段提取到内容，长度: {}", message.length());
                return message;
            }

            String content = data.getString("content");
            if (content != null && !content.trim().isEmpty()) {
                log.info("从content字段提取到内容，长度: {}", content.length());
                return content;
            }

            log.debug("响应数据中未找到message或content字段");
            return null;

        } catch (Exception e) {
            log.warn("提取响应内容失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从message/list接口获取消息
     */
    private String fetchMessagesFromAPI(String chatId, String conversationId) {
        try {
            String messageListUrl = apiUrl.replace("/v3/chat", "/v3/chat/message/list");
            String queryUrl = String.format("%s?chat_id=%s&conversation_id=%s",
                    messageListUrl, chatId, conversationId);

            log.info("尝试从message/list获取内容: {}", queryUrl);

            HttpHeaders headers = buildHeaders();
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    queryUrl,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            String responseBody = response.getBody();
            log.debug("message/list响应: {}", responseBody);

            if (responseBody == null || responseBody.trim().isEmpty()) {
                log.warn("message/list返回空响应");
                return null;
            }

            JSONObject jsonResponse = JSON.parseObject(responseBody);
            Integer code = jsonResponse.getInteger("code");

            if (code == null || code != 0) {
                String errorMsg = jsonResponse.getString("msg");
                log.error("message/list调用失败: code={}, msg={}", code, errorMsg);
                return null;
            }

            JSONArray messages = jsonResponse.getJSONArray("data");
            if (messages == null || messages.isEmpty()) {
                log.warn("message/list返回空消息列表");
                return null;
            }

            // 从消息列表中提取助手的回答
            for (int i = messages.size() - 1; i >= 0; i--) {
                JSONObject msg = messages.getJSONObject(i);
                String role = msg.getString("role");
                String type = msg.getString("type");

                if ("assistant".equals(role) && "answer".equals(type)) {
                    String content = msg.getString("content");
                    if (content != null && !content.trim().isEmpty()) {
                        log.info("从message/list提取到内容，长度: {}", content.length());
                        return content;
                    }
                }
            }

            log.warn("message/list中未找到助手的回答");
            return null;

        } catch (Exception e) {
            log.error("调用message/list接口失败", e);
            return null;
        }
    }

    /**
     * 轮询等待对话完成
     */
    private String pollForCompletion(String chatId, String conversationId, HttpHeaders headers) {
        int maxAttempts = 20;
        int pollInterval = 2000;
        String retrieveUrl = apiUrl.replace("/v3/chat", "/v3/chat/retrieve");

        log.info("开始轮询对话结果 - chatId: {}, conversationId: {}", chatId, conversationId);

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                Thread.sleep(pollInterval);

                String queryUrl = String.format("%s?chat_id=%s&conversation_id=%s",
                        retrieveUrl, chatId, conversationId);

                log.debug("第 {}/{} 次轮询: {}", attempt, maxAttempts, queryUrl);

                HttpEntity<Void> entity = new HttpEntity<>(headers);
                ResponseEntity<String> response = restTemplate.exchange(
                        queryUrl,
                        HttpMethod.GET,
                        entity,
                        String.class
                );

                JSONObject jsonResponse = JSON.parseObject(response.getBody());
                Integer code = jsonResponse.getInteger("code");

                if (code == null || code != 0) {
                    log.warn("轮询返回异常code: {}", code);
                    continue;
                }

                JSONObject data = jsonResponse.getJSONObject("data");
                if (data == null) {
                    log.warn("轮询响应缺少data");
                    continue;
                }

                String status = data.getString("status");
                log.info("第{}次轮询 - 状态: {}", attempt, status);

                if ("completed".equals(status)) {
                    String content = fetchMessagesFromAPI(chatId, conversationId);
                    if (content != null && !content.trim().isEmpty()) {
                        log.info("轮询成功获取到内容，长度: {}", content.length());
                        return content;
                    }
                    log.warn("状态为completed但内容为空，继续尝试");
                } else if ("failed".equals(status)) {
                    JSONObject lastError = data.getJSONObject("last_error");
                    String errorMsg = lastError != null ? lastError.getString("msg") : "未知错误";
                    throw new RuntimeException("AI处理失败: " + errorMsg);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("轮询被中断");
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                log.warn("轮询第{}次出错: {}", attempt, e.getMessage());
            }
        }

        throw new RuntimeException("轮询超时：AI响应时间过长，请稍后重试");
    }

    /**
     * 解析食物信息
     */
    private void parseFoodInfo(String aiResponse, FoodRecognitionResponse response) {
        try {
            log.info("开始解析AI响应，原始长度: {}", aiResponse.length());

            // 提取JSON部分
            String jsonStr = extractJSON(aiResponse);
            log.debug("提取的JSON: {}", jsonStr);

            if (jsonStr == null || jsonStr.isEmpty()) {
                throw new RuntimeException("AI响应中未找到有效的JSON数据");
            }

            // 解析JSON
            JSONObject foodInfo = JSON.parseObject(jsonStr);

            // 设置食物名称
            String foodName = foodInfo.getString("foodName");
            response.setFoodName(foodName != null ? foodName : "未知食物");

            // 设置食物类别
            String category = foodInfo.getString("foodCategory");
            response.setFoodCategory(validateCategory(category));

            // 设置营养信息
            response.setCalories(getBigDecimalOrDefault(foodInfo, "calories", new BigDecimal("100")));
            response.setProtein(getBigDecimalOrDefault(foodInfo, "protein", new BigDecimal("5")));
            response.setCarbs(getBigDecimalOrDefault(foodInfo, "carbs", new BigDecimal("15")));
            response.setFat(getBigDecimalOrDefault(foodInfo, "fat", new BigDecimal("3")));

            // 设置置信度
            Double confidence = foodInfo.getDouble("confidence");
            response.setConfidence(confidence != null ? confidence : 0.8);

            // 设置描述
            String description = foodInfo.getString("description");
            response.setDescription(description != null ? description : "AI识别结果");

            log.info("食物信息解析成功");

        } catch (Exception e) {
            log.error("解析AI响应失败: {}", aiResponse, e);

            // 如果解析失败，使用默认值
            response.setFoodName("未知食物");
            response.setFoodCategory("其他");
            response.setCalories(new BigDecimal("100"));
            response.setProtein(new BigDecimal("5"));
            response.setCarbs(new BigDecimal("15"));
            response.setFat(new BigDecimal("3"));
            response.setConfidence(0.3);
            response.setDescription("AI识别结果不确定，请手动调整");
        }
    }

    /**
     * 从文本中提取JSON
     */
    private String extractJSON(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }

        // 移除markdown代码块标记
        String cleaned = text
                .replaceAll("```json\\s*", "")
                .replaceAll("```\\s*", "")
                .trim();

        // 查找JSON对象
        int start = cleaned.indexOf('{');
        int end = cleaned.lastIndexOf('}');

        if (start >= 0 && end > start) {
            return cleaned.substring(start, end + 1);
        }

        return cleaned;
    }

    /**
     * 安全获取BigDecimal值
     */
    private BigDecimal getBigDecimalOrDefault(JSONObject json, String key, BigDecimal defaultValue) {
        try {
            BigDecimal value = json.getBigDecimal(key);
            return value != null ? value : defaultValue;
        } catch (Exception e) {
            log.warn("获取{}失败，使用默认值: {}", key, defaultValue);
            return defaultValue;
        }
    }

    /**
     * 验证食物类别
     */
    private String validateCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return "其他";
        }

        String[] validCategories = {"主食", "蔬菜", "肉类", "水果", "零食", "饮料", "其他"};
        for (String valid : validCategories) {
            if (category.contains(valid)) {
                return valid;
            }
        }

        return "其他";
    }
}