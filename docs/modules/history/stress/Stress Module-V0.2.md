> **《Displacement 模块文档包（V0.2 封口终稿 / 北京时间版）》**
> **包含：前端架构说明 + DTO 定义 + 完整接口文档 + SQL/时间/认证约束**

你可以直接丢给隔壁审查。

---

# Displacement 模块文档包（V0.2 封口终稿 / 北京时间版）

---

# 0. 模块范围锁定

本轮只定义 **displacement** 模块 MVP，范围固定：

* 前端：1 个页面 `DisplacementMonitorPage`
* 后端：3 个接口：

  * `POST /api/sensor/displacement/upload`
  * `POST /api/data/displacement/latest`
  * `POST /api/data/displacement/history`
* 数据库：1 张表 `displacement_data`

当前不包含：

* 多轴合成计算
* 云图
* 告警逻辑
* 多模块联动
* 导出
* 删除
* 编辑
* 公网部署
* 线上测试

---

## 0.1 环境边界

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
* 后续若切到线上环境，只允许替换 BaseURL / 部署配置，不改业务接口语义

---

# 1. 前端架构说明

## 1.1 页面名称与骨架

**`DisplacementMonitorPage`**

```text
DisplacementMonitorPage
├─ QueryBar
├─ LatestValueCard
├─ DisplacementTrendChart
└─ DisplacementHistoryTable
```

---

## 1.2 组件职责

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
* 所有时间提交前统一格式化为：`yyyy-MM-dd HH:mm:ss`

不允许：

* 在组件内部硬编码接口地址
* 在组件内部缓存 POST 查询状态
* 在组件内部发起多个业务请求
* 在组件内部自行排序或拼装业务结果

---

### B. LatestValueCard

职责：

* 展示最新位移值

固定展示字段：

* `sensorId`
* `displacementValue`
* `collectTime`

---

### C. DisplacementTrendChart

职责：

* 展示历史位移趋势曲线

映射规则：

* X 轴：`collectTime`
* Y 轴：`displacementValue`

约束：

* 后端 `history` 返回结果固定为 `ASC`
* 前端不二次排序
* 前端只做映射，不做修正
* 数据为空时显示空状态，不造默认点

---

### D. DisplacementHistoryTable

职责：

* 展示历史记录明细

固定列：

* 序号
* sensorId
* displacementValue
* collectTime

约束：

* 图表与表格必须共用同一份 `historyList`
* 严禁图表与表格分别发起 `history` 接口请求

---

## 1.3 页面数据流（锁死模板）

必须严格按以下顺序执行，禁止并发：

```text
用户点击查询
↓
调用 latest 接口
↓
刷新 LatestValueCard
↓
调用 history 接口
↓
刷新 DisplacementTrendChart + DisplacementHistoryTable
```

---

## 1.4 页面状态设计

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
  displacementValue: null,
  collectTime: ''
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

## 1.5 API 层职责

前端 `api/displacement.js` 只定义：

* `getDisplacementLatest`
* `getDisplacementHistory`

要求：

* 所有请求统一从可配置 BaseURL 发起
* 当前 BaseURL 指向内网服务地址
* 不允许把接口地址硬编码到组件内部
* 后续切换线上环境时，只替换 BaseURL，不改页面业务逻辑

---

# 2. DTO 详细定义

## 2.1 DisplacementUploadDTO

字段如下：

| 字段名               | 类型      | 必填 | 说明                         |
| ----------------- | ------- | -: | -------------------------- |
| sensorId          | string  |  是 | 传感器 ID                     |
| displacementValue | decimal |  是 | 位移主展示值                     |
| collectTime       | string  |  是 | 格式固定 `yyyy-MM-dd HH:mm:ss` |

说明：

* `displacementValue` 在当前阶段固定映射到数据库字段 `vertical_deflection`

---

## 2.2 DisplacementQueryDTO

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

# 3. 接口文档

---

## 3.1 接口总览

### 设备侧接口（ApiKey）

1. `POST /api/sensor/displacement/upload`

### 业务侧接口（JWT）

2. `POST /api/data/displacement/latest`
3. `POST /api/data/displacement/history`

---

## 3.2 认证规则

### A. 设备侧上传接口

路径前缀：

```text
/api/sensor/**
```

认证方式：

```text
X-API-KEY
```

要求：

* displacement 模块必须使用 displacement 专属 key
* 不允许与其他模块共用 key

---

### B. 业务侧查询接口

路径前缀：

```text
/api/data/**
```

认证方式：

```text
Authorization: Bearer <JWT>
```

---

## 3.3 状态码约定

* `200 OK`：请求成功
* `400 Bad Request`：请求参数非法 / 时间格式非法
* `401 Unauthorized`：未认证 / Token 非法 / ApiKey 错误
* `403 Forbidden`：已认证但无权限

---

# 4. 详细接口定义

---

## 4.1 设备侧：数据上传

### 路径

```http
POST /api/sensor/displacement/upload
```

### Header

```http
Content-Type: application/json
X-API-KEY: <displacement_specific_key>
```

### Request Body

```json
{
  "sensorId": "D001",
  "displacementValue": 12.45,
  "collectTime": "2026-04-17 14:30:00"
}
```

### 字段规则

