# Stress API

## 模块说明

当前 Stress 模块 API 已按现有代码字段对齐，当前联调范围固定为：

1. 原始数据上传
2. latest 查询
3. history 查询

当前对齐 DTO：

- `StressRawUploadDTO`
- `StressQueryDTO`

---

## 接口列表

| 序号 | 接口名称 | 方法 | 路径 |
| --- | --- | --- | --- |
| 1 | 原始应力数据上传 | POST | `/api/sensor/stress/raw/upload` |
| 2 | 查询最新应力数据 | POST | `/api/data/stress/latest` |
| 3 | 查询应力历史数据 | POST | `/api/data/stress/history` |

---

## 1. 原始应力数据上传

### 基本信息

- Method: `POST`
- Path: `/api/sensor/stress/raw/upload`
- Content-Type: `application/json`

### 请求参数

| 字段名 | 类型 | 必填 | 说明 |
| --- | --- | ---: | --- |
| `sensorId` | string | 是 | 传感器ID |
| `forceValue` | number | 是 | 力值 |
| `displacementValue` | number | 是 | 位移值 |
| `collectTime` | string | 是 | 采集时间，格式固定 `yyyy-MM-dd HH:mm:ss` |

### 请求示例

```json
{
  "sensorId": "SENSOR_001",
  "forceValue": 125.68,
  "displacementValue": 2.35,
  "collectTime": "2026-04-18 14:30:00"
}
```

### 成功响应示例

```json
{
  "code": 200,
  "message": "上传成功",
  "data": 1
}
```

### 错误响应示例

```json
{
  "code": 400,
  "message": "参数错误",
  "data": null
}
```

```json
{
  "code": 500,
  "message": "上传失败",
  "data": null
}
```

---

## 2. 查询最新应力数据

### 基本信息

- Method: `POST`
- Path: `/api/data/stress/latest`
- Content-Type: `application/json`

### 请求参数

| 字段名 | 类型 | 必填 | 说明 |
| --- | --- | ---: | --- |
| `sensorId` | string | 是 | 传感器ID |
| `limit` | integer | 否 | 返回条数限制，建议传 `1` |

### 请求示例

```json
{
  "sensorId": "SENSOR_001",
  "limit": 1
}
```

### 成功响应示例

```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "sensorId": "SENSOR_001",
    "forceValue": 125.68,
    "displacementValue": 2.35,
    "collectTime": "2026-04-18 14:30:00"
  }
}
```

### 空结果示例

```json
{
  "code": 200,
  "message": "暂无数据",
  "data": null
}
```

---

## 3. 查询应力历史数据

### 基本信息

- Method: `POST`
- Path: `/api/data/stress/history`
- Content-Type: `application/json`

### 请求参数

| 字段名 | 类型 | 必填 | 说明 |
| --- | --- | ---: | --- |
| `sensorId` | string | 是 | 传感器ID |
| `startTime` | string | 否 | 开始时间，格式固定 `yyyy-MM-dd HH:mm:ss` |
| `endTime` | string | 否 | 结束时间，格式固定 `yyyy-MM-dd HH:mm:ss` |
| `limit` | integer | 否 | 返回条数限制，建议传 `100` |

### 请求示例

```json
{
  "sensorId": "SENSOR_001",
  "startTime": "2026-04-18 00:00:00",
  "endTime": "2026-04-18 23:59:59",
  "limit": 100
}
```

### 成功响应示例

```json
{
  "code": 200,
  "message": "查询成功",
  "data": [
    {
      "sensorId": "SENSOR_001",
      "forceValue": 125.68,
      "displacementValue": 2.35,
      "collectTime": "2026-04-18 14:30:00"
    },
    {
      "sensorId": "SENSOR_001",
      "forceValue": 126.12,
      "displacementValue": 2.41,
      "collectTime": "2026-04-18 15:00:00"
    }
  ]
}
```

### 空结果示例

```json
{
  "code": 200,
  "message": "暂无数据",
  "data": []
}
```
