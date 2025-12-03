-- ============================================
-- 大学生健康生活管理与预警系统 - 数据库脚本
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS health_manage_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE health_manage_system;

-- ============================================
-- 第一部分：学生端相关表
-- ============================================

-- 1. 学生用户表
DROP TABLE IF EXISTS `student_user`;
CREATE TABLE `student_user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '学生ID',
  `student_no` VARCHAR(50) UNIQUE NOT NULL COMMENT '学号',
  `password` VARCHAR(100) NOT NULL COMMENT '密码(加密)',
  `name` VARCHAR(50) NOT NULL COMMENT '姓名',
  `gender` TINYINT NOT NULL COMMENT '性别:1男,2女',
  `age` INT COMMENT '年龄',
  `phone` VARCHAR(20) COMMENT '联系电话',
  `email` VARCHAR(100) COMMENT '邮箱',
  `major` VARCHAR(100) COMMENT '专业',
  `class_name` VARCHAR(50) COMMENT '班级',
  `avatar` VARCHAR(255) COMMENT '头像URL',
  `status` TINYINT DEFAULT 1 COMMENT '状态:1正常,0禁用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_student_no (`student_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生用户表';

-- 2. 饮食记录表
DROP TABLE IF EXISTS `diet_record`;
CREATE TABLE `diet_record` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
  `student_id` BIGINT NOT NULL COMMENT '学生ID',
  `record_date` DATE NOT NULL COMMENT '记录日期',
  `meal_type` TINYINT NOT NULL COMMENT '餐次:1早餐,2午餐,3晚餐,4加餐',
  `food_name` VARCHAR(200) COMMENT '食物名称',
  `food_category` VARCHAR(50) COMMENT '食物类别',
  `calories` DECIMAL(10,2) COMMENT '热量(卡路里)',
  `protein` DECIMAL(10,2) COMMENT '蛋白质(克)',
  `carbs` DECIMAL(10,2) COMMENT '碳水化合物(克)',
  `fat` DECIMAL(10,2) COMMENT '脂肪(克)',
  `description` TEXT COMMENT '备注说明',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_student_date (`student_id`, `record_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='饮食记录表';

-- 3. 运动记录表
DROP TABLE IF EXISTS `exercise_record`;
CREATE TABLE `exercise_record` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
  `student_id` BIGINT NOT NULL COMMENT '学生ID',
  `record_date` DATE NOT NULL COMMENT '记录日期',
  `exercise_type` VARCHAR(50) NOT NULL COMMENT '运动类型',
  `duration` INT NOT NULL COMMENT '运动时长(分钟)',
  `calories_burned` DECIMAL(10,2) COMMENT '消耗热量(卡路里)',
  `intensity` TINYINT COMMENT '运动强度:1低,2中,3高',
  `distance` DECIMAL(10,2) COMMENT '运动距离(公里)',
  `description` TEXT COMMENT '备注说明',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_student_date (`student_id`, `record_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运动记录表';

-- 4. 作息记录表
DROP TABLE IF EXISTS `sleep_record`;
CREATE TABLE `sleep_record` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
  `student_id` BIGINT NOT NULL COMMENT '学生ID',
  `record_date` DATE NOT NULL COMMENT '记录日期',
  `sleep_time` DATETIME NOT NULL COMMENT '入睡时间',
  `wake_time` DATETIME NOT NULL COMMENT '起床时间',
  `duration` DECIMAL(5,2) COMMENT '睡眠时长(小时)',
  `quality` TINYINT COMMENT '睡眠质量:1差,2一般,3良好,4优秀',
  `deep_sleep_duration` DECIMAL(5,2) COMMENT '深度睡眠时长(小时)',
  `dream_count` INT DEFAULT 0 COMMENT '做梦次数',
  `description` TEXT COMMENT '备注说明',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_student_date (`student_id`, `record_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作息记录表';

-- 5. 情绪记录表
DROP TABLE IF EXISTS `mood_record`;
CREATE TABLE `mood_record` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
  `student_id` BIGINT NOT NULL COMMENT '学生ID',
  `record_date` DATE NOT NULL COMMENT '记录日期',
  `record_time` DATETIME NOT NULL COMMENT '记录时间',
  `mood_type` TINYINT NOT NULL COMMENT '情绪类型:1开心,2平静,3焦虑,4悲伤,5愤怒,6压力',
  `mood_score` TINYINT COMMENT '情绪评分:1-10',
  `trigger_event` VARCHAR(200) COMMENT '触发事件',
  `description` TEXT COMMENT '详细描述',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_student_date (`student_id`, `record_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='情绪记录表';

-- 6. 体检报告表
DROP TABLE IF EXISTS `health_examination`;
CREATE TABLE `health_examination` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '体检ID',
  `student_id` BIGINT NOT NULL COMMENT '学生ID',
  `exam_date` DATE NOT NULL COMMENT '体检日期',
  `exam_type` VARCHAR(50) COMMENT '体检类型',
  `height` DECIMAL(5,2) COMMENT '身高(cm)',
  `weight` DECIMAL(5,2) COMMENT '体重(kg)',
  `bmi` DECIMAL(5,2) COMMENT 'BMI指数',
  `blood_pressure_high` INT COMMENT '收缩压',
  `blood_pressure_low` INT COMMENT '舒张压',
  `heart_rate` INT COMMENT '心率',
  `vision_left` DECIMAL(3,1) COMMENT '左眼视力',
  `vision_right` DECIMAL(3,1) COMMENT '右眼视力',
  `blood_sugar` DECIMAL(5,2) COMMENT '血糖(mmol/L)',
  `hemoglobin` DECIMAL(5,2) COMMENT '血红蛋白(g/L)',
  `white_blood_cell` DECIMAL(5,2) COMMENT '白细胞计数',
  `platelet` DECIMAL(5,2) COMMENT '血小板计数',
  `report_file` VARCHAR(255) COMMENT '报告文件URL',
  `overall_conclusion` TEXT COMMENT '总体结论',
  `doctor_advice` TEXT COMMENT '医生建议',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_student_date (`student_id`, `exam_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='体检报告表';

-- 7. 健康指标历史表(用于趋势分析)
DROP TABLE IF EXISTS `health_indicator_history`;
CREATE TABLE `health_indicator_history` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
  `student_id` BIGINT NOT NULL COMMENT '学生ID',
  `record_date` DATE NOT NULL COMMENT '记录日期',
  `indicator_type` VARCHAR(50) NOT NULL COMMENT '指标类型',
  `indicator_value` DECIMAL(10,2) NOT NULL COMMENT '指标值',
  `unit` VARCHAR(20) COMMENT '单位',
  `reference_min` DECIMAL(10,2) COMMENT '参考值下限',
  `reference_max` DECIMAL(10,2) COMMENT '参考值上限',
  `status` TINYINT COMMENT '状态:1正常,2偏低,3偏高,4异常',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_student_type_date (`student_id`, `indicator_type`, `record_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康指标历史表';

-- 8. AI健康报告表
DROP TABLE IF EXISTS `ai_health_report`;
CREATE TABLE `ai_health_report` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '报告ID',
  `student_id` BIGINT NOT NULL COMMENT '学生ID',
  `report_date` DATE NOT NULL COMMENT '报告日期',
  `analysis_period` VARCHAR(50) COMMENT '分析周期',
  `overall_score` DECIMAL(5,2) COMMENT '总体健康评分',
  `diet_analysis` TEXT COMMENT '饮食分析',
  `exercise_analysis` TEXT COMMENT '运动分析',
  `sleep_analysis` TEXT COMMENT '睡眠分析',
  `mood_analysis` TEXT COMMENT '情绪分析',
  `health_risks` TEXT COMMENT '健康风险提示',
  `recommendations` TEXT COMMENT '改善建议',
  `ai_model_version` VARCHAR(50) COMMENT 'AI模型版本',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_student_date (`student_id`, `report_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI健康报告表';

-- 9. 系统通知表
DROP TABLE IF EXISTS `system_notification`;
CREATE TABLE `system_notification` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '通知ID',
  `student_id` BIGINT NOT NULL COMMENT '学生ID',
  `title` VARCHAR(200) NOT NULL COMMENT '通知标题',
  `content` TEXT NOT NULL COMMENT '通知内容',
  `type` TINYINT NOT NULL COMMENT '通知类型:1系统通知,2健康提醒,3体检通知',
  `priority` TINYINT DEFAULT 1 COMMENT '优先级:1普通,2重要,3紧急',
  `is_read` TINYINT DEFAULT 0 COMMENT '是否已读:0未读,1已读',
  `read_time` DATETIME COMMENT '阅读时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_student_read (`student_id`, `is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统通知表';

-- ============================================
-- 第二部分：管理员端相关表
-- ============================================

-- 10. 管理员用户表
DROP TABLE IF EXISTS `admin_user`;
CREATE TABLE `admin_user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '管理员ID',
  `employee_no` VARCHAR(50) UNIQUE NOT NULL COMMENT '工号',
  `password` VARCHAR(100) NOT NULL COMMENT '密码(加密)',
  `name` VARCHAR(50) NOT NULL COMMENT '姓名',
  `gender` TINYINT COMMENT '性别:1男,2女',
  `phone` VARCHAR(20) COMMENT '联系电话',
  `email` VARCHAR(100) COMMENT '邮箱',
  `department` VARCHAR(100) COMMENT '部门',
  `avatar` VARCHAR(255) COMMENT '头像URL',
  `status` TINYINT DEFAULT 1 COMMENT '状态:1正常,0禁用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_employee_no (`employee_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员用户表';

-- 11. 管理员工号预设表（用于验证注册时的工号是否有效）
DROP TABLE IF EXISTS `admin_employee_list`;
CREATE TABLE `admin_employee_list` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
  `employee_no` VARCHAR(50) UNIQUE NOT NULL COMMENT '工号',
  `name` VARCHAR(50) NOT NULL COMMENT '姓名',
  `department` VARCHAR(100) COMMENT '部门',
  `is_registered` TINYINT DEFAULT 0 COMMENT '是否已注册:0未注册,1已注册',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员工号预设表';

-- ============================================
-- 第三部分：测试数据
-- ============================================

-- 插入学生测试数据（密码为: password123）
INSERT INTO `student_user` (`student_no`, `password`, `name`, `gender`, `age`, `phone`, `email`, `major`, `class_name`) 
VALUES 
('2021001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张三', 1, 20, '13800138000', 'zhangsan@example.com', '计算机科学与技术', '计科2101'),
('2021002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李四', 2, 21, '13800138001', 'lisi@example.com', '软件工程', '软工2101'),
('2021003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王五', 1, 19, '13800138002', 'wangwu@example.com', '信息安全', '信安2101'),
('2021004', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '赵六', 2, 20, '13800138003', 'zhaoliu@example.com', '数据科学', '数科2101'),
('2021005', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '孙七', 1, 22, '13800138004', 'sunqi@example.com', '人工智能', '人智2101');

-- 插入预设的管理员工号数据（用于管理员注册验证）
INSERT INTO `admin_employee_list` (`employee_no`, `name`, `department`) VALUES
('ADMINTEST', '系统管理员', '学生工作处'),
('ADMIN001', '系统管理员', '学生工作处'),
('ADMIN002', '张教授', '计算机学院'),
('ADMIN003', '李老师', '体育学院'),
('ADMIN004', '王主任', '心理健康中心'),
('ADMIN005', '陈医生', '校医院'),
('ADMIN006', '刘辅导员', '学生工作处'),
('ADMIN007', '周老师', '教务处'),
('ADMIN008', '吴医生', '校医院'),
('ADMIN009', '郑主任', '后勤管理处'),
('ADMIN010', '钱老师', '体育学院');

-- 插入默认管理员账号（工号: ADMIN001, 密码: password123）
INSERT INTO `admin_user` (`employee_no`, `password`, `name`, `gender`, `phone`, `email`, `department`) VALUES
('ADMIN001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 1, '13800000000', 'admin@example.com', '学生工作处');

-- 标记该工号已注册
UPDATE `admin_employee_list` SET `is_registered` = 1 WHERE `employee_no` = 'ADMIN001';

-- ============================================
-- 第四部分：学生健康测试数据
-- ============================================

-- 为学生1插入饮食记录（近7天）
INSERT INTO `diet_record` (`student_id`, `record_date`, `meal_type`, `food_name`, `food_category`, `calories`, `protein`, `carbs`, `fat`, `description`) VALUES
(1, CURDATE(), 1, '牛奶+面包+鸡蛋', '早餐', 450, 20, 45, 15, '营养早餐'),
(1, CURDATE(), 2, '红烧牛肉+米饭+青菜', '午餐', 750, 35, 80, 25, '食堂午餐'),
(1, CURDATE(), 3, '鱼香肉丝+馒头+汤', '晚餐', 600, 25, 65, 20, '食堂晚餐'),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 1, '豆浆+包子', '早餐', 380, 15, 50, 10, ''),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 2, '宫保鸡丁+米饭', '午餐', 680, 30, 75, 22, ''),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 3, '番茄鸡蛋面', '晚餐', 520, 18, 70, 15, ''),
(1, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 1, '粥+咸菜+鸡蛋', '早餐', 320, 12, 40, 8, ''),
(1, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 2, '糖醋排骨+米饭+蔬菜', '午餐', 800, 32, 85, 28, ''),
(1, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 3, '饺子', '晚餐', 580, 22, 60, 18, '猪肉白菜饺子'),
(1, DATE_SUB(CURDATE(), INTERVAL 3 DAY), 1, '三明治+牛奶', '早餐', 420, 18, 48, 14, ''),
(1, DATE_SUB(CURDATE(), INTERVAL 3 DAY), 2, '麻婆豆腐+米饭', '午餐', 620, 25, 70, 20, ''),
(1, DATE_SUB(CURDATE(), INTERVAL 3 DAY), 3, '炒面', '晚餐', 550, 15, 75, 18, '');

-- 为学生2插入饮食记录
INSERT INTO `diet_record` (`student_id`, `record_date`, `meal_type`, `food_name`, `food_category`, `calories`, `protein`, `carbs`, `fat`, `description`) VALUES
(2, CURDATE(), 1, '酸奶+水果', '早餐', 280, 10, 35, 8, '轻食早餐'),
(2, CURDATE(), 2, '沙拉+鸡胸肉', '午餐', 450, 40, 20, 15, '减脂餐'),
(2, CURDATE(), 3, '蔬菜汤+全麦面包', '晚餐', 350, 12, 45, 10, ''),
(2, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 1, '燕麦片+牛奶', '早餐', 320, 12, 45, 8, ''),
(2, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 2, '清蒸鱼+米饭+蔬菜', '午餐', 550, 35, 50, 15, '');

-- 为学生1插入运动记录（近7天）
INSERT INTO `exercise_record` (`student_id`, `record_date`, `exercise_type`, `duration`, `calories_burned`, `intensity`, `distance`, `description`) VALUES
(1, CURDATE(), '跑步', 30, 300, 2, 4.5, '操场跑步'),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), '篮球', 60, 450, 3, NULL, '和同学打篮球'),
(1, DATE_SUB(CURDATE(), INTERVAL 2 DAY), '游泳', 45, 400, 2, 1.0, '游泳馆'),
(1, DATE_SUB(CURDATE(), INTERVAL 3 DAY), '健身', 50, 350, 2, NULL, '健身房力量训练'),
(1, DATE_SUB(CURDATE(), INTERVAL 5 DAY), '羽毛球', 40, 280, 2, NULL, ''),
(1, DATE_SUB(CURDATE(), INTERVAL 6 DAY), '跑步', 25, 250, 2, 3.5, '晨跑');