* `sensorId` 不能为空
* `displacementValue` 不能为空
* `collectTime` 必须严格匹配格式：`yyyy-MM-dd HH:mm:ss`
* 不接受毫秒
* 不接受 ISO 时间串
* 不接受时间戳

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
  "message": "collectTime格式错误，必须为yyyy-MM-dd HH:mm:ss",
  "data": null
}
```

---

## 4.2 业务侧：最新值查询

### 路径

```http
POST /api/data/displacement/latest
```

### Header

```http
Content-Type: application/json
Authorization: Bearer <JWT>
```

### Request Body

```json
{
  "sensorId": "D001"
}
```

### 查询语义

**latest 固定定义为：**

> 按 `sensorId` 过滤，且 `deleted = 0`，按 `collectTime DESC` 取最新 1 条

不允许：

* 不带 `sensorId`
* 不加 `deleted = 0`
* 用 `id DESC` 替代 `collectTime DESC`

### Success Response

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "sensorId": "D001",
    "displacementValue": 12.45,
    "collectTime": "2026-04-17 14:30:00"
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

## 4.3 业务侧：历史数据查询

### 路径

```http
POST /api/data/displacement/history
```

### Header

```http
Content-Type: application/json
Authorization: Bearer <JWT>
```

### Request Body

```json
{
  "sensorId": "D001",
  "startTime": "2026-04-17 00:00:00",
  "endTime": "2026-04-17 23:59:59",
  "limit": 100
}
```

### 查询语义

**history 固定定义为：**

> 按 `sensorId` 过滤，且 `deleted = 0`，按时间范围筛选，结果固定 `collectTime ASC`

### 规则

* `sensorId` 必填
* `startTime` / `endTime` 可空
* 若传时间，必须严格格式：`yyyy-MM-dd HH:mm:ss`
* `limit` 可空
* 若前端未传 `limit`，后端按 `100` 处理
* 后端强制最大上限为 `500`
* 排序必须固定 ASC
* 不允许返回乱序结果给前端再排序

### Success Response

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "sensorId": "D001",
      "displacementValue": 12.45,
      "collectTime": "2026-04-17 14:30:00"
    },
    {
      "sensorId": "D001",
      "displacementValue": 12.52,
      "collectTime": "2026-04-17 14:35:00"
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

# 5. 业务映射与 SQL 约束

## 5.1 字段映射锁死

当前阶段固定规则如下：

* 核心字段：`displacementValue`
* `displacementValue` **唯一且固定** 映射至数据库中的 `vertical_deflection`
* 本期仅围绕 `vertical_deflection` 进行上传、存储、查询与展示

允许但不参与本期逻辑的字段：

* `horizontal_displacement`
* 其他位移分量字段

说明：

* 其他字段即使在表结构中预留，也统一不参与本期上传、查询与展示逻辑

---

## 5.2 SQL 执行标准

### Latest 语义

必须满足：

```sql
WHERE sensor_id = ?
AND deleted = 0
ORDER BY collect_time DESC
LIMIT 1
```

### History 语义

必须满足：

```sql
WHERE sensor_id = ?
AND deleted = 0
AND (collect_time >= startTime) -- 按需动态拼接
AND (collect_time <= endTime)   -- 按需动态拼接
ORDER BY collect_time ASC
LIMIT ?
```

---

## 5.3 SQL 红线

统一要求：

* 禁止 `SELECT *`
* 所有查询必须显式包含 `deleted = 0`
* `history` 必须固定按 `collect_time ASC`
* `limit` 必须严格按入参截断
* 后端强制最大上限 `500`

---

# 6. 时间与运行约束

## 6.1 时间协议

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

## 6.2 时间语义

前后端所有时间统一按 **北京时间（UTC+8，Asia/Shanghai）** 理解该字符串。

说明：

* 当前阶段传输的是“北京时间语义下的字符串”
* 不是“带时区的时间对象”
* 不是“做时区换算后的结果”

---

## 6.3 运行约束

* 服务禁止只绑定 `localhost`
* 必须允许局域网 IP 访问
* 内网访问 != 取消认证
* `X-API-KEY` 与 `JWT` 拦截器必须全程开启

---

# 7. 自检清单（封口校验）

* [x] 是否所有接口响应都统一为 `{code, message, data}` 结构？
* [x] `latest` 与 `history` 是否均有明确的 JSON Request Body 示例？
* [x] 页面数据流是否锁定为：点击查询 → `latest` → `history`？
* [x] `Chart` 与 `Table` 是否共用同一份 `historyList`？
* [x] 模块名、路径、表名是否已全局替换为 `displacement`？
* [x] 是否已移除所有与 User 模块、admin 权限等无关内容？
* [x] `displacementValue` 是否已唯一映射至 `vertical_deflection`？
* [x] 是否已锁死 `deleted = 0`、`ASC` 排序、禁止 `SELECT *`？
* [x] 是否已明确 `limit`：前端默认 100、后端兜底 100、最大上限 500？
* [x] 是否已明确当前仅内网访问，但不弱化认证要求？
* [x] 是否已明确全链路时间统一按北京时间（UTC+8，Asia/Shanghai）处理？

---

# 8. 最终裁定

本稿当前定位为：

> **Displacement 模块 V0.2 封口终稿（北京时间版）**

可直接移交生成端施工。
建议施工顺序：

1. `DisplacementUploadDTO`
2. `DisplacementQueryDTO`
3. `Controller`
4. `ServiceImpl`
5. `Mapper`

如果你愿意，我下一条直接给你补一个 **“丢给隔壁的施工指令模板”**。
