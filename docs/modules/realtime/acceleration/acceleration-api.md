# Ansteel SHM - Acceleration Module API 接口说明

加速度模块联调最终版（可导入 APIFox）。

已按当前直采实时模块字段对齐：AccelerationUploadDTO / AccelerationQueryDTO。
模块目标：上传原始数据 -> 查询 latest -> 查询 history。

**Base URL**：`http://localhost:8080`

**模块标签**：`Acceleration`

## 接口清单

- `POST /api/sensor/acceleration/upload`：原始加速度数据上传
- `POST /api/data/acceleration/latest`：查询最新加速度数据
- `POST /api/data/acceleration/history`：查询加速度历史数据

## 接口详情

### POST /api/sensor/acceleration/upload

**摘要**：原始加速度数据上传

**说明**：接收传感器/设备上传的原始数据，写入 acceleration_data。

**标签**：`Acceleration`

#### 请求体

Schema：`AccelerationUploadDTO`

### AccelerationUploadDTO

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| sensorId | string | 是 | 传感器ID；示例：`SENSOR_001` |
| accelerationValue | number | 是 | 加速度值；示例：`0.98` |
| collectTime | string | 是 | 采集时间，格式固定 yyyy-MM-dd HH:mm:ss；示例：`2026-04-18 14:30:00` |

#### 请求示例

**正常上传**

```json
{
  "sensorId": "SENSOR_001",
  "accelerationValue": 0.98,
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

### POST /api/data/acceleration/latest

**摘要**：查询最新加速度数据

**说明**：根据 sensorId 查询指定传感器最新一条数据。

**标签**：`Acceleration`

#### 请求体

Schema：`AccelerationLatestQueryDTO`

### AccelerationLatestQueryDTO

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

Schema：`AccelerationLatestResponse`

**success**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "sensorId": "SENSOR_001",
    "accelerationValue": 0.98,
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

### POST /api/data/acceleration/history

**摘要**：查询加速度历史数据

**说明**：根据 sensorId + 时间范围查询历史数据列表。

**标签**：`Acceleration`

#### 请求体

Schema：`AccelerationHistoryQueryDTO`

### AccelerationHistoryQueryDTO

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

Schema：`AccelerationHistoryResponse`

**success**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "sensorId": "SENSOR_001",
      "accelerationValue": 0.98,
      "collectTime": "2026-04-18 14:30:00"
    },
    {
      "sensorId": "SENSOR_001",
      "accelerationValue": 1.48,
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

### AccelerationUploadDTO

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| sensorId | string | 是 | 传感器ID；示例：`SENSOR_001` |
| accelerationValue | number | 是 | 加速度值；示例：`0.98` |
| collectTime | string | 是 | 采集时间，格式固定 yyyy-MM-dd HH:mm:ss；示例：`2026-04-18 14:30:00` |

### AccelerationLatestQueryDTO

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| sensorId | string | 是 | 传感器ID；示例：`SENSOR_001` |

### AccelerationHistoryQueryDTO

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| sensorId | string | 是 | 传感器ID；示例：`SENSOR_001` |
| startTime | string | 否 | 开始时间，格式固定 yyyy-MM-dd HH:mm:ss；示例：`2026-04-18 00:00:00` |
| endTime | string | 否 | 结束时间，格式固定 yyyy-MM-dd HH:mm:ss；示例：`2026-04-18 23:59:59` |
| limit | integer | 否 | 返回条数限制，后端会做兜底和截断；示例：`100` |

### AccelerationRawVO

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| sensorId | string | 否 | 示例：`SENSOR_001` |
| accelerationValue | number | 否 | 示例：`0.98` |
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

### AccelerationLatestResponse

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| code | integer | 否 | 示例：`200` |
| message | string | 否 | 示例：`success` |
| data | AccelerationRawVO / null | 否 | - |

### AccelerationHistoryResponse

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| code | integer | 否 | 示例：`200` |
| message | string | 否 | 示例：`success` |
| data | array | 否 | - |
