下面是可直接丢给隔壁审查的 **《Stress 模块文档包（V0.3.1 封口终稿 / 核验闸口版 / 北京时间版）》**。

这版已经把你要的核心逻辑写死了：

* 传感器上传的是 **力 + 位移**
* Python 回写的是 **应力 + 应变**
* **核验通过才算生效**
* **核验不通过打回重算**
* 前端业务查询默认 **只看 VERIFIED 数据**
* 数据库这次 **一次性把坑位建齐**

---

# Stress 模块文档包（V0.3.1 封口终稿 / 核验闸口版 / 北京时间版）

---

# 0. 模块范围锁定

本轮定义 **stress** 模块的真实 MVP 版本，范围固定为：

* 前端：1 个业务展示页面 `StressMonitorPage`
* 后端：5 个核心接口

  1. `POST /api/sensor/stress/raw/upload`
  2. `POST /api/sensor/stress/result/upload`
  3. `POST /api/data/stress/verify`
  4. `POST /api/data/stress/latest`
  5. `POST /api/data/stress/history`
* 数据库：1 张表 `stress_data`

---

## 0.1 当前模块定位

当前阶段 **Stress 模块不是“传感器直接上传应力值”模块**，而是：

> **传感器上传原始力 / 位移数据 → Python计算应力 / 应变 → 核验通过后数据才正式生效**

因此本模块分三层数据：

### A. 原始输入层

* `forceValue`
* `displacementValue`

### B. 计算结果层

* `calcStress`
* `calcStrain`

### C. 核验结果层

* `verifiedStress`
* `verifiedStrain`
* `verifyStatus`
* `verifyRemark`

---

## 0.2 核心业务原则（本版封口重点）

本模块必须满足以下业务控制原则：

1. **原始数据先入库**
2. **Python 结果只能回写已有原始记录**
3. **核验通过前，业务查询默认不可见**
4. **核验不通过必须打回**
5. **打回后允许 Python 重新回写覆盖计算结果**
6. **已核验通过的数据默认禁止再次被结果接口覆盖**

---

## 0.3 当前不包含

当前阶段不包含：

* 主应力 / 等效应力 / 应力幅 / 热点应力多字段联展
* 云图
* 多模块联动计算
* 告警规则引擎
* 公网部署
* 线上测试

---

## 0.4 环境边界

当前阶段仅限 **局域网 / 内网访问**。

约束如下：

* 必须预留 BaseURL 配置位
* 禁止硬编码访问地址
* 禁止把服务仅绑定为 `localhost`
* 必须允许局域网 IP 访问
* 当前不考虑公网部署与线上测试
* 当前不做 HTTPS、域名解析、外网联调

同时要求：

* 内网访问 != 取消认证
* `X-API-KEY` 与 `JWT` 校验必须保留
* 后续切线上环境时，只允许替换 BaseURL / 部署配置，不改业务接口语义

---

# 1. 状态机定义（核验闸口锁死）

## 1.1 verifyStatus 业务状态

`verifyStatus` 不是普通字段，而是本模块的**主流程闸门**。

固定状态如下：

| 状态值 | 状态名        | 含义                 |
| --- | ---------- | ------------------ |
| 0   | RAW        | 只有原始数据，Python 尚未回写 |
| 1   | CALCULATED | Python 已回写结果，等待核验  |
| 2   | VERIFIED   | 核验通过，业务查询可见        |
| 3   | REJECTED   | 核验不通过，打回重算         |

---

## 1.2 状态流转规则

固定流转如下：

```text
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
```

---

## 1.3 状态流转红线

禁止以下行为：

* `RAW` 直接跳到 `VERIFIED`
* `REJECTED` 不经 Python 重算直接变 `VERIFIED`
* `VERIFIED` 被结果回写接口直接覆盖
* 找不到原始记录却单独新插“计算结果记录”

---

# 2. 前端架构说明

## 2.1 页面名称与骨架

**`StressMonitorPage`**

