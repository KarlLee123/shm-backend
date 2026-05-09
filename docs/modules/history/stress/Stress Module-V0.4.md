《Stress 模块文档包（V0.4 封口终稿 / 数据分流版 / 北京时间版）》

# 0. 模块范围锁定

本轮定义 stress 模块第一版的真实可落地方案，范围固定为：

- 前端：1 个业务展示页面 `StressMonitorPage`
- 后端：5 个核心接口
  1. `POST /api/sensor/stress/raw/upload`
  2. `POST /api/sensor/stress/result/upload`
  3. `POST /api/data/stress/verify`
  4. `POST /api/data/stress/latest`
  5. `POST /api/data/stress/history`
- 数据库：1 张表 `stress_data`

说明：

- 当前第一版先落地“传感器原始数据接收与展示”闭环；
- Python 计算链路与核验链路仍保留在正式规格中；
- 当前若 Python 接口暂不可用，不影响原始传感器数据先行接入与前端展示；
- 本版不是推翻原模型，而是在原模型基础上补充“数据分流规则”，使第一版实现与后续完整版本兼容。

---

# 0.1 当前模块定位

当前 stress 模块必须兼容两类数据处理流程，而不是把所有数据强行塞进同一条链路。

## A. 原始直采数据流程（可直接采信）

适用对象：

- 力值 `forceValue`
- 位移值 `displacementValue`

特点：

- 由传感器直接上传
- 不依赖 Python 计算
- 可直接入库
- 可直接前端展示
- 不强制经过核验闸口后才可见

固定链路：

传感器上传原始数据
→ Java 后端入库
→ 前端查询展示

---

## B. 计算结果数据流程（需计算后采信）

适用对象：

- 应力 `calcStress / verifiedStress`
- 应变 `calcStrain / verifiedStrain`
- 后续其他需算法处理才能形成业务结果的数据

特点：

- 必须基于原始记录产生
- 需由 Python 或其他算法服务处理
- 进入业务采信前可设置核验闸口
- 核验通过后才允许业务前端展示结果数据

固定链路：

传感器上传原始数据
→ Python 回写计算结果
→ 核验通过
→ 前端展示结果数据

---

# 0.2 当前阶段现实约束（第一版施工口径）

当前已知前提如下：

- 传感器当前只上传“力 + 位移”
- Python 接口当前暂不可用
- 第一版目标是先把传感器数据接收、存储、展示跑通
- 数据库结构不能因第一版简化而被砍残
- 后续仍需兼容 Python 处理链路与核验链路

因此本版裁定如下：

1. 数据库一次性按完整结构建齐
2. 第一版先启用原始直采数据流程
3. 原始力值、位移值允许直接展示
4. 应力、应变等计算结果字段当前允许为空
5. 核验闸口不作为所有数据的统一前置条件
6. 核验闸口仅对“需计算后采信的数据”生效

---

# 0.3 核心业务原则（本版封口重点）

本模块必须满足以下业务控制原则：

1. 原始采样数据必须先入库
2. 原始直采数据可直接对业务页展示
3. Python 结果只能回写已有原始记录
4. 需计算后采信的数据必须核验通过后才允许展示
5. 核验不通过的数据不得被业务结果页采信
6. 已核验通过的结果数据默认禁止再次被结果接口覆盖
7. 当前第一版虽不启用 Python，但不得破坏后续接入路径

---

# 0.4 当前不包含

当前阶段不包含：

- 主应力 / 等效应力 / 应力幅 / 热点应力多字段联展
- 云图
- 多模块联动计算
- 告警规则引擎
- 公网部署
- 线上测试

---

# 0.5 环境边界

当前阶段仅限局域网 / 内网访问。

约束如下：

- 必须预留 BaseURL 配置位
- 禁止硬编码访问地址
- 禁止把服务仅绑定为 `localhost`
- 必须允许局域网 IP 访问
- 当前不考虑公网部署与线上测试
- 当前不做 HTTPS、域名解析、外网联调

同时要求：

- 内网访问 != 取消认证
- `X-API-KEY` 与 `JWT` 校验必须保留
- 后续切线上环境时，只允许替换 BaseURL / 部署配置，不改业务接口语义

---

# 1. 数据分流规则（本版新增锁死）

## 1.1 为什么必须分流

当前模块中的数据并不处于同一业务语义层级：

- 力值、位移值是传感器原始直采数据；
- 应力、应变是后续计算结果数据。

因此系统不能再用“全部数据统一核验后展示”的单一规则覆盖所有字段，否则会导致：

