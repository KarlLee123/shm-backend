# Stress API Usage

## 文件说明

1. `stress-module-openapi.json`
   - OpenAPI 3.0 文件
   - 可直接导入 APIFox

2. `stress-module-postman-collection.json`
   - Postman Collection 文件
   - 也可导入 APIFox

---

## 推荐导入方式（APIFox）

1. 打开 APIFox
2. 进入项目或新建项目
3. 选择“导入”
4. 选择 **OpenAPI / Swagger**
5. 导入 `stress-module-openapi.json`

---

## 备用方式

1. 选择“导入”
2. 选择 **Postman Collection**
3. 导入 `stress-module-postman-collection.json`

---

## 当前字段已按代码对齐

- `StressRawUploadDTO`
- `StressQueryDTO`

---

## 核心请求体

### 上传

```json
{
  "sensorId": "SENSOR_001",
  "forceValue": 125.68,
  "displacementValue": 2.35,
  "collectTime": "2026-04-18 14:30:00"
}
```

### latest

```json
{
  "sensorId": "SENSOR_001",
  "limit": 1
}
```

### history

```json
{
  "sensorId": "SENSOR_001",
  "startTime": "2026-04-18 00:00:00",
  "endTime": "2026-04-18 23:59:59",
  "limit": 100
}
```

---

## 使用建议

- APIFox 导入优先使用 OpenAPI 文件
- Postman Collection 作为备用方案
- 当前联调阶段默认服务地址为 `http://localhost:8080`
