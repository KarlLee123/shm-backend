# Vibrating-Wire Module

## Purpose

This module prepares raw data ingestion and query support for a vibrating-wire acquisition device. It is intentionally kept as a raw-data module and is not merged into the legacy stress module.

## Supported Data Flow

1. Receive raw payload
2. Persist raw record to MySQL
3. Query latest record
4. Query historical records
5. Display data on the frontend

## API Endpoints

- `POST /api/sensor/vibrating-wire/raw/upload`
- `POST /api/data/vibrating-wire/latest`
- `POST /api/data/vibrating-wire/history`

## Storage Model

Table:

- `vibrating_wire_data`

Conventions:

- `id` is the primary key
- `create_time` is stored in Beijing time format
- `deleted` is used for soft delete compatibility
- `sensor_id` and `collect_time` are indexed

## Example Fields

Typical raw intake fields include:

- `sensorId`
- `deviceNo`
- `devType`
- `channelNo`
- `collectOption`
- `sensorStatus`
- `frequencyStatus`
- `temperatureStatus`
- `frequency`
- `amplitude`
- `temperature`
- `sensorNo`
- `rawHex`
- `collectTime`

## Notes

- No advanced calibration logic is included
- No architecture change is required
- The module is testable through black-box HTTP requests

