《监测系统-应力模块V1.1-审查终稿》

# 0. 模块定位

本模块名称固定为：`stress`

本模块在全系统中的定位固定为：

> **计算核验类模块**

即：
- 不属于“传感器直采即可直接展示”的实时模块；
- 属于“需要 Python 算法处理后，再经人工核验，最后才允许业务展示”的模块。

当前系统中与本模块同类的仅有：
- `deflection`
- `stress`

其中：
- 位移、加速度、应变、振动属于直采实时模块；
- 挠度、应力属于计算核验模块。

因此，Stress 模块必须严格走：

原始数据上传
→ Python 结果回写
→ 人工核验
→ 核验通过后业务展示

---

# 0.1 当前模块边界

Stress 模块当前只负责“应力结果”的业务闭环，不再承担“应变模块”的职责。

说明：
- 应变 `strain` 已单独归入直采实时模块；
- 因此 Stress 模块不得再把“应变”作为本模块主输出结果；
- Stress 模块当前主结果字段只保留 `stress` 相关结果。

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

# 0.2 核心业务原则

本模块必须满足以下原则：

1. 原始数据必须先入库；
2. Python 结果只能回写已有原始记录；
3. 核验通过前，业务页默认不可见；
4. 核验不通过必须打回；
5. 打回后允许 Python 重新计算并覆盖旧结果；
6. 已核验通过的数据，默认禁止结果接口再次覆盖；
7. `latest/history` 业务查询默认只返回 `VERIFIED` 数据；
8. 不允许脱离原始记录单独新插“结果记录”。

---

# 0.3 原始输入与结果输出

## 原始输入层

当前 Stress 模块原始上传字段固定为：
- `forceValue`
- `displacementValue`

说明：
- 这是 Stress 模块用于计算应力的原始输入；
- 这些字段属于 Stress 模块内部原始输入，不等同于全系统中“位移模块”的业务展示数据；
- 本模块前端业务页最终采信的是核验后的应力结果，而不是原始输入本身。

## 计算结果层

当前 Stress 模块计算输出字段固定为：
- `calcStress`

## 核验结果层

当前 Stress 模块核验输出字段固定为：
- `verifiedStress`
- `verifyStatus`
- `verifyRemark`

---

# 1. 状态机定义

`verifyStatus` 为本模块主流程闸门，固定状态如下：

| 状态值 | 状态名 | 含义 |
| --- | --- | --- |
| 0 | RAW | 只有原始数据，Python 尚未回写 |
| 1 | CALCULATED | Python 已回写结果，等待核验 |
| 2 | VERIFIED | 核验通过，业务查询可见 |
| 3 | REJECTED | 核验不通过，打回重算 |

## 1.1 状态流转

RAW
↓（Python回写成功）
CALCULATED
↓（核验通过）
VERIFIED

CALCULATED
↓（核验不通过）
REJECTED
↓（Python重新回写）
CALCULATED
↓（再次核验通过）
VERIFIED

## 1.2 状态流转红线

禁止以下行为：
- `RAW` 直接跳到 `VERIFIED`；
- `REJECTED` 不经 Python 重算直接变 `VERIFIED`；
- `VERIFIED` 被结果回写接口再次覆盖；
- 找不到原始记录却新插结果记录；
- `latest/history` 返回 `RAW / CALCULATED / REJECTED` 数据给业务页。

---

# 2. 认证与访问规则

## 2.1 设备 / 算法侧接口

路径前缀：
- `/api/sensor/**`

认证方式：
- `X-API-KEY`

要求：
- Stress 模块必须使用 Stress 模块专属 key；
- 原始上传与结果回写可使用不同 key，但必须都属于 Stress 模块范围；
- 不允许与其他模块共用 key。

## 2.2 业务 / 管理侧接口

路径前缀：
- `/api/data/**`

认证方式：
- `Authorization: Bearer <JWT>`

