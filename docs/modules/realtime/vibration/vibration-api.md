# Ansteel SHM - Vibration Module API 接口说明

振动模块联调最终版（可导入 APIFox）。

已按当前直采实时模块字段对齐：VibrationUploadDTO / VibrationQueryDTO。
模块目标：上传原始数据 -> 查询 latest -> 查询 history。

**Base URL**：`http://localhost:8080`

**模块标签**：`Vibration`

## 接口清单

- `POST /api/sensor/vibration/upload`：原始振动数据上传
- `POST /api/data/vibration/latest`：查询最新振动数据
- `POST /api/data/vibration/history`：查询振动历史数据

## 接口详情

### POST /api/sensor/vibration/upload

**摘要**：原始振动数据上传

**说明**：接收传感器/设备上传的原始数据，写入 vibration_data。

**标签**：`Vibration`

#### 请求体

Schema：`VibrationUploadDTO`

### VibrationUploadDTO

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| sensorId | string | 是 | 传感器ID；示例：`SENSOR_001` |
| vibrationValue | number | 是 | 振动值；示例：`12.34` |
| collectTime | string | 是 | 采集时间，格式固定 yyyy-MM-dd HH:mm:ss；示例：`2026-04-18 14:30:00` |

#### 请求示例

**正常上传**

```json
{
  "sensorId": "SENSOR_001",
  "vibrationValue": 12.34,
  "collectTime": "2026-04-18 14:30:00"
}
```

#### 响应说明

**200**：上传成功

Schema：`GenericSuccessResponse`

**success**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

**400**：请求参数错误

Schema：`GenericErrorResponse`

**500**：服务端错误

Schema：`GenericErrorResponse`

---

### POST /api/data/vibration/latest

**摘要**：查询最新振动数据

**说明**：根据 sensorId 查询指定传感器最新一条数据。

**标签**：`Vibration`

#### 请求体

Schema：`VibrationLatestQueryDTO`

### VibrationLatestQueryDTO

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| sensorId | string | 是 | 传感器ID；示例：`SENSOR_001` |

#### 请求示例

**按传感器查询**

```json
{
  "sensorId": "SENSOR_001"
}
```

#### 响应说明

**200**：查询成功

Schema：`VibrationLatestResponse`

**success**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "sensorId": "SENSOR_001",
    "vibrationValue": 12.34,
    "collectTime": "2026-04-18 14:30:00"
  }
}
```

**empty**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

**400**：请求参数错误

Schema：`GenericErrorResponse`

**500**：服务端错误

Schema：`GenericErrorResponse`

---

### POST /api/data/vibration/history

**摘要**：查询振动历史数据

**说明**：根据 sensorId + 时间范围查询历史数据列表。

**标签**：`Vibration`

#### 请求体

Schema：`VibrationHistoryQueryDTO`

### VibrationHistoryQueryDTO

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| sensorId | string | 是 | 传感器ID；示例：`SENSOR_001` |
| startTime | string | 否 | 开始时间，格式固定 yyyy-MM-dd HH:mm:ss；示例：`2026-04-18 00:00:00` |
| endTime | string | 否 | 结束时间，格式固定 yyyy-MM-dd HH:mm:ss；示例：`2026-04-18 23:59:59` |
| limit | integer | 否 | 返回条数限制，后端会做兜底和截断；示例：`100` |

#### 请求示例

**正常历史查询**

```json
{
  "sensorId": "SENSOR_001",
  "startTime": "2026-04-18 00:00:00",
  "endTime": "2026-04-18 23:59:59",
  "limit": 100
}
```

#### 响应说明

**200**：查询成功

Schema：`VibrationHistoryResponse`

**success**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "sensorId": "SENSOR_001",
      "vibrationValue": 12.34,
      "collectTime": "2026-04-18 14:30:00"
    },
    {
      "sensorId": "SENSOR_001",
      "vibrationValue": 12.84,
      "collectTime": "2026-04-18 15:00:00"
    }
  ]
}
```

**empty**

```json
{
  "code": 200,
  "message": "success",
  "data": []
}
```

**400**：请求参数错误

Schema：`GenericErrorResponse`

**500**：服务端错误

Schema：`GenericErrorResponse`

---

## Schema 定义

### VibrationUploadDTO

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| sensorId | string | 是 | 传感器ID；示例：`SENSOR_001` |
| vibrationValue | number | 是 | 振动值；示例：`12.34` |
| collectTime | string | 是 | 采集时间，格式固定 yyyy-MM-dd HH:mm:ss；示例：`2026-04-18 14:30:00` |

### VibrationLatestQueryDTO

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| sensorId | string | 是 | 传感器ID；示例：`SENSOR_001` |

### VibrationHistoryQueryDTO

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| sensorId | string | 是 | 传感器ID；示例：`SENSOR_001` |
| startTime | string | 否 | 开始时间，格式固定 yyyy-MM-dd HH:mm:ss；示例：`2026-04-18 00:00:00` |
| endTime | string | 否 | 结束时间，格式固定 yyyy-MM-dd HH:mm:ss；示例：`2026-04-18 23:59:59` |
| limit | integer | 否 | 返回条数限制，后端会做兜底和截断；示例：`100` |

### VibrationRawVO

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| sensorId | string | 否 | 示例：`SENSOR_001` |
| vibrationValue | number | 否 | 示例：`12.34` |
| collectTime | string | 否 | 示例：`2026-04-18 14:30:00` |

### GenericSuccessResponse

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| code | integer | 否 | 示例：`200` |
| message | string | 否 | 示例：`success` |
| data | - | 否 | - |

### GenericErrorResponse

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| code | integer | 否 | 示例：`500` |
| message | string | 否 | 示例：`failed` |
| data |  (nullable) | 否 | - |

### VibrationLatestResponse

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| code | integer | 否 | 示例：`200` |
| message | string | 否 | 示例：`success` |
| data | VibrationRawVO / null | 否 | - |

### VibrationHistoryResponse

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| code | integer | 否 | 示例：`200` |
| message | string | 否 | 示例：`success` |
| data | array | 否 | - |
