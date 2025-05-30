#include <WiFi.h>
#include <FirebaseESP32.h>
#include <DHT.h>

// WiFi
#define WIFI_SSID "S24Ultra"
#define WIFI_PASSWORD "00000000"

// Firebase
#define FIREBASE_HOST "https://nhom6-14bca-default-rtdb.firebaseio.com//"
#define FIREBASE_AUTH "KbEu1iuzXmmMOFJqy5kG8xMfvHiqL3X4hkKnt4EP"

// Cảm biến và LED
#define DHTPIN 4
#define LED_BUILTIN_PIN 2
#define DHTTYPE DHT22

DHT dht(DHTPIN, DHTTYPE);

FirebaseData firebaseData;
FirebaseConfig config;
FirebaseAuth auth;

void setup() {
  Serial.begin(115200);
  pinMode(LED_BUILTIN_PIN, OUTPUT);

  // Kết nối WiFi
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to WiFi");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }
  Serial.println();
  Serial.println("✅ WiFi Connected!");
  Serial.printf("IP Address: %s\n", WiFi.localIP().toString().c_str());

  // Cấu hình Firebase
  config.database_url = FIREBASE_HOST;
  config.signer.tokens.legacy_token = FIREBASE_AUTH;

  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);
  
  Serial.println("✅ Firebase Connected!");

  dht.begin();
  
  // Khởi tạo giá trị mặc định
  Firebase.setBool(firebaseData, "Led/status", false);
  delay(2000);
}

void loop() {
  // Đọc cảm biến
  float temperature = dht.readTemperature();
  float humidity = dht.readHumidity();

  if (!isnan(temperature) && !isnan(humidity)) {
    Serial.printf("🌡️ Temperature: %.1f °C | 💧 Humidity: %.1f %%\n", temperature, humidity);
    
    // Gửi temperature
    if (Firebase.setFloat(firebaseData, "Sensor/temperature", temperature)) {
      Serial.println("✅ Temperature sent successfully");
    } else {
      Serial.printf("❌ Temperature failed: %s\n", firebaseData.errorReason().c_str());
    }
    
    // Gửi humidity
    if (Firebase.setFloat(firebaseData, "Sensor/humidity", humidity)) {
      Serial.println("✅ Humidity sent successfully");
    } else {
      Serial.printf("❌ Humidity failed: %s\n", firebaseData.errorReason().c_str());
    }
  } else {
    Serial.println("⚠️ Failed to read DHT sensor!");
  }

  // Kiểm tra LED status
  if (Firebase.getBool(firebaseData, "Led/status")) {
    bool ledState = firebaseData.boolData();
    digitalWrite(LED_BUILTIN_PIN, ledState);
    Serial.printf("💡 LED: %s\n", ledState ? "ON" : "OFF");
  } else {
    Serial.printf("❌ LED read failed: %s\n", firebaseData.errorReason().c_str());
  }

  delay(3000);
}