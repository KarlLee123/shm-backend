# Ansteel SHM - Stress Module API 接口说明

Stress 模块分离终稿。仅服务于应力模块，不含 deflection 字段。

**Base URL**：`http://localhost:8080`

**模块标签**：`Stress`

## 接口清单

- `POST /api/sensor/stress/raw/upload`：原始数据上传
- `POST /api/sensor/stress/result/upload`：结果回写
- `POST /api/data/stress/verify`：人工核验
- `POST /api/data/stress/latest`：查询最新核验通过数据
- `POST /api/data/stress/history`：查询历史核验通过数据

## 接口详情

### POST /api/sensor/stress/raw/upload

**摘要**：原始数据上传

**标签**：`Stress`

#### 请求体

Schema：`StressRawUploadDTO`

### StressRawUploadDTO

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| sensorId | string | 是 | 示例：`S001` |
| forceValue | number | 是 | 示例：`125.6` |
| displacementValue | number | 是 | 示例：`3.42` |
| collectTime | string | 是 | 示例：`2026-04-20 14:30:00` |

#### 响应说明

**200**：success

Schema：`ResultVoid`

---

### POST /api/sensor/stress/result/upload

**摘要**：结果回写

**标签**：`Stress`

#### 请求体

Schema：`StressResultUploadDTO`

### StressResultUploadDTO

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| sensorId | string | 是 | 示例：`S001` |
| calcStress | number | 是 | 示例：`98.56` |
| collectTime | string | 是 | 示例：`2026-04-20 14:30:00` |
| calcTime | string | 否 | 示例：`2026-04-20 14:30:05` |

#### 响应说明

**200**：success

Schema：`ResultVoid`

---

### POST /api/data/stress/verify

**摘要**：人工核验

**标签**：`Stress`

#### 请求体

Schema：`StressVerifyDTO`

### StressVerifyDTO

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| sensorId | string | 是 | 示例：`S001` |
| action | string | 是 | 示例：`PASS` |
| verifiedStress | number | 否 | 示例：`98.4` |
| verifyRemark | string | 否 | 示例：`应力结果异常，要求重新计算` |
| collectTime | string | 是 | 示例：`2026-04-20 14:30:00` |
| verifyTime | string | 否 | 示例：`2026-04-20 14:31:00` |

#### 响应说明

**200**：success

Schema：`ResultVoid`

---

### POST /api/data/stress/latest

**摘要**：查询最新核验通过数据

**标签**：`Stress`

#### 请求体

Schema：`StressQueryDTO`

### StressQueryDTO

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| sensorId | string | 是 | 示例：`S001` |
| startTime | string | 否 | 示例：`2026-04-20 00:00:00` |
| endTime | string | 否 | 示例：`2026-04-20 23:59:59` |
| limit | integer | 否 | 示例：`100` |

#### 响应说明

**200**：success

Schema：`StressLatestResponse`

---

### POST /api/data/stress/history

**摘要**：查询历史核验通过数据

**标签**：`Stress`

#### 请求体

Schema：`StressQueryDTO`

### StressQueryDTO

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| sensorId | string | 是 | 示例：`S001` |
| startTime | string | 否 | 示例：`2026-04-20 00:00:00` |
| endTime | string | 否 | 示例：`2026-04-20 23:59:59` |
| limit | integer | 否 | 示例：`100` |

#### 响应说明

**200**：success

Schema：`StressHistoryResponse`

---

## Schema 定义

### StressRawUploadDTO

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| sensorId | string | 是 | 示例：`S001` |
| forceValue | number | 是 | 示例：`125.6` |
| displacementValue | number | 是 | 示例：`3.42` |
| collectTime | string | 是 | 示例：`2026-04-20 14:30:00` |

### StressResultUploadDTO

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| sensorId | string | 是 | 示例：`S001` |
| calcStress | number | 是 | 示例：`98.56` |
| collectTime | string | 是 | 示例：`2026-04-20 14:30:00` |
| calcTime | string | 否 | 示例：`2026-04-20 14:30:05` |

### StressVerifyDTO

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| sensorId | string | 是 | 示例：`S001` |
| action | string | 是 | 示例：`PASS` |
| verifiedStress | number | 否 | 示例：`98.4` |
| verifyRemark | string | 否 | 示例：`应力结果异常，要求重新计算` |
| collectTime | string | 是 | 示例：`2026-04-20 14:30:00` |
| verifyTime | string | 否 | 示例：`2026-04-20 14:31:00` |

### StressQueryDTO

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| sensorId | string | 是 | 示例：`S001` |
| startTime | string | 否 | 示例：`2026-04-20 00:00:00` |
| endTime | string | 否 | 示例：`2026-04-20 23:59:59` |
| limit | integer | 否 | 示例：`100` |

### StressMonitorVO

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| sensorId | string | 否 | 示例：`S001` |
| forceValue | number | 否 | 示例：`125.6` |
| displacementValue | number | 否 | 示例：`3.42` |
| calcStress | number | 否 | 示例：`98.56` |
| verifiedStress | number | 否 | 示例：`98.4` |
| verifyStatus | integer | 否 | 示例：`2` |
| verifyRemark | string (nullable) | 否 | - |
| collectTime | string | 否 | 示例：`2026-04-20 14:30:00` |
| calcTime | string | 否 | 示例：`2026-04-20 14:30:05` |
| verifyTime | string | 否 | 示例：`2026-04-20 14:31:00` |

### ResultVoid

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| code | integer | 否 | 示例：`200` |
| message | string | 否 | 示例：`success` |
| data |  (nullable) | 否 | - |

### StressLatestResponse

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| code | integer | 否 | 示例：`200` |
| message | string | 否 | 示例：`success` |
| data | StressMonitorVO | 否 | - |

### StressHistoryResponse

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| code | integer | 否 | 示例：`200` |
| message | string | 否 | 示例：`success` |
| data | array | 否 | - |
