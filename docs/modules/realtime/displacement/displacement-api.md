# Ansteel SHM - Displacement Module API 接口说明

位移模块联调最终版（可导入 APIFox）。

已按当前直采实时模块字段对齐：DisplacementUploadDTO / DisplacementQueryDTO。
模块目标：上传原始数据 -> 查询 latest -> 查询 history。

**Base URL**：`http://localhost:8080`

**模块标签**：`Displacement`

## 接口清单

- `POST /api/sensor/displacement/upload`：原始位移数据上传
- `POST /api/data/displacement/latest`：查询最新位移数据
- `POST /api/data/displacement/history`：查询位移历史数据

## 接口详情

### POST /api/sensor/displacement/upload

**摘要**：原始位移数据上传

**说明**：接收传感器/设备上传的原始数据，写入 displacement_data。

**标签**：`Displacement`

#### 请求体

Schema：`DisplacementUploadDTO`

### DisplacementUploadDTO

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| sensorId | string | 是 | 传感器ID；示例：`SENSOR_001` |
| displacementValue | number | 是 | 位移值；示例：`12.34` |
| collectTime | string | 是 | 采集时间，格式固定 yyyy-MM-dd HH:mm:ss；示例：`2026-04-18 14:30:00` |

#### 请求示例

**正常上传**

```json
{
  "sensorId": "SENSOR_001",
  "displacementValue": 12.34,
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

### POST /api/data/displacement/latest

**摘要**：查询最新位移数据

**说明**：根据 sensorId 查询指定传感器最新一条数据。

**标签**：`Displacement`

#### 请求体

Schema：`DisplacementLatestQueryDTO`

### DisplacementLatestQueryDTO

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

Schema：`DisplacementLatestResponse`

**success**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "sensorId": "SENSOR_001",
    "displacementValue": 12.34,
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

### POST /api/data/displacement/history

**摘要**：查询位移历史数据

**说明**：根据 sensorId + 时间范围查询历史数据列表。

**标签**：`Displacement`

#### 请求体

Schema：`DisplacementHistoryQueryDTO`

### DisplacementHistoryQueryDTO

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

Schema：`DisplacementHistoryResponse`

**success**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "sensorId": "SENSOR_001",
      "displacementValue": 12.34,
      "collectTime": "2026-04-18 14:30:00"
    },
    {
      "sensorId": "SENSOR_001",
      "displacementValue": 12.84,
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

### DisplacementUploadDTO

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| sensorId | string | 是 | 传感器ID；示例：`SENSOR_001` |
| displacementValue | number | 是 | 位移值；示例：`12.34` |
| collectTime | string | 是 | 采集时间，格式固定 yyyy-MM-dd HH:mm:ss；示例：`2026-04-18 14:30:00` |

### DisplacementLatestQueryDTO

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| sensorId | string | 是 | 传感器ID；示例：`SENSOR_001` |

### DisplacementHistoryQueryDTO

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| sensorId | string | 是 | 传感器ID；示例：`SENSOR_001` |
| startTime | string | 否 | 开始时间，格式固定 yyyy-MM-dd HH:mm:ss；示例：`2026-04-18 00:00:00` |
| endTime | string | 否 | 结束时间，格式固定 yyyy-MM-dd HH:mm:ss；示例：`2026-04-18 23:59:59` |
| limit | integer | 否 | 返回条数限制，后端会做兜底和截断；示例：`100` |

### DisplacementRawVO

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| sensorId | string | 否 | 示例：`SENSOR_001` |
| displacementValue | number | 否 | 示例：`12.34` |
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

### DisplacementLatestResponse

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| code | integer | 否 | 示例：`200` |
| message | string | 否 | 示例：`success` |
| data | DisplacementRawVO / null | 否 | - |

### DisplacementHistoryResponse

| 字段 | 类型 | 必填 | 说明/示例 |
|---|---|---|---|
| code | integer | 否 | 示例：`200` |
| message | string | 否 | 示例：`success` |
| data | array | 否 | - |
