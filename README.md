# PocketClinic

PocketClinic turns an ordinary Android phone into an on-device health-check kit. It runs active tests for **heart rate, hand tremor, hearing and vision** using only the phone's camera, motion sensors, speaker and screen. All signal processing happens locally, and no data leaves the device.

> 🚧 Work in progress. The architecture, data layer and signal-processing building blocks are in place, and sensor wiring and clinical algorithms are being completed (see [PLAN.md](PLAN.md)). Not a medical device.

## Tests
| Test | Sensor | Technique |
| --- | --- | --- |
| Heart rate & HRV | Rear camera + flash (PPG) | Butterworth filtering, peak detection, RMSSD |
| Tremor | Gyroscope / accelerometer | FFT, isolating the 4–12 Hz band |
| Hearing | Headphones (pure tones) | Hughson-Westlake threshold search |
| Visual acuity | Screen | Staircase method with physical-size calibration |

## Architecture
- **Kotlin + Jetpack Compose** UI with a forced high-contrast dark theme for accessibility
- **Clean architecture**: `ui` → `domain` (use cases, models) → `data` (repositories)
- **Room** database for profiles, sessions and per-test results, with exported schemas
- **Hilt** dependency injection
- **Sensor layer** (`sensor/`): CameraX PPG processor, gyroscope tremor processor, pure-tone generator, and reusable DSP (`ButterworthFilter`, `FftProcessor`, `PeakDetector`, `RmssdCalculator`)
- Tested with JUnit, instrumented DAO and navigation tests

## Build
Open in Android Studio (Hedgehog or newer) and run the `app` configuration, or:
```bash
./gradlew assembleDebug
```

Design details: [DESIGN.md](DESIGN.md) · Progress: [PROGRESS.md](PROGRESS.md)
