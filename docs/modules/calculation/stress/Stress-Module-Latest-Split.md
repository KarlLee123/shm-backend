# 监测系统-应力模块 V2.0-分离终稿

## 0. 模块定位

本模块名称固定为：`stress`

本模块在系统中的定位固定为：

> **计算核验模块**

说明：
- 本模块只负责“应力”链路；
- 本模块不承担“挠度”链路；
- 本模块不承担位移、加速度、应变、振动四类直采模块职责。

---

## 0.1 模块边界

本模块固定范围为：

- 前端：1 个业务展示页面 `StressMonitorPage`
- 后端：5 个核心接口
  1. `POST /api/sensor/stress/raw/upload`
  2. `POST /api/sensor/stress/result/upload`
  3. `POST /api/data/stress/verify`
  4. `POST /api/data/stress/latest`
  5. `POST /api/data/stress/history`
- 数据库：1 张表 `stress_data`

---

## 0.2 核心业务原则

本模块必须满足以下原则：

1. 原始输入必须先入库；
2. Python 结果只能回写已有原始记录；
3. 核验通过前，业务查询默认不可见；
4. 核验不通过必须打回；
5. 打回后允许 Python 重新计算并覆盖旧结果；
6. 已核验通过的数据，默认禁止再次结果回写；
7. `latest/history` 默认只返回 `VERIFIED` 数据；
8. 不允许脱离原始记录单独新插结果记录。

---

## 0.3 原始输入与结果输出

### 原始输入层

当前 Stress 模块原始上传字段固定为：
- `forceValue`
- `displacementValue`

### 计算结果层

当前 Stress 模块计算输出字段固定为：
- `calcStress`

### 核验结果层

当前 Stress 模块核验输出字段固定为：
- `verifiedStress`
- `verifyStatus`
- `verifyRemark`

---

## 1. 状态机定义

`verifyStatus` 固定状态如下：

| 状态值 | 状态名 | 含义 |
| --- | --- | --- |
| 0 | RAW | 只有原始数据，尚未回写结果 |
| 1 | CALCULATED | 已回写计算结果，等待核验 |
| 2 | VERIFIED | 核验通过，业务查询可见 |
| 3 | REJECTED | 核验不通过，打回重算 |

### 1.1 状态流转

RAW  
↓（结果回写成功）  
CALCULATED  
↓（核验通过）  
VERIFIED  

CALCULATED  
↓（核验不通过）  
REJECTED  
↓（重新回写结果）  
CALCULATED  
↓（再次核验通过）  
VERIFIED  

### 1.2 状态流转红线

禁止以下行为：
- `RAW` 直接跳到 `VERIFIED`
- `REJECTED` 不经重算直接变 `VERIFIED`
- `VERIFIED` 被结果接口再次覆盖
- 找不到原始记录却新插结果记录
- `latest/history` 返回 `RAW / CALCULATED / REJECTED` 给业务页

---

## 2. DTO 定义

### 2.1 StressRawUploadDTO

| 字段名 | 类型 | 必填 | 说明 |
| --- | --- | ---: | --- |
| sensorId | string | 是 | 传感器 ID |
| forceValue | decimal | 是 | 原始力值 |
| displacementValue | decimal | 是 | 原始位移值 |
| collectTime | string | 是 | 格式固定 `yyyy-MM-dd HH:mm:ss` |

### 2.2 StressResultUploadDTO

| 字段名 | 类型 | 必填 | 说明 |
| --- | --- | ---: | --- |
| sensorId | string | 是 | 传感器 ID |
| calcStress | decimal | 是 | 计算应力 |
| collectTime | string | 是 | 对应原始采样时间 |
| calcTime | string | 否 | 计算完成时间 |

### 2.3 StressVerifyDTO

| 字段名 | 类型 | 必填 | 说明 |
| --- | --- | ---: | --- |
| sensorId | string | 是 | 传感器 ID |
| action | string | 是 | `PASS` 或 `FAIL` |
| verifiedStress | decimal | 否 | `PASS` 时必填 |
| verifyRemark | string | 否 | `FAIL` 时必填 |
| collectTime | string | 是 | 对应原始采样时间 |
| verifyTime | string | 否 | 核验时间 |

### 2.4 StressQueryDTO

| 字段名 | 类型 | 必填 | 说明 |
| --- | --- | ---: | --- |
| sensorId | string | 是 | 传感器 ID |
| startTime | string | 否 | 格式固定 `yyyy-MM-dd HH:mm:ss` |
| endTime | string | 否 | 格式固定 `yyyy-MM-dd HH:mm:ss` |
| limit | integer | 否 | 查询条数限制，默认 100，最大 500 |

---

## 3. 接口文档

### 3.1 原始数据上传

**路径**  
`POST /api/sensor/stress/raw/upload`

**Request Body**
```json
{
  "sensorId": "S001",
  "forceValue": 125.60,
  "displacementValue": 3.42,
  "collectTime": "2026-04-20 14:30:00"
}
```

### 3.2 结果回写

**路径**  
`POST /api/sensor/stress/result/upload`

**Request Body**
```json
{
  "sensorId": "S001",
  "calcStress": 98.56,
  "collectTime": "2026-04-20 14:30:00",
  "calcTime": "2026-04-20 14:30:05"
}
```

### 3.3 核验接口

**路径**  
`POST /api/data/stress/verify`

**Request Body（通过）**
```json
{
  "sensorId": "S001",
  "action": "PASS",
  "verifiedStress": 98.40,
  "collectTime": "2026-04-20 14:30:00",
  "verifyTime": "2026-04-20 14:31:00"
}
```

**Request Body（打回）**
```json
{
  "sensorId": "S001",
  "action": "FAIL",
  "verifyRemark": "应力结果异常，要求重新计算",
  "collectTime": "2026-04-20 14:30:00",
  "verifyTime": "2026-04-20 14:31:00"
}
```

### 3.4 最新值查询

**路径**  
`POST /api/data/stress/latest`

**Request Body**
```json
{
  "sensorId": "S001"
}
```

### 3.5 历史查询

**路径**  
`POST /api/data/stress/history`

**Request Body**
```json
{
  "sensorId": "S001",
  "startTime": "2026-04-20 00:00:00",
  "endTime": "2026-04-20 23:59:59",
  "limit": 100
}
```

---

## 4. 业务查询返回字段

`latest/history` 业务返回字段固定为：

- `sensorId`
- `forceValue`
- `displacementValue`
- `calcStress`
- `verifiedStress`
- `verifyStatus`
- `verifyRemark`
- `collectTime`
- `calcTime`
- `verifyTime`

---

## 5. 最终裁定

本文件只服务于 `stress` 模块，不与 `deflection` 共用命名、不混用结果字段。
