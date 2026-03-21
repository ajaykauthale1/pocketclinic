# PocketClinic — Figma Reference Screens

| File | Screen | Nav Tab | Notes |
|------|--------|---------|-------|
| `01-design-system.html` | Design System / Critical Alerts | Command | Full design token showcase; shows alert state |
| `02-critical-alerts.html` | Patient Profile (Clinical History) | Profile | Alex Rivera PX-9928-B; bio-sync card |
| `03-patient-profile.html` | Sensor Calibration | Tests | PPG, Accelerometer, ARCore sensor stack |
| `04-sensor-calibration.html` | VascularPulse (PPG live feed) | Tests | White waveform + Amber R-peak dots; HRV grid |
| `05-vascular-pulse.html` | Visual Acuity (Optotype E) | Tests | Giant "E"; distance scale; touch quadrants |
| `06-visual-acuity.html` | NeuralSteady (Tremor target) | Tests | Hollow circle + FFT frequency graph |
| `07-neural-steady.html` | Data Deep-Dive (Anomaly chart) | Reports | 30-day trend; anomaly log table |
| `08-data-deep-dive.html` | Command Center (Bento grid) | Command | 2×2 sense grid; live PPG + tremor cards |
| `09-command-center.html` | Results Report (Printout) | Reports | HRV/Acuity/Tremor shape-based status rows |
| `10-results-report.html` | Test Init Modal (Hearing Threshold) | — | ModalBottomSheet pattern; START TEST CTA |

## Design Tokens (consistent across all screens)
- **Background:** `#131313` / **Surface:** `#1E1E1E`
- **Primary Accent (Amber):** `#FFB300` (`primary-container`)
- **Primary Text:** `#FFFFFF` / **Secondary Text:** `#AAAAAA` (`on-secondary-container`)
- **On-Accent:** `#432C00` (`on-primary`)
- **Fonts:** `JetBrains Mono` (data/headers) · `Space Grotesk` (headlines) · `Inter` (body)
- **Border radius:** `0px` everywhere (sharp/terminal aesthetic)
- **Status shapes:** `[ ● ]` = Stable · `[ ▲ ]` = Alert

## Navigation Structure
4 tabs: **Command** · **Tests** · **Reports** · **Profile**
- Active tab highlighted in `primary-container` amber
- FAB: `w-14 h-14 bg-primary-container` (add_chart icon) fixed bottom-right
