# Frontend Page Index

## API Files

The frontend API layer includes:

- `src/api/acceleration.js`
- `src/api/crack.js`
- `src/api/deflection.js`
- `src/api/displacement.js`
- `src/api/fatigue.js`
- `src/api/fiber.js`
- `src/api/load.js`
- `src/api/prediction.js`
- `src/api/safety.js`
- `src/api/strain.js`
- `src/api/stress.js`
- `src/api/vibration.js`
- `src/api/vibratingWire.js`
- `src/api/vibrationDat.js`

## View Files

The frontend page layer includes:

- `src/views/crack/CrackCenterPage.vue`
- `src/views/monitor/MonitorCenterPage.vue`
- `src/views/monitor/PredictionCenterPage.vue`
- `src/views/monitor/acceleration/AccelerationMonitorPage.vue`
- `src/views/monitor/deflection/DeflectionMonitorPage.vue`
- `src/views/monitor/displacement/DisplacementMonitorPage.vue`
- `src/views/monitor/fatigue/FatigueMonitorPage.vue`
- `src/views/monitor/fiber/FiberMonitorPage.vue`
- `src/views/monitor/load/LoadMonitorPage.vue`
- `src/views/monitor/safety/SafetyMonitorPage.vue`
- `src/views/monitor/strain/StrainMonitorPage.vue`
- `src/views/monitor/stress/StressMonitorPage.vue`
- `src/views/monitor/vibration/VibrationMonitorPage.vue`
- `src/views/monitor/vibration-dat/VibrationDatMonitorPage.vue`
- `src/views/monitor/vibrating-wire/VibratingWireMonitorPage.vue`

## Real Device Page Mapping

- `vibrating-wire` -> `src/views/monitor/vibrating-wire/VibratingWireMonitorPage.vue`
- `fiber` -> `src/views/monitor/fiber/FiberMonitorPage.vue`
- `vibration-dat` -> `src/views/monitor/vibration-dat/VibrationDatMonitorPage.vue`

## Page Notes

- The new pages are frontend display entry points for the real-device raw data V1 delivery.
- They are aligned with the new backend raw-data endpoints and keep the existing Vue structure.

