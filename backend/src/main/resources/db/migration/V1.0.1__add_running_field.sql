-- 添加 running 字段到 device 表
-- 用于跟踪设备的运行状态（工作中/待机）

ALTER TABLE device 
ADD COLUMN `running` TINYINT NOT NULL DEFAULT 0 
COMMENT '运行状态:0待机 1工作中' 
AFTER `online`;

-- 可选：如果你想给现有设备一个初始状态，可以执行以下语句
-- UPDATE device SET running = 0 WHERE running IS NULL;
