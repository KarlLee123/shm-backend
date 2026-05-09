CREATE TABLE IF NOT EXISTS vibration_dat_data (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  sensor_id VARCHAR(64) NOT NULL,
  device_no VARCHAR(64) NOT NULL,
  file_name VARCHAR(255) NOT NULL,
  file_path VARCHAR(500) NOT NULL,
  file_size BIGINT DEFAULT NULL,
  sample_rate DECIMAL(18,6) DEFAULT NULL,
  channel_count INT DEFAULT NULL,
  point_count INT DEFAULT NULL,
  duration_seconds DECIMAL(18,6) DEFAULT NULL,
  raw_content LONGTEXT,
  collect_time DATETIME NOT NULL,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  UNIQUE KEY uk_vibration_dat_sensor_collect (sensor_id, collect_time),
  KEY idx_vibration_dat_sensor_collect (sensor_id, collect_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
