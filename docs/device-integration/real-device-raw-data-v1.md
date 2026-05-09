# Real Device Raw Data V1

## Overview

This document describes the first delivery of the real-device raw data access preparation for an industrial structural health monitoring prototype. The scope is limited to raw data intake, persistence, query, and front-end visualization.

The system is positioned as:

- industrial structural health monitoring prototype
- civil infrastructure sensor data platform
- sensor data acquisition and visualization system
- applied engineering software for smart infrastructure

## Scope

This version only covers the following modules:

1. `vibrating-wire` raw data module
2. `fiber` raw data module for OS-265
3. `vibration-dat` raw data module

## Non-Goals

- No complex algorithm parsing
- No real protocol decoding
- No business routing by sensor type
- No architecture rewrite
- No new framework introduction

## API Surface

The backend exposes 9 black-box testable endpoints:

- `POST /api/sensor/vibrating-wire/raw/upload`
- `POST /api/data/vibrating-wire/latest`
- `POST /api/data/vibrating-wire/history`
- `POST /api/sensor/fiber/raw/upload`
- `POST /api/data/fiber/latest`
- `POST /api/data/fiber/history`
- `POST /api/sensor/vibration-dat/raw/upload`
- `POST /api/data/vibration-dat/latest`
- `POST /api/data/vibration-dat/history`

## Runtime Conventions

- Backend: Spring Boot + MyBatis + MySQL
- Time format: `yyyy-MM-dd HH:mm:ss`
- Time zone: Beijing time
- Unified response envelope: `Result`

Example:

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

## Acceptance Summary

- Backend startup: passed
- Frontend build: passed
- SQL tables: passed
- 9 APIs: passed
- Ready to submit: yes

