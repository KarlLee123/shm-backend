# Deflection Database Design

## 模块说明

本文件只服务于 `deflection` 模块。

当前建表采用 **“原始输入 + 结果回写 + 核验闸口”** 的策略。

### 原始输入字段
- `sensor_id`
- `displacement_value`
- `collect_time`

### 结果回写字段
- `calc_deflection`
- `calc_time`

### 核验字段
- `verified_deflection`
- `verify_status`
- `verify_remark`
- `verify_time`

---

## 字段清单

| 字段名 | 类型建议 | 说明 |
| --- | --- | --- |
| `id` | BIGINT UNSIGNED | 主键 ID |
| `sensor_id` | VARCHAR(64) | 传感器 ID |
| `displacement_value` | DECIMAL(18,6) | 原始位移值 |
| `calc_deflection` | DECIMAL(18,6) | Python 计算挠度 |
| `verified_deflection` | DECIMAL(18,6) | 核验通过后的挠度值 |
| `verify_status` | TINYINT | 状态：0/1/2/3 |
| `verify_remark` | VARCHAR(255) | 核验备注 |
| `collect_time` | DATETIME | 采样时间 |
| `calc_time` | DATETIME | 结果回写时间 |
| `verify_time` | DATETIME | 核验时间 |
| `create_time` | DATETIME | 创建时间 |
| `deleted` | TINYINT | 逻辑删除标记 |

---

## 唯一性约束

```sql
UNIQUE KEY `uk_sensor_collect_time` (`sensor_id`, `collect_time`)
```

---

## SQL 语义

### 原始上传
```sql
INSERT INTO deflection_data
(sensor_id, displacement_value, collect_time, create_time, verify_status, deleted)
VALUES (?, ?, ?, NOW(), 0, 0);
```

### 结果回写
```sql
UPDATE deflection_data
SET calc_deflection = ?,
    calc_time = ?,
    verify_status = 1
WHERE sensor_id = ?
  AND collect_time = ?
  AND deleted = 0
  AND verify_status IN (0, 3);
```

### 核验通过
```sql
UPDATE deflection_data
SET verified_deflection = ?,
    verify_time = ?,
    verify_remark = NULL,
    verify_status = 2
WHERE sensor_id = ?
  AND collect_time = ?
  AND deleted = 0
  AND verify_status = 1;
```

### 核验驳回
```sql
UPDATE deflection_data
SET verify_remark = ?,
    verify_time = ?,
    verify_status = 3
WHERE sensor_id = ?
  AND collect_time = ?
  AND deleted = 0
  AND verify_status = 1;
```

### 最新业务数据
```sql
SELECT ...
FROM deflection_data
WHERE sensor_id = ?
  AND deleted = 0
  AND verify_status = 2
ORDER BY collect_time DESC
LIMIT 1;
```

### 历史业务数据
```sql
SELECT ...
FROM deflection_data
WHERE sensor_id = ?
  AND deleted = 0
  AND verify_status = 2
  AND collect_time >= ?
  AND collect_time <= ?
ORDER BY collect_time ASC
LIMIT ?;
```

---

## 一句话结论

> `deflection_data` 只服务于挠度模块，不承载 stress 结果字段。
