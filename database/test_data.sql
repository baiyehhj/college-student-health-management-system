-- ============================================
-- 测试数据脚本
-- 包含：基本信息 + 30天健康数据 + 体检 + AI报告 + 通知
-- ============================================

USE health_manage_system;

-- ============================================
-- 1. 插入基本信息
-- ============================================
INSERT INTO `student_user` (`student_no`, `password`, `name`, `gender`, `age`, `phone`, `email`, `major`, `class_name`, `status`) 
VALUES ('STUDENTTEST', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '学生测试', 1, 21, '13312345678', 'studenttest@example.com', '计算机科学与技术', '计科1班', 1);

INSERT INTO `admin_user` (`id`, `employee_no`, `password`, `name`, `gender`, `phone`, `email`, `department`, `avatar`, `status`, `create_time`, `update_time`) VALUES (2, 'ADMINTEST', '$2a$10$ARRQ9AAlkqOB6sLjKvC3DOtxRP9CSW.aKs4CjECKfTuIhBJNSxzHi', '管理员测试', 1, '13387654321', '', '学生工作处', NULL, 1, NULL, NULL);

-- -- 获取学生ID（假设为STUDENTTEST，如果不是请根据实际情况修改）
SET @student_id = (SELECT id FROM student_user WHERE student_no = 'STUDENTTEST');

-- ============================================
-- 2. 饮食记录（近30天，每天3餐）
-- ============================================

-- 第1天（今天）
INSERT INTO `diet_record` (`student_id`, `record_date`, `meal_type`, `food_name`, `food_category`, `calories`, `protein`, `carbs`, `fat`, `description`) VALUES
(@student_id, CURDATE(), 1, '牛奶+全麦面包+煎蛋', '早餐', 420, 18, 42, 16, '营养均衡的早餐'),
(@student_id, CURDATE(), 2, '红烧排骨+米饭+炒青菜+紫菜蛋花汤', '午餐', 780, 32, 85, 28, '食堂二楼'),
(@student_id, CURDATE(), 3, '清炒虾仁+馒头+凉拌黄瓜', '晚餐', 520, 28, 55, 15, '');

-- 第2天
INSERT INTO `diet_record` (`student_id`, `record_date`, `meal_type`, `food_name`, `food_category`, `calories`, `protein`, `carbs`, `fat`, `description`) VALUES
(@student_id, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 1, '豆浆+肉包子+茶叶蛋', '早餐', 450, 20, 48, 18, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 2, '宫保鸡丁+米饭+番茄炒蛋', '午餐', 720, 30, 78, 25, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 3, '牛肉面+卤蛋', '晚餐', 650, 28, 72, 22, '兰州拉面');

-- 第3天
INSERT INTO `diet_record` (`student_id`, `record_date`, `meal_type`, `food_name`, `food_category`, `calories`, `protein`, `carbs`, `fat`, `description`) VALUES
(@student_id, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 1, '小米粥+花卷+咸鸭蛋', '早餐', 380, 14, 52, 12, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 2, '糖醋里脊+米饭+炒豆芽', '午餐', 750, 28, 82, 26, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 3, '饺子（猪肉白菜）+醋', '晚餐', 580, 24, 62, 20, '20个饺子');

-- 第4天
INSERT INTO `diet_record` (`student_id`, `record_date`, `meal_type`, `food_name`, `food_category`, `calories`, `protein`, `carbs`, `fat`, `description`) VALUES
(@student_id, DATE_SUB(CURDATE(), INTERVAL 3 DAY), 1, '三明治（火腿芝士）+酸奶', '早餐', 480, 22, 45, 20, '便利店买的'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 3 DAY), 2, '鱼香肉丝+米饭+麻婆豆腐', '午餐', 700, 26, 75, 24, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 3 DAY), 3, '炒饭+可乐', '晚餐', 620, 18, 78, 22, '蛋炒饭');

-- 第5天
INSERT INTO `diet_record` (`student_id`, `record_date`, `meal_type`, `food_name`, `food_category`, `calories`, `protein`, `carbs`, `fat`, `description`) VALUES
(@student_id, DATE_SUB(CURDATE(), INTERVAL 4 DAY), 1, '燕麦片+牛奶+苹果', '早餐', 350, 12, 48, 10, '健康早餐'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 4 DAY), 2, '烤鸭饭+青菜汤', '午餐', 680, 28, 70, 26, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 4 DAY), 3, '小龙虾+啤酒+毛豆', '晚餐', 850, 35, 40, 38, '和同学聚餐');