```text
StressMonitorPage
├─ QueryBar
├─ LatestRawCard
├─ LatestCalcCard
├─ LatestVerifyCard
├─ StressTrendChart
├─ StrainTrendChart
└─ StressHistoryTable
```

---

## 2.2 页面定位

当前页面是**业务展示页**，不是核验管理页。

因此本页面默认只展示：

> **`verifyStatus = VERIFIED` 的数据**

本页面不负责：

* 原始数据上传
* Python 结果回写
* 核验动作提交
* 打回操作
* 云图渲染

---

## 2.3 组件职责

### A. QueryBar

职责：

* 输入 `sensorId`
* 选择 `startTime`
* 选择 `endTime`
* 触发查询

规则：

* `sensorId` 必填
* `startTime` 可空
* `endTime` 可空
* `limit` 前端默认传 `100`
* 所有时间提交前统一格式化为 `yyyy-MM-dd HH:mm:ss`

不允许：

* 在组件内部硬编码接口地址
* 在组件内部缓存 POST 查询状态
* 在组件内部发起多个业务请求
* 在组件内部自行排序或拼装业务结果

---

### B. LatestRawCard

职责：

展示最新**已生效**记录中的原始输入字段：

* `sensorId`
* `forceValue`
* `displacementValue`
* `collectTime`

---

### C. LatestCalcCard

职责：

展示最新**已生效**记录中的 Python 计算结果：

* `calcStress`
* `calcStrain`
* `calcTime`

规则：

* 前端只展示后端返回结果
* 不允许前端自己推算计算值

---

### D. LatestVerifyCard

职责：

展示最新**已生效**记录中的核验结果：

* `verifiedStress`
* `verifiedStrain`
* `verifyStatus`
* `verifyTime`

规则：

* 当前业务页中 `verifyStatus` 固定应为 `VERIFIED`
* 不做状态变更操作

---

### E. StressTrendChart

职责：

展示历史应力趋势曲线。

映射规则：

* X 轴：`collectTime`
* Y 轴：`verifiedStress`

说明：

* 业务页只展示已核验通过数据，因此曲线主值取 `verifiedStress`

---

### F. StrainTrendChart

职责：

展示历史应变趋势曲线。

映射规则：

* X 轴：`collectTime`
* Y 轴：`verifiedStrain`

约束：

* 与 `StressTrendChart` 共用同一份 `historyList`
* 不允许再次单独请求接口

---

### G. StressHistoryTable

职责：

展示历史明细表。

固定列：

* 序号
* sensorId
* forceValue
* displacementValue
* calcStress
* calcStrain
* verifiedStress
* verifiedStrain
* verifyStatus
* collectTime
* verifyTime

约束：

* 图表与表格必须共用同一份 `historyList`
* 严禁图表与表格分别发起 `history` 接口请求

---

## 2.4 页面数据流（锁死模板）

必须严格按以下顺序执行，禁止并发：

```text
用户点击查询
↓
调用 latest 接口
↓
刷新 LatestRawCard + LatestCalcCard + LatestVerifyCard
↓
调用 history 接口
↓
刷新 StressTrendChart + StrainTrendChart + StressHistoryTable
```

---

## 2.5 页面状态设计

页面层统一管理状态：

```js
queryForm = {
  sensorId: '',
  startTime: '',
  endTime: '',
  limit: 100
}

latestData = {
  sensorId: '',
  forceValue: null,
  displacementValue: null,
  calcStress: null,
  calcStrain: null,
  verifiedStress: null,
  verifiedStrain: null,
  verifyStatus: '',
  collectTime: '',
  calcTime: '',
  verifyTime: ''
}

historyList = []

loading = {
  latest: false,
  history: false
}

errorState = {
  latest: '',
  history: ''
}
```

要求：

* `loading.latest` 和 `loading.history` 必须在成功、失败、超时三种场景下强制复位
* 页面层负责 loading / error / 空状态
* 展示组件不自行管理业务请求状态

---

## 2.6 API 层职责

前端 `api/stress.js` 只定义：

