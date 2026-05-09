# SQL Documentation

This folder is used for database delivery artifacts related to the backend raw-device integration work.

## Included Tables

- `vibrating_wire_data`
- `fiber_raw_data`
- `vibration_dat_data`

## Execution Order

When applying SQL to the target database, use the prepared scripts in the following order:

1. `vibrating_wire_data.sql`
2. `fiber_raw_data.sql`
3. `vibration_dat_data.sql`

## Runtime Convention

- Database: MySQL
- Target schema: `stress_data`
- Time format: `yyyy-MM-dd HH:mm:ss`
- Time zone: Beijing time

