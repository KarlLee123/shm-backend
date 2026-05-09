# API Document Index

## OpenAPI / Apifox Location

- OpenAPI JSON: `api/apifox/real-device-raw-data-openapi.json`

## Real Device Access V1 Documentation

- Overview: `docs/device-integration/real-device-raw-data-v1.md`
- Vibrating-wire module: `docs/device-integration/vibrating-wire-module.md`
- Fiber module: `docs/device-integration/fiber-module.md`
- Vibration DAT module: `docs/device-integration/vibration-dat-module.md`
- Final acceptance report: `docs/device-integration/final-acceptance-report.md`

## 9 Interfaces

- `POST /api/sensor/vibrating-wire/raw/upload`
- `POST /api/data/vibrating-wire/latest`
- `POST /api/data/vibrating-wire/history`
- `POST /api/sensor/fiber/raw/upload`
- `POST /api/data/fiber/latest`
- `POST /api/data/fiber/history`
- `POST /api/sensor/vibration-dat/raw/upload`
- `POST /api/data/vibration-dat/latest`
- `POST /api/data/vibration-dat/history`

## How to Import into Apifox

1. Open Apifox.
2. Choose import from OpenAPI / JSON.
3. Select `api/apifox/real-device-raw-data-openapi.json`.
4. Verify the server address is `http://localhost:8080`.
5. Confirm the three tags:
   - `vibrating-wire`
   - `fiber`
   - `vibration-dat`

## Notes

- The response envelope uses the unified `Result` structure.
- The document is intended for black-box verification, sharing, and archiving.