* `getStressLatest`
* `getStressHistory`

说明：

* 当前展示页面不直接调用上传接口
* 上传接口仅供传感器与 Python 服务使用
* `verify` 接口属于核验流程，不属于当前业务展示页调用范围

要求：

* 所有请求统一从可配置 BaseURL 发起
* 当前 BaseURL 指向内网服务地址
* 不允许把接口地址硬编码到组件内部
* 后续切线上环境时，只替换 BaseURL，不改页面业务逻辑

---

# 3. 数据库设计（一次建齐）

## 3.1 表名

```text
stress_data
```

---

## 3.2 字段清单

建议本期一次建齐以下字段：

| 字段名                | 类型建议          | 说明           |
| ------------------ | ------------- | ------------ |
| id                 | bigint        | 主键           |
| sensor_id          | varchar(64)   | 传感器 ID       |
| force_value        | decimal(18,6) | 原始力值         |
| displacement_value | decimal(18,6) | 原始位移值        |
| calc_stress        | decimal(18,6) | Python计算应力   |
| calc_strain        | decimal(18,6) | Python计算应变   |
| verified_stress    | decimal(18,6) | 核验应力         |
| verified_strain    | decimal(18,6) | 核验应变         |
| verify_status      | tinyint       | 核验状态：0/1/2/3 |
| verify_remark      | varchar(255)  | 核验备注         |
| collect_time       | datetime      | 原始采样时间       |
| calc_time          | datetime      | Python计算回写时间 |
| verify_time        | datetime      | 核验时间         |
| create_time        | datetime      | 创建时间         |
| deleted            | tinyint       | 逻辑删除标记，0/1   |

---

## 3.3 唯一性约束

必须保证：

> **同一个 `sensor_id + collect_time` 只对应一条原始采样记录**

建议建立唯一约束：

```text
uk_sensor_collect_time (sensor_id, collect_time)
```

原因：

* Python 结果回写要靠 `sensorId + collectTime` 定位
* 核验结果也要靠 `sensorId + collectTime` 定位
* 不允许原始记录重复插入导致状态流转失真

---

## 3.4 当前阶段字段职责

### 当前必参与主流程的字段

* `sensor_id`
* `force_value`
* `displacement_value`
* `calc_stress`
* `calc_strain`
* `verify_status`
* `collect_time`
* `calc_time`
* `verify_time`
* `deleted`

### 当前预留但允许为空的字段

* `verified_stress`
* `verified_strain`
* `verify_remark`

---

# 4. DTO 详细定义

## 4.1 StressRawUploadDTO

用于传感器上传原始数据。

字段如下：

| 字段名               | 类型      | 必填 | 说明                         |
| ----------------- | ------- | -: | -------------------------- |
| sensorId          | string  |  是 | 传感器 ID                     |
| forceValue        | decimal |  是 | 原始力值                       |
| displacementValue | decimal |  是 | 原始位移值                      |
| collectTime       | string  |  是 | 格式固定 `yyyy-MM-dd HH:mm:ss` |

---

## 4.2 StressResultUploadDTO

用于 Python 回写计算结果。

字段如下：

| 字段名         | 类型      | 必填 | 说明       |
| ----------- | ------- | -: | -------- |
| sensorId    | string  |  是 | 传感器 ID   |
| calcStress  | decimal |  是 | 计算应力     |
| calcStrain  | decimal |  是 | 计算应变     |
| collectTime | string  |  是 | 对应原始采样时间 |
| calcTime    | string  |  否 | 计算完成时间   |

说明：

* `sensorId + collectTime` 用于定位已有原始记录
* 当前阶段该接口不新增独立记录，而是**回写已有原始记录**
* 若记录当前状态为 `REJECTED`，允许覆盖旧计算结果并重置状态

---

## 4.3 StressQueryDTO

用于 latest / history 查询。

字段如下：

