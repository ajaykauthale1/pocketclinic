# PocketClinic: Comprehensive System & UI/UX Architecture

## 1. Core Idea & Product Philosophy
**PocketClinic** transforms a standard Android smartphone into a clinical-grade, accessible diagnostic sensor suite.
* **The Goal:** Provide high-precision, active medical tests (Vision, Hearing, Heart Rate, Neurological) using standard on-device hardware (Camera, Mic, IMU).
* **Privacy First:** 100% on-device local signal processing (FFT, ML Kit Face Mesh). No cloud dependency.
* **Accessibility First:** Designed with a forced high-contrast dark mode to prevent pupil constriction during vision tests, reduce OLED battery drain, and ensure WCAG AAA compliance for visually impaired users.

---

## 2. Global Design Tokens (Styles)
**Theme Name:** Accessibility Amber (Strict Dark Mode)
**Design Paradigm:** Terminal-Clinical. Zero reliance on red/green color paradigms; utilizes luminance, iconography, and shape to convey health status.

### Color Palette
* **Background:** `#121212` (True Black) - Maximizes contrast and battery life.
* **Surface:** `#1E1E1E` (Dark Gray) - Elevated elements, cards, and bottom sheets.
* **Primary Accent:** `#FFB300` (Vivid Amber) - Strictly reserved for critical active states, "START" buttons, and abnormal health alerts.
* **Primary Text:** `#FFFFFF` (Pure White) - Used for primary metrics and normal/stable statuses.
* **Secondary Text:** `#AAAAAA` (Light Gray) - Used for raw sensor data overlays and timestamps.
* **On-Accent Text:** `#000000` (Pure Black) - Used on top of Amber buttons for maximum legibility.

### Typography & Iconography
* **Headers / Live Data:** `JetBrains Mono` (or similar monospace) for an engineered, precise feel.
* **Body / Instructions:** `Inter` (sans-serif) for clean readability.
* **Status Shapes:**
  * `[ ● ]` Solid Circle = Stable / Normal Baseline.
  * `[ ▲ ]` Triangle = Alert / Abnormal / Action Needed.

---

## 3. Navigation Architecture
* **Single Activity Structure:** The app utilizes a single `MainActivity` with Jetpack Compose Navigation.
* **Dashboard Hub:** There is no deep navigational nesting. The main screen is an immutable command center.
* **Modal Overlay Pattern:** Tapping any test does not push a new screen to the backstack. Instead, it triggers a `ModalBottomSheet` for test instructions, which expands into a full-screen `AnimatedVisibility` overlay during the active test.

---

## 4. Detailed Screen Layouts

### Screen 1: The Command Center (Main Dashboard)
*The default view. A scrollable, high-density grid of live sensor cards.*
* **Top App Bar (Sticky):** "POCKETCLINIC" title. Sync Status: "● Sensors Ready".
* **Sense Grid (2x2 Layout):**
  * **Vision Card:** Shows dynamic "E" optotype and live Face Distance (cm) using camera feed background.
  * **Audio Card:** Live ambient decibel meter. Bars turn from White to Amber if room is > 30dB.
  * **Pulse Card (Wide):** Live PPG waveform in White. Heart Rate BPM in large text.
  * **Neuro Card:** Tremor % stability meter.
* **Floating Action Button (FAB):** Massive Amber button with a Black Stethoscope icon. Triggers a 60-second automated "Full Checkup".

### Screen 2: Test Initialization Modal
*The bottom sheet that slides up when a dashboard card is tapped.*
* **Header:** Pure White title (e.g., "HEARING THRESHOLD").
* **Instructions:** Clear, sans-serif text with simple iconography.
* **Calibration Bar:** White progress indicator checking hardware readiness (e.g., "Checking ambient noise...").
* **Action:** Full-width Amber button with Black text: "START TEST".

### Screen 3: Visual Acuity (Dynamic Optotype)
* **Canvas:** Pitch Black (`#121212`).
* **The Target:** A single Pure White "E" that dynamically resizes based on ARCore Z-axis depth data.
* **Guidance UI:** Vertical White scale on the left edge. Amber bracket indicates the "Target Distance" zone. Device clicks haptically when the user's face is positioned correctly.
* **Input:** Invisible quadrant touch zones for the user to indicate the orientation of the "E" (Up, Down, Left, Right).

### Screen 4: VascularPulse (Heart & HRV)
* **Waveform View (Top 50%):** Real-time White line graph plotting the Red-channel pixel intensity (PPG). An Amber dot flashes at the peak of each heartbeat (R-peak).
* **Data Grid (Bottom 50%):**
  * Massive White text for BPM.
  * HRV (RMSSD) metric. If HRV is low (stress), an Amber `[ ▲ ]` appears.
* **Debug Overlay:** Small light gray text at bottom showing `FPS: 60 | YUV_420`.

### Screen 5: NeuralSteady (Tremor & Balance)
* **Target UI:** Large hollow White circle in the center.
* **Sensor UI:** Solid White dot driven by the Gyroscope/Accelerometer.
* **Mechanic:** User holds phone flat. If the white dot touches the outer boundary, a tremor is logged, the dot turns Amber, and the phone buzzes.
* **Data UI:** Bottom of screen displays a live Fast Fourier Transform (FFT) graph to isolate specific tremor frequencies (e.g., 4-6Hz).

### Screen 6: Reports & Digital Wellbeing Integration
* **Layout:** Simple vertically scrolling list on Dark Surface (`#1E1E1E`).
* **Data Visualization:** Shape-based trend logs:
  * `HRV Stress: 45ms [ ● Stable ]`
  * `Visual Acuity: 20/40 [ ▼ Decreased ]`
* **Víram Integration:** Automated triggers passing diagnostic states to the víram app (e.g., "High heart stress detected: Víram will suggest a 20-minute digital focus lock").
* **Action:** Amber "EXPORT PDF" button for medical professional review.

---

## 5. Technical Stack & Modules
* **UI:** Jetpack Compose (Material 3).
* **Camera:** CameraX (YUV_420_888 stream) + ML Kit Face Detection (Contours/Mesh).
* **Audio:** `AudioTrack` API for pure-tone sine wave generation.
* **Signal Processing:** Kotlin Coroutines/Flow + localized Math libraries for FFT and Butterworth Bandpass filtering.
* **Storage:** Room Database (Local encryption).
