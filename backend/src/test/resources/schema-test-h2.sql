-- 管理员用户表
CREATE TABLE IF NOT EXISTS admin_user (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '管理员ID',
                                          employee_no VARCHAR(50) NOT NULL UNIQUE COMMENT '工号',
                                          password VARCHAR(200) NOT NULL COMMENT '密码(加密)',
                                          name VARCHAR(50) NOT NULL COMMENT '姓名',
                                          gender INTEGER DEFAULT 1 COMMENT '性别: 1-男, 2-女',
                                          phone VARCHAR(20) COMMENT '手机号',
                                          email VARCHAR(100) COMMENT '邮箱',
                                          department VARCHAR(100) COMMENT '部门',
                                          avatar VARCHAR(500) COMMENT '头像URL',
                                          status INTEGER DEFAULT 1 COMMENT '状态: 1-正常, 0-禁用',
                                          create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);

-- 管理员员工列表
CREATE TABLE IF NOT EXISTS admin_employee_list (
                                                   id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '员工ID',
                                                   employee_no VARCHAR(50) NOT NULL UNIQUE COMMENT '工号',
                                                   name VARCHAR(50) NOT NULL COMMENT '姓名',
                                                   department VARCHAR(100) COMMENT '部门',
                                                   is_registered INTEGER DEFAULT 0 COMMENT '是否注册: 0-未注册, 1-已注册',
                                                   create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);

-- 系统通知表
CREATE TABLE IF NOT EXISTS system_notification (
                                                   id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '通知ID',
                                                   student_id BIGINT COMMENT '学生ID',
                                                   title VARCHAR(200) NOT NULL COMMENT '标题',
                                                   content TEXT COMMENT '内容',
                                                   type INTEGER COMMENT '类型',
                                                   priority INTEGER COMMENT '优先级',
                                                   is_read INTEGER COMMENT '是否已读',
                                                   read_time TIMESTAMP COMMENT '阅读时间',
                                                   create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);

-- 运动记录表
CREATE TABLE IF NOT EXISTS exercise_record (
                                               id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
                                               student_id BIGINT NOT NULL COMMENT '学生ID',
                                               record_date DATE NOT NULL COMMENT '记录日期',
                                               exercise_type VARCHAR(50) NOT NULL COMMENT '运动类型',
                                               duration INTEGER NOT NULL COMMENT '运动时长(分钟)',
                                               calories_burned DECIMAL(10,2) COMMENT '消耗卡路里',
                                               intensity INTEGER COMMENT '运动强度',
                                               distance DECIMAL(10,2) COMMENT '运动距离(公里)',
                                               description VARCHAR(500) COMMENT '运动描述',
                                               create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);

-- 心情记录表
CREATE TABLE IF NOT EXISTS mood_record (
                                           id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
                                           student_id BIGINT NOT NULL COMMENT '学生ID',
                                           record_date DATE NOT NULL COMMENT '记录日期',
                                           record_time TIMESTAMP COMMENT '记录时间',
                                           mood_type INTEGER NOT NULL COMMENT '心情类型',
                                           mood_score INTEGER COMMENT '心情评分(1-10)',
                                           trigger_event VARCHAR(500) COMMENT '触发事件',
                                           description VARCHAR(500) COMMENT '心情描述',
                                           create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);

-- 体检记录表
CREATE TABLE IF NOT EXISTS health_examination (
                                                  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '体检ID',
                                                  student_id BIGINT NOT NULL COMMENT '学生ID',
                                                  exam_date DATE NOT NULL COMMENT '体检日期',
                                                  exam_type VARCHAR(50) COMMENT '体检类型',
                                                  height DECIMAL(5,2) COMMENT '身高(cm)',
                                                  weight DECIMAL(5,2) COMMENT '体重(kg)',
                                                  bmi DECIMAL(5,2) COMMENT 'BMI指数',
                                                  blood_pressure_high INTEGER COMMENT '收缩压',
                                                  blood_pressure_low INTEGER COMMENT '舒张压',
                                                  heart_rate INTEGER COMMENT '心率',
                                                  vision_left DECIMAL(3,1) COMMENT '左眼视力',
                                                  vision_right DECIMAL(3,1) COMMENT '右眼视力',
                                                  blood_sugar DECIMAL(5,2) COMMENT '血糖',
                                                  hemoglobin DECIMAL(5,2) COMMENT '血红蛋白',
                                                  white_blood_cell DECIMAL(5,2) COMMENT '白细胞',
                                                  platelet DECIMAL(5,2) COMMENT '血小板',
                                                  report_file VARCHAR(500) COMMENT '报告文件路径',
                                                  overall_conclusion VARCHAR(1000) COMMENT '总体结论',
                                                  doctor_advice VARCHAR(1000) COMMENT '医生建议',
                                                  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);

