-- Seata demo schema and seed data
-- Run with: mysql -u root -p < seata-demo-schema.sql

-- 1) Create databases
CREATE DATABASE IF NOT EXISTS `order_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE DATABASE IF NOT EXISTS `user_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 2) Order DB schema
USE `order_db`;

-- orders table
CREATE TABLE IF NOT EXISTS `orders` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_no` VARCHAR(64) NOT NULL,
  `user_id` BIGINT NOT NULL,
  `amount` DECIMAL(12,2) NOT NULL,
  `status` VARCHAR(32) NOT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Seata undo_log table (required for rm datasource)
CREATE TABLE IF NOT EXISTS `undo_log` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `branch_id` BIGINT(20) NOT NULL,
  `xid` VARCHAR(100) NOT NULL,
  `context` VARCHAR(128) DEFAULT NULL,
  `rollback_info` LONGBLOB NOT NULL,
  `log_status` INT(11) NOT NULL,
  `log_created` DATETIME DEFAULT NULL,
  `log_modified` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_undo_log_xid` (`xid`),
  KEY `idx_undo_log_log_status` (`log_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3) User DB schema
USE `user_db`;

-- users table with balance for demo
CREATE TABLE IF NOT EXISTS `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(64) NOT NULL,
  `email` VARCHAR(128) DEFAULT NULL,
  `balance` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Seata undo_log table in user_db as well
CREATE TABLE IF NOT EXISTS `undo_log` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `branch_id` BIGINT(20) NOT NULL,
  `xid` VARCHAR(100) NOT NULL,
  `context` VARCHAR(128) DEFAULT NULL,
  `rollback_info` LONGBLOB NOT NULL,
  `log_status` INT(11) NOT NULL,
  `log_created` DATETIME DEFAULT NULL,
  `log_modified` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_undo_log_xid` (`xid`),
  KEY `idx_undo_log_log_status` (`log_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4) Seed demo data
USE `user_db`;
INSERT INTO users (id, username, email, balance) VALUES (1, 'demo_user', 'demo@example.com', 1000.00)
  ON DUPLICATE KEY UPDATE username=VALUES(username), email=VALUES(email), balance=VALUES(balance);

USE `order_db`;
-- optional sample order (can be empty for demo)
INSERT INTO orders (order_no, user_id, amount, status) VALUES ('ORD-DEMO-0001', 1, 100.00, 'CREATED')
  ON DUPLICATE KEY UPDATE amount=VALUES(amount), status=VALUES(status);

-- End of file