要求：
- `verify` 接口必须走 JWT；
- `latest/history` 属于业务查询接口，也必须走 JWT；
- 当前文档不展开角色体系，但核验接口默认不属于普通展示用户。

---

# 3. DTO 定义

## 3.1 StressRawUploadDTO

用于原始数据上传。

| 字段名 | 类型 | 必填 | 说明 |
| --- | --- | ---: | --- |
| sensorId | string | 是 | 传感器 ID |
| forceValue | decimal | 是 | 原始力值 |
| displacementValue | decimal | 是 | 原始位移值 |
| collectTime | string | 是 | 格式固定 `yyyy-MM-dd HH:mm:ss` |

## 3.2 StressResultUploadDTO

用于 Python 结果回写。

| 字段名 | 类型 | 必填 | 说明 |
| --- | --- | ---: | --- |
| sensorId | string | 是 | 传感器 ID |
| calcStress | decimal | 是 | 计算应力 |
| collectTime | string | 是 | 对应原始采样时间 |
| calcTime | string | 否 | 计算完成时间 |

说明：
- `sensorId + collectTime` 用于定位已有原始记录；
- 当前接口不得新增独立记录，只允许回写已有原始记录；
- 若当前状态为 `REJECTED`，允许覆盖旧计算结果并重置状态。

## 3.3 StressVerifyDTO

用于核验闸口。

| 字段名 | 类型 | 必填 | 说明 |
| --- | --- | ---: | --- |
| sensorId | string | 是 | 传感器 ID |
| action | string | 是 | `PASS` 或 `FAIL` |
| verifiedStress | decimal | 否 | `PASS` 时必填 |
| verifyRemark | string | 否 | `FAIL` 时建议必填 |
| collectTime | string | 是 | 对应原始采样时间 |
| verifyTime | string | 否 | 核验时间 |

规则：
- `PASS` 时必须提供 `verifiedStress`；
- `FAIL` 时不要求提供 `verifiedStress`，但应写入 `verifyRemark`。

## 3.4 StressQueryDTO

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

# 4. 接口文档

## 4.1 原始数据上传接口

### 路径
`POST /api/sensor/stress/raw/upload`

### Header
- `Content-Type: application/json`
- `X-API-KEY: <stress_raw_key>`

### Request Body
```json
{
  "sensorId": "S001",
  "forceValue": 125.60,
  "displacementValue": 3.42,
  "collectTime": "2026-04-18 14:30:00"
}
```

### 业务语义
固定定义为：

> 插入一条 Stress 模块原始采样记录。

插入字段包括：
- `sensor_id`
- `force_value`
- `displacement_value`
- `collect_time`

同时初始化：
- `calc_stress = null`
- `verified_stress = null`
- `verify_status = RAW`
- `verify_remark = null`
- `deleted = 0`

### 不允许
- 相同 `sensorId + collectTime` 重复插入；
- 通过该接口上传 `calcStress`；
- 通过该接口上传 `verifiedStress`；
- 通过该接口直接写入核验状态。

### Success Response
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## 4.2 Python 结果回写接口

### 路径
`POST /api/sensor/stress/result/upload`

### Header
- `Content-Type: application/json`
- `X-API-KEY: <stress_result_key>`

### Request Body
```json
{
  "sensorId": "S001",
  "calcStress": 98.56,
  "collectTime": "2026-04-18 14:30:00",
  "calcTime": "2026-04-18 14:30:05"
}
```

### 业务语义
固定定义为：

> 按 `sensorId + collectTime` 定位原始记录，并回写应力计算结果。

回写字段包括：
- `calc_stress`
- `calc_time`

同时更新状态：
- 当前状态是 `RAW` → 更新后改为 `CALCULATED`
- 当前状态是 `REJECTED` → 允许覆盖旧结果，并重置为 `CALCULATED`

### 不允许
- 找不到原始记录时直接新插一条“结果记录”；
- 当前状态是 `VERIFIED` 时继续覆盖结果；
- 用该接口回写核验结果；
- 用该接口更新 `forceValue / displacementValue`。