-- 为学生2插入运动记录
INSERT INTO `exercise_record` (`student_id`, `record_date`, `exercise_type`, `duration`, `calories_burned`, `intensity`, `distance`, `description`) VALUES
(2, CURDATE(), '瑜伽', 45, 180, 1, NULL, '晨练瑜伽'),
(2, DATE_SUB(CURDATE(), INTERVAL 1 DAY), '跑步', 20, 200, 2, 3.0, ''),
(2, DATE_SUB(CURDATE(), INTERVAL 3 DAY), '健身操', 30, 220, 2, NULL, '');

-- 为学生3插入运动记录（运动较少，用于预警测试）
INSERT INTO `exercise_record` (`student_id`, `record_date`, `exercise_type`, `duration`, `calories_burned`, `intensity`, `distance`, `description`) VALUES
(3, DATE_SUB(CURDATE(), INTERVAL 5 DAY), '散步', 15, 50, 1, 1.0, '饭后散步');

-- 为学生1插入睡眠记录（近7天）
INSERT INTO `sleep_record` (`student_id`, `record_date`, `sleep_time`, `wake_time`, `duration`, `quality`, `deep_sleep_duration`, `dream_count`, `description`) VALUES
(1, CURDATE(), CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 23:30:00'), CONCAT(CURDATE(), ' 07:30:00'), 8.0, 4, 3.5, 1, '睡眠质量好'),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 2 DAY), ' 23:00:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 07:00:00'), 8.0, 4, 3.2, 0, ''),
(1, DATE_SUB(CURDATE(), INTERVAL 2 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 3 DAY), ' 00:30:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 2 DAY), ' 07:30:00'), 7.0, 3, 2.8, 2, '熬夜了'),
(1, DATE_SUB(CURDATE(), INTERVAL 3 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 4 DAY), ' 23:00:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 3 DAY), ' 06:30:00'), 7.5, 3, 3.0, 1, ''),
(1, DATE_SUB(CURDATE(), INTERVAL 4 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 5 DAY), ' 22:30:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 4 DAY), ' 07:00:00'), 8.5, 4, 3.8, 0, '早睡早起'),
(1, DATE_SUB(CURDATE(), INTERVAL 5 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 6 DAY), ' 01:00:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 5 DAY), ' 08:00:00'), 7.0, 2, 2.5, 3, '失眠'),
(1, DATE_SUB(CURDATE(), INTERVAL 6 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 7 DAY), ' 23:30:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 6 DAY), ' 07:30:00'), 8.0, 3, 3.2, 1, '');

-- 为学生2插入睡眠记录
INSERT INTO `sleep_record` (`student_id`, `record_date`, `sleep_time`, `wake_time`, `duration`, `quality`, `deep_sleep_duration`, `dream_count`, `description`) VALUES
(2, CURDATE(), CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 22:00:00'), CONCAT(CURDATE(), ' 06:30:00'), 8.5, 4, 4.0, 0, ''),
(2, DATE_SUB(CURDATE(), INTERVAL 1 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 2 DAY), ' 22:30:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 07:00:00'), 8.5, 4, 3.8, 1, ''),
(2, DATE_SUB(CURDATE(), INTERVAL 2 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 3 DAY), ' 23:00:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 2 DAY), ' 07:30:00'), 8.5, 4, 3.5, 0, '');

-- 为学生4插入睡眠记录（睡眠不足，用于预警测试）
INSERT INTO `sleep_record` (`student_id`, `record_date`, `sleep_time`, `wake_time`, `duration`, `quality`, `deep_sleep_duration`, `dream_count`, `description`) VALUES
(4, CURDATE(), CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 02:00:00'), CONCAT(CURDATE(), ' 07:00:00'), 5.0, 2, 1.5, 2, '熬夜复习'),
(4, DATE_SUB(CURDATE(), INTERVAL 1 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 2 DAY), ' 01:30:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 06:30:00'), 5.0, 2, 1.8, 3, ''),
(4, DATE_SUB(CURDATE(), INTERVAL 2 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 3 DAY), ' 02:30:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 2 DAY), ' 07:30:00'), 5.0, 1, 1.2, 4, '失眠严重'),
(4, DATE_SUB(CURDATE(), INTERVAL 3 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 4 DAY), ' 01:00:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 3 DAY), ' 06:00:00'), 5.0, 2, 1.5, 2, ''),
(4, DATE_SUB(CURDATE(), INTERVAL 4 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 5 DAY), ' 03:00:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 4 DAY), ' 08:00:00'), 5.0, 1, 1.0, 5, '通宵后补觉');

-- 为学生1插入情绪记录（近7天）
INSERT INTO `mood_record` (`student_id`, `record_date`, `record_time`, `mood_type`, `mood_score`, `trigger_event`, `description`) VALUES
(1, CURDATE(), CONCAT(CURDATE(), ' 10:00:00'), 1, 8, '完成作业', '今天效率很高'),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 15:00:00'), 2, 6, '', '平静的一天'),
(1, DATE_SUB(CURDATE(), INTERVAL 2 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 2 DAY), ' 20:00:00'), 3, 4, '考试压力', '期末考试临近'),
(1, DATE_SUB(CURDATE(), INTERVAL 3 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 3 DAY), ' 12:00:00'), 1, 9, '和朋友聚餐', '很开心'),
(1, DATE_SUB(CURDATE(), INTERVAL 4 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 4 DAY), ' 18:00:00'), 2, 7, '', ''),
(1, DATE_SUB(CURDATE(), INTERVAL 5 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 5 DAY), ' 22:00:00'), 6, 3, '项目deadline', '压力很大'),
(1, DATE_SUB(CURDATE(), INTERVAL 6 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 6 DAY), ' 09:00:00'), 1, 8, '周末', '');

-- 为学生2插入情绪记录
INSERT INTO `mood_record` (`student_id`, `record_date`, `record_time`, `mood_type`, `mood_score`, `trigger_event`, `description`) VALUES
(2, CURDATE(), CONCAT(CURDATE(), ' 09:00:00'), 1, 9, '天气好', '心情愉悦'),
(2, DATE_SUB(CURDATE(), INTERVAL 1 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 14:00:00'), 1, 8, '', ''),
(2, DATE_SUB(CURDATE(), INTERVAL 2 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 2 DAY), ' 11:00:00'), 2, 7, '', '');

-- 为学生5插入情绪记录（负面情绪较多，用于预警测试）
INSERT INTO `mood_record` (`student_id`, `record_date`, `record_time`, `mood_type`, `mood_score`, `trigger_event`, `description`) VALUES
(5, CURDATE(), CONCAT(CURDATE(), ' 10:00:00'), 3, 3, '考试挂科', '很焦虑'),
(5, DATE_SUB(CURDATE(), INTERVAL 1 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 16:00:00'), 4, 2, '和朋友吵架', '心情低落'),
(5, DATE_SUB(CURDATE(), INTERVAL 2 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 2 DAY), ' 20:00:00'), 6, 2, '作业太多', '压力很大'),
(5, DATE_SUB(CURDATE(), INTERVAL 3 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 3 DAY), ' 11:00:00'), 3, 4, '', ''),
(5, DATE_SUB(CURDATE(), INTERVAL 4 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 4 DAY), ' 15:00:00'), 5, 3, '被老师批评', '很生气'),
(5, DATE_SUB(CURDATE(), INTERVAL 5 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 5 DAY), ' 19:00:00'), 4, 3, '', '情绪低落');

-- 为学生1插入体检记录
INSERT INTO `health_examination` (`student_id`, `exam_date`, `exam_type`, `height`, `weight`, `bmi`, `blood_pressure_high`, `blood_pressure_low`, `heart_rate`, `vision_left`, `vision_right`, `blood_sugar`, `hemoglobin`, `white_blood_cell`, `platelet`, `overall_conclusion`, `doctor_advice`) VALUES
(1, DATE_SUB(CURDATE(), INTERVAL 30 DAY), '年度体检', 175.5, 68.0, 22.1, 118, 75, 72, 5.0, 4.9, 5.2, 145.0, 6.5, 220.0, '身体状况良好，各项指标正常', '继续保持良好的生活习惯，注意用眼卫生'),
(1, DATE_SUB(CURDATE(), INTERVAL 180 DAY), '入学体检', 174.0, 65.0, 21.5, 115, 72, 70, 5.1, 5.0, 5.0, 142.0, 6.2, 215.0, '身体健康', '');

-- 为学生2插入体检记录
INSERT INTO `health_examination` (`student_id`, `exam_date`, `exam_type`, `height`, `weight`, `bmi`, `blood_pressure_high`, `blood_pressure_low`, `heart_rate`, `vision_left`, `vision_right`, `blood_sugar`, `overall_conclusion`, `doctor_advice`) VALUES
(2, DATE_SUB(CURDATE(), INTERVAL 60 DAY), '年度体检', 162.0, 52.0, 19.8, 108, 68, 68, 5.2, 5.2, 4.8, '身体状况良好', '注意营养均衡');

-- 为学生1插入AI健康报告
INSERT INTO `ai_health_report` (`student_id`, `report_date`, `analysis_period`, `overall_score`, `diet_analysis`, `exercise_analysis`, `sleep_analysis`, `mood_analysis`, `health_risks`, `recommendations`, `ai_model_version`) VALUES
(1, CURDATE(), '近7天', 82.5, 
'饮食较为规律，三餐按时，营养摄入基本均衡。蛋白质摄入充足，但碳水化合物偏高，建议适当减少主食摄入。', 
'运动频率良好，每周保持4-5次运动，运动类型多样化。建议增加有氧运动时间，提高心肺功能。', 
'睡眠时间基本充足，平均7.5小时。但存在偶尔熬夜情况，建议保持规律作息，每晚23点前入睡。', 
'情绪状态总体良好，偶有学业压力导致的焦虑。建议通过运动和社交活动缓解压力。', 
'1. 偶发熬夜可能影响免疫力\n2. 学业压力需要关注', 
'1. 保持规律作息，减少熬夜\n2. 适当增加蔬果摄入\n3. 学会压力管理，必要时寻求心理咨询', 
'HealthAI v1.0');

-- 为学生1插入系统通知
INSERT INTO `system_notification` (`student_id`, `title`, `content`, `type`, `priority`, `is_read`) VALUES
(1, '体检提醒', '您的年度体检即将到期，请于本月内完成体检。', 3, 2, 0),
(1, '健康周报已生成', '您的本周健康报告已生成，请查看。', 2, 1, 1),
(1, '运动目标达成', '恭喜您完成本周运动目标！继续保持！', 2, 1, 1),
(1, '系统更新通知', '系统已升级，新增AI健康分析功能。', 1, 1, 0);

-- 为学生2插入系统通知
INSERT INTO `system_notification` (`student_id`, `title`, `content`, `type`, `priority`, `is_read`) VALUES
(2, '睡眠建议', '根据您的睡眠数据，建议您保持良好的睡眠习惯。', 2, 1, 0);

-- ============================================
-- 脚本执行完成
-- ============================================

-- 账号信息汇总：
-- 学生账号：
--   学号: 2021001, 密码: password123 (张三)
--   学号: 2021002, 密码: password123 (李四)
--   学号: 2021003, 密码: password123 (王五)
--   学号: 2021004, 密码: password123 (赵六) - 睡眠不足预警测试
--   学号: 2021005, 密码: password123 (孙七) - 情绪异常预警测试

-- 管理员账号：
--   工号: ADMIN001, 密码: password123 (系统管理员) - 已注册
--   其他预设工号 ADMIN002-ADMIN010 可用于注册新管理员

-- 测试账号：
-- 	学生账号：
-- 		学号：STUDENTTEST, 密码:123456
-- 	管理员账号：
-- 		工号：ADMINTEST, 密码:123456
-- 