-- 第6天
INSERT INTO `diet_record` (`student_id`, `record_date`, `meal_type`, `food_name`, `food_category`, `calories`, `protein`, `carbs`, `fat`, `description`) VALUES
(@student_id, DATE_SUB(CURDATE(), INTERVAL 5 DAY), 1, '包子（菜包+肉包）+豆腐脑', '早餐', 420, 16, 55, 14, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 5 DAY), 2, '回锅肉+米饭+酸辣土豆丝', '午餐', 760, 25, 80, 30, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 5 DAY), 3, '炸酱面+黄瓜丝', '晚餐', 550, 18, 68, 18, '');

-- 第7天
INSERT INTO `diet_record` (`student_id`, `record_date`, `meal_type`, `food_name`, `food_category`, `calories`, `protein`, `carbs`, `fat`, `description`) VALUES
(@student_id, DATE_SUB(CURDATE(), INTERVAL 6 DAY), 1, '油条+豆浆+茶叶蛋', '早餐', 480, 18, 52, 22, '传统早餐'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 6 DAY), 2, '青椒肉丝+米饭+西红柿蛋汤', '午餐', 680, 28, 72, 24, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 6 DAY), 3, '火锅（牛肉+蔬菜+豆腐）', '晚餐', 920, 42, 65, 45, '周末和舍友吃火锅');

-- 第8-14天（第二周）
INSERT INTO `diet_record` (`student_id`, `record_date`, `meal_type`, `food_name`, `food_category`, `calories`, `protein`, `carbs`, `fat`, `description`) VALUES
(@student_id, DATE_SUB(CURDATE(), INTERVAL 7 DAY), 1, '蛋饼+豆浆', '早餐', 400, 15, 45, 16, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 7 DAY), 2, '黄焖鸡米饭', '午餐', 720, 32, 75, 28, '外卖'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 7 DAY), 3, '麻辣烫', '晚餐', 580, 22, 60, 22, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 8 DAY), 1, '粥+榨菜+馒头', '早餐', 320, 10, 55, 6, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 8 DAY), 2, '咖喱鸡肉饭', '午餐', 700, 28, 78, 25, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 8 DAY), 3, '炒河粉+卤味', '晚餐', 620, 20, 72, 24, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 9 DAY), 1, '牛奶+面包+香蕉', '早餐', 380, 12, 52, 12, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 9 DAY), 2, '红烧肉+米饭+蔬菜', '午餐', 800, 30, 82, 32, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 9 DAY), 3, '馄饨+小笼包', '晚餐', 520, 22, 58, 18, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 10 DAY), 1, '煎饼果子+豆浆', '早餐', 450, 14, 55, 18, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 10 DAY), 2, '水煮鱼+米饭', '午餐', 750, 35, 70, 30, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 10 DAY), 3, '蛋炒饭+紫菜汤', '晚餐', 550, 18, 65, 20, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 11 DAY), 1, '豆沙包+牛奶', '早餐', 380, 12, 52, 12, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 11 DAY), 2, '宫保虾仁+米饭', '午餐', 680, 32, 70, 24, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 11 DAY), 3, '披萨+可乐', '晚餐', 780, 25, 85, 32, '必胜客'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 12 DAY), 1, '肠粉+豆浆', '早餐', 350, 12, 45, 12, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 12 DAY), 2, '酸菜鱼+米饭', '午餐', 720, 35, 68, 28, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 12 DAY), 3, '炒面+卤蛋', '晚餐', 580, 20, 70, 20, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 13 DAY), 1, '鸡蛋灌饼+牛奶', '早餐', 420, 16, 48, 18, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 13 DAY), 2, '盖浇饭（茄子肉末）', '午餐', 680, 22, 80, 25, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 13 DAY), 3, '烧烤+啤酒', '晚餐', 950, 38, 55, 52, '周末聚餐');