### Success Response
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## 4.3 核验接口

### 路径
`POST /api/data/stress/verify`

### Header
- `Content-Type: application/json`
- `Authorization: Bearer <JWT>`

### Request Body（通过）
```json
{
  "sensorId": "S001",
  "action": "PASS",
  "verifiedStress": 98.40,
  "collectTime": "2026-04-18 14:30:00",
  "verifyTime": "2026-04-18 14:31:00"
}
```

### Request Body（打回）
```json
{
  "sensorId": "S001",
  "action": "FAIL",
  "verifyRemark": "应力结果异常，要求重新计算",
  "collectTime": "2026-04-18 14:30:00",
  "verifyTime": "2026-04-18 14:31:00"
}
```

### 业务语义
固定定义为：

> 按 `sensorId + collectTime` 定位一条已计算记录，并执行核验闸口动作。

#### 当 `action = PASS`
必须执行：
- 写入 `verified_stress`
- 写入 `verify_time`
- 清空或覆盖 `verify_remark`
- 状态改为 `VERIFIED`

#### 当 `action = FAIL`
必须执行：
- 写入 `verify_remark`
- 写入 `verify_time`
- 状态改为 `REJECTED`

### 不允许
- 对 `RAW` 状态直接核验；
- 对不存在的记录核验；
- `PASS` 时不提供 `verifiedStress`；
- `FAIL` 后仍保持 `CALCULATED`；
- 核验通过后又改回未核验状态，除非后续另开专门重测流程。

### Success Response
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## 4.4 业务侧：最新值查询

### 路径
`POST /api/data/stress/latest`

### Header
- `Content-Type: application/json`
- `Authorization: Bearer <JWT>`

### Request Body
```json
{
  "sensorId": "S001"
}
```

### 查询语义
固定定义为：

> 按 `sensorId` 过滤，且 `deleted = 0`，且 `verifyStatus = VERIFIED`，按 `collectTime DESC` 取最新 1 条完整记录。

### Success Response
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "sensorId": "S001",
    "forceValue": 125.60,
    "displacementValue": 3.42,
    "calcStress": 98.56,
    "verifiedStress": 98.40,
    "verifyStatus": "VERIFIED",
    "collectTime": "2026-04-18 14:30:00",
    "calcTime": "2026-04-18 14:30:05",
    "verifyTime": "2026-04-18 14:31:00"
  }
}
```

---

## 4.5 业务侧：历史数据查询

### 路径
`POST /api/data/stress/history`

### Header
- `Content-Type: application/json`
- `Authorization: Bearer <JWT>`

### Request Body
```json
{
  "sensorId": "S001",
  "startTime": "2026-04-18 00:00:00",
  "endTime": "2026-04-18 23:59:59",
  "limit": 100
}
```

### 查询语义
固定定义为：

> 按 `sensorId` 过滤，且 `deleted = 0`，且 `verifyStatus = VERIFIED`，按时间范围筛选，结果固定 `collectTime ASC`。

### Success Response
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "sensorId": "S001",
      "forceValue": 125.60,
      "displacementValue": 3.42,
      "calcStress": 98.56,
      "verifiedStress": 98.40,
      "verifyStatus": "VERIFIED",
      "collectTime": "2026-04-18 14:30:00",
      "calcTime": "2026-04-18 14:30:05",
      "verifyTime": "2026-04-18 14:31:00"
    }
  ]
}
```

---

# 5. 数据库设计

## 5.1 表名
`stress_data`

## 5.2 字段清单

| 字段名 | 类型建议 | 说明 |
| --- | --- | --- |
| id | bigint | 主键 |
| sensor_id | varchar(64) | 传感器 ID |
| force_value | decimal(18,6) | 原始力值 |
| displacement_value | decimal(18,6) | 原始位移值 |
| calc_stress | decimal(18,6) | Python 计算应力 |
| verified_stress | decimal(18,6) | 核验应力 |
| verify_status | tinyint | 核验状态：0/1/2/3 |
| verify_remark | varchar(255) | 核验备注 |
| collect_time | datetime | 原始采样时间 |
| calc_time | datetime | Python 计算回写时间 |
| verify_time | datetime | 核验时间 |
| create_time | datetime | 创建时间 |
| deleted | tinyint | 逻辑删除标记，0/1 |

