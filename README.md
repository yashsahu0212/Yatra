# 🚌 YATRA — Smart Travel & Mobility Data Platform

> **Your Automated Travel Research Assistant**  
> An Android app for automated trip detection, multimodal journey tracking, and urban mobility research.

---

## 📋 About

**YATRA** (Your Automated Travel Research Assistant) is an Android application built for **Smart India Hackathon 2026** that automates the collection and analysis of travel behaviour data in Indian cities.

The app passively detects trips, classifies transport modes (walking, bus, train, car, bicycle, auto-rickshaw), and builds complete door-to-door journey chains — helping urban planners and researchers understand how people actually move through cities.

Traditional travel surveys are expensive, inaccurate, and outdated within months. YATRA replaces pen-and-paper diaries with smartphone-based sensing that captures real mobility patterns continuously and effortlessly.

---

## ✨ Key Features

- **Automated Trip Detection** — Detects trip start/end using GPS and motion sensors without manual input
- **Multimodal Journey Chains** — Reconstructs complete door-to-door trips with walk → bus → walk segments
- **Transport Mode Classification** — Identifies walking, bus, car, train, bicycle, auto/taxi using sensor fusion (accelerometer + gyroscope + barometer)
- **Confidence Scoring** — Every detected mode gets a confidence score; low-confidence trips are flagged for user verification
- **User Verification Flow** — Clean UI for users to confirm or correct detected transport modes
- **Trip Purpose & Companion Logging** — Users can tag trips with purpose (work, education, shopping) and companion info
- **Offline-First Architecture** — All data stored locally in Room DB; syncs to research servers when connectivity is available
- **Privacy-First Design** — Full transparency about what data is collected, with user-controlled data deletion
- **Mobility Insights Dashboard** — Personal analytics showing weekly/monthly travel patterns, active commute time, and CO₂ savings
- **Interactive Map Visualization** — Custom Canvas-based map rendering with mode-adaptive styling (different visual themes for walking, bus, train routes)

---

## 🏗️ Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Kotlin |
| **UI Framework** | Jetpack Compose (Material 3) |
| **Architecture** | MVVM (ViewModel + StateFlow) |
| **Local Database** | Room (SQLite) |
| **Navigation** | Compose Navigation with animated transitions |
| **Networking** | Retrofit + OkHttp + Moshi |
| **Async** | Kotlin Coroutines + Flow |
| **Build** | Gradle with Kotlin DSL + Version Catalog |
| **Testing** | JUnit 4 + Robolectric + Roborazzi (screenshot testing) |
| **Min SDK** | 24 (Android 7.0) |

---

## 📁 Project Structure

```
app/src/main/java/com/example/
├── MainActivity.kt                    # Entry point + bottom navigation + screen routing
├── data/
│   ├── local/
│   │   ├── Converters.kt              # Room type converters for enums and lists
│   │   ├── TripDao.kt                 # Data Access Object for trip CRUD operations
│   │   └── YatraDatabase.kt           # Room database singleton
│   ├── model/
│   │   ├── MobilityGoal.kt            # Personal mobility goals (distance, trips, CO₂)
│   │   ├── TravelMode.kt              # Transport mode enum with icons, colors, emissions
│   │   └── TripEntity.kt              # Trip data model + sensor telemetry state
│   └── repository/
│       └── TripRepository.kt          # Data layer abstracting DAO + seed data
└── ui/
    ├── components/
    │   ├── ConfidenceAndModeComponents.kt  # Confidence badges, mode selectors, journey chain viz
    │   ├── GlassPanel.kt                   # Glassmorphism panels, pulsing dots, telemetry pills
    │   ├── MapCanvas.kt                    # Full interactive map with mode-adaptive rendering
    │   └── TripAndBentoCards.kt            # Trip cards, stat cards, mini route canvas
    ├── screens/
    │   ├── ActiveTripScreen.kt         # Live trip recording with real-time stats
    │   ├── ConsentScreen.kt            # Data collection consent flow
    │   ├── HomeScreen.kt               # Dashboard with bento stats + recent trips
    │   ├── InsightsScreen.kt           # Mobility analytics + personal goals
    │   ├── OfflineSyncScreen.kt        # Offline sync status + manual upload
    │   ├── OnboardingScreen.kt         # First-time user onboarding
    │   ├── PrivacyCenterScreen.kt      # Privacy controls + data transparency
    │   ├── ProfileScreen.kt            # User profile + settings
    │   ├── TripCompleteScreen.kt       # Post-trip summary + purpose/companion tagging
    │   ├── TripDetailsScreen.kt        # Detailed single trip view
    │   ├── TripVerificationScreen.kt   # Mode correction UI
    │   └── TripsScreen.kt              # Trip history list with filters
    ├── theme/
    │   ├── Color.kt                    # Full colour palette (light/dark, mode semantics)
    │   ├── Theme.kt                    # Material 3 theme configuration
    │   └── Type.kt                     # Typography scale
    └── viewmodel/
        └── YatraViewModel.kt           # Central app state + trip simulation logic
```