-- 第15-21天（第三周）
INSERT INTO `diet_record` (`student_id`, `record_date`, `meal_type`, `food_name`, `food_category`, `calories`, `protein`, `carbs`, `fat`, `description`) VALUES
(@student_id, DATE_SUB(CURDATE(), INTERVAL 14 DAY), 1, '蒸饺+小米粥', '早餐', 400, 16, 50, 14, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 14 DAY), 2, '干锅鸡+米饭', '午餐', 750, 35, 72, 30, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 14 DAY), 3, '凉皮+肉夹馍', '晚餐', 620, 18, 75, 22, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 15 DAY), 1, '粽子+豆浆', '早餐', 450, 10, 65, 15, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 15 DAY), 2, '红烧牛肉面', '午餐', 680, 30, 72, 25, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 15 DAY), 3, '寿司拼盘', '晚餐', 550, 22, 65, 18, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 16 DAY), 1, '牛奶+蛋糕', '早餐', 420, 12, 55, 18, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 16 DAY), 2, '土豆烧鸡+米饭', '午餐', 700, 28, 75, 26, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 16 DAY), 3, '炒米粉+卤味', '晚餐', 580, 20, 68, 22, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 17 DAY), 1, '肉松面包+酸奶', '早餐', 400, 14, 48, 16, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 17 DAY), 2, '麻辣香锅', '午餐', 850, 35, 70, 42, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 17 DAY), 3, '清汤面+卤蛋', '晚餐', 480, 18, 62, 15, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 18 DAY), 1, '小笼包+豆浆', '早餐', 420, 16, 52, 16, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 18 DAY), 2, '孜然羊肉+米饭', '午餐', 750, 32, 70, 32, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 18 DAY), 3, '烤冷面+烤肠', '晚餐', 550, 15, 65, 22, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 19 DAY), 1, '葱油饼+豆腐脑', '早餐', 380, 12, 48, 16, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 19 DAY), 2, '辣子鸡+米饭', '午餐', 720, 30, 72, 28, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 19 DAY), 3, '汉堡+薯条+可乐', '晚餐', 880, 28, 95, 38, '麦当劳'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 20 DAY), 1, '油条+粥', '早餐', 350, 8, 48, 14, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 20 DAY), 2, '蒜蓉蒸虾+米饭+蔬菜', '午餐', 620, 35, 60, 20, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 20 DAY), 3, '韩式拌饭', '晚餐', 580, 22, 70, 18, '');

-- 第22-30天（第四周+）
INSERT INTO `diet_record` (`student_id`, `record_date`, `meal_type`, `food_name`, `food_category`, `calories`, `protein`, `carbs`, `fat`, `description`) VALUES
(@student_id, DATE_SUB(CURDATE(), INTERVAL 21 DAY), 1, '牛奶+三明治', '早餐', 420, 18, 45, 16, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 21 DAY), 2, '红烧茄子+米饭+蛋汤', '午餐', 650, 20, 78, 24, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 21 DAY), 3, '炒年糕+关东煮', '晚餐', 600, 15, 75, 22, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 22 DAY), 1, '包子+牛奶', '早餐', 400, 14, 50, 15, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 22 DAY), 2, '木须肉+米饭', '午餐', 680, 25, 72, 26, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 22 DAY), 3, '炒饭+蛋花汤', '晚餐', 550, 18, 68, 20, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 23 DAY), 1, '豆浆+油条+茶叶蛋', '早餐', 450, 16, 50, 20, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 23 DAY), 2, '铁板牛肉+米饭', '午餐', 750, 35, 70, 30, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 23 DAY), 3, '螺蛳粉', '晚餐', 580, 18, 70, 22, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 24 DAY), 1, '燕麦+水果+酸奶', '早餐', 350, 10, 50, 10, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 24 DAY), 2, '剁椒鱼头+米饭', '午餐', 700, 38, 65, 28, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 24 DAY), 3, '锅贴+紫菜汤', '晚餐', 520, 18, 58, 20, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 25 DAY), 1, '粥+肉松+咸蛋', '早餐', 380, 14, 48, 14, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 25 DAY), 2, '酱爆鸡丁+米饭', '午餐', 680, 28, 72, 25, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 25 DAY), 3, '意大利面', '晚餐', 620, 22, 75, 22, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 26 DAY), 1, '煎蛋+吐司+牛奶', '早餐', 420, 18, 42, 18, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 26 DAY), 2, '东坡肉+米饭+蔬菜', '午餐', 780, 28, 75, 35, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 26 DAY), 3, '煲仔饭', '晚餐', 650, 25, 72, 25, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 27 DAY), 1, '豆沙包+小米粥', '早餐', 350, 10, 55, 10, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 27 DAY), 2, '水煮肉片+米饭', '午餐', 750, 32, 70, 32, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 27 DAY), 3, '兰州拉面', '晚餐', 580, 25, 68, 20, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 28 DAY), 1, '鸡蛋饼+豆浆', '早餐', 400, 15, 48, 16, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 28 DAY), 2, '京酱肉丝+米饭', '午餐', 700, 28, 75, 26, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 28 DAY), 3, '烤串+啤酒', '晚餐', 850, 35, 50, 45, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 29 DAY), 1, '牛奶+蛋糕+水果', '早餐', 450, 12, 58, 18, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 29 DAY), 2, '蚂蚁上树+米饭', '午餐', 650, 22, 78, 24, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 29 DAY), 3, '日式拉面', '晚餐', 620, 28, 65, 25, '');