-- AI健康报告表
CREATE TABLE IF NOT EXISTS ai_health_report (
                                                id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '报告ID',
                                                student_id BIGINT NOT NULL COMMENT '学生ID',
                                                report_date DATE NOT NULL COMMENT '报告日期',
                                                analysis_period VARCHAR(100) COMMENT '分析周期',
                                                overall_score DECIMAL(5,2) COMMENT '总体评分',
                                                diet_analysis TEXT COMMENT '饮食分析',
                                                exercise_analysis TEXT COMMENT '运动分析',
                                                sleep_analysis TEXT COMMENT '睡眠分析',
                                                mood_analysis TEXT COMMENT '心情分析',
                                                health_risks TEXT COMMENT '健康风险',
                                                recommendations TEXT COMMENT '建议',
                                                ai_model_version VARCHAR(50) COMMENT 'AI模型版本',
                                                create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);

-- 饮食记录表
CREATE TABLE IF NOT EXISTS diet_record (
                                           id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
                                           student_id BIGINT NOT NULL COMMENT '学生ID',
                                           record_date DATE NOT NULL COMMENT '记录日期',
                                           meal_type INTEGER COMMENT '1早餐 2午餐 3晚餐 4加餐',
                                           food_name VARCHAR(100) COMMENT '食物名称',
                                           food_category VARCHAR(50) COMMENT '食物类别',
                                           calories DECIMAL(10,2) COMMENT '卡路里',
                                           protein DECIMAL(10,2) COMMENT '蛋白质(克)',
                                           carbs DECIMAL(10,2) COMMENT '碳水化合物(克)',
                                           fat DECIMAL(10,2) COMMENT '脂肪(克)',
                                           description VARCHAR(500) COMMENT '描述',
                                           create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);

-- 睡眠记录表
CREATE TABLE IF NOT EXISTS sleep_record (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
                                            student_id BIGINT NOT NULL COMMENT '学生ID',
                                            record_date DATE NOT NULL COMMENT '记录日期',
                                            sleep_time TIMESTAMP COMMENT '入睡时间',
                                            wake_time TIMESTAMP COMMENT '醒来时间',
                                            duration DECIMAL(5,2) COMMENT '睡眠时长(小时)',
                                            quality INTEGER COMMENT '睡眠质量',
                                            deep_sleep_duration DECIMAL(5,2) COMMENT '深度睡眠时长(小时)',
                                            dream_count INTEGER COMMENT '做梦次数',
                                            description VARCHAR(500) COMMENT '描述',
                                            create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);

-- 学生用户表
CREATE TABLE IF NOT EXISTS student_user (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '学生ID',
                                            student_no VARCHAR(50) NOT NULL UNIQUE COMMENT '学号',
                                            password VARCHAR(200) NOT NULL COMMENT '密码(加密)',
                                            name VARCHAR(50) NOT NULL COMMENT '姓名',
                                            gender INTEGER DEFAULT 1 COMMENT '性别: 1-男, 2-女',
                                            age INTEGER COMMENT '年龄',
                                            phone VARCHAR(20) COMMENT '手机号',
                                            email VARCHAR(100) COMMENT '邮箱',
                                            major VARCHAR(100) COMMENT '专业',
                                            class_name VARCHAR(100) COMMENT '班级',
                                            avatar VARCHAR(500) COMMENT '头像URL',
                                            status INTEGER DEFAULT 1 COMMENT '状态: 1-正常, 0-禁用',
                                            create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                            update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);

