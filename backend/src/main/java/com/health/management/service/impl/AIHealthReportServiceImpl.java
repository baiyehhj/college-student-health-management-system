package com.health.management.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.health.management.common.Result;
import com.health.management.entity.*;
import com.health.management.mapper.*;
import com.health.management.service.AIHealthReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * AI健康报告服务实现类
 * 基于官方Coze API最佳实践
 */
@Slf4j
@Service
public class AIHealthReportServiceImpl implements AIHealthReportService {

    @Value("${coze.api.url:https://api.coze.cn/v3/chat}")
    private String cozeApiUrl;

    @Value("${coze.api.token}")
    private String cozeToken;

    @Value("${coze.api.bot-id}")
    private String botId;

    @Autowired
    private AIHealthReportMapper reportMapper;

    @Autowired
    private DietRecordMapper dietRecordMapper;

    @Autowired
    private ExerciseRecordMapper exerciseRecordMapper;

    @Autowired
    private SleepRecordMapper sleepRecordMapper;

    @Autowired
    private MoodRecordMapper moodRecordMapper;

    @Autowired
    private StudentUserMapper studentUserMapper;

    private RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    @Override
    public Result generateReport(Long studentId, LocalDate startDate, LocalDate endDate) {

        try {
            log.info("开始生成AI健康报告 - 学生ID: {}, 周期: {} 至 {}", studentId, startDate, endDate);

            // 1. 收集用户数据
            Map<String, Object> healthData = collectHealthData(studentId, startDate, endDate);

            // 检查是否有足够的数据
            if (!hasEnoughData(healthData)) {
                log.warn("健康数据不足，无法生成报告");
                return Result.error("数据不足，请先记录更多健康数据");
            }

            // 2. 构建AI提示词
            String prompt = buildPrompt(healthData, startDate, endDate);
            log.debug("AI提示词: {}", prompt);

            // 3. 调用Coze API
            String aiAnalysis = callCozeAPIImproved(prompt);
            log.info("AI分析完成，响应长度: {}", aiAnalysis != null ? aiAnalysis.length() : 0);

            // 4. 解析AI响应并保存报告
            AIHealthReport report = parseAndSaveReport(studentId, aiAnalysis, startDate, endDate);

            log.info("AI健康报告生成成功 - 报告ID: {}", report.getId());
            return Result.success("报告生成成功", report);

        } catch (IllegalArgumentException e) {
            log.error("参数错误: {}", e.getMessage());
            return Result.error("参数错误: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("生成AI健康报告失败", e);
            return Result.error("生成报告失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("生成AI健康报告时发生未知错误", e);
            return Result.error("系统错误，请稍后重试");
        }
    }

    /**
     * Coze API调用方法
     * 基于官方SDK的实践优化
     */
    private String callCozeAPIImproved(String prompt) {
        try {
            log.info("准备调用Coze API (改进版)");

            // 参数验证
            validateCozeConfig();

            // 构建请求
            HttpHeaders headers = buildHeaders();
            Map<String, Object> requestBody = buildRequestBody(prompt);

            log.debug("请求URL: {}", cozeApiUrl);
            log.debug("请求体: {}", JSON.toJSONString(requestBody));

            // 发送请求
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    cozeApiUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // 处理响应
            return handleCozeResponse(response, headers);

        } catch (Exception e) {
            log.error("调用Coze API失败", e);
            throw new RuntimeException("调用AI服务失败: " + e.getMessage(), e);
        }
    }

    /**
     * 验证Coze配置
     */
    private void validateCozeConfig() {
        if (cozeApiUrl == null || cozeApiUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Coze API URL未配置");
        }
        if (cozeToken == null || cozeToken.trim().isEmpty()) {
            throw new IllegalArgumentException("Coze API Token未配置");
        }
        if (botId == null || botId.trim().isEmpty()) {
            throw new IllegalArgumentException("Coze Bot ID未配置");
        }
    }

    /**
     * 构建请求头
     */
    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(cozeToken);
        headers.set("Accept", "application/json");
        return headers;
    }

    /**
     * 构建请求体
     */
    private Map<String, Object> buildRequestBody(String prompt) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("bot_id", botId);
        requestBody.put("user_id", "health_system_" + System.currentTimeMillis());
        requestBody.put("stream", false);
        requestBody.put("auto_save_history", true);

        // 构建消息
        List<Map<String, Object>> messages = new ArrayList<>();
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);
        message.put("content_type", "text");

