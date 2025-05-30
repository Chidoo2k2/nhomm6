# 🌡️ SMART TEMPERATURE & HUMIDITY MEASURING APP

This Android app enables **real-time monitoring of temperature and humidity**, and **remote LED control** using Firebase. Perfect for smart home and IoT automation projects.

---

## 1. 🔥 FEATURES

- 📡 Real-time monitoring of temperature and humidity via ESP32 sensors
- 💡 Remote control to turn LED on/off via app
- ☁️ Firebase integration for cloud-based data synchronization
- 🏠 Smart home ready – easily adaptable to IoT systems

---

## 2. 📱 APP DEMO

| App UI | Firebase Database | ESP32 & Sensor |
|--------|-------------------|----------------|
| <img src="https://github.com/Chidoo2k2/Nhom6/blob/main/Screenshot%202025-05-31%20015750.png" width="200"/> | <img src="https://github.com/Chidoo2k2/Nhom6/blob/main/Screenshot%202025-05-31%20032629.png" width="200"/> | <img src="https://github.com/user-attachments/assets/827856b9-8fab-49b0-a3c7-9370f5360036" width="200"/> |
 

🎥 **Demo video**: [[Watch on YouTube](https://www.youtube.com/shorts/kAMZqgzcTyo)]
📦  Download APK: [Click here to download](https://drive.google.com/drive/u/0/folders/18C19Sm_iaaV_w_GECM63jiz7ozL6AFi6)
---

## 3. 🛠️ TECHNOLOGIES USED

- Android (Kotlin)
- ESP32 + DHT11/DHT22 Sensor
- Firebase Realtime Database
- Arduino IDE + Firebase Library
- Gradle Kotlin DSL

---

## 4. ⚙️ HOW TO BUILD & RUN

### 4.1. Requirements:
- Android Studio (Electric Eel or later)
- Firebase project with Realtime Database enabled
- ESP32 board, LED, DHT22 sensor

### 4.2. Steps: git clone https://github.com/0862897614/smart-temp-humidity-app.git


- Open the project in Android Studio (`File > Open`)
- Connect Firebase or replace the `google-services.json` file
- Run the app on a physical device (API 21+)
- Upload code to ESP32 from the `ESP32SensorProject` folder using Arduino IDE

---

## 5. 🔌 HARDWARE CONNECTION TABLE

| Component        | ESP32 Pin  |
|------------------|------------|
| DHT11 (Signal)   | D4         |
| LED (Anode)      | D2         |
| GND              | GND        |
| VCC (3.3V/5V)    | 3.3V       |



---

## 6. 📄 LICENSE

This project is licensed under the **MIT License** – feel free to use and modify it with proper attribution.
