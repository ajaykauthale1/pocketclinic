# PocketClinic Android App — Phased TDD Implementation Plan

## Table of Contents

1. [Project Structure](#1-project-structure)
2. [Gradle & Dependency Setup](#2-gradle--dependency-setup)
3. [Material 3 Theme (Design Token System)](#3-material-3-theme--design-token-system)
4. [Room Database Schema](#4-room-database-schema)
5. [Phase 1 — Foundation (Navigation Shell + Theme)](#5-phase-1--foundation)
6. [Phase 2 — Signal Processing Core](#6-phase-2--signal-processing-core)
7. [Phase 3 — Sensor Data Layer](#7-phase-3--sensor-data-layer)
8. [Phase 4 — Feature: VascularPulse](#8-phase-4--feature-vascularpulse)
9. [Phase 5 — Feature: NeuralSteady](#9-phase-5--feature-neuralsteady)
10. [Phase 6 — Feature: Visual Acuity](#10-phase-6--feature-visual-acuity)
11. [Phase 7 — Feature: Hearing Threshold](#11-phase-7--feature-hearing-threshold)
12. [Phase 8 — Command Center Dashboard](#12-phase-8--command-center-dashboard)
13. [Phase 9 — Reports & Data Deep-Dive](#13-phase-9--reports--data-deep-dive)
14. [Phase 10 — Patient Profile](#14-phase-10--patient-profile)
15. [Phase 11 — Full Checkup Orchestration (FAB Flow)](#15-phase-11--full-checkup-orchestration)
16. [Phase 12 — Polish, Accessibility & E2E Tests](#16-phase-12--polish-accessibility--e2e-tests)
17. [Figma Screen → Compose Screen Mapping](#17-figma-screen--compose-screen-mapping)
18. [Test Coverage Matrix](#18-test-coverage-matrix)

---

## 1. Project Structure

```
app/
└── src/
    ├── main/
    │   └── java/com/pocketclinic/app/
    │       ├── PocketClinicApp.kt
    │       ├── MainActivity.kt
    │       │
    │       ├── ui/
    │       │   ├── theme/
    │       │   │   ├── PocketClinicTheme.kt
    │       │   │   ├── Color.kt
    │       │   │   ├── Type.kt
    │       │   │   └── Shape.kt
    │       │   ├── navigation/
    │       │   │   ├── AppNavHost.kt
    │       │   │   ├── BottomNavBar.kt
    │       │   │   └── NavDestination.kt
    │       │   ├── command/
    │       │   │   ├── CommandScreen.kt
    │       │   │   └── CommandViewModel.kt
    │       │   ├── tests/
    │       │   │   ├── TestsScreen.kt
    │       │   │   ├── TestInitModal.kt
    │       │   │   ├── pulse/
    │       │   │   │   ├── VascularPulseScreen.kt
    │       │   │   │   └── VascularPulseViewModel.kt
    │       │   │   ├── neuro/
    │       │   │   │   ├── NeuralSteadyScreen.kt
    │       │   │   │   └── NeuralSteadyViewModel.kt
    │       │   │   ├── vision/
    │       │   │   │   ├── VisualAcuityScreen.kt
    │       │   │   │   └── VisualAcuityViewModel.kt
    │       │   │   └── hearing/
    │       │   │       ├── HearingScreen.kt
    │       │   │       └── HearingViewModel.kt
    │       │   ├── reports/
    │       │   │   ├── ReportsScreen.kt
    │       │   │   ├── DataDeepDiveScreen.kt
    │       │   │   └── ReportsViewModel.kt
    │       │   ├── profile/
    │       │   │   ├── ProfileScreen.kt
    │       │   │   └── ProfileViewModel.kt
    │       │   └── components/
    │       │       ├── SenseCard.kt
    │       │       ├── PpgWaveformCanvas.kt
    │       │       ├── FftBarGraph.kt
    │       │       ├── StatusShape.kt        # [ ● ] / [ ▲ ] indicators
    │       │       ├── AmberButton.kt
    │       │       └── DecibelMeter.kt
    │       │
    │       ├── domain/
    │       │   ├── model/
    │       │   │   ├── TestSession.kt
    │       │   │   ├── HeartRateResult.kt
    │       │   │   ├── HrvResult.kt
    │       │   │   ├── TremorResult.kt
    │       │   │   ├── VisualAcuityResult.kt
    │       │   │   ├── HearingResult.kt
    │       │   │   └── PatientProfile.kt
    │       │   ├── usecase/
    │       │   │   ├── ComputeHeartRateUseCase.kt
    │       │   │   ├── ComputeHrvUseCase.kt
    │       │   │   ├── ComputeTremorUseCase.kt
    │       │   │   ├── ComputeVisualAcuityUseCase.kt
    │       │   │   ├── RunFullCheckupUseCase.kt
    │       │   │   └── GetRecentSessionsUseCase.kt
    │       │   └── repository/               # Interfaces only
    │       │       ├── SessionRepository.kt
    │       │       └── ProfileRepository.kt
    │       │
    │       ├── data/
    │       │   ├── db/
    │       │   │   ├── PocketClinicDatabase.kt
    │       │   │   ├── dao/
    │       │   │   │   ├── SessionDao.kt
    │       │   │   │   ├── HeartRateDao.kt
    │       │   │   │   ├── TremorDao.kt
    │       │   │   │   ├── VisualAcuityDao.kt
    │       │   │   │   ├── HearingDao.kt
    │       │   │   │   └── ProfileDao.kt
    │       │   │   └── entity/
    │       │   │       ├── SessionEntity.kt
    │       │   │       ├── HeartRateEntity.kt
    │       │   │       ├── TremorEntity.kt
    │       │   │       ├── VisualAcuityEntity.kt
    │       │   │       ├── HearingEntity.kt
    │       │   │       └── ProfileEntity.kt
    │       │   ├── repository/
    │       │   │   ├── SessionRepositoryImpl.kt
    │       │   │   └── ProfileRepositoryImpl.kt
    │       │   └── mapper/
    │       │       ├── HeartRateMapper.kt
    │       │       ├── TremorMapper.kt
    │       │       ├── VisualAcuityMapper.kt
    │       │       └── HearingMapper.kt
    │       │
    │       └── sensor/
    │           ├── camera/
    │           │   ├── CameraXManager.kt
    │           │   ├── PpgFrameProcessor.kt
    │           │   └── FaceDistanceProcessor.kt
    │           ├── audio/
    │           │   ├── PureToneGenerator.kt
    │           │   └── AmbientDecibelMonitor.kt
    │           ├── imu/
    │           │   ├── GyroscopeReader.kt
    │           │   └── TremorSampler.kt
    │           └── signal/
    │               ├── FftProcessor.kt
    │               ├── ButterworthFilter.kt
    │               ├── PeakDetector.kt
    │               └── RmssdCalculator.kt
    │
    ├── test/                                 # JVM Unit Tests
    │   └── java/com/pocketclinic/app/
    │       ├── signal/
    │       │   ├── FftProcessorTest.kt
    │       │   ├── ButterworthFilterTest.kt
    │       │   ├── PeakDetectorTest.kt
    │       │   └── RmssdCalculatorTest.kt
    │       ├── domain/
    │       │   ├── ComputeHeartRateUseCaseTest.kt
    │       │   ├── ComputeHrvUseCaseTest.kt
    │       │   ├── ComputeTremorUseCaseTest.kt
    │       │   └── ComputeVisualAcuityUseCaseTest.kt
    │       ├── data/
    │       │   ├── SessionRepositoryImplTest.kt
    │       │   └── mapper/HeartRateMapperTest.kt
    │       └── ui/
    │           ├── VascularPulseViewModelTest.kt
    │           ├── NeuralSteadyViewModelTest.kt
    │           ├── VisualAcuityViewModelTest.kt
    │           ├── HearingViewModelTest.kt
    │           ├── CommandViewModelTest.kt
    │           └── ReportsViewModelTest.kt
    │
    └── androidTest/                          # Instrumented Tests
        └── java/com/pocketclinic/app/
            ├── db/
            │   ├── SessionDaoTest.kt
            │   ├── HeartRateDaoTest.kt
            │   └── ProfileDaoTest.kt
            └── ui/
                ├── CommandScreenTest.kt
                ├── VascularPulseScreenTest.kt
                ├── NeuralSteadyScreenTest.kt
                ├── VisualAcuityScreenTest.kt
                ├── ReportsScreenTest.kt
                └── NavigationTest.kt
```

---

## 2. Gradle & Dependency Setup

Run `/setup-test-dependencies` after creating the project.

```kotlin
// app/build.gradle.kts
android {
    compileSdk = 35
    defaultConfig { minSdk = 26; targetSdk = 35 }
    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = "1.5.14" }
    testOptions {
        unitTests { isIncludeAndroidResources = true; isReturnDefaultValues = true }
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.09.00")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-text-google-fonts:1.7.3")
    implementation("androidx.navigation:navigation-compose:2.8.3")

    // CameraX
    implementation("androidx.camera:camera-camera2:1.3.4")
    implementation("androidx.camera:camera-lifecycle:1.3.4")
    implementation("androidx.camera:camera-view:1.3.4")

    // ML Kit + ARCore
    implementation("com.google.mlkit:face-detection:16.1.6")
    implementation("com.google.ar:core:1.46.0")

    // Room
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // Coroutines / DI / Lifecycle
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("com.google.dagger:hilt-android:2.52")
    ksp("com.google.dagger:hilt-compiler:2.52")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")

    // Test — Unit (JVM)
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.12")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    testImplementation("app.cash.turbine:turbine:1.1.0")
    testImplementation("com.google.truth:truth:1.4.4")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("org.robolectric:robolectric:4.13")

    // Test — Instrumented
    androidTestImplementation(composeBom)
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("io.mockk:mockk-android:1.13.12")
    androidTestImplementation("androidx.navigation:navigation-testing:2.8.3")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.52")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
```

---

## 3. Material 3 Theme & Design Token System

**First code written. All screens depend on it.**

### `ui/theme/Color.kt`
```kotlin
// From DESIGN.md + figma/INDEX.md tokens
val Background         = Color(0xFF131313)   // #131313 True Black
val Surface            = Color(0xFF1E1E1E)   // #1E1E1E Dark Gray
val PrimaryAmber       = Color(0xFFFFB300)   // #FFB300 Vivid Amber (primary-container)
val PrimaryAmberLight  = Color(0xFFFFD79B)   // #FFD79B primary
val OnPrimary          = Color(0xFF432C00)   // #432C00 on-primary (Black on Amber)
val PrimaryText        = Color(0xFFFFFFFF)   // Pure White
val SecondaryText      = Color(0xFFAAAAAA)   // #AAAAAA Light Gray
val SurfaceVariant     = Color(0xFF2A2A2A)   // surface-container-high
val OutlineVariant     = Color(0xFF514532)
```

### `ui/theme/Type.kt`
```kotlin
// JetBrains Mono  → data/headers/live readings (displayLarge, headlineMedium, labelSmall)
// Space Grotesk   → screen headlines (headlineLarge, titleLarge)
// Inter           → body text / instructions (bodyMedium, bodySmall)
```
Bundle `.ttf` files in `res/font/` or use `GoogleFont` provider.

### `ui/theme/Shape.kt`
```kotlin
// 0px everywhere — strict terminal aesthetic
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(0.dp),
    small      = RoundedCornerShape(0.dp),
    medium     = RoundedCornerShape(0.dp),
    large      = RoundedCornerShape(0.dp),
    extraLarge = RoundedCornerShape(0.dp)
)
```

### `ui/theme/PocketClinicTheme.kt`
```kotlin
// ALWAYS dark — forced dark regardless of system setting.
// Never expose a light variant.
```

**TDD:**
- Unit (Robolectric): `MaterialTheme.colorScheme.background == Background` when theme applied.
- Compose instrumented: Snapshot-verify `AmberButton` renders correct amber background.

---

## 4. Room Database Schema

```
TestSession
  id: Long (PK autoGenerate)
  startedAt: Long
  completedAt: Long?
  sessionType: String  // "FULL_CHECKUP" | "PULSE" | "VISION" | "TREMOR" | "HEARING"

HeartRateEntity
  id, sessionId (FK), timestampMs, bpm: Float, rmssdMs: Float, stressFlag: Boolean

TremorEntity
  id, sessionId (FK), timestampMs, stabilityPercent: Float,
  peakFrequencyHz: Float, tremorFlag: Boolean

VisualAcuityEntity
  id, sessionId (FK), timestampMs, measuredDistanceCm: Float,
  snellenNumerator: Int, snellenDenominator: Int, acuityFlag: Boolean

HearingEntity
  id, sessionId (FK), timestampMs, frequencyHz: Float,
  thresholdDbHl: Float, hearingFlag: Boolean

ProfileEntity
  id: Long (always 1 — single row), patientName, patientId, dateOfBirth, lastSyncAt: Long?
```

**TDD:** All DAOs tested with `Room.inMemoryDatabaseBuilder` in `androidTest/db/`. Each DAO verifies insert, retrieve-by-session, cascade delete, and empty-list edge case.

---

## 5. Phase 1 — Foundation

**Goal:** Navigable shell. Four stub screens. No sensor code.

**TDD slash commands:** `/tdd-red-green-refactor`, `/generate-compose-ui-test`

### 1.1 Theme
RED → test `Background == #131313` via Robolectric.
GREEN → implement Color.kt, Type.kt, Shape.kt, PocketClinicTheme.kt.

### 1.2 Navigation Shell
RED → instrumented test: all four tabs reachable via `BottomNavBar`.
GREEN → `NavDestination` (sealed), `BottomNavBar`, `AppNavHost` with 4 stub composables.

### 1.3 MainActivity
RED → launches and Command tab is selected by default.
GREEN → `setContent { PocketClinicTheme { AppNavHost() } }`.

---

## 6. Phase 2 — Signal Processing Core

**Pure Kotlin/JVM. Zero Android dependencies. Exhaustively unit-tested before any sensor wiring.**

**TDD slash command:** `/tdd-red-green-refactor` for each class.

### 2.1 ButterworthFilter (bandpass IIR)
Tests:
- DC signal filtered to near-zero (0.5–4 Hz bandpass)
- 1 Hz sine passes with < 3 dB attenuation
- 10 Hz sine attenuated > 40 dB
- Empty input returns empty DoubleArray without exception
- Coefficient count matches expected order

GREEN: Direct-form II transposed IIR. Pre-calculated coefficients for 30 fps (PPG) and 100 Hz (IMU).
REFACTOR: `FilterConfig(lowCutHz, highCutHz, sampleRateHz, order)`.

### 2.2 FftProcessor (Cooley-Tukey radix-2)
Tests:
- 4-second 1 Hz sine at 30 fps → dominant bin at 1 Hz
- Non-power-of-2 input → zero-padded automatically
- Flat zero signal → all magnitudes zero
- 5 Hz signal at 100 Hz rate → 5 Hz bin highest

GREEN: Pure Kotlin FFT. No library dependency.
REFACTOR: `FftResult(frequencies: FloatArray, magnitudes: FloatArray)` + `findDominantFrequency()`.

### 2.3 PeakDetector
Tests:
- Known peaks at indices 30, 60, 90 → correctly identified
- Flat signal → empty list
- Noise bumps below threshold → ignored
- Two peaks closer than minDistance → only one returned

GREEN: Sliding-window local maximum with amplitude threshold and min inter-peak distance.
REFACTOR: Returns `PeakResult(peakIndices: List<Int>, instantaneousBpm: Float)`.

### 2.4 RmssdCalculator
Tests:
- `[800, 810, 790, 820]` ms RR intervals → RMSSD ≈ 17.3 ms
- Fewer than 2 intervals → returns null
- Identical intervals → RMSSD = 0.0

GREEN: `sqrt(mean of squared successive RR differences)`.
REFACTOR: Accepts `List<Long>` peak timestamps, computes RR intervals internally.

---

## 7. Phase 3 — Sensor Data Layer

**TDD slash commands:** `/generate-repository-with-tests`, `/create-test-class`

### 3.1 Room DAOs (instrumented — `androidTest/db/`)
Pattern: in-memory DB, insert → retrieve, cascade delete, empty result edge case.
Files: `SessionDaoTest`, `HeartRateDaoTest`, `TremorDaoTest`, `VisualAcuityDaoTest`, `HearingDaoTest`, `ProfileDaoTest`.

### 3.2 Mappers (unit tests)
Each mapper: `entity → domain` and `domain → entity` round-trip.

### 3.3 SessionRepositoryImpl (unit tests with mockk DAOs)
- `saveHeartRate` maps to entity and calls `dao.insert`
- `getRecentSessions` returns mapped domain models
- DAO throws → `Result.failure` is returned

---

## 8. Phase 4 — Feature: VascularPulse

**Figma:** `04-sensor-calibration.html` (PPG live feed screen)

**TDD slash commands:** `/generate-viewmodel-with-tests`, `/generate-compose-ui-test`

### 4.1 PpgFrameProcessor
RED: Mock `ImageProxy` with known YUV_420_888 data → assert `extractRedChannelMean()` returns expected float.
GREEN: Extract mean red channel intensity from center 20% of Y-plane.

### 4.2 VascularPulseViewModel (unit + Turbine)
State machine: `Idle → Calibrating → Measuring → Done`
Tests:
- Initial state is Idle
- `startMeasurement()` → Calibrating
- Valid PPG frames flow → Measuring, BPM updates
- `stopMeasurement()` or 60s elapsed → Done with result saved
- RMSSD < 20ms → amber HRV stress flag

GREEN: ViewModel pipes `Flow<FloatArray>` through `ButterworthFilter → PeakDetector → RmssdCalculator`.

### 4.3 VascularPulseScreen (instrumented Compose)
Tests: waveform canvas visible in Measuring state; BPM text updates; amber dot at R-peak; debug overlay shows `FPS: 60 | YUV_420`; HRV amber `[ ▲ ]` appears when stressFlag.

GREEN: Top 50% = `PpgWaveformCanvas` (`Canvas` white polyline + amber peak circles). Bottom 50% = data grid (BPM large JetBrains Mono, RMSSD, stress shape).

---

## 9. Phase 5 — Feature: NeuralSteady

**Figma:** `06-visual-acuity.html` (tremor circle + FFT screen)

### 5.1 TremorSampler (unit)
Tests: 100 Hz gyro for 4s → correct dominant frequency; stability% > 95 for low jitter; 5 Hz oscillation → tremorFlag = true.

### 5.2 NeuralSteadyViewModel (unit + Turbine)
Tests: dot starts at center; gyro input → dot position updates; boundary crossed → tremor logged + haptic event emitted; FFT graph data updates every 250ms; after 30s results saved.

### 5.3 NeuralSteadyScreen (instrumented Compose)
Tests: hollow white circle at center; dot position reflects state; dot turns amber on boundary; FFT bar graph shows frequency buckets; stability % displayed.

GREEN: `Canvas` — hollow white circle (stroke), filled white dot at `uiState.dotOffset`. `FftBarGraph` composable using `drawRect` for frequency buckets. Bottom stats row.

---

## 10. Phase 6 — Feature: Visual Acuity

**Figma:** `05-vascular-pulse.html` (the large "E" optotype screen)

### 6.1 FaceDistanceProcessor (unit + instrumented)
Tests: known ML Kit z-landmark → result within 5cm; no face → null emitted.
GREEN: ARCore `acquireDepthImage16Bits()` + ML Kit face contours for region of interest.

### 6.2 VisualAcuityViewModel (unit + Turbine)
Tests: starts at maximum E size; face at 40cm ± 5cm → haptic confirmation; correct quadrant tap → smaller E (ETDRS staircase); wrong tap → larger E; 5 reversals → Snellen computed; orientation randomized per trial.

### 6.3 VisualAcuityScreen (instrumented Compose)
Tests: pitch black canvas; white E sized to Snellen spec; vertical scale bar on left; amber bracket at target zone; four invisible touch quadrants.

GREEN: `BoxWithConstraints` mapping Snellen to dp via screen DPI. E drawn as `Canvas` path (not text). Touch quadrants = 4 transparent `Box` composables.

---

## 11. Phase 7 — Feature: Hearing Threshold

**Figma:** `10-results-report.html` (TestInitModal with Hearing Threshold)

### 7.1 PureToneGenerator (unit)
Tests: 1000 Hz at 44100 rate for 1s → buffer length 44100; amplitude 0.5 → all samples in [-0.5, 0.5]; 0 Hz → all zeros.
GREEN: `generateSineSamples(frequencyHz, durationMs, amplitudeLinear, sampleRate): ShortArray`.

### 7.2 HearingViewModel (unit + Turbine)
Tests: starts at 1000 Hz descending volume; heard → volume -5dB; not heard → volume +5dB; 3 reversals per frequency → record threshold, advance to next; covers 500/1000/2000/4000 Hz.

### 7.3 HearingScreen + TestInitModal (instrumented Compose)
Modal tests: slides up on card tap; calibration bar animates; START TEST button triggers test; back gesture dismisses.

---

## 12. Phase 8 — Command Center Dashboard

**Figma:** `08-data-deep-dive.html` (2×2 bento grid)

### 8.1 CommandViewModel (unit + Turbine)
Tests: all four cards in idle initially; camera permission granted → Vision card live; PPG flows → Pulse shows BPM; ambient dB > 30 → Audio card amber; FAB tap → `startFullCheckup()`.

GREEN: `combine()` on four sensor flows → `CommandUiState(visionCard, audioCard, pulseCard, neuroCard)`.

### 8.2 SenseCard composable (instrumented Compose — isolated)
Tests: Pulse card shows BPM; Audio bars turn amber at > 30dB; Vision shows face distance; Neuro shows stability %.

### 8.3 CommandScreen layout
`LazyVerticalGrid(columns = Fixed(2))`. Pulse card: `span = { GridItemSpan(2) }`. FAB via `Scaffold`.

---

## 13. Phase 9 — Reports & Data Deep-Dive

**Figma:** `09-command-center.html` (results printout) + `07-neural-steady.html` (anomaly chart)

### 9.1 ReportsViewModel (unit + Turbine)
Tests: loads last 30 days from repository; sessions sorted newest-first; result flags map to shape (`[ ● ]` / `[ ▲ ]`); export event emitted on button tap.

### 9.2 StatusShape composable (unit — isolated)
Renders `[ ● ]` in white or `[ ▲ ]` in amber via `Canvas`. Test both variants in isolation.

### 9.3 ReportsScreen (instrumented Compose)
Tests: HRV row with RMSSD + correct shape; Visual Acuity row with Snellen; amber EXPORT PDF button; rows newest-first.

### 9.4 DataDeepDiveScreen
30-day line chart (`Canvas` drawn). Anomaly log table (`LazyColumn`). Time period toggle (24H / 7D / 30D / ALL).

---

## 14. Phase 10 — Patient Profile

**Figma:** `02-critical-alerts.html` (Patient Profile — Alex Rivera)

### 10.1 ProfileViewModel (unit + Turbine)
Tests: loads from repository on init; absent profile shows empty setup state; editing name updates optimistically; save commits to DB.

### 10.2 ProfileScreen (instrumented Compose)
Bio card (name, patient ID badge, DOB). Bio-sync card (last sync timestamp). Sensor readiness status. INITIATE NEW TEST amber button. EXPORT DOSSIER outline button.

---

## 15. Phase 11 — Full Checkup Orchestration

**Figma:** `03-patient-profile.html` (Sensor Calibration stack)

### 11.1 RunFullCheckupUseCase (unit)
Tests: begins with calibration; runs Vision → Pulse → Tremor → Hearing sequentially; single test failure is non-blocking; unavailable sensor → skip with reason code; completes with `SessionEntity` containing all four sub-results.

GREEN: `suspend fun run(): Flow<CheckupProgress>`. Events: `Calibrating`, `RunningTest(type)`, `TestComplete(type, result)`, `CheckupDone(sessionId)`.

### 11.2 TestInitModal (instrumented Compose)
Shared `ModalBottomSheet` used by all four tests. Config-driven title/instructions/sensor check. Amber START TEST CTA.

### 11.3 SensorCalibrationScreen (instrumented Compose)
Lists PPG / Accelerometer / ARCore with white progress bars. `[ ● CALIBRATED ]` or `[ ▲ REQUIRES SYNC ]` status badges (matching Figma `03-patient-profile.html`).

---

## 16. Phase 12 — Polish, Accessibility & E2E

### 12.1 Accessibility
- All interactive elements have `contentDescription` semantics.
- White on `#131313` = 19.1:1 contrast (WCAG AAA).
- Amber `#FFB300` on `#131313` = 9.85:1 contrast (WCAG AAA).
- `AccessibilityChecks.enable()` in all Compose instrumented tests.

### 12.2 End-to-End Navigation Tests
```kotlin
// NavigationTest.kt
@Test fun `full checkup flow: FAB → calibration → pulse test → reports`()
@Test fun `tab switching preserves scroll position`()
@Test fun `back press from test screen returns to command tab`()
```

### 12.3 Performance Baselines
- PPG frame processing < 16ms (one frame at 60 fps) — timing assertion in `ButterworthFilterTest`.
- FFT on 256 samples < 5ms — timing assertion in `FftProcessorTest`.

---

## 17. Figma Screen → Compose Screen Mapping

| Figma File | Screen Content | Compose Destination | Phase |
|---|---|---|---|
| `01-design-system.html` | Design System / Alert state | `PocketClinicTheme` + `StatusShape` | 1 + 9 |
| `02-critical-alerts.html` | Patient Profile (Alex Rivera) | `ProfileScreen` | 10 |
| `03-patient-profile.html` | Sensor Calibration stack | `SensorCalibrationScreen` | 11 |
| `04-sensor-calibration.html` | VascularPulse — live PPG | `VascularPulseScreen` | 4 |
| `05-vascular-pulse.html` | Visual Acuity — Optotype E | `VisualAcuityScreen` | 6 |
| `06-visual-acuity.html` | NeuralSteady — tremor circle | `NeuralSteadyScreen` | 5 |
| `07-neural-steady.html` | Data Deep-Dive — anomaly chart | `DataDeepDiveScreen` | 9 |
| `08-data-deep-dive.html` | Command Center — bento grid | `CommandScreen` | 8 |
| `09-command-center.html` | Results Report — printout | `ReportsScreen` | 9 |
| `10-results-report.html` | Test Init Modal — Hearing | `TestInitModal` | 11 |

---

## 18. Test Coverage Matrix

| Component | Type | File | Phase |
|---|---|---|---|
| `ButterworthFilter` | Unit (JVM) | `test/.../signal/ButterworthFilterTest.kt` | 2 |
| `FftProcessor` | Unit (JVM) | `test/.../signal/FftProcessorTest.kt` | 2 |
| `PeakDetector` | Unit (JVM) | `test/.../signal/PeakDetectorTest.kt` | 2 |
| `RmssdCalculator` | Unit (JVM) | `test/.../signal/RmssdCalculatorTest.kt` | 2 |
| `PureToneGenerator` | Unit (JVM) | `test/.../audio/PureToneGeneratorTest.kt` | 7 |
| `HeartRateMapper` | Unit (JVM) | `test/.../data/mapper/HeartRateMapperTest.kt` | 3 |
| `VascularPulseViewModel` | Unit + Turbine | `test/.../ui/VascularPulseViewModelTest.kt` | 4 |
| `NeuralSteadyViewModel` | Unit + Turbine | `test/.../ui/NeuralSteadyViewModelTest.kt` | 5 |
| `VisualAcuityViewModel` | Unit + Turbine | `test/.../ui/VisualAcuityViewModelTest.kt` | 6 |
| `HearingViewModel` | Unit + Turbine | `test/.../ui/HearingViewModelTest.kt` | 7 |
| `CommandViewModel` | Unit + Turbine | `test/.../ui/CommandViewModelTest.kt` | 8 |
| `ReportsViewModel` | Unit + Turbine | `test/.../ui/ReportsViewModelTest.kt` | 9 |
| `SessionRepositoryImpl` | Unit (JVM) | `test/.../data/SessionRepositoryImplTest.kt` | 3 |
| `RunFullCheckupUseCase` | Unit (JVM) | `test/.../domain/RunFullCheckupUseCaseTest.kt` | 11 |
| `SessionDao` | Instrumented (Room) | `androidTest/.../db/SessionDaoTest.kt` | 3 |
| `HeartRateDao` | Instrumented (Room) | `androidTest/.../db/HeartRateDaoTest.kt` | 3 |
| `PpgFrameProcessor` | Instrumented | `androidTest/.../sensor/PpgFrameProcessorTest.kt` | 4 |
| `VascularPulseScreen` | Instrumented (Compose) | `androidTest/.../ui/VascularPulseScreenTest.kt` | 4 |
| `NeuralSteadyScreen` | Instrumented (Compose) | `androidTest/.../ui/NeuralSteadyScreenTest.kt` | 5 |
| `VisualAcuityScreen` | Instrumented (Compose) | `androidTest/.../ui/VisualAcuityScreenTest.kt` | 6 |
| `CommandScreen` | Instrumented (Compose) | `androidTest/.../ui/CommandScreenTest.kt` | 8 |
| `ReportsScreen` | Instrumented (Compose) | `androidTest/.../ui/ReportsScreenTest.kt` | 9 |
| `Navigation (E2E)` | Instrumented (Compose) | `androidTest/.../ui/NavigationTest.kt` | 1 + 12 |
| `PocketClinicTheme` | Robolectric | `test/.../ui/theme/PocketClinicThemeTest.kt` | 1 |

**Target distribution:** ~70% unit (JVM) · ~20% instrumented · ~10% Robolectric.

---

## Build Order

```
Phase 1:  Theme → Navigation Shell → MainActivity
Phase 2:  ButterworthFilter → FftProcessor → PeakDetector → RmssdCalculator
Phase 3:  Room Entities → DAOs → Mappers → RepositoryImpl
Phase 4:  PpgFrameProcessor → VascularPulseViewModel → VascularPulseScreen
Phase 5:  TremorSampler → NeuralSteadyViewModel → NeuralSteadyScreen
Phase 6:  FaceDistanceProcessor → VisualAcuityViewModel → VisualAcuityScreen
Phase 7:  PureToneGenerator → HearingViewModel → HearingScreen
Phase 8:  CommandViewModel → SenseCards → CommandScreen
Phase 9:  ReportsViewModel → StatusShape → ReportsScreen → DataDeepDiveScreen
Phase 10: ProfileViewModel → ProfileScreen
Phase 11: TestInitModal → SensorCalibrationScreen → RunFullCheckupUseCase
Phase 12: Accessibility audit → E2E tests → Performance baselines
```