| 字段名       | 类型      | 必填 | 说明                         |
| --------- | ------- | -: | -------------------------- |
| sensorId  | string  |  是 | 传感器 ID                     |
| startTime | string  |  否 | 格式固定 `yyyy-MM-dd HH:mm:ss` |
| endTime   | string  |  否 | 格式固定 `yyyy-MM-dd HH:mm:ss` |
| limit     | integer |  否 | 查询条数限制                     |

规则：

* 前端默认传 `100`
* 后端若未收到 `limit`，统一按 `100` 处理
* 后端强制最大上限为 `500`

---

## 4.4 StressVerifyDTO

用于核验闸口。

字段如下：

| 字段名            | 类型      | 必填 | 说明                |
| -------------- | ------- | -: | ----------------- |
| sensorId       | string  |  是 | 传感器 ID            |
| action         | string  |  是 | `PASS` 或 `FAIL`   |
| verifiedStress | decimal |  否 | action=PASS 时必填   |
| verifiedStrain | decimal |  否 | action=PASS 时必填   |
| verifyRemark   | string  |  否 | action=FAIL 时建议必填 |
| collectTime    | string  |  是 | 对应原始采样时间          |
| verifyTime     | string  |  否 | 核验时间              |

规则：

* `PASS` 时必须提供 `verifiedStress + verifiedStrain`
* `FAIL` 时不要求提供 verified 数值，但应写入 `verifyRemark`

---

# 5. 接口文档

---

## 5.1 接口总览

### 设备 / 内部计算侧接口（ApiKey）

1. `POST /api/sensor/stress/raw/upload`
2. `POST /api/sensor/stress/result/upload`

### 业务 / 管理侧接口（JWT）

3. `POST /api/data/stress/verify`
4. `POST /api/data/stress/latest`
5. `POST /api/data/stress/history`

---

## 5.2 认证规则

### A. 原始数据 / 结果回写接口

路径前缀：

```text
/api/sensor/**
```

认证方式：

```text
X-API-KEY
```

要求：

* stress 模块必须使用 stress 模块专属 key
* 原始上传与结果回写允许使用不同 key，但都必须属于 stress 模块范围
* 不允许与其他模块共用 key

---

### B. 业务查询 / 核验接口

路径前缀：

```text
/api/data/**
```

认证方式：

```text
Authorization: Bearer <JWT>
```

说明：

* `verify` 属于受控业务接口，必须走 JWT
* 当前文档不展开角色体系，但核验接口默认不属于普通展示用户

---

## 5.3 状态码约定

* `200 OK`：请求成功
* `400 Bad Request`：请求参数非法 / 时间格式非法 / 业务语义不满足
* `401 Unauthorized`：未认证 / Token 非法 / ApiKey 错误
* `403 Forbidden`：已认证但无权限
* `500 Internal Server Error`：系统异常

---

# 6. 详细接口定义

---

## 6.1 原始数据上传接口

### 路径

```http
POST /api/sensor/stress/raw/upload
```

### Header

```http
Content-Type: application/json
X-API-KEY: <stress_raw_key>
```

### Request Body

```json
{
  "sensorId": "S001",
  "forceValue": 125.60,
  "displacementValue": 3.42,
  "collectTime": "2026-04-17 14:30:00"
}
```

### 字段规则

* `sensorId` 不能为空
* `forceValue` 不能为空
* `displacementValue` 不能为空
* `collectTime` 必须严格匹配格式：`yyyy-MM-dd HH:mm:ss`
* 不接受毫秒
* 不接受 ISO 时间串
* 不接受时间戳

### 业务语义

该接口固定定义为：

> **插入一条原始采样记录**

插入内容包括：

* `sensor_id`
* `force_value`
* `displacement_value`
* `collect_time`

同时初始化：

* `calc_stress = null`
* `calc_strain = null`
* `verified_stress = null`
* `verified_strain = null`
* `verify_status = RAW`
* `verify_remark = null`
* `deleted = 0`

### 不允许

* 相同 `sensorId + collectTime` 重复插入
* 通过该接口上传 `calcStress / calcStrain`
* 通过该接口上传 `verifiedStress / verifiedStrain`