- 原始可用数据被无意义阻塞；
- 第一版前端无法展示传感器真实数据；
- 文档与实现产生冲突；
- 后续计算链路接入时语义混乱。

---

## 1.2 分流规则锁死

### 原始直采数据

以下字段属于原始直采数据：

- `forceValue`
- `displacementValue`

规则：

- 传感器上传后直接入库
- 可直接参与 latest/history 查询
- 当前前端页面第一版主要展示该类数据
- 不以 `verifyStatus = VERIFIED` 作为展示前提

---

### 计算结果数据

以下字段属于计算结果数据：

- `calcStress`
- `calcStrain`
- `verifiedStress`
- `verifiedStrain`

规则：

- 不允许脱离原始记录单独生成
- 需通过 Python 或其他算法链路写入
- 若进入业务采信流程，必须经过核验控制
- 业务结果展示必须只采信已核验通过的数据

---

## 1.3 查询语义分离

### 原始数据查询语义

查询目标为传感器原始监测值时：

- 允许直接查询有效原始记录
- 只要求 `deleted = 0`
- 不强制要求 `verify_status = VERIFIED`

---

### 结果数据查询语义

查询目标为算法加工后的业务结果时：

- 必须只查询已核验通过记录
- 必须显式带 `verify_status = VERIFIED`

---

# 2. 状态机定义（适用范围修订）

## 2.1 verifyStatus 业务状态

`verifyStatus` 仍保留为本模块中“计算结果链路”的流程闸门。

固定状态如下：

| 状态值 | 状态名        | 含义 |
| --- | ---------- | --- |
| 0   | RAW        | 只有原始数据，Python 尚未回写 |
| 1   | CALCULATED | Python 已回写结果，等待核验 |
| 2   | VERIFIED   | 核验通过，结果数据可见 |
| 3   | REJECTED   | 核验不通过，打回重算 |

---

## 2.2 状态机适用范围

本状态机主要适用于：

- 应力
- 应变
- 其他需计算、需核验后采信的结果型数据

本状态机不强制作用于：

- 传感器直接上传的力值
- 传感器直接上传的位移值
- 其他可直接采信的原始监测数据

---

## 2.3 状态流转规则

结果型数据固定流转如下：

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

---

## 2.4 状态流转红线

禁止以下行为：

- `RAW` 直接跳到 `VERIFIED`（结果型数据）
- `REJECTED` 不经 Python 重算直接变 `VERIFIED`
- `VERIFIED` 被结果回写接口直接覆盖
- 找不到原始记录却单独新插“计算结果记录”

说明：

- 上述红线仅针对结果型数据链路；
- 原始直采数据不要求经过本状态机才允许前端展示。

---

# 3. 前端架构说明

## 3.1 页面名称与骨架

**`StressMonitorPage`**

StressMonitorPage
├─ QueryBar
├─ LatestRawCard
├─ OptionalResultCard
├─ ForceTrendChart
├─ DisplacementTrendChart
└─ StressHistoryTable

---

## 3.2 页面定位

当前页面第一版首先是“原始监测数据展示页”。

因此本页面默认展示：

- 最新原始力值
- 最新原始位移值
- 历史力值趋势
- 历史位移趋势
- 历史原始记录表

若后续 Python 结果链路恢复，可扩展展示：

- 最新计算结果
- 最新核验结果
- 结果型数据趋势与明细

---

## 3.3 组件职责

### A. QueryBar

职责：

- 输入 `sensorId`
- 选择 `startTime`
- 选择 `endTime`
- 触发查询

规则：

- `sensorId` 必填
- `startTime` 可空
- `endTime` 可空
- `limit` 前端默认传 `100`
- 所有时间提交前统一格式化为 `yyyy-MM-dd HH:mm:ss`

---

### B. LatestRawCard

职责：

展示最新原始监测记录中的字段：

- `sensorId`
- `forceValue`
- `displacementValue`
- `collectTime`

---

### C. OptionalResultCard

职责：

预留展示后续计算结果字段：

- `calcStress`
- `calcStrain`
- `verifiedStress`
- `verifiedStrain`
- `verifyStatus`
- `calcTime`
- `verifyTime`

当前第一版：

- 可不展示；
- 或展示为空占位；
- 不作为首版主功能验收项。

---

### D. ForceTrendChart

职责：

展示力值历史趋势曲线。

映射规则：

- X 轴：`collectTime`
- Y 轴：`forceValue`

---

