# Ansteel SHM - Deflection Module API 接口说明

Deflection 模块分离终稿。仅服务于挠度模块，不含 stress 字段。

**Base URL**：`http://localhost:8080`

**模块标签**：`Deflection`

## 接口清单

- `POST /api/sensor/deflection/raw/upload`：原始数据上传
- `POST /api/sensor/deflection/result/upload`：结果回写
- `POST /api/data/deflection/verify`：人工核验
- `POST /api/data/deflection/latest`：查询最新核验通过数据
- `POST /api/data/deflection/history`：查询历史核验通过数据

## 接口详情

### POST /api/sensor/deflection/raw/upload

**摘要**：原始数据上传

**标签**：`Deflection`

#### 请求体

Schema：`DeflectionRawUploadDTO`

### DeflectionRawUploadDTO

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| sensorId | string | 是 | 示例：`D001` |
| displacementValue | number | 是 | 示例：`3.42` |
| collectTime | string | 是 | 示例：`2026-04-20 14:30:00` |

#### 响应说明

**200**：success

Schema：`ResultVoid`

---

### POST /api/sensor/deflection/result/upload

**摘要**：结果回写

**标签**：`Deflection`

#### 请求体

Schema：`DeflectionResultUploadDTO`

### DeflectionResultUploadDTO

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| sensorId | string | 是 | 示例：`D001` |
| calcDeflection | number | 是 | 示例：`12.56` |
| collectTime | string | 是 | 示例：`2026-04-20 14:30:00` |
| calcTime | string | 否 | 示例：`2026-04-20 14:30:05` |

#### 响应说明

**200**：success

Schema：`ResultVoid`

---

### POST /api/data/deflection/verify

**摘要**：人工核验

**标签**：`Deflection`

#### 请求体

Schema：`DeflectionVerifyDTO`

### DeflectionVerifyDTO

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| sensorId | string | 是 | 示例：`D001` |
| action | string | 是 | 示例：`PASS` |
| verifiedDeflection | number | 否 | 示例：`12.4` |
| verifyRemark | string | 否 | 示例：`挠度结果异常，要求重新计算` |
| collectTime | string | 是 | 示例：`2026-04-20 14:30:00` |
| verifyTime | string | 否 | 示例：`2026-04-20 14:31:00` |

#### 响应说明

**200**：success

Schema：`ResultVoid`

---

### POST /api/data/deflection/latest

**摘要**：查询最新核验通过数据

**标签**：`Deflection`

#### 请求体

Schema：`DeflectionQueryDTO`

### DeflectionQueryDTO

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| sensorId | string | 是 | 示例：`D001` |
| startTime | string | 否 | 示例：`2026-04-20 00:00:00` |
| endTime | string | 否 | 示例：`2026-04-20 23:59:59` |
| limit | integer | 否 | 示例：`100` |

#### 响应说明

**200**：success

Schema：`DeflectionLatestResponse`

---

### POST /api/data/deflection/history

**摘要**：查询历史核验通过数据

**标签**：`Deflection`

#### 请求体

Schema：`DeflectionQueryDTO`

### DeflectionQueryDTO

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| sensorId | string | 是 | 示例：`D001` |
| startTime | string | 否 | 示例：`2026-04-20 00:00:00` |
| endTime | string | 否 | 示例：`2026-04-20 23:59:59` |
| limit | integer | 否 | 示例：`100` |

#### 响应说明

**200**：success

Schema：`DeflectionHistoryResponse`

---

## Schema 定义

### DeflectionRawUploadDTO

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| sensorId | string | 是 | 示例：`D001` |
| displacementValue | number | 是 | 示例：`3.42` |
| collectTime | string | 是 | 示例：`2026-04-20 14:30:00` |

### DeflectionResultUploadDTO

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| sensorId | string | 是 | 示例：`D001` |
| calcDeflection | number | 是 | 示例：`12.56` |
| collectTime | string | 是 | 示例：`2026-04-20 14:30:00` |
| calcTime | string | 否 | 示例：`2026-04-20 14:30:05` |

### DeflectionVerifyDTO

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| sensorId | string | 是 | 示例：`D001` |
| action | string | 是 | 示例：`PASS` |
| verifiedDeflection | number | 否 | 示例：`12.4` |
| verifyRemark | string | 否 | 示例：`挠度结果异常，要求重新计算` |
| collectTime | string | 是 | 示例：`2026-04-20 14:30:00` |
| verifyTime | string | 否 | 示例：`2026-04-20 14:31:00` |

### DeflectionQueryDTO

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| sensorId | string | 是 | 示例：`D001` |
| startTime | string | 否 | 示例：`2026-04-20 00:00:00` |
| endTime | string | 否 | 示例：`2026-04-20 23:59:59` |
| limit | integer | 否 | 示例：`100` |

### DeflectionMonitorVO

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| sensorId | string | 否 | 示例：`D001` |
| displacementValue | number | 否 | 示例：`3.42` |
| calcDeflection | number | 否 | 示例：`12.56` |
| verifiedDeflection | number | 否 | 示例：`12.4` |
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

### DeflectionLatestResponse

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| code | integer | 否 | 示例：`200` |
| message | string | 否 | 示例：`success` |
| data | DeflectionMonitorVO | 否 | - |

### DeflectionHistoryResponse

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| code | integer | 否 | 示例：`200` |
| message | string | 否 | 示例：`success` |
| data | array | 否 | - |
