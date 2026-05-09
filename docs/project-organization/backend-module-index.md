# Backend Module Index

## Module Summary

| Module | Purpose | Main Interfaces | Table Name | Type |
| --- | --- | --- | --- | --- |
| `stress` | Stress monitoring and raw/result handling | `SensorStressController`, `StressDataController` | legacy stress tables | original module |
| `displacement` | Displacement monitoring and raw data handling | `SensorDisplacementController`, `DisplacementDataController` | legacy displacement tables | original module |
| `acceleration` | Acceleration monitoring and raw data handling | `SensorAccelerationController`, `AccelerationDataController` | legacy acceleration tables | original module |
| `strain` | Strain monitoring and raw data handling | `SensorStrainController`, `StrainDataController` | legacy strain tables | original module |
| `vibration` | Legacy vibration monitoring and raw data handling | `SensorVibrationController`, `VibrationDataController` | legacy vibration tables | original module |
| `vibrating-wire` | Real-device vibrating-wire raw data access | `VibratingWireSensorController`, `VibratingWireDataController` | `vibrating_wire_data` | new real-device access module |
| `fiber` | Real-device OS-265 fiber raw data access | `FiberSensorController`, `FiberDataController` | `fiber_raw_data` | new real-device access module |
| `vibration-dat` | Real-device vibration DAT metadata access | `VibrationDatSensorController`, `VibrationDatDataController` | `vibration_dat_data` | new real-device access module |

## Notes

- The original modules remain unchanged in scope.
- The new real-device modules are only for raw intake, persistence, latest query, history query, and frontend display readiness.
- No complex algorithm parsing or architecture rewrite is included.

