-- =====================================================
-- 紫微斗数模块 - 数据库初始化脚本
-- 数据库：ziwei
-- =====================================================

CREATE DATABASE IF NOT EXISTS ziwei DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ziwei;

-- 命盘主表
DROP TABLE IF EXISTS ziwei_chart;
CREATE TABLE ziwei_chart (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    user_id BIGINT COMMENT '用户ID',
    -- 出生信息
    solar_year INT NOT NULL COMMENT '公历年',
    solar_month TINYINT NOT NULL COMMENT '公历月',
    solar_day TINYINT NOT NULL COMMENT '公历日',
    solar_hour TINYINT NOT NULL COMMENT '公历时',
    solar_minute TINYINT DEFAULT 0 COMMENT '公历分',
    gender TINYINT NOT NULL COMMENT '0-女 1-男',
    birth_place VARCHAR(200) COMMENT '出生地',
    is_dst BIT DEFAULT 0 COMMENT '是否夏令时',
    -- 农历
    lunar_year INT COMMENT '农历年',
    lunar_month TINYINT COMMENT '农历月',
    lunar_day TINYINT COMMENT '农历日',
    is_leap_month BIT DEFAULT 0 COMMENT '是否闰月',
    -- 八字
    year_pillar VARCHAR(10) COMMENT '年柱',
    month_pillar VARCHAR(10) COMMENT '月柱',
    day_pillar VARCHAR(10) COMMENT '日柱',
    hour_pillar VARCHAR(10) COMMENT '时柱',
    -- 命盘核心
    ming_gong_dizhi VARCHAR(5) COMMENT '命宫地支',
    shen_gong_dizhi VARCHAR(5) COMMENT '身宫地支',
    wuxing_ju VARCHAR(20) COMMENT '五行局',
    -- 完整JSON备查
    chart_json LONGTEXT COMMENT '命盘完整JSON',
    -- 标准字段
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    creator VARCHAR(64), updater VARCHAR(64), deleted BIT DEFAULT 0,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='紫微斗数-命盘';

-- 十二宫位
DROP TABLE IF EXISTS ziwei_palace;
CREATE TABLE ziwei_palace (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    chart_id BIGINT NOT NULL COMMENT '命盘ID',
    palace_type TINYINT NOT NULL COMMENT '宫位类型 1-12',
    dizhi VARCHAR(5) NOT NULL COMMENT '宫位地支',
    tian_gan VARCHAR(5) COMMENT '宫干',
    is_shen_gong BIT DEFAULT 0 COMMENT '是否身宫',
    da_xian_start_age TINYINT COMMENT '大限起始岁数',
    da_xian_end_age TINYINT COMMENT '大限结束岁数',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator VARCHAR(64), updater VARCHAR(64), deleted BIT DEFAULT 0,
    INDEX idx_chart_id (chart_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='紫微斗数-宫位';

-- 星曜落位
DROP TABLE IF EXISTS ziwei_star_position;
CREATE TABLE ziwei_star_position (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    chart_id BIGINT NOT NULL COMMENT '命盘ID',
    palace_id BIGINT NOT NULL COMMENT '宫位ID',
    star_code VARCHAR(20) NOT NULL COMMENT '星曜编码',
    star_type TINYINT NOT NULL COMMENT '1-主星 2-辅星 3-杂曜',
    brightness TINYINT COMMENT '1-庙 2-旺 3-得 4-利 5-平 6-陷',
    sihua_type TINYINT COMMENT '四化 1-禄 2-权 3-科 4-忌',
    sort_order INT DEFAULT 0 COMMENT '同宫排序',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator VARCHAR(64), updater VARCHAR(64), deleted BIT DEFAULT 0,
    INDEX idx_chart_id (chart_id),
    INDEX idx_palace_id (palace_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='紫微斗数-星曜落位';

-- 四化
DROP TABLE IF EXISTS ziwei_sihua;
CREATE TABLE ziwei_sihua (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    chart_id BIGINT NOT NULL COMMENT '命盘ID',
    sihua_scope TINYINT NOT NULL COMMENT '1-生年 2-大限 3-流年',
    ref_year INT COMMENT '参考年',
    hua_lu_star_code VARCHAR(20) COMMENT '化禄星',
    hua_quan_star_code VARCHAR(20) COMMENT '化权星',
    hua_ke_star_code VARCHAR(20) COMMENT '化科星',
    hua_ji_star_code VARCHAR(20) COMMENT '化忌星',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator VARCHAR(64), updater VARCHAR(64), deleted BIT DEFAULT 0,
    INDEX idx_chart_id (chart_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='紫微斗数-四化';

-- 大限
DROP TABLE IF EXISTS ziwei_daxian;
CREATE TABLE ziwei_daxian (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    chart_id BIGINT NOT NULL COMMENT '命盘ID',
    sequence_order TINYINT NOT NULL COMMENT '序号1-12',
    age_start TINYINT NOT NULL COMMENT '起始岁数',
    age_end TINYINT NOT NULL COMMENT '结束岁数',
    cal_year_start INT COMMENT '公历起始年',
    cal_year_end INT COMMENT '公历结束年',
    palace_type TINYINT NOT NULL COMMENT '所在宫位',
    direction BIT NOT NULL COMMENT '0-逆行 1-顺行',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator VARCHAR(64), updater VARCHAR(64), deleted BIT DEFAULT 0,
    INDEX idx_chart_id (chart_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='紫微斗数-大限';
