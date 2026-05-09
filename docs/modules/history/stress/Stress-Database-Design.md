# Stress Database Design

## 模块说明

当前建表采用 **“MVP 先跑通、结果链路预留”** 的策略。

表中以下字段用于承载传感器原始数据，支持直接入库与前端展示：

- `sensor_id`
- `force_value`
- `displacement_value`
- `collect_time`

以下字段用于后续 Python 计算与人工核验链路，当前阶段先预留：

- `calc_stress`
- `verified_stress`
- `verify_status`
- `verify_remark`
- `calc_time`
- `verify_time`

当前阶段，这些预留字段 **不作为原始数据展示的前置条件**。

---

## 建表设计说明

### 1. 先把 MVP 跑通

当前最小可行闭环只需要：

- `sensor_id`
- `force_value`
- `displacement_value`
- `collect_time`

这四类原始数据就足够前端展示最新值和历史值。

### 2. 把后续坑位一次留好

后面还要接：

- `calc_stress`
- `verified_stress`
- `verify_status`
- `verify_remark`
- `calc_time`
- `verify_time`

现在不建，以后还得改表。

### 3. `verify_status` 不再阻塞原始数据展示

该字段当前定义为：

- `0`：只有原始数据，或还没进入结果流程
- `1`：已计算，待核验
- `2`：核验通过
- `3`：核验驳回

注意：

- 原始数据查询：**不看 `verify_status`**
- 结果数据查询：**只看 `verify_status = 2`**

这点是当前 1.1 版的核心。

---

## 字段清单

| 字段名 | 类型建议 | 说明 |
| --- | --- | --- |
| `id` | BIGINT UNSIGNED | 主键 ID |
| `sensor_id` | VARCHAR(64) | 传感器 ID |
| `force_value` | DECIMAL(18,6) | 原始力值 |
| `displacement_value` | DECIMAL(18,6) | 原始位移值 |
| `calc_stress` | DECIMAL(18,6) | Python 计算得到的应力值 |
| `verified_stress` | DECIMAL(18,6) | 核验通过后的应力值 |
| `verify_status` | TINYINT | 计算结果核验状态：0/1/2/3 |
| `verify_remark` | VARCHAR(255) | 核验备注 |
| `collect_time` | DATETIME | 传感器采集时间（北京时间） |
| `calc_time` | DATETIME | Python 结果回写时间（北京时间） |
| `verify_time` | DATETIME | 核验时间（北京时间） |
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
当前建表采用“MVP先跑通、结果链路预留”的策略。表中 force_value、displacement_value、collect_time 等字段用于承载传感器原始数据，支持直接入库与前端展示；calc_stress、verified_stress、verify_status 等字段用于后续 Python 计算与人工核验链路，当前阶段仅预留，不作为原始数据展示的前置条件。
```

---

## 常用 SQL 语义

### 1. 原始数据上传

```sql
INSERT INTO stress_data
(sensor_id, force_value, displacement_value, collect_time, create_time, deleted, verify_status)
VALUES (?, ?, ?, ?, NOW(), 0, 0);
```

### 2. 查最新原始数据

```sql
SELECT id, sensor_id, force_value, displacement_value, collect_time
FROM stress_data
WHERE sensor_id = ?
  AND deleted = 0
ORDER BY collect_time DESC
LIMIT 1;
```

### 3. 查历史原始数据

```sql
SELECT id, sensor_id, force_value, displacement_value, collect_time
FROM stress_data
WHERE sensor_id = ?
  AND deleted = 0
  AND collect_time >= ?
  AND collect_time <= ?
ORDER BY collect_time ASC
LIMIT ?;
```

---

## 一句话结论

> 这张表当前的定位是：**原始数据先跑，结果字段先占坑，后续 Python 与核验链路直接接上。**