-- ============================================
-- 3. 运动记录（近30天）
-- ============================================
INSERT INTO `exercise_record` (`student_id`, `record_date`, `exercise_type`, `duration`, `calories_burned`, `intensity`, `distance`, `description`) VALUES
-- 本周运动
(@student_id, CURDATE(), '跑步', 35, 350, 2, 5.0, '操场晨跑'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 1 DAY), '篮球', 60, 480, 3, NULL, '和同学打篮球'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 2 DAY), '健身', 50, 320, 2, NULL, '健身房力量训练：胸肌+三头'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 3 DAY), '游泳', 45, 400, 2, 1.2, '游泳馆自由泳'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 4 DAY), '跳绳', 20, 200, 3, NULL, '宿舍楼下'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 5 DAY), '羽毛球', 50, 350, 2, NULL, '体育馆'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 6 DAY), '跑步', 40, 400, 2, 6.0, '晚间跑步'),
-- 第二周运动
(@student_id, DATE_SUB(CURDATE(), INTERVAL 7 DAY), '足球', 70, 550, 3, NULL, '班级足球赛'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 8 DAY), '健身', 55, 350, 2, NULL, '背部+二头训练'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 9 DAY), '骑行', 45, 300, 2, 12.0, '校园骑行'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 10 DAY), '跑步', 30, 300, 2, 4.5, '操场跑步'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 12 DAY), '乒乓球', 40, 220, 1, NULL, '活动室'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 13 DAY), '健身', 60, 380, 2, NULL, '腿部训练'),
-- 第三周运动
(@student_id, DATE_SUB(CURDATE(), INTERVAL 14 DAY), '游泳', 50, 450, 2, 1.5, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 15 DAY), '跑步', 35, 350, 2, 5.0, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 17 DAY), '篮球', 55, 440, 3, NULL, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 18 DAY), '健身', 50, 320, 2, NULL, '肩部训练'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 19 DAY), '跳绳', 25, 250, 3, NULL, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 20 DAY), '散步', 60, 180, 1, 4.0, '饭后散步'),
-- 第四周运动
(@student_id, DATE_SUB(CURDATE(), INTERVAL 21 DAY), '跑步', 40, 400, 2, 6.0, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 22 DAY), '羽毛球', 45, 320, 2, NULL, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 24 DAY), '健身', 55, 350, 2, NULL, '全身训练'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 25 DAY), '游泳', 40, 360, 2, 1.0, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 26 DAY), '篮球', 50, 400, 2, NULL, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 27 DAY), '跑步', 30, 300, 2, 4.5, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 28 DAY), '瑜伽', 45, 150, 1, NULL, '宿舍拉伸'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 29 DAY), '足球', 60, 480, 3, NULL, '周末踢球');

