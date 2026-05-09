《监测系统-振动模块V1.0-审查终稿》

# 0. 模块定位

本模块名称固定为：`vibration`

本模块在全系统中的定位固定为：

> **传感器直采实时模块**

即：
- 属于“传感器直采即可直接展示”的实时模块；
- 不属于“需要 Python 算法处理后，再经人工核验，最后才允许业务展示”的计算核验模块；
- 当前模块数据上传后即可直接入库，并供业务页面查询展示。

当前系统中与本模块同类的有：
- `displacement`
- `acceleration`
- `strain`
- `vibration`

其中：
- 位移、加速度、应变、振动属于直采实时模块；
- 挠度、应力属于计算核验模块。

因此，振动模块必须严格走：

传感器数据上传
→ 数据入库
→ latest/history 查询
→ 业务展示

---

# 0.1 当前模块边界

本模块固定范围为：
- 前端：1 个业务展示页面 `VibrationMonitorPage`
- 后端：3 个核心接口
  1. `POST /api/sensor/vibration/upload`
  2. `POST /api/data/vibration/latest`
  3. `POST /api/data/vibration/history`
- 数据库：1 张表 `vibration_data`

---

# 0.2 核心业务原则

本模块必须满足以下原则：

1. 传感器原始数据直接入库；
2. 数据上传成功后即可供业务页查询展示；
3. `latest/history` 默认查询有效原始数据；
4. 本模块不依赖 Python 计算；
5. 本模块不依赖人工核验；
6. 不允许重复插入同一 `sensorId + collectTime` 的原始记录；
7. 本模块默认按北京时间字符串接收时间；
8. 本模块不引入 `verify_status` 作为数据展示前置条件。

---

# 0.3 原始输入层

当前 振动模块原始上传字段固定为：
- `vibrationValue`

说明：
- 这是本模块的唯一主业务值；
- 属于传感器直接采集并可直接展示的数据；
- 前端业务页展示的就是该原始值本身，而不是计算结果。

---

# 1. 认证与访问规则

## 1.1 设备侧接口

路径前缀：
- `/api/sensor/**`

认证方式：
- `X-API-KEY`

要求：
- vibration 模块必须使用 vibration 模块专属 key；
- 不允许与其他模块共用 key。

## 1.2 业务侧接口

路径前缀：
- `/api/data/**`

认证方式：
- `Authorization: Bearer <JWT>`

要求：
- `latest/history` 属于业务查询接口，必须走 JWT；
- 当前文档不展开角色体系，但查询接口不属于设备侧。

---

# 2. DTO 定义

## 2.1 VibrationUploadDTO

用于原始数据上传。

| 字段名 | 类型 | 必填 | 说明 |
| --- | --- | ---: | --- |
| sensorId | string | 是 | 传感器 ID |
| vibrationValue | decimal | 是 | 振动值 |
| collectTime | string | 是 | 格式固定 `yyyy-MM-dd HH:mm:ss` |

## 2.2 VibrationQueryDTO

用于 latest / history 查询。

| 字段名 | 类型 | 必填 | 说明 |
| --- | --- | ---: | --- |
| sensorId | string | 是 | 传感器 ID |
| startTime | string | 否 | 格式固定 `yyyy-MM-dd HH:mm:ss` |
| endTime | string | 否 | 格式固定 `yyyy-MM-dd HH:mm:ss` |
| limit | integer | 否 | 查询条数限制 |

规则：
- 前端默认传 `100`；
- 后端若未收到 `limit`，统一按 `100` 处理；
- 后端强制最大上限为 `500`。

---

# 3. 接口文档

## 3.1 原始数据上传接口

### 路径
`POST /api/sensor/vibration/upload`

### Header
- `Content-Type: application/json`
- `X-API-KEY: <vibration_key>`

### Request Body
```json
{
  "sensorId": "SENSOR_001",
  "vibrationValue": 12.34,
  "collectTime": "2026-04-18 14:30:00"
}
```

### 业务语义
固定定义为：

> 插入一条 振动模块原始采样记录。

插入字段包括：
- `sensor_id`
- `vibration_value`
- `collect_time`
- `create_time`
- `deleted`

