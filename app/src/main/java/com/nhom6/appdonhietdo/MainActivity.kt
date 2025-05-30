package com.nhom6.appdonhietdo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.*
import com.nhom6.appdonhietdo.ui.theme.AppdonhietdoTheme

class MainActivity : ComponentActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var sensorRef: DatabaseReference
    private lateinit var ledRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        database = FirebaseDatabase.getInstance("https://nhom6-14bca-default-rtdb.firebaseio.com/")
        sensorRef = database.getReference("Sensor")
        ledRef = database.getReference("Led")

        enableEdgeToEdge()
        setContent {
            AppdonhietdoTheme {
                TemperatureMonitorScreen(
                    sensorRef = sensorRef,
                    ledRef = ledRef
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemperatureMonitorScreen(
    sensorRef: DatabaseReference,
    ledRef: DatabaseReference
) {
    // State variables
    var temperature by remember { mutableStateOf(0.0) }
    var humidity by remember { mutableStateOf(0.0) }
    var ledStatus by remember { mutableStateOf(false) }
    var isConnected by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    // Listen to Sensor data changes
    LaunchedEffect(Unit) {
        // Listen to temperature
        sensorRef.child("temperature").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val temp = snapshot.getValue(Double::class.java) ?: 0.0
                temperature = temp
                isConnected = true
                isLoading = false
                Log.d("Firebase", "Temperature: $temp")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error reading temperature: ${error.message}")
                isConnected = false
                isLoading = false
            }
        })

        // Listen to humidity
        sensorRef.child("humidity").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val hum = snapshot.getValue(Double::class.java) ?: 0.0
                humidity = hum
                Log.d("Firebase", "Humidity: $hum")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error reading humidity: ${error.message}")
            }
        })

        // Listen to LED status
        ledRef.child("status").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val status = snapshot.getValue(Boolean::class.java) ?: false
                ledStatus = status
                Log.d("Firebase", "LED Status: $status")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error reading LED status: ${error.message}")
            }
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Cảm Biến Nhiệt Độ",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(32.dp)
                )
                Text("Đang kết nối Firebase...")
            } else {
                // Temperature Card
                SensorCard(
                    title = "Nhiệt Độ",
                    value = "${"%.1f".format(temperature)}°C",
                    icon = Icons.Default.Thermostat,
                    backgroundColor = Color(0xFFFFE0B2),
                    iconColor = Color(0xFFFF9800)
                )

                // Humidity Card
                SensorCard(
                    title = "Độ Ẩm",
                    value = "${"%.0f".format(humidity)}%",
                    icon = Icons.Default.WaterDrop,
                    backgroundColor = Color(0xFFE1F5FE),
                    iconColor = Color(0xFF2196F3)
                )

                // LED Status Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Lightbulb,
                                contentDescription = "LED",
                                tint = if (ledStatus) Color(0xFFFFEB3B) else Color.Gray,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                "Đèn LED",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Switch(
                            checked = ledStatus,
                            onCheckedChange = { newStatus ->
                                // Update LED status in Firebase
                                ledRef.child("status").setValue(newStatus)
                                    .addOnSuccessListener {
                                        Log.d("Firebase", "LED status updated: $newStatus")
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.e("Firebase", "Failed to update LED: ${exception.message}")
                                    }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFFFFEB3B),
                                checkedTrackColor = Color(0xFFFFF9C4)
                            )
                        )
                    }
                }
            }

            // Status Indicator
            Card(
                modifier = Modifier
                    .padding(horizontal = 24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(
                                if (isConnected) Color(0xFF4CAF50)
                                else Color(0xFFF44336)
                            )
                    )
                    Text(
                        if (isConnected) "Đang kết nối" else "Mất kết nối",
                        fontSize = 14.sp,
                        color = if (isConnected) Color(0xFF4CAF50) else Color(0xFFF44336),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Manual Refresh Button (optional)
            OutlinedButton(
                onClick = {
                    // Force refresh data
                    isLoading = true
                    sensorRef.child("temperature").get()
                    sensorRef.child("humidity").get()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Làm mới dữ liệu",
                    modifier = Modifier.padding(8.dp),
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun SensorCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    backgroundColor: Color,
    iconColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor.copy(alpha = 0.3f))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(48.dp)
            )

            Text(
                title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Text(
                value,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = iconColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TemperatureMonitorPreview() {
    AppdonhietdoTheme {
        // Preview with mock data
    }
}