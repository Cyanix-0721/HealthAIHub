-- 创建数据库
DROP DATABASE IF EXISTS health_ai_hub;

CREATE DATABASE health_ai_hub;

USE health_ai_hub;

-- 创建用户表
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    password VARCHAR(60) NOT NULL COMMENT '密码',
    -- 通常密码哈希长度为60
    avatar VARCHAR(255) COMMENT '头像',
    email VARCHAR(100) UNIQUE NOT NULL COMMENT '邮箱',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间'
) COMMENT '用户表';

-- 创建用户好友表
CREATE TABLE user_friends (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    friend_id BIGINT NOT NULL COMMENT '好友的用户ID',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '好友状态（1=已添加，0=请求中，-1=已拒绝）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '添加好友的时间',
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (friend_id) REFERENCES user(id)
) COMMENT '用户好友表';

-- 创建健康数据表
CREATE TABLE health_data (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '数据ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    height FLOAT NOT NULL COMMENT '身高(cm)',
    weight FLOAT NOT NULL COMMENT '体重(kg)',
    heart_rate INT NOT NULL COMMENT '心率(次/分)',
    blood_pressure VARCHAR(7) NOT NULL COMMENT '血压(mmHg)',
    blood_oxygen INT NOT NULL COMMENT '血氧(%)',
    blood_sugar FLOAT NOT NULL COMMENT '血糖(mmol/L)',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日期',
    FOREIGN KEY (user_id) REFERENCES user(id)
) COMMENT '健康数据表';

-- 创建管理员表
CREATE TABLE admin (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '管理员ID',
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '管理员用户名',
    password VARCHAR(60) NOT NULL COMMENT '管理员密码',
    -- 通常密码哈希长度为60
    avatar VARCHAR(255) COMMENT '头像',
    email VARCHAR(100) UNIQUE NOT NULL COMMENT '邮箱',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间'
) COMMENT '管理员表';

-- 创建管理员操作日志表
CREATE TABLE admin_operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    admin_id BIGINT NOT NULL COMMENT '管理员ID',
    operation TEXT NOT NULL COMMENT '操作内容',
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    FOREIGN KEY (admin_id) REFERENCES admin(id)
) COMMENT '管理员操作日志表';

-- 创建用户操作日志表
CREATE TABLE user_operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    operation TEXT NOT NULL COMMENT '操作内容',
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    FOREIGN KEY (user_id) REFERENCES user(id)
) COMMENT '用户操作日志表';

-- 插入测试数据
-- 插入用户数据 密码均为Test12345
INSERT INTO
    user (username, password, email)
VALUES
    (
        'user1',
        '$2a$10$9d/Kg9lWM.yru0yyz6oC1uPW.L7Po49/gyXX.at9EkWRKjnEy/oiq',
        'user1@example.com'
    ),
    (
        'user2',
        '$2a$10$9d/Kg9lWM.yru0yyz6oC1uPW.L7Po49/gyXX.at9EkWRKjnEy/oiq',
        'user2@example.com'
    ),
    (
        'user3',
        '$2a$10$9d/Kg9lWM.yru0yyz6oC1uPW.L7Po49/gyXX.at9EkWRKjnEy/oiq',
        'user3@example.com'
    );

-- 插入用户好友数据
INSERT INTO
    user_friends (user_id, friend_id, status)
VALUES
    (1, 2, 1),
    (1, 3, 1),
    (2, 3, 0);

-- 插入健康数据
INSERT INTO
    health_data (
        user_id,
        height,
        weight,
        heart_rate,
        blood_pressure,
        blood_oxygen,
        blood_sugar
    )
VALUES
    (1, 175.5, 70.2, 72, '120/80', 98, 5.4),
    (2, 180.0, 75.5, 68, '118/78', 99, 5.2),
    (3, 165.0, 60.0, 75, '122/82', 97, 5.6);

-- 插入管理员数据
INSERT INTO
    admin (username, password, email)
VALUES
    (
        'admin1',
        '$2a$10$9d/Kg9lWM.yru0yyz6oC1uPW.L7Po49/gyXX.at9EkWRKjnEy/oiq',
        'admin1@example.com'
    ),
    (
        'admin2',
        '$2a$10$9d/Kg9lWM.yru0yyz6oC1uPW.L7Po49/gyXX.at9EkWRKjnEy/oiq',
        'admin2@example.com'
    );

-- 插入管理员操作日志
INSERT INTO
    admin_operation_log (admin_id, operation)
VALUES
    (1, '修改了用户1的权限'),
    (2, '删除了一条违规评论');

-- 插入用户操作日志
INSERT INTO
    user_operation_log (user_id, operation)
VALUES
    (1, '更新了个人信息'),
    (2, '添加了新的健康数据'),
    (3, '发送了一条私信');