-- ============================================
-- 4. 睡眠记录（近30天）
-- ============================================
INSERT INTO `sleep_record` (`student_id`, `record_date`, `sleep_time`, `wake_time`, `duration`, `quality`, `deep_sleep_duration`, `dream_count`, `description`) VALUES
-- 本周睡眠
(@student_id, CURDATE(), CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 23:15:00'), CONCAT(CURDATE(), ' 07:15:00'), 8.0, 4, 3.5, 1, '睡眠质量很好'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 1 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 2 DAY), ' 23:30:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 07:00:00'), 7.5, 3, 3.0, 2, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 2 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 3 DAY), ' 00:00:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 2 DAY), ' 07:30:00'), 7.5, 3, 2.8, 1, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 3 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 4 DAY), ' 23:00:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 3 DAY), ' 06:30:00'), 7.5, 3, 3.2, 0, '早起有课'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 4 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 5 DAY), ' 01:00:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 4 DAY), ' 08:30:00'), 7.5, 2, 2.5, 3, '熬夜写作业'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 5 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 6 DAY), ' 23:30:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 5 DAY), ' 07:30:00'), 8.0, 4, 3.5, 1, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 6 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 7 DAY), ' 00:30:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 6 DAY), ' 09:00:00'), 8.5, 4, 4.0, 0, '周末补觉'),
-- 第二周睡眠
(@student_id, DATE_SUB(CURDATE(), INTERVAL 7 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 8 DAY), ' 23:00:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 7 DAY), ' 07:00:00'), 8.0, 4, 3.5, 1, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 8 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 9 DAY), ' 23:30:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 8 DAY), ' 07:00:00'), 7.5, 3, 3.0, 2, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 9 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 10 DAY), ' 00:00:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 9 DAY), ' 07:30:00'), 7.5, 3, 2.8, 1, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 10 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 11 DAY), ' 22:30:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 10 DAY), ' 06:30:00'), 8.0, 4, 3.8, 0, '早睡早起'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 11 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 12 DAY), ' 01:30:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 11 DAY), ' 08:00:00'), 6.5, 2, 2.2, 4, '熬夜打游戏'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 12 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 13 DAY), ' 23:00:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 12 DAY), ' 07:30:00'), 8.5, 4, 3.8, 1, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 13 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 14 DAY), ' 02:00:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 13 DAY), ' 10:00:00'), 8.0, 3, 3.0, 2, '周末'),
-- 第三周睡眠
(@student_id, DATE_SUB(CURDATE(), INTERVAL 14 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 15 DAY), ' 23:00:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 14 DAY), ' 07:00:00'), 8.0, 4, 3.5, 1, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 15 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 16 DAY), ' 23:30:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 15 DAY), ' 07:00:00'), 7.5, 3, 3.0, 2, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 16 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 17 DAY), ' 00:30:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 16 DAY), ' 07:30:00'), 7.0, 3, 2.5, 2, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 17 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 18 DAY), ' 23:00:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 17 DAY), ' 06:30:00'), 7.5, 3, 3.2, 1, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 18 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 19 DAY), ' 22:30:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 18 DAY), ' 07:00:00'), 8.5, 4, 4.0, 0, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 19 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 20 DAY), ' 23:30:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 19 DAY), ' 07:30:00'), 8.0, 3, 3.2, 1, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 20 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 21 DAY), ' 01:00:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 20 DAY), ' 09:00:00'), 8.0, 3, 3.0, 3, '周末'),
-- 第四周睡眠
(@student_id, DATE_SUB(CURDATE(), INTERVAL 21 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 22 DAY), ' 23:00:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 21 DAY), ' 07:00:00'), 8.0, 4, 3.5, 1, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 22 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 23 DAY), ' 23:30:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 22 DAY), ' 07:30:00'), 8.0, 3, 3.2, 2, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 23 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 24 DAY), ' 00:00:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 23 DAY), ' 07:00:00'), 7.0, 3, 2.8, 1, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 24 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 25 DAY), ' 23:00:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 24 DAY), ' 06:30:00'), 7.5, 3, 3.0, 1, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 25 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 26 DAY), ' 22:30:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 25 DAY), ' 07:00:00'), 8.5, 4, 4.0, 0, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 26 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 27 DAY), ' 23:30:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 26 DAY), ' 07:30:00'), 8.0, 4, 3.5, 1, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 27 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 28 DAY), ' 02:00:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 27 DAY), ' 10:00:00'), 8.0, 2, 2.5, 4, '熬夜'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 28 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 29 DAY), ' 23:00:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 28 DAY), ' 07:00:00'), 8.0, 4, 3.5, 1, ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 29 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 30 DAY), ' 23:30:00'), CONCAT(DATE_SUB(CURDATE(), INTERVAL 29 DAY), ' 07:30:00'), 8.0, 3, 3.2, 2, '');

