package com.eyalm.betteroref

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import kotlinx.coroutines.*
import java.io.EOFException

class AlertsService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())

    override fun onCreate() {
        super.onCreate()
        Log.d("AlertsService", "Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("AlertsService", "Service Started")
        val notification = CreateNotification()
        startForeground(1, notification)
        startCheckingForAlerts()
        return START_STICKY
    }

    private fun startCheckingForAlerts() {

        serviceScope.launch {
            while (isActive) {
                Log.d("AlertsService", "Checking for alerts...")
                try {
                    val response = PikudHaorefApi.retrofitService.getAlertsAsRawString()
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody.isNullOrBlank()) {
                            Log.d("AlertsService", "No active alerts found (empty or whitespace response)")
                        } else {
                            Log.d("AlertsService", "Response body: $responseBody")
                            try {
                                val gson = Gson()
                                val alerts = gson.fromJson(responseBody, AlertsResponse::class.java)
                                Log.d("AlertsService", "Alerts received: $alerts")
                            } catch (e: EOFException) {
                                Log.d("AlertsService", "No active alerts found (EOFException)")
                            } catch (e: Exception) {
                                Log.e("AlertsService", "Error parsing alerts: ${e.message}", e)
                            }
                        }
                    } else {
                        Log.e("AlertsService", "Error: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.e("AlertsService", "Exception: ${e.message}", e)
                }
                delay(1000)
            }
        }
    }

    private fun CreateNotification(): Notification {
        val channelId = "alerts_service_channel"
        val channelName = "Alerts Service"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            val service = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(chan)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("BetterOref פעיל")
            .setContentText("האפליקציה בודקת התרעות ברקע")
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        Log.d("AlertsService", "Service destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}