### Success Response

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### Fail Response Example

```json
{
  "code": 400,
  "message": "同一sensorId与collectTime的原始记录已存在",
  "data": null
}
```

---

## 6.2 Python 结果回写接口

### 路径

```http
POST /api/sensor/stress/result/upload
```

### Header

```http
Content-Type: application/json
X-API-KEY: <stress_result_key>
```

### Request Body

```json
{
  "sensorId": "S001",
  "calcStress": 98.56,
  "calcStrain": 0.001235,
  "collectTime": "2026-04-17 14:30:00",
  "calcTime": "2026-04-17 14:30:05"
}
```

### 字段规则

* `sensorId` 不能为空
* `calcStress` 不能为空
* `calcStrain` 不能为空
* `collectTime` 必须严格匹配格式：`yyyy-MM-dd HH:mm:ss`
* `calcTime` 若传，必须严格匹配格式：`yyyy-MM-dd HH:mm:ss`

### 业务语义

该接口固定定义为：

> **按 `sensorId + collectTime` 定位原始记录，并回写计算结果**

回写字段包括：

* `calc_stress`
* `calc_strain`
* `calc_time`

同时更新状态：

* 当前状态是 `RAW` → 更新后改为 `CALCULATED`
* 当前状态是 `REJECTED` → 允许覆盖旧计算结果，并重置为 `CALCULATED`

### 不允许

* 找不到原始记录时直接新插一条“结果记录”
* 当前状态是 `VERIFIED` 时继续覆盖结果
* 用该接口回写核验结果
* 用该接口更新 `forceValue / displacementValue`

### Success Response

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### Fail Response Example（无原始记录）

```json
{
  "code": 400,
  "message": "未找到对应的原始记录，无法回写计算结果",
  "data": null
}
```

### Fail Response Example（已核验通过）

```json
{
  "code": 400,
  "message": "当前记录已核验通过，禁止再次覆盖计算结果",
  "data": null
}
```

---

## 6.3 核验接口

### 路径

```http
POST /api/data/stress/verify
```

### Header

```http
Content-Type: application/json
Authorization: Bearer <JWT>
```

### Request Body（通过）

```json
{
  "sensorId": "S001",
  "action": "PASS",
  "verifiedStress": 98.40,
  "verifiedStrain": 0.001230,
  "collectTime": "2026-04-17 14:30:00",
  "verifyTime": "2026-04-17 14:31:00"
}
```

### Request Body（打回）

```json
{
  "sensorId": "S001",
  "action": "FAIL",
  "verifyRemark": "应力结果异常，要求重新计算",
  "collectTime": "2026-04-17 14:30:00",
  "verifyTime": "2026-04-17 14:31:00"
}
```

### 业务语义

该接口固定定义为：

> **按 `sensorId + collectTime` 定位一条已计算记录，并执行核验闸口动作**

#### 当 `action = PASS`

必须执行：

* 写入 `verified_stress`
* 写入 `verified_strain`
* 写入 `verify_time`
* 清空或覆盖 `verify_remark`
* 状态改为 `VERIFIED`

#### 当 `action = FAIL`

必须执行：

* 写入 `verify_remark`
* 写入 `verify_time`
* 状态改为 `REJECTED`

说明：

* `FAIL` 不得让该记录继续被业务查询采信
* `FAIL` 后允许 Python 再次通过结果回写接口重算覆盖

### 不允许

* 对 `RAW` 状态直接核验
* 对不存在的记录核验
* `PASS` 时不提供 `verifiedStress / verifiedStrain`
* `FAIL` 后仍保持 `CALCULATED`
* 核验通过后又改回未核验状态，除非后续另开专门重测流程

### Success Response

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### Fail Response Example

```json
{
  "code": 400,
  "message": "当前记录尚未进入待核验状态，禁止执行核验",
  "data": null
}
```

---

## 6.4 业务侧：最新值查询

### 路径

```http
POST /api/data/stress/latest
```

### Header