        messages.add(message);
        requestBody.put("additional_messages", messages);

        return requestBody;
    }

    /**
     * 处理Coze响应
     */
    private String handleCozeResponse(ResponseEntity<String> response, HttpHeaders headers) {
        String responseBody = response.getBody();
        log.debug("API原始响应: {}", responseBody);

        if (responseBody == null || responseBody.trim().isEmpty()) {
            throw new RuntimeException("API返回空响应");
        }

        JSONObject jsonResponse = JSON.parseObject(responseBody);

        // 检查响应码
        Integer code = jsonResponse.getInteger("code");
        if (code == null) {
            // 有些版本的API可能直接返回data
            if (jsonResponse.containsKey("data")) {
                return processResponseData(jsonResponse.getJSONObject("data"), headers);
            }
            throw new RuntimeException("API响应格式异常：缺少code字段");
        }

        if (code != 0) {
            String errorMsg = jsonResponse.getString("msg");
            log.error("API返回错误: code={}, msg={}", code, errorMsg);
            throw new RuntimeException("API调用失败: " + errorMsg);
        }

        // 处理data字段
        JSONObject data = jsonResponse.getJSONObject("data");
        if (data == null) {
            throw new RuntimeException("API响应缺少data字段");
        }

        return processResponseData(data, headers);
    }

    /**
     * 处理响应数据
     */
    private String processResponseData(JSONObject data, HttpHeaders headers) {
        String status = data.getString("status");
        log.info("对话状态: {}", status);

        // 根据状态处理
        if ("completed".equals(status)) {
            // 直接完成，提取内容
            String content = extractContentFromData(data);
            if (content != null && !content.trim().isEmpty()) {
                return content;
            }
            throw new RuntimeException("对话已完成但未获取到内容");
        } else if ("in_progress".equals(status) || "created".equals(status)) {
            // 需要轮询
            String chatId = data.getString("id");
            String conversationId = data.getString("conversation_id");
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
     * 从data中提取内容
     */
    private String extractContentFromData(JSONObject data) {
        // 方法1: 从messages数组提取（初始响应可能包含）
        JSONArray messages = data.getJSONArray("messages");
        if (messages != null && !messages.isEmpty()) {
            for (int i = messages.size() - 1; i >= 0; i--) {
                JSONObject msg = messages.getJSONObject(i);
                String role = msg.getString("role");
                String type = msg.getString("type");

                // 查找助手的回答
                if ("assistant".equals(role) && "answer".equals(type)) {
                    String content = msg.getString("content");
                    if (content != null && !content.trim().isEmpty()) {
                        log.info("从messages数组提取到内容");
                        return content;
                    }
                }
            }
        }

        // 方法2: 如果没有messages，需要调用message/list接口
        String chatId = data.getString("id");
        String conversationId = data.getString("conversation_id");
        if (chatId != null && conversationId != null) {
            log.info("数据中无messages，尝试调用message/list接口");
            return fetchMessagesFromAPI(chatId, conversationId);
        }

        // 方法3: 尝试其他可能的字段
        String content = data.getString("content");
        if (content != null && !content.trim().isEmpty()) {
            log.info("从content字段提取到内容");
            return content;
        }

        content = data.getString("answer");
        if (content != null && !content.trim().isEmpty()) {
            log.info("从answer字段提取到内容");
            return content;
        }

        log.warn("未能从data中提取到内容，data keys: {}", data.keySet());
        return null;
    }

    /**
     * 调用message/list接口获取消息
     */
    private String fetchMessagesFromAPI(String chatId, String conversationId) {
        try {
            String messageListUrl = cozeApiUrl.replace("/v3/chat", "/v3/chat/message/list");
            String queryUrl = String.format("%s?chat_id=%s&conversation_id=%s",
                    messageListUrl, chatId, conversationId);

            log.debug("调用message/list接口: {}", queryUrl);

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
        int maxAttempts = 30;  // 轮询次数
        int pollInterval = 2000;  // 2秒轮询一次
        String retrieveUrl = cozeApiUrl.replace("/v3/chat", "/v3/chat/retrieve");

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
                    // 状态完成后，调用message/list获取内容
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
     * 检查是否有足够的数据生成报告
     */
    private boolean hasEnoughData(Map<String, Object> healthData) {
        List<DietRecord> dietRecords = (List<DietRecord>) healthData.get("dietRecords");
        List<ExerciseRecord> exerciseRecords = (List<ExerciseRecord>) healthData.get("exerciseRecords");
        List<SleepRecord> sleepRecords = (List<SleepRecord>) healthData.get("sleepRecords");

        return (dietRecords != null && !dietRecords.isEmpty()) ||
                (exerciseRecords != null && !exerciseRecords.isEmpty()) ||
                (sleepRecords != null && !sleepRecords.isEmpty());
    }

    /**
     * 收集健康数据
     */
    private Map<String, Object> collectHealthData(Long studentId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> data = new HashMap<>();

        try {
            StudentUser student = studentUserMapper.selectById(studentId);
            if (student == null) {
                throw new IllegalArgumentException("学生信息不存在");
            }
            data.put("studentInfo", student);

            // 饮食数据
            QueryWrapper<DietRecord> dietWrapper = new QueryWrapper<>();
            dietWrapper.eq("student_id", studentId)
                    .between("record_date", startDate, endDate)
                    .orderByAsc("record_date");
            List<DietRecord> dietRecords = dietRecordMapper.selectList(dietWrapper);
            data.put("dietRecords", dietRecords);
            data.put("dietStats", calculateDietStats(dietRecords));

            // 运动数据
            QueryWrapper<ExerciseRecord> exerciseWrapper = new QueryWrapper<>();
            exerciseWrapper.eq("student_id", studentId)
                    .between("record_date", startDate, endDate)
                    .orderByAsc("record_date");
            List<ExerciseRecord> exerciseRecords = exerciseRecordMapper.selectList(exerciseWrapper);
            data.put("exerciseRecords", exerciseRecords);
            data.put("exerciseStats", calculateExerciseStats(exerciseRecords));

            // 睡眠数据
            QueryWrapper<SleepRecord> sleepWrapper = new QueryWrapper<>();
            sleepWrapper.eq("student_id", studentId)
                    .between("record_date", startDate, endDate)
                    .orderByAsc("record_date");
            List<SleepRecord> sleepRecords = sleepRecordMapper.selectList(sleepWrapper);
            data.put("sleepRecords", sleepRecords);
            data.put("sleepStats", calculateSleepStats(sleepRecords));

            // 情绪数据
            QueryWrapper<MoodRecord> moodWrapper = new QueryWrapper<>();
            moodWrapper.eq("student_id", studentId)
                    .between("record_date", startDate, endDate)
                    .orderByAsc("record_date");
            List<MoodRecord> moodRecords = moodRecordMapper.selectList(moodWrapper);
            data.put("moodRecords", moodRecords);
            data.put("moodStats", calculateMoodStats(moodRecords));

            log.info("健康数据收集完成 - 饮食: {}, 运动: {}, 睡眠: {}, 情绪: {}",
                    dietRecords.size(), exerciseRecords.size(),
                    sleepRecords.size(), moodRecords.size());

        } catch (Exception e) {
            log.error("收集健康数据失败", e);
            throw new RuntimeException("数据收集失败: " + e.getMessage());
        }

        return data;
    }

    /**
     * 构建AI提示词
     */
    private String buildPrompt(Map<String, Object> healthData, LocalDate startDate, LocalDate endDate) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("请作为专业的健康顾问，分析以下大学生的健康数据并根据健康数据生成详细的个性化健康报告，内容包括：综合健康评分、饮食分析及建议、运动分析及建议、睡眠分析及建议、情绪分析及建议、健康风险提示、综合改善建议等\n\n");
        prompt.append("分析周期: ").append(startDate).append(" 至 ").append(endDate).append("\n\n");

        // 饮食数据
        Map<String, Object> dietStats = (Map<String, Object>) healthData.get("dietStats");
        prompt.append("【饮食数据】\n");
        prompt.append("- 平均每日摄入热量: ").append(dietStats.get("avgCalories")).append(" 千卡\n");
        prompt.append("- 平均蛋白质: ").append(dietStats.get("avgProtein")).append(" 克\n");
        prompt.append("- 平均碳水化合物: ").append(dietStats.get("avgCarbs")).append(" 克\n");
        prompt.append("- 平均脂肪: ").append(dietStats.get("avgFat")).append(" 克\n\n");

        // 运动数据
        Map<String, Object> exerciseStats = (Map<String, Object>) healthData.get("exerciseStats");
        prompt.append("【运动数据】\n");
        prompt.append("- 运动次数: ").append(exerciseStats.get("totalDays")).append(" 天\n");
        prompt.append("- 总运动时长: ").append(exerciseStats.get("totalDuration")).append(" 分钟\n");
        prompt.append("- 平均每日运动: ").append(exerciseStats.get("avgDuration")).append(" 分钟\n");
        prompt.append("- 消耗热量: ").append(exerciseStats.get("totalCalories")).append(" 千卡\n\n");

        // 睡眠数据
        Map<String, Object> sleepStats = (Map<String, Object>) healthData.get("sleepStats");
        prompt.append("【睡眠数据】\n");
        prompt.append("- 平均睡眠时长: ").append(sleepStats.get("avgDuration")).append(" 小时\n");
        prompt.append("- 平均睡眠质量: ").append(sleepStats.get("avgQuality")).append(" 分\n");
        prompt.append("- 优质睡眠占比: ").append(sleepStats.get("goodSleepRate")).append("%\n\n");

        // 情绪数据
        Map<String, Object> moodStats = (Map<String, Object>) healthData.get("moodStats");
        prompt.append("【情绪数据】\n");
        prompt.append("- 平均情绪评分: ").append(moodStats.get("avgScore")).append(" 分\n");
        prompt.append("- 积极情绪占比: ").append(moodStats.get("positiveRate")).append("%\n");
        prompt.append("- 主要情绪类型: ").append(moodStats.get("mainMoodType")).append("\n\n");

        prompt.append("请提供以下内容（必须严格按照JSON格式返回，不要包含任何markdown标记或其他文字说明）:\n");
        prompt.append("{\n");
        prompt.append("  \"overallScore\": 综合健康评分(0-100的数字),\n");
        prompt.append("  \"dietAnalysis\": \"饮食分析及建议(字符串)\",\n");
        prompt.append("  \"exerciseAnalysis\": \"运动分析及建议(字符串)\",\n");
        prompt.append("  \"sleepAnalysis\": \"睡眠分析及建议(字符串)\",\n");
        prompt.append("  \"moodAnalysis\": \"情绪分析及建议(字符串)\",\n");
        prompt.append("  \"healthRisks\": \"健康风险提示(字符串)\",\n");
        prompt.append("  \"recommendations\": \"综合改善建议(字符串)\"\n");
        prompt.append("}\n");

        return prompt.toString();
    }

    /**
     * 解析并保存报告
     */
    private AIHealthReport parseAndSaveReport(Long studentId, String aiAnalysis,
                                              LocalDate startDate, LocalDate endDate) {
        try {
            log.info("开始解析AI响应，原始长度: {}", aiAnalysis.length());

            // 清理响应内容
            String cleanJson = cleanJsonResponse(aiAnalysis);
            log.debug("清理后的JSON: {}", cleanJson);

            // 解析JSON
            JSONObject analysis = JSON.parseObject(cleanJson);

            // 构建报告对象
            AIHealthReport report = new AIHealthReport();
            report.setStudentId(studentId);
            report.setReportDate(LocalDate.now());
            report.setAnalysisPeriod(startDate + " ~ " + endDate);

            // 设置各项分析结果
            BigDecimal score = analysis.getBigDecimal("overallScore");
            report.setOverallScore(score != null ? score : new BigDecimal("60"));
            report.setDietAnalysis(getStringOrDefault(analysis, "dietAnalysis", "暂无分析"));
            report.setExerciseAnalysis(getStringOrDefault(analysis, "exerciseAnalysis", "暂无分析"));
            report.setSleepAnalysis(getStringOrDefault(analysis, "sleepAnalysis", "暂无分析"));
            report.setMoodAnalysis(getStringOrDefault(analysis, "moodAnalysis", "暂无分析"));
            report.setHealthRisks(getStringOrDefault(analysis, "healthRisks", "暂无风险"));
            report.setRecommendations(getStringOrDefault(analysis, "recommendations", "保持良好习惯"));
            report.setAiModelVersion("Coze-1.0");
            report.setCreateTime(LocalDateTime.now());

            // 保存到数据库
            reportMapper.insert(report);
            log.info("报告保存成功 - ID: {}", report.getId());

            return report;

        } catch (Exception e) {
            log.error("解析报告失败，原始内容: {}", aiAnalysis, e);
            throw new RuntimeException("解析报告失败: " + e.getMessage());
        }
    }

    /**
     * 清理JSON响应
     */
    private String cleanJsonResponse(String response) {
        if (response == null || response.trim().isEmpty()) {
            throw new IllegalArgumentException("响应内容为空");
        }

        // 移除markdown代码块标记
        String cleaned = response
                .replaceAll("```json\\s*", "")
                .replaceAll("```\\s*", "")
                .trim();

        // 查找JSON对象
        int jsonStart = cleaned.indexOf("{");
        int jsonEnd = cleaned.lastIndexOf("}");

        if (jsonStart == -1 || jsonEnd == -1 || jsonStart >= jsonEnd) {
            throw new IllegalArgumentException("响应中未找到有效的JSON对象");
        }

        return cleaned.substring(jsonStart, jsonEnd + 1);
    }

    /**
     * 安全获取字符串值
     */
    private String getStringOrDefault(JSONObject json, String key, String defaultValue) {
        String value = json.getString(key);
        return (value != null && !value.trim().isEmpty()) ? value : defaultValue;
    }

    // ==================== 统计计算方法 ====================

    private Map<String, Object> calculateDietStats(List<DietRecord> records) {
        Map<String, Object> stats = new HashMap<>();
        if (records == null || records.isEmpty()) {
            stats.put("avgCalories", "0.00");
            stats.put("avgProtein", "0.00");
            stats.put("avgCarbs", "0.00");
            stats.put("avgFat", "0.00");
            return stats;
        }

        double totalCal = records.stream()
                .mapToDouble(r -> r.getCalories() != null ? r.getCalories().doubleValue() : 0)
                .sum();
        double totalPro = records.stream()
                .mapToDouble(r -> r.getProtein() != null ? r.getProtein().doubleValue() : 0)
                .sum();
        double totalCarbs = records.stream()
                .mapToDouble(r -> r.getCarbs() != null ? r.getCarbs().doubleValue() : 0)
                .sum();
        double totalFat = records.stream()
                .mapToDouble(r -> r.getFat() != null ? r.getFat().doubleValue() : 0)
                .sum();

        long days = records.stream()
                .map(DietRecord::getRecordDate)
                .filter(Objects::nonNull)
                .distinct()
                .count();
        days = days > 0 ? days : 1;

        stats.put("avgCalories", String.format("%.2f", totalCal / days));
        stats.put("avgProtein", String.format("%.2f", totalPro / days));
        stats.put("avgCarbs", String.format("%.2f", totalCarbs / days));
        stats.put("avgFat", String.format("%.2f", totalFat / days));
        return stats;
    }

    private Map<String, Object> calculateExerciseStats(List<ExerciseRecord> records) {
        Map<String, Object> stats = new HashMap<>();
        if (records == null || records.isEmpty()) {
            stats.put("totalDays", 0);
            stats.put("totalDuration", 0);
            stats.put("avgDuration", 0);
            stats.put("totalCalories", "0.00");
            return stats;
        }

        long days = records.stream()
                .map(ExerciseRecord::getRecordDate)
                .filter(Objects::nonNull)
                .distinct()
                .count();
        int duration = records.stream()
                .mapToInt(r -> r.getDuration() != null ? r.getDuration() : 0)
                .sum();
        double cal = records.stream()
                .mapToDouble(r -> r.getCaloriesBurned() != null ? r.getCaloriesBurned().doubleValue() : 0)
                .sum();

        stats.put("totalDays", days);
        stats.put("totalDuration", duration);
        stats.put("avgDuration", days > 0 ? duration / days : 0);
        stats.put("totalCalories", String.format("%.2f", cal));
        return stats;
    }

    private Map<String, Object> calculateSleepStats(List<SleepRecord> records) {
        Map<String, Object> stats = new HashMap<>();
        if (records == null || records.isEmpty()) {
            stats.put("avgDuration", "0.00");
            stats.put("avgQuality", "0.00");
            stats.put("goodSleepRate", "0.00");
            return stats;
        }

        double avgDur = records.stream()
                .mapToDouble(r -> r.getDuration() != null ? r.getDuration().doubleValue() : 0)
                .average()
                .orElse(0);
        double avgQual = records.stream()
                .mapToInt(r -> r.getQuality() != null ? r.getQuality() : 0)
                .average()
                .orElse(0);
        long goodCount = records.stream()
                .filter(r -> r.getQuality() != null && r.getQuality() >= 3)
                .count();

        stats.put("avgDuration", String.format("%.2f", avgDur));
        stats.put("avgQuality", String.format("%.2f", avgQual));
        stats.put("goodSleepRate", String.format("%.2f", (goodCount * 100.0) / records.size()));
        return stats;
    }

    private Map<String, Object> calculateMoodStats(List<MoodRecord> records) {
        Map<String, Object> stats = new HashMap<>();
        if (records == null || records.isEmpty()) {
            stats.put("avgScore", "0.00");
            stats.put("positiveRate", "0.00");
            stats.put("mainMoodType", "未知");
            return stats;
        }

        double avgScore = records.stream()
                .mapToInt(r -> r.getMoodScore() != null ? r.getMoodScore() : 0)
                .average()
                .orElse(0);
        long posCount = records.stream()
                .filter(r -> r.getMoodType() != null && (r.getMoodType() == 1 || r.getMoodType() == 2))
                .count();

        Map<Integer, Long> typeCount = new HashMap<>();
        for (MoodRecord r : records) {
            if (r.getMoodType() != null) {
                typeCount.merge(r.getMoodType(), 1L, Long::sum);
            }
        }

        Integer mainType = typeCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(0);

        stats.put("avgScore", String.format("%.2f", avgScore));
        stats.put("positiveRate", String.format("%.2f", (posCount * 100.0) / records.size()));
        stats.put("mainMoodType", getMoodTypeName(mainType));
        return stats;
    }

    private String getMoodTypeName(Integer type) {
        if (type == null) return "未知";
        switch (type) {
            case 1: return "开心";
            case 2: return "平静";
            case 3: return "焦虑";
            case 4: return "悲伤";
            case 5: return "愤怒";
            case 6: return "压力";
            default: return "未知";
        }
    }

    // ==================== 查询方法 ====================

    @Override
    public Result listReports(Long studentId, Integer page, Integer size) {
        try {
            if (page == null || page < 1) page = 1;
            if (size == null || size < 1) size = 10;

            QueryWrapper<AIHealthReport> wrapper = new QueryWrapper<>();
            wrapper.eq("student_id", studentId)
                    .orderByDesc("report_date")
                    .orderByDesc("create_time");

            int offset = (page - 1) * size;
            wrapper.last("LIMIT " + offset + ", " + size);

            List<AIHealthReport> reports = reportMapper.selectList(wrapper);

            QueryWrapper<AIHealthReport> countWrapper = new QueryWrapper<>();
            countWrapper.eq("student_id", studentId);
            Long total = reportMapper.selectCount(countWrapper);

            Map<String, Object> result = new HashMap<>();
            result.put("list", reports);
            result.put("total", total);
            result.put("page", page);
            result.put("size", size);

            return Result.success("查询成功", result);
        } catch (Exception e) {
            log.error("查询报告列表失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result getReportDetail(Long id, Long studentId) {
        try {
            AIHealthReport report = reportMapper.selectById(id);
            if (report == null) {
                return Result.error("报告不存在");
            }
            if (!report.getStudentId().equals(studentId)) {
                return Result.error("无权访问");
            }
            return Result.success("查询成功", report);
        } catch (Exception e) {
            log.error("查询报告详情失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result getLatestReport(Long studentId) {
        try {
            QueryWrapper<AIHealthReport> wrapper = new QueryWrapper<>();
            wrapper.eq("student_id", studentId)
                    .orderByDesc("report_date")
                    .orderByDesc("create_time")
                    .last("LIMIT 1");

            AIHealthReport report = reportMapper.selectOne(wrapper);
            if (report == null) {
                return Result.error("暂无报告");
            }
            return Result.success("查询成功", report);
        } catch (Exception e) {
            log.error("查询最新报告失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }
}