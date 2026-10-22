# SLIMiot PPG

Android app that streams the **photoplethysmography (PPG)** signal from SlimIoT wearables over Bluetooth LE and plots the waveform in real time.

## Features

- Scan for and connect to SlimIoT BLE devices
- Real-time PPG waveform graph
- Heart rate and basic HRV (RMSSD, SDNN) from the PPG signal
- User profile and cloud sync (Firebase)

## Build

1. Open the `SLIMiot PPG` folder in **Android Studio**.
2. Add your own Firebase config: download `google-services.json` from your Firebase project and place it in `SLIMiot PPG/app/`. (It is git-ignored and not included in this repo.)
3. Build and run on a phone with Bluetooth LE (Android 7.0 / API 24 or later).

## Tech stack

Java · Android SDK 33 · BLE via [FastBle](https://github.com/Jasonchenlijian/FastBle) and [RxAndroidBle](https://github.com/dariuszseweryn/RxAndroidBle) · [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) / [AndroidPlot](https://androidplot.com/) · Apache Commons Math · Firebase Auth & Firestore

---

Part of the [SlimIoT](https://www.slimiot.com) health-tech ecosystem. Questions or pilots: [info@slimiot.com](mailto:info@slimiot.com)
