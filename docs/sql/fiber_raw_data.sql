CREATE TABLE IF NOT EXISTS fiber_raw_data (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  sensor_id VARCHAR(64) NOT NULL,
  device_no VARCHAR(64) NOT NULL,
  fiber_no VARCHAR(64) NOT NULL,
  raw_value DECIMAL(18,6) NOT NULL,
  wavelength DECIMAL(18,6) NOT NULL,
  wavelength_shift DECIMAL(18,6) NOT NULL,
  intensity DECIMAL(18,6) NOT NULL,
  collect_time DATETIME NOT NULL,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  UNIQUE KEY uk_fiber_sensor_collect (sensor_id, collect_time),
  KEY idx_fiber_sensor_collect (sensor_id, collect_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