### E. DisplacementTrendChart

职责：

展示位移历史趋势曲线。

映射规则：

- X 轴：`collectTime`
- Y 轴：`displacementValue`

---

### F. StressHistoryTable

职责：

展示历史明细表。

第一版固定列：

- 序号
- sensorId
- forceValue
- displacementValue
- collectTime

扩展预留列：

- calcStress
- calcStrain
- verifiedStress
- verifiedStrain
- verifyStatus
- verifyTime

说明：

- 第一版可只展示原始数据核心列；
- 扩展列可后续逐步放开。

---

## 3.4 页面数据流（第一版锁死模板）

必须严格按以下顺序执行，禁止并发：

用户点击查询
↓
调用 latest 接口
↓
刷新 LatestRawCard
↓
调用 history 接口
↓
刷新 ForceTrendChart + DisplacementTrendChart + StressHistoryTable

---

## 3.5 页面状态设计

页面层统一管理状态：

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

要求：

- `loading.latest` 和 `loading.history` 必须在成功、失败、超时三种场景下强制复位
- 页面层负责 loading / error / 空状态
- 展示组件不自行管理业务请求状态

---

## 3.6 API 层职责

前端 `api/stress.js` 当前只强制定义：

- `getStressLatest`
- `getStressHistory`

前端可预留但当前不接入页面主流程的接口：

- `verifyStressData`

说明：

- 当前展示页面不直接调用上传接口
- 上传接口仅供传感器与后续 Python 服务使用
- `verify` 接口属于管理/核验流程，不属于当前业务展示页默认调用范围

---

# 4. 数据库设计（一次建齐）

## 4.1 表名

`stress_data`

---

## 4.2 字段清单

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

## 4.3 唯一性约束

必须保证：

同一个 `sensor_id + collect_time` 只对应一条原始采样记录

建议建立唯一约束：

`uk_sensor_collect_time (sensor_id, collect_time)`

原因：

- Python 结果回写要靠 `sensorId + collectTime` 定位
- 核验结果也要靠 `sensorId + collectTime` 定位
- 不允许原始记录重复插入导致状态流转失真

---

## 4.4 当前阶段字段职责

### 当前第一版必参与主流程的字段

- `sensor_id`
- `force_value`
- `displacement_value`
- `collect_time`
- `create_time`
- `deleted`

### 当前预留但允许为空的字段

- `calc_stress`
- `calc_strain`
- `verified_stress`
- `verified_strain`
- `verify_status`
- `verify_remark`
- `calc_time`
- `verify_time`

说明：

- 第一版接收的是原始数据；
- 结果型字段保留坑位但当前允许为空；
- 不得因为第一版暂不用就删除上述字段。

---

# 5. DTO 详细定义

## 5.1 StressRawUploadDTO

用于传感器上传原始数据。

字段如下：

| 字段名               | 类型      | 必填 | 说明                         |
| ----------------- | ------- | -: | -------------------------- |
| sensorId          | string  |  是 | 传感器 ID                     |
| forceValue        | decimal |  是 | 原始力值                       |
| displacementValue | decimal |  是 | 原始位移值                      |
| collectTime       | string  |  是 | 格式固定 `yyyy-MM-dd HH:mm:ss` |

---

## 5.2 StressResultUploadDTO

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

- 当前第一版可暂不启用该接口；
- 但 DTO 规格必须保留；
- 后续恢复 Python 链路时不得重新定义字段语义。

---

## 5.3 StressQueryDTO

用于 latest / history 查询。

字段如下：

| 字段名       | 类型      | 必填 | 说明                         |
| --------- | ------- | -: | -------------------------- |
| sensorId  | string  |  是 | 传感器 ID                     |
| startTime | string  |  否 | 格式固定 `yyyy-MM-dd HH:mm:ss` |
| endTime   | string  |  否 | 格式固定 `yyyy-MM-dd HH:mm:ss` |
| limit     | integer |  否 | 查询条数限制                     |

规则：

- 前端默认传 `100`
- 后端若未收到 `limit`，统一按 `100` 处理
- 后端强制最大上限为 `500`

---

## 5.4 StressVerifyDTO

用于结果型数据核验。

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

- `PASS` 时必须提供 `verifiedStress + verifiedStrain`
- `FAIL` 时不要求提供 verified 数值，但应写入 `verifyRemark`
- 当前第一版若无结果型数据，该接口可暂不启用，但规格必须保留

---

# 6. 接口文档

## 6.1 接口总览

### 设备 / 内部计算侧接口（ApiKey）