---

## 🚀 Getting Started

### Prerequisites

- [Android Studio](https://developer.android.com/studio) (Ladybug or newer recommended)
- JDK 11+
- An Android device or emulator running API 24+

### Build & Run

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/yatra-sih26.git
   cd yatra-sih26
   ```

2. **Open in Android Studio**
   - File → Open → Select the project root directory
   - Let Gradle sync complete

3. **Remove debug signing config** (if not present)
   - If you don't have a `debug.keystore` in the project root, remove this line from `app/build.gradle.kts`:
     ```kotlin
     signingConfig = signingConfigs.getByName("debugConfig")
     ```

4. **Run the app**
   - Select your target device/emulator
   - Click ▶️ Run

---

## 🎨 Design Philosophy

YATRA follows a **research-grade yet user-friendly** design approach:

- **Glassmorphism panels** with subtle blur and border effects for floating overlays
- **Mode-adaptive map styling** — the map background, road grid, and route colours change based on the detected transport mode (green for walking, blue for bus, indigo for train, etc.)
- **Bento grid layouts** for dashboard statistics
- **High-contrast accessibility** with proper dark mode support
- **Micro-animations** — pulsing status dots, animated screen transitions, live trip timer

---

## 🔒 Privacy

YATRA is designed with research ethics in mind:

- All data stays on-device by default
- Users have full control over what gets shared
- One-tap data deletion from the Privacy Center
- No personally identifiable information is transmitted
- Users can pause or stop tracking at any time

---

## 📊 How It Works

```
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│  GPS + IMU   │───▶│  Mode        │───▶│  Journey     │
│  Sensor Data │    │  Classifier  │    │  Chain       │
└──────────────┘    └──────────────┘    │  Builder     │
                                        └──────┬───────┘
                                               │
                    ┌──────────────┐    ┌───────▼───────┐
                    │  User        │◀──▶│  Trip         │
                    │  Verification│    │  Database     │
                    └──────────────┘    └───────┬───────┘
                                               │
                                        ┌──────▼───────┐
                                        │  Research     │
                                        │  Sync Engine  │
                                        └──────────────┘
```

1. **Sense** — Background sensors (GPS, accelerometer, gyroscope, barometer) continuously capture motion data
2. **Classify** — Sensor fusion algorithms classify the transport mode with confidence scores
3. **Chain** — Individual segments are stitched into complete door-to-door multimodal journeys
4. **Verify** — Low-confidence detections are presented to users for quick confirmation
5. **Sync** — Verified trips are uploaded to the research backend when connectivity is available

---

## 🤝 Contributing

This project was built as part of SIH 2026. Contributions, suggestions, and feedback are welcome!

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/my-feature`)
3. Commit your changes (`git commit -m 'Add some feature'`)
4. Push to the branch (`git push origin feature/my-feature`)
5. Open a Pull Request

---

## 📄 License

This project is developed for academic and research purposes under SIH 2026.

---

## 🙏 Acknowledgements

- Built with [Jetpack Compose](https://developer.android.com/jetpack/compose) and [Material 3](https://m3.material.io/)
- Room persistence library for offline-first data storage
- Inspired by the need for better urban mobility data in Indian cities

---

<p align="center">
  <b>YATRA</b> — Understanding how India moves, one trip at a time. 🇮🇳
</p>
