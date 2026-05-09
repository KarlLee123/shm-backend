# Vibration DAT Module

## Purpose

This module prepares raw data handling for vibration DAT files. It only provides interface and data-structure readiness for file-level access, without complex file parsing.

## Supported Data Flow

1. Receive raw DAT file metadata and raw content placeholder
2. Persist record to MySQL
3. Query latest record
4. Query historical records
5. Display file metadata on the frontend

## API Endpoints

- `POST /api/sensor/vibration-dat/raw/upload`
- `POST /api/data/vibration-dat/latest`
- `POST /api/data/vibration-dat/history`

## Storage Model

Table:

- `vibration_dat_data`

Conventions:

- `id` is the primary key
- `create_time` is stored in Beijing time format
- `deleted` is used for soft delete compatibility
- `sensor_id` and `collect_time` are indexed

## Example Fields

Typical raw intake fields include:

- `sensorId`
- `deviceNo`
- `channelNo`
- `filePath`
- `fileName`
- `fileSize`
- `version`
- `sampleFreq`
- `filterFreq`
- `gainAmplifier`
- `triggerType`
- `dataLength`
- `dataCount`
- `rawContent`
- `dataJson`
- `collectTime`

## Notes

- No complex DAT parsing is included
- Only interface and storage preparation is covered

