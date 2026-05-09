CREATE TABLE IF NOT EXISTS vibrating_wire_data (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  sensor_id VARCHAR(64) NOT NULL,
  device_no VARCHAR(64) NOT NULL,
  channel_no VARCHAR(32) NOT NULL,
  frequency DECIMAL(18,6) NOT NULL,
  temperature DECIMAL(18,6) NOT NULL,
  tension DECIMAL(18,6) DEFAULT NULL,
  strain_value DECIMAL(18,6) DEFAULT NULL,
  collect_time DATETIME NOT NULL,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  UNIQUE KEY uk_vibrating_wire_sensor_collect (sensor_id, collect_time),
  KEY idx_vibrating_wire_sensor_collect (sensor_id, collect_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
