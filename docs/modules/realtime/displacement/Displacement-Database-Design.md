# Displacement Database Design

## 模块说明

当前建表采用 **“直采即展示、最小闭环优先”** 的策略。

表中以下字段用于承载传感器原始数据，并支持直接入库与前端展示：

- `sensor_id`
- `displacement_value`
- `collect_time`

当前阶段，本模块不引入 Python 计算字段与人工核验字段。

---

## 建表设计说明

### 1. 先把 MVP 跑通

当前最小可行闭环只需要：

- `sensor_id`
- `displacement_value`
- `collect_time`

这三类原始数据就足够前端展示最新值和历史值。

### 2. 本模块属于直采实时模块

本模块数据特点为：

- 传感器直接采集；
- 后端直接入库；
- 前端直接展示；
- 不依赖 `verify_status`；
- 不依赖 `calc_*` 或 `verified_*` 字段。

### 3. `deleted` 作为统一逻辑删除标记

当前阶段查询统一要求：

- latest 查询：`deleted = 0`
- history 查询：`deleted = 0`

---

## 字段清单

| 字段名 | 类型建议 | 说明 |
| --- | --- | --- |
| `id` | BIGINT UNSIGNED | 主键 ID |
| `sensor_id` | VARCHAR(64) | 传感器 ID |
| `displacement_value` | DECIMAL(18,6) | 位移值 |
| `collect_time` | DATETIME | 传感器采集时间（北京时间） |
| `create_time` | DATETIME | 记录创建时间 |
| `deleted` | TINYINT | 逻辑删除标记：0 未删除，1 已删除 |

---

## 唯一性约束

建议建立唯一约束：

```sql
UNIQUE KEY `uk_sensor_collect_time` (`sensor_id`, `collect_time`)
```

含义：

> 同一个 `sensor_id + collect_time` 只对应一条原始采样记录。

---

## 给审查方的说明

可直接附这句：

```text
当前建表采用“直采即展示、最小闭环优先”的策略。表中 displacement_value、collect_time 等字段用于承载传感器原始数据，支持直接入库与前端展示；本模块当前阶段不引入 Python 计算与人工核验链路。
```

---

## 常用 SQL 语义

### 1. 原始数据上传

```sql
INSERT INTO displacement_data
(sensor_id, displacement_value, collect_time, create_time, deleted)
VALUES (?, ?, ?, NOW(), 0);
```

### 2. 查最新原始数据

```sql
SELECT id, sensor_id, displacement_value, collect_time
FROM displacement_data
WHERE sensor_id = ?
  AND deleted = 0
ORDER BY collect_time DESC
LIMIT 1;
```

### 3. 查历史原始数据

```sql
SELECT id, sensor_id, displacement_value, collect_time
FROM displacement_data
WHERE sensor_id = ?
  AND deleted = 0
  AND collect_time >= ?
  AND collect_time <= ?
ORDER BY collect_time ASC
LIMIT ?;
```

---

## 一句话结论

> 这张表当前的定位是：**原始数据直接跑通，后端直接展示，无需计算与核验。**
