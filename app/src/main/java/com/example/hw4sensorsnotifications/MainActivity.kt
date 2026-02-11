package com.example.hw4sensorsnotifications

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.hw4sensorsnotifications.ui.theme.HW4SensorsNotificationsTheme
import androidx.compose.material3.Button
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


class MainActivity : ComponentActivity() {
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }
    private lateinit var sensorManager: android.hardware.SensorManager
    private var accelerometer: android.hardware.Sensor? = null
    private var ax by mutableStateOf(0f)
    private var ay by mutableStateOf(0f)
    private var az by mutableStateOf(0f)

    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                ax = event.values[0]   // reuse ax to store temperature
                ay = 0f
                az = 0f
            }
        }
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationHelper.createChannel(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        sensorManager = getSystemService(android.content.Context.SENSOR_SERVICE) as android.hardware.SensorManager
        accelerometer = sensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_AMBIENT_TEMPERATURE)
        enableEdgeToEdge()
        setContent {
            HW4SensorsNotificationsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Button(
                        onClick = {
                            val work = OneTimeWorkRequestBuilder<NotifyWorker>()
                                .setInitialDelay(10, TimeUnit.SECONDS) // fires in 10s
                                .setInputData(
                                    workDataOf(
                                        "title" to "HW4 Background Notification",
                                        "text" to "Temperature: ${"%.1f".format(ax)} °C (from sensor)"
                                    )
                                )
                                .build()

                            WorkManager.getInstance(this@MainActivity).enqueue(work)
                        },
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        Text("Schedule notification in 10 seconds\nTemperature: ${"%.1f".format(ax)} °C")

                    }

                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(
                sensorListener,
                it,
                android.hardware.SensorManager.SENSOR_DELAY_UI
            )
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(sensorListener)
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HW4SensorsNotificationsTheme {
        Greeting("Android")
    }
}