-- ============================================
-- 5. 情绪记录（近30天）
-- ============================================
INSERT INTO `mood_record` (`student_id`, `record_date`, `record_time`, `mood_type`, `mood_score`, `trigger_event`, `description`) VALUES
-- 本周情绪
(@student_id, CURDATE(), CONCAT(CURDATE(), ' 09:30:00'), 1, 8, '天气晴朗', '今天心情很好，准备去运动'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 1 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 14:00:00'), 2, 7, '', '平静地度过一天'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 2 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 2 DAY), ' 20:00:00'), 3, 5, '作业太多', '有点焦虑，ddl快到了'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 3 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 3 DAY), ' 11:00:00'), 1, 9, '考试通过', '数据结构考试过了！'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 4 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 4 DAY), ' 16:00:00'), 6, 4, '项目压力', '小组项目进度落后'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 5 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 5 DAY), ' 19:00:00'), 2, 6, '', ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 6 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 6 DAY), ' 15:00:00'), 1, 8, '和朋友聚餐', '周末很开心'),
-- 第二周情绪
(@student_id, DATE_SUB(CURDATE(), INTERVAL 7 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 7 DAY), ' 10:00:00'), 2, 7, '', ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 8 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 8 DAY), ' 14:30:00'), 1, 8, '运动后', '健身后心情愉悦'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 9 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 9 DAY), ' 21:00:00'), 3, 5, '失眠', '晚上睡不着有点烦'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 10 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 10 DAY), ' 12:00:00'), 2, 6, '', ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 11 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 11 DAY), ' 16:00:00'), 1, 9, '拿到奖学金', '努力得到回报'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 12 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 12 DAY), ' 18:00:00'), 2, 7, '', ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 13 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 13 DAY), ' 22:00:00'), 1, 8, '和家人视频', '和父母聊天很开心'),
-- 第三周情绪
(@student_id, DATE_SUB(CURDATE(), INTERVAL 14 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 14 DAY), ' 09:00:00'), 2, 6, '', ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 15 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 15 DAY), ' 15:00:00'), 3, 4, '被老师批评', '实验报告写得不好'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 16 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 16 DAY), ' 11:00:00'), 2, 7, '', ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 17 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 17 DAY), ' 17:00:00'), 1, 8, '比赛获奖', '篮球赛获得第二名'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 18 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 18 DAY), ' 20:00:00'), 6, 5, '考试周', '期末考试压力大'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 19 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 19 DAY), ' 14:00:00'), 2, 6, '', ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 20 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 20 DAY), ' 16:00:00'), 1, 9, '周末放松', '睡了个懒觉'),
-- 第四周情绪
(@student_id, DATE_SUB(CURDATE(), INTERVAL 21 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 21 DAY), ' 10:00:00'), 2, 7, '', ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 22 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 22 DAY), ' 13:00:00'), 1, 8, '代码写完', '终于完成了项目'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 23 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 23 DAY), ' 19:00:00'), 4, 4, '和室友争吵', '为小事吵了一架'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 24 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 24 DAY), ' 11:00:00'), 2, 6, '', ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 25 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 25 DAY), ' 15:00:00'), 1, 8, '和室友和好', '化解了矛盾'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 26 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 26 DAY), ' 18:00:00'), 2, 7, '', ''),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 27 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 27 DAY), ' 21:00:00'), 3, 5, '熬夜', '熬夜后状态不好'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 28 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 28 DAY), ' 10:00:00'), 1, 8, '', '恢复正常'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 29 DAY), CONCAT(DATE_SUB(CURDATE(), INTERVAL 29 DAY), ' 14:00:00'), 2, 7, '', '');