```http
Content-Type: application/json
Authorization: Bearer <JWT>
```

### Request Body

```json
{
  "sensorId": "S001"
}
```

### 查询语义

**latest 固定定义为：**

> 按 `sensorId` 过滤，且 `deleted = 0`，且 `verifyStatus = VERIFIED`，按 `collectTime DESC` 取最新 1 条完整记录

说明：

* 该接口默认只返回**已核验通过**的数据
* `RAW / CALCULATED / REJECTED` 数据默认对业务页不可见

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
    "calcStrain": 0.001235,
    "verifiedStress": 98.40,
    "verifiedStrain": 0.001230,
    "verifyStatus": "VERIFIED",
    "collectTime": "2026-04-17 14:30:00",
    "calcTime": "2026-04-17 14:30:05",
    "verifyTime": "2026-04-17 14:31:00"
  }
}
```

### Empty Response

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## 6.5 业务侧：历史数据查询

### 路径

```http
POST /api/data/stress/history
```

### Header

```http
Content-Type: application/json
Authorization: Bearer <JWT>
```

### Request Body

```json
{
  "sensorId": "S001",
  "startTime": "2026-04-17 00:00:00",
  "endTime": "2026-04-17 23:59:59",
  "limit": 100
}
```

### 查询语义

**history 固定定义为：**

> 按 `sensorId` 过滤，且 `deleted = 0`，且 `verifyStatus = VERIFIED`，按时间范围筛选，结果固定 `collectTime ASC`

### 规则

* `sensorId` 必填
* `startTime / endTime` 可空
* 若传时间，必须严格格式：`yyyy-MM-dd HH:mm:ss`
* `limit` 可空
* 若前端未传 `limit`，后端按 `100` 处理
* 后端强制最大上限为 `500`
* 排序必须固定 ASC
* 不允许返回未核验通过数据给业务页

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
      "calcStrain": 0.001235,
      "verifiedStress": 98.40,
      "verifiedStrain": 0.001230,
      "verifyStatus": "VERIFIED",
      "collectTime": "2026-04-17 14:30:00",
      "calcTime": "2026-04-17 14:30:05",
      "verifyTime": "2026-04-17 14:31:00"
    }
  ]
}
```

### Empty Response

```json
{
  "code": 200,
  "message": "success",
  "data": []
}
```

---

# 7. 业务映射与 SQL 约束

## 7.1 字段映射锁死

当前阶段固定规则如下：

### 原始输入字段

* `forceValue` → `force_value`
* `displacementValue` → `displacement_value`

### Python 计算结果字段

* `calcStress` → `calc_stress`
* `calcStrain` → `calc_strain`

### 核验结果字段

* `verifiedStress` → `verified_stress`
* `verifiedStrain` → `verified_strain`
* `verifyStatus` → `verify_status`
* `verifyRemark` → `verify_remark`

---

## 7.2 SQL 执行标准

### Raw Upload 语义

```sql
INSERT INTO stress_data
(sensor_id, force_value, displacement_value, collect_time, create_time, verify_status, deleted)
VALUES (?, ?, ?, ?, NOW(), 0, 0)
```

要求：

* 依赖 `sensor_id + collect_time` 唯一约束
* 失败不允许吞异常

---

### Result Upload 语义

```sql
UPDATE stress_data
SET calc_stress = ?,
    calc_strain = ?,
    calc_time = ?,
    verify_status = 1
WHERE sensor_id = ?
  AND collect_time = ?
  AND deleted = 0
  AND verify_status IN (0, 3)
```

要求：

* 必须只更新 `RAW / REJECTED`
* 必须更新已有原始记录
* 不允许 update 0 行后假装成功

---

### Verify PASS 语义

```sql
UPDATE stress_data
SET verified_stress = ?,
    verified_strain = ?,
    verify_time = ?,
    verify_remark = NULL,
    verify_status = 2
WHERE sensor_id = ?
  AND collect_time = ?
  AND deleted = 0
  AND verify_status = 1
```

---

### Verify FAIL 语义

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

说明：