## 5.3 唯一性约束

必须保证：

> 同一个 `sensor_id + collect_time` 只对应一条原始采样记录。

建议建立唯一约束：
`uk_sensor_collect_time (sensor_id, collect_time)`

---

# 6. SQL 执行标准

## 6.1 Raw Upload 语义
```sql
INSERT INTO stress_data
(sensor_id, force_value, displacement_value, collect_time, create_time, verify_status, deleted)
VALUES (?, ?, ?, ?, NOW(), 0, 0)
```

## 6.2 Result Upload 语义
```sql
UPDATE stress_data
SET calc_stress = ?,
    calc_time = ?,
    verify_status = 1
WHERE sensor_id = ?
  AND collect_time = ?
  AND deleted = 0
  AND verify_status IN (0, 3)
```

## 6.3 Verify PASS 语义
```sql
UPDATE stress_data
SET verified_stress = ?,
    verify_time = ?,
    verify_remark = NULL,
    verify_status = 2
WHERE sensor_id = ?
  AND collect_time = ?
  AND deleted = 0
  AND verify_status = 1
```

## 6.4 Verify FAIL 语义
```sql
UPDATE stress_data
SET verify_remark = ?,
    verify_time = ?,
    verify_status = 3
WHERE sensor_id = ?
  AND collect_time = ?
  AND deleted = 0
  AND verify_status = 1
```

## 6.5 Latest 语义
```sql
WHERE sensor_id = ?
AND deleted = 0
AND verify_status = 2
ORDER BY collect_time DESC
LIMIT 1
```

## 6.6 History 语义
```sql
WHERE sensor_id = ?
AND deleted = 0
AND verify_status = 2
AND (collect_time >= startTime)
AND (collect_time <= endTime)
ORDER BY collect_time ASC
LIMIT ?
```

---

# 7. 前端页面说明

页面名称固定为：`StressMonitorPage`

页面骨架固定为：

StressMonitorPage
├─ QueryBar
├─ LatestRawCard
├─ LatestCalcCard
├─ LatestVerifyCard
├─ StressTrendChart
└─ StressHistoryTable

## 页面默认展示
- 最新原始输入：`forceValue`、`displacementValue`
- 最新计算结果：`calcStress`
- 最新核验结果：`verifiedStress`、`verifyStatus`
- 应力历史趋势
- 历史明细表

## 页面数据流
用户点击查询
↓
调用 latest 接口
↓
刷新 LatestRawCard + LatestCalcCard + LatestVerifyCard
↓
调用 history 接口
↓
刷新 StressTrendChart + StressHistoryTable

---

# 8. 时间与环境约束

- 全链路时间统一使用北京时间（UTC+8，Asia/Shanghai）；
- 时间格式固定为：`yyyy-MM-dd HH:mm:ss`；
- 禁止 ISO 8601；
- 禁止时间戳；
- 禁止后端隐含时区换算；
- 服务禁止只绑定 `localhost`；
- 必须允许局域网 IP 访问；
- 内网访问不等于取消认证。

---

# 9. 最终裁定

本稿当前定位为：

> **Stress 模块接口文档 V1.0 审查终稿（计算核验版 / 北京时间版）**

本版结论：
- 名字固定为 `stress`；
- 模块类型固定为“计算核验模块”；
- 原始输入为 `force + displacement`；
- Python 输出为 `calcStress`；
- 核验通过后输出 `verifiedStress`；
- `latest/history` 默认只对业务侧返回 `VERIFIED` 数据；
- 不再将 `strain` 并入 Stress 模块结果定义。