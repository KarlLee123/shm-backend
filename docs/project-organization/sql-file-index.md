# SQL File Index

## SQL Files Under `docs/sql`

- `docs/sql/fiber_raw_data.sql`
- `docs/sql/vibrating_wire_data.sql`
- `docs/sql/vibration_dat_data.sql`
- `docs/sql/README.md`

## Table Mapping

| SQL File | Table Name | Module Type |
| --- | --- | --- |
| `vibrating_wire_data.sql` | `vibrating_wire_data` | new real-device access module |
| `fiber_raw_data.sql` | `fiber_raw_data` | new real-device access module |
| `vibration_dat_data.sql` | `vibration_dat_data` | new real-device access module |

## Original vs New Tables

### Original modules

The legacy modules remain in the codebase and continue to use their existing tables.

- `stress`
- `displacement`
- `acceleration`
- `strain`
- `vibration`

### Real device access V1

The newly introduced raw-data tables are:

- `vibrating_wire_data`
- `fiber_raw_data`
- `vibration_dat_data`

## Notes

- The SQL scripts are already prepared and should not be moved or regenerated automatically.
- The execution target is the `stress_data` database.