1. `POST /api/sensor/stress/raw/upload`
2. `POST /api/sensor/stress/result/upload`

### 业务 / 管理侧接口（JWT）

3. `POST /api/data/stress/verify`
4. `POST /api/data/stress/latest`
5. `POST /api/data/stress/history`

---

## 6.2 认证规则

### A. 原始数据 / 结果回写接口

路径前缀：

`/api/sensor/**`

认证方式：

`X-API-KEY`

要求：

- stress 模块必须使用 stress 模块专属 key
- 原始上传与结果回写允许使用不同 key，但都必须属于 stress 模块范围
- 不允许与其他模块共用 key

---

### B. 业务查询 / 核验接口

路径前缀：

`/api/data/**`

认证方式：

`Authorization: Bearer <JWT>`

说明：

- `verify` 属于受控业务接口，必须走 JWT
- 当前文档不展开角色体系，但核验接口默认不属于普通展示用户

---

## 6.3 状态码约定

- `200 OK`：请求成功
- `400 Bad Request`：请求参数非法 / 时间格式非法 / 业务语义不满足
- `401 Unauthorized`：未认证 / Token 非法 / ApiKey 错误
- `403 Forbidden`：已认证但无权限
- `500 Internal Server Error`：系统异常

---

# 7. 详细接口定义

## 7.1 原始数据上传接口

### 路径

`POST /api/sensor/stress/raw/upload`

### Header

`Content-Type: application/json`
`X-API-KEY: <stress_raw_key>`

### Request Body

{
  "sensorId": "S001",
  "forceValue": 125.60,
  "displacementValue": 3.42,
  "collectTime": "2026-04-17 14:30:00"
}

### 业务语义

该接口固定定义为：

插入一条原始采样记录

插入内容包括：

- `sensor_id`
- `force_value`
- `displacement_value`
- `collect_time`

同时初始化：

- `calc_stress = null`
- `calc_strain = null`
- `verified_stress = null`
- `verified_strain = null`
- `verify_status = null`
- `verify_remark = null`
- `deleted = 0`

说明：

- 原始数据可被业务页直接查询展示；
- 当前第一版不以 `verify_status = VERIFIED` 作为原始数据展示前提。

---

## 7.2 Python 结果回写接口

### 路径

`POST /api/sensor/stress/result/upload`

### 业务语义

该接口固定定义为：

按 `sensorId + collectTime` 定位原始记录，并回写计算结果

回写字段包括：

- `calc_stress`
- `calc_strain`
- `calc_time`

同时更新状态：

- 当前状态是 `RAW` 或结果字段为空 → 更新后改为 `CALCULATED`
- 当前状态是 `REJECTED` → 允许覆盖旧计算结果，并重置为 `CALCULATED`

说明：

- 当前第一版该接口可暂不启用；
- 但正式规格与数据库语义必须保留。

---

## 7.3 核验接口

### 路径

`POST /api/data/stress/verify`

### 业务语义

该接口固定定义为：

按 `sensorId + collectTime` 定位一条已计算记录，并执行核验闸口动作

当 `action = PASS`：

- 写入 `verified_stress`
- 写入 `verified_strain`
- 写入 `verify_time`
- 清空或覆盖 `verify_remark`
- 状态改为 `VERIFIED`

当 `action = FAIL`：

- 写入 `verify_remark`
- 写入 `verify_time`
- 状态改为 `REJECTED`

说明：

- 核验接口只针对结果型数据；
- 不用于原始力值、位移值的日常展示放行。

---

## 7.4 业务侧：最新值查询

### 路径

`POST /api/data/stress/latest`

### 查询语义

latest 必须区分查询对象：

### A. 当前第一版原始监测数据查询

按 `sensorId` 过滤，且 `deleted = 0`，按 `collectTime DESC` 取最新 1 条原始记录。

返回重点字段：

- `sensorId`
- `forceValue`
- `displacementValue`
- `collectTime`

### B. 后续结果型数据查询

若后续前端进入结果展示模式，则必须只返回：

- `verifyStatus = VERIFIED`
- 且结果字段完整的记录

---

## 7.5 业务侧：历史数据查询

### 路径

`POST /api/data/stress/history`

### 查询语义

history 也必须区分查询对象：

### A. 当前第一版原始监测数据查询

按 `sensorId` 过滤，且 `deleted = 0`，按时间范围筛选，结果固定 `collectTime ASC`。

### B. 后续结果型数据查询

若查询目标是应力/应变等业务结果，则必须显式附加：