* `FAIL` 不强制清空旧 `calc_stress / calc_strain`
* 但业务查询因只查 `VERIFIED`，因此该记录不会被业务页采信

---

### Latest 语义

必须满足：

```sql
WHERE sensor_id = ?
AND deleted = 0
AND verify_status = 2
ORDER BY collect_time DESC
LIMIT 1
```

---

### History 语义

必须满足：

```sql
WHERE sensor_id = ?
AND deleted = 0
AND verify_status = 2
AND (collect_time >= startTime) -- 按需动态拼接
AND (collect_time <= endTime)   -- 按需动态拼接
ORDER BY collect_time ASC
LIMIT ?
```

---

## 7.3 SQL 红线

统一要求：

* 禁止 `SELECT *`
* 所有查询必须显式包含 `deleted = 0`
* 业务页查询必须显式包含 `verify_status = VERIFIED`
* `history` 必须固定按 `collect_time ASC`
* `limit` 必须严格按入参截断
* 后端强制最大上限 `500`

---

# 8. 时间与运行约束

## 8.1 时间协议

全链路时间统一使用 **北京时间（UTC+8，Asia/Shanghai）**，格式固定为：

```text
yyyy-MM-dd HH:mm:ss
```

要求：

* 严格按北京时间处理
* 严格解析
* 禁止宽松格式
* 禁止 ISO 8601
* 禁止时间戳
* 禁止后端隐含时区换算
* 禁止 UTC 自动转换

---

## 8.2 时间语义

前后端所有时间统一按 **北京时间（UTC+8，Asia/Shanghai）** 理解该字符串。

说明：

* 当前阶段传输的是“北京时间语义下的字符串”
* 不是“带时区的时间对象”
* 不是“做时区换算后的结果”

---

## 8.3 运行约束

* 服务禁止只绑定 `localhost`
* 必须允许局域网 IP 访问
* 内网访问 != 取消认证
* `X-API-KEY` 与 `JWT` 拦截器必须全程开启

---

# 9. 自检清单（封口校验）

* [x] 是否已将 Stress 模块改为“原始输入 + 计算结果 + 核验闸口”模型？
* [x] 是否已明确传感器上传的是 `forceValue + displacementValue`？
* [x] 是否已明确 Python 回写的是 `calcStress + calcStrain`？
* [x] 是否已明确 `verifyStatus` 是主流程闸门而非普通字段？
* [x] 是否已定义 `RAW / CALCULATED / VERIFIED / REJECTED` 状态机？
* [x] 是否已明确 `FAIL` 打回后允许 Python 重算覆盖？
* [x] 是否已明确 `VERIFIED` 状态默认禁止结果接口再次覆盖？
* [x] 是否已明确 latest/history 默认只查询 `VERIFIED` 数据？
* [x] 是否已一次性把数据库字段建齐？
* [x] 是否已明确原始上传与结果回写是两个不同接口？
* [x] 是否已明确核验接口从“预留”升级为“核心施工接口”？
* [x] 是否已锁死 `deleted = 0`、`ASC` 排序、禁止 `SELECT *`？
* [x] 是否已明确 `limit`：前端默认 100、后端兜底 100、最大上限 500？
* [x] 是否已明确当前仅内网访问，但不弱化认证要求？
* [x] 是否已明确全链路时间统一按北京时间（UTC+8，Asia/Shanghai）处理？

---

# 10. 最终裁定

本稿当前定位为：

> **Stress 模块 V0.3.1 封口终稿（核验闸口版 / 北京时间版）**

可直接移交隔壁审查。
建议施工顺序：

1. 建表结构先落
2. `StressRawUploadDTO`
3. `StressResultUploadDTO`
4. `StressQueryDTO`
5. `StressVerifyDTO`
6. `Controller`
7. `ServiceImpl`
8. `Mapper`
9. mapper.xml

如果你愿意，我下一条可以继续给你单独拆一份 **“Stress 建表规格审查稿”**，只保留表结构与索引，不掺接口描述。