-- ============================================
-- 6. 体检报告（2次）
-- ============================================
INSERT INTO `health_examination` (`student_id`, `exam_date`, `exam_type`, `height`, `weight`, `bmi`, `blood_pressure_high`, `blood_pressure_low`, `heart_rate`, `vision_left`, `vision_right`, `blood_sugar`, `hemoglobin`, `white_blood_cell`, `platelet`, `overall_conclusion`, `doctor_advice`) VALUES
(@student_id, DATE_SUB(CURDATE(), INTERVAL 30 DAY), '年度体检', 178.0, 72.0, 22.7, 120, 78, 75, 4.8, 4.9, 5.1, 148.0, 6.8, 225.0, 
'身体状况良好，各项指标基本正常。BMI指数在正常范围内，心肺功能良好。',
'1. 继续保持良好的运动习惯\n2. 注意用眼卫生，避免长时间使用电子设备\n3. 保持规律作息，减少熬夜\n4. 饮食注意营养均衡，适当增加蔬果摄入'),
(@student_id, DATE_SUB(CURDATE(), INTERVAL 210 DAY), '入学体检', 177.0, 70.0, 22.3, 118, 75, 72, 5.0, 5.0, 4.9, 145.0, 6.5, 220.0, 
'身体健康，适合参加各项体育活动。',
'保持健康的生活方式');

-- ============================================
-- 7. AI健康报告（3份）
-- ============================================
INSERT INTO `ai_health_report` (`student_id`, `report_date`, `analysis_period`, `overall_score`, `diet_analysis`, `exercise_analysis`, `sleep_analysis`, `mood_analysis`, `health_risks`, `recommendations`, `ai_model_version`) VALUES
(@student_id, CURDATE(), '近7天', 85.0, 
'【饮食分析】
本周饮食规律性良好，三餐按时率达到95%。
- 热量摄入：平均每日2050千卡，处于正常范围
- 蛋白质：每日平均摄入85g，满足日常需求
- 碳水化合物：摄入适中，占总能量55%
- 脂肪：略偏高，建议减少油炸食品

亮点：早餐坚持率高，营养搭配较为合理
改进：晚餐偶有高热量食物，建议控制', 

'【运动分析】
本周运动频率良好，共进行6次体育锻炼。
- 运动时长：累计260分钟，日均37分钟
- 热量消耗：总计2100千卡
- 运动类型：跑步、篮球、健身、游泳等多样化
- 运动强度：中等强度为主，合理搭配高强度

亮点：运动种类丰富，有氧与力量训练结合
建议：保持当前运动频率，可适当增加柔韧性训练', 

'【睡眠分析】
本周睡眠质量整体良好。
- 平均睡眠时长：7.7小时，达标
- 深度睡眠：平均3.2小时，占比41%，良好
- 入睡时间：大部分在23:00-24:00之间
- 睡眠质量：平均3.3分（满分4分）

注意：有1天熬夜至凌晨1点，影响次日状态
建议：保持规律作息，尽量在23:00前入睡', 

'【情绪分析】
本周情绪状态总体积极正面。
- 正面情绪（开心、平静）：占比71%
- 负面情绪（焦虑、压力）：占比29%
- 平均情绪评分：6.7分（满分10分）

主要情绪触发因素：
- 正面：考试通过、运动、社交
- 负面：作业压力、项目deadline

建议：学会时间管理，提前规划任务避免临时压力', 

'【潜在健康风险】
1. 偶发熬夜可能影响免疫力和第二天的精神状态
2. 晚餐偶有高热量饮食，长期可能导致体重增加
3. 学业压力需要关注，避免持续焦虑', 

'【改善建议】
1. 睡眠：坚持23:00前入睡，保持8小时睡眠
2. 饮食：控制晚餐热量，减少油炸食品和含糖饮料
3. 运动：继续保持，可增加瑜伽或拉伸训练
4. 情绪：学习压力管理技巧，如冥想、深呼吸
5. 用眼：每用眼45分钟休息5-10分钟，多远眺
6. 社交：保持与朋友、家人的交流，有助于情绪调节', 

'HealthAI v1.0'),