### 不允许
- 相同 `sensorId + collectTime` 重复插入；
- 上传非本模块主值字段；
- 通过该接口写入计算结果或核验结果。

### Success Response
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## 3.2 业务侧：最新值查询

### 路径
`POST /api/data/vibration/latest`

### Header
- `Content-Type: application/json`
- `Authorization: Bearer <JWT>`

### Request Body
```json
{
  "sensorId": "SENSOR_001"
}
```

### 查询语义
固定定义为：

> 按 `sensorId` 过滤，且 `deleted = 0`，按 `collectTime DESC` 取最新 1 条原始记录。

### Success Response
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

---

## 3.3 业务侧：历史数据查询

### 路径
`POST /api/data/vibration/history`

### Header
- `Content-Type: application/json`
- `Authorization: Bearer <JWT>`

### Request Body
```json
{
  "sensorId": "SENSOR_001",
  "startTime": "2026-04-18 00:00:00",
  "endTime": "2026-04-18 23:59:59",
  "limit": 100
}
```

### 查询语义
固定定义为：

> 按 `sensorId` 过滤，且 `deleted = 0`，按时间范围筛选，结果固定 `collectTime ASC`。

### Success Response
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "sensorId": "SENSOR_001",
      "vibrationValue": 12.34,
      "collectTime": "2026-04-18 14:30:00"
    }
  ]
}
```

---

# 4. 数据库设计

## 4.1 表名
`vibration_data`

## 4.2 字段清单

| 字段名 | 类型建议 | 说明 |
| --- | --- | --- |
| id | bigint | 主键 |
| sensor_id | varchar(64) | 传感器 ID |
| vibration_value | decimal(18,6) | 振动值 |
| collect_time | datetime | 原始采样时间 |
| create_time | datetime | 创建时间 |
| deleted | tinyint | 逻辑删除标记，0/1 |

## 4.3 唯一性约束

必须保证：

> 同一个 `sensor_id + collect_time` 只对应一条原始采样记录。

建议建立唯一约束：
`uk_sensor_collect_time (sensor_id, collect_time)`

---

# 5. SQL 执行标准

## 5.1 Upload 语义
```sql
INSERT INTO vibration_data
(sensor_id, vibration_value, collect_time, create_time, deleted)
VALUES (?, ?, ?, NOW(), 0)
```

## 5.2 Latest 语义
```sql
WHERE sensor_id = ?
AND deleted = 0
ORDER BY collect_time DESC
LIMIT 1
```

## 5.3 History 语义
```sql
WHERE sensor_id = ?
AND deleted = 0
AND (collect_time >= startTime)
AND (collect_time <= endTime)
ORDER BY collect_time ASC
LIMIT ?
```

---

# 6. 前端页面说明

页面名称固定为：`VibrationMonitorPage`

页面骨架固定为：

VibrationMonitorPage
├─ QueryBar
├─ LatestCard
├─ TrendChart
└─ HistoryTable

## 页面默认展示
- 最新原始值：`vibrationValue`
- 历史趋势图
- 历史明细表

## 页面数据流
用户点击查询
↓
调用 latest 接口
↓
刷新 LatestCard
↓
调用 history 接口
↓
刷新 TrendChart + HistoryTable

---

# 7. 时间与环境约束

- 全链路时间统一使用北京时间（UTC+8，Asia/Shanghai）；
- 时间格式固定为：`yyyy-MM-dd HH:mm:ss`；
- 禁止 ISO 8601；
- 禁止时间戳；
- 禁止后端隐含时区换算；
- 服务禁止只绑定 `localhost`；
- 必须允许局域网 IP 访问；
- 内网访问不等于取消认证。

---

# 8. 最终裁定

本稿当前定位为：

> **监测系统-振动模块V1.0-审查终稿（直采实时版 / 北京时间版）**

本版结论：
- 名字固定为 `vibration`；
- 模块类型固定为“传感器直采实时模块”；
- 原始输入即业务展示值；
- 不依赖 Python；
- 不依赖人工核验；
- `latest/history` 默认直接返回有效原始数据。