- `verify_status = VERIFIED`

---

# 8. 业务映射与 SQL 约束

## 8.1 字段映射锁死

### 原始输入字段

- `forceValue` → `force_value`
- `displacementValue` → `displacement_value`

### Python 计算结果字段

- `calcStress` → `calc_stress`
- `calcStrain` → `calc_strain`

### 核验结果字段

- `verifiedStress` → `verified_stress`
- `verifiedStrain` → `verified_strain`
- `verifyStatus` → `verify_status`
- `verifyRemark` → `verify_remark`

---

## 8.2 SQL 执行标准

### Raw Upload 语义

INSERT INTO stress_data
(sensor_id, force_value, displacement_value, collect_time, create_time, deleted)
VALUES (?, ?, ?, ?, NOW(), 0)

要求：

- 依赖 `sensor_id + collect_time` 唯一约束
- 失败不允许吞异常

---

### Result Upload 语义

UPDATE stress_data
SET calc_stress = ?,
    calc_strain = ?,
    calc_time = ?,
    verify_status = 1
WHERE sensor_id = ?
  AND collect_time = ?
  AND deleted = 0
  AND (verify_status IS NULL OR verify_status IN (0, 3))

---

### Verify PASS 语义

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

---

### Verify FAIL 语义

UPDATE stress_data
SET verify_remark = ?,
    verify_time = ?,
    verify_status = 3
WHERE sensor_id = ?
  AND collect_time = ?
  AND deleted = 0
  AND verify_status = 1

---

### Latest（第一版原始数据）语义

WHERE sensor_id = ?
AND deleted = 0
ORDER BY collect_time DESC
LIMIT 1

---

### History（第一版原始数据）语义

WHERE sensor_id = ?
AND deleted = 0
AND (collect_time >= startTime)
AND (collect_time <= endTime)
ORDER BY collect_time ASC
LIMIT ?

---

### 后续结果型数据查询语义

必须额外满足：

AND verify_status = 2

---

## 8.3 SQL 红线

统一要求：

- 禁止 `SELECT *`
- 所有查询必须显式包含 `deleted = 0`
- 原始数据查询不强制 `verify_status = VERIFIED`
- 结果数据查询必须显式包含 `verify_status = VERIFIED`
- `history` 必须固定按 `collect_time ASC`
- `limit` 必须严格按入参截断
- 后端强制最大上限 `500`

---

# 9. 时间与运行约束

## 9.1 时间协议

全链路时间统一使用北京时间（UTC+8，Asia/Shanghai），格式固定为：

`yyyy-MM-dd HH:mm:ss`

要求：

- 严格按北京时间处理
- 严格解析
- 禁止宽松格式
- 禁止 ISO 8601
- 禁止时间戳
- 禁止后端隐含时区换算
- 禁止 UTC 自动转换

---

## 9.2 运行约束

- 服务禁止只绑定 `localhost`
- 必须允许局域网 IP 访问
- 内网访问 != 取消认证
- `X-API-KEY` 与 `JWT` 拦截器必须全程开启

---

# 10. 自检清单（封口校验）

- [x] 是否保留了完整数据库结构而非为了第一版砍残？
- [x] 是否明确第一版先落地“力值 + 位移值”的接收展示闭环？
- [x] 是否明确原始直采数据允许直接展示？
- [x] 是否明确核验闸口只适用于结果型数据？
- [x] 是否明确 Python 链路虽暂不可用但规格仍保留？
- [x] 是否明确 latest/history 的查询语义要按“原始数据 / 结果数据”分流？
- [x] 是否明确前端第一版优先展示力值、位移值而非伪造应力结果？
- [x] 是否保留后续 `result/upload + verify` 的正式扩展路径？
- [x] 是否明确 SQL 查询中原始数据与结果数据的过滤条件不同？
- [x] 是否明确当前仍为内网环境且认证要求不削弱？

---

# 11. 最终裁定

本稿当前定位为：

Stress 模块 V0.4 封口终稿（数据分流版 / 北京时间版）

最终业务裁定如下：

1. 数据库一次建齐，不因第一版简化而删除后续字段
2. 第一版先接入传感器原始力值、位移值
3. 原始直采数据可直接入库并前端展示
4. 应力、应变等结果型数据保留后续 Python 处理与核验链路
5. 核验后才展示不是全量规则，只适用于结果型数据
6. 当前实现先跑通原始监测展示闭环，后续再补足完整计算闭环

可直接移交隔壁审查。