(@student_id, DATE_SUB(CURDATE(), INTERVAL 7 DAY), '近7天', 82.5, 
'饮食基本规律，营养摄入均衡。蛋白质摄入充足，但周末聚餐热量偏高，需注意控制。建议减少烧烤、火锅等高脂肪食物的频率。', 
'运动表现优秀，本周进行了足球、健身、骑行等多种运动，总时长达到280分钟。建议保持当前运动频率，注意运动后拉伸放松。', 
'睡眠时长达标，但有2天入睡时间超过凌晨1点，影响睡眠质量。深度睡眠比例有所下降，建议改善睡前习惯，避免使用电子设备。', 
'情绪总体稳定，本周获得奖学金带来的喜悦明显提升了整体情绪水平。偶有失眠导致的烦躁，建议通过运动或音乐放松。', 
'1. 周末饮食热量超标\n2. 熬夜频率需要控制\n3. 注意眼睛疲劳问题', 
'1. 周末聚餐选择更健康的方式\n2. 尽量减少熬夜，保持规律作息\n3. 继续保持运动习惯\n4. 适当进行眼保健操', 
'HealthAI v1.0'),

(@student_id, DATE_SUB(CURDATE(), INTERVAL 14 DAY), '近7天', 80.0, 
'饮食规律性一般，有3天晚餐时间较晚。快餐摄入频率偏高，建议增加蔬菜水果的摄入量，减少油炸食品。', 
'运动频率良好，但种类略显单一。建议在跑步基础上增加力量训练和柔韧性训练，形成更全面的运动计划。', 
'睡眠时长达标，但入睡时间不规律。有1天因熬夜导致睡眠质量较差，深度睡眠时间不足。建议建立固定的睡眠时间。', 
'本周经历了被老师批评的负面事件，情绪有所波动。但通过运动和社交活动有效调节。建议遇到挫折时积极面对，寻求支持。', 
'1. 快餐摄入频率偏高\n2. 睡眠时间不规律\n3. 情绪波动需要关注', 
'1. 减少快餐，增加食堂和自制餐的比例\n2. 固定睡眠时间，培养良好睡眠习惯\n3. 遇到困难及时与老师、朋友沟通\n4. 保持运动，运动是最好的情绪调节方式', 
'HealthAI v1.0');

-- ============================================
-- 8. 系统通知
-- ============================================
INSERT INTO `system_notification` (`student_id`, `title`, `content`, `type`, `priority`, `is_read`, `read_time`) VALUES
(@student_id, '欢迎使用健康管理系统', '亲爱的周小明同学，欢迎使用大学生健康生活管理与预警系统！请及时记录您的健康数据，我们将为您提供个性化的健康建议。', 1, 1, 1, DATE_SUB(NOW(), INTERVAL 25 DAY)),
(@student_id, '本周健康报告已生成', '您的本周健康报告已生成，整体健康评分85分，请点击查看详情。', 2, 2, 1, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(@student_id, '运动目标达成提醒', '恭喜！您本周运动时长已达260分钟，超额完成每周150分钟的运动目标！继续保持！', 2, 1, 1, NOW()),
(@student_id, '睡眠建议', '系统检测到您近期有熬夜情况，建议保持规律作息，每晚23:00前入睡，保证7-8小时睡眠。', 2, 2, 0, NULL),
(@student_id, '体检提醒', '距离您上次体检已过去30天，建议您关注身体变化，如有不适请及时就医。下次年度体检时间约在6个月后。', 3, 1, 0, NULL),
(@student_id, '饮食营养建议', '根据您近期的饮食记录，建议增加蔬菜水果的摄入，每日蔬果摄入量建议达到500克以上。', 2, 1, 0, NULL),
(@student_id, '系统更新通知', '系统已升级至2.0版本，新增管理员后台功能，包含数据统计分析、健康预警、批量数据导出等功能。', 1, 1, 1, DATE_SUB(NOW(), INTERVAL 3 DAY));

-- ============================================
-- 脚本执行完成
-- ============================================
-- 学生6账号信息：
-- 学号：2021006
-- 密码：password123
-- 姓名：周小明
-- 
-- 数据统计：
-- - 饮食记录：90条（30天 × 3餐）
-- - 运动记录：28条
-- - 睡眠记录：30条
-- - 情绪记录：30条
-- - 体检报告：2份
-- - AI健康报告：3份
-- - 系统通知：7条
-- ============================================
