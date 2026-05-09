# Fiber Module

## Purpose

This module prepares raw data access for the OS-265 fiber grating device. It keeps the payload in a raw access layer and does not hard-code the data into strain, displacement, or acceleration business modules.

## Supported Data Flow

1. Receive raw fiber payload
2. Persist raw record to MySQL
3. Query latest record
4. Query historical records
5. Display data on the frontend

## API Endpoints

- `POST /api/sensor/fiber/raw/upload`
- `POST /api/data/fiber/latest`
- `POST /api/data/fiber/history`

## Storage Model

Table:

- `fiber_raw_data`

Conventions:

- `id` is the primary key
- `create_time` is stored in Beijing time format
- `deleted` is used for soft delete compatibility
- `sensor_id` and `collect_time` are indexed

## Example Fields

Typical raw intake fields include:

- `sensorId`
- `deviceNo`
- `fiberNo`
- `channelNo`
- `rawValue`
- `wavelength`
- `wavelengthShift`
- `intensity`
- `collectTime`

## Notes

- No fixed mapping to strain, displacement, or acceleration
- No complex protocol parsing
- Only raw data access preparation is covered in this version
- The current DTO contract excludes `physicalValue`, `unit`, `dataType`, and `rawPayload`.
