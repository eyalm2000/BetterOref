package com.eyalm.betteroref

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.google.gson.Gson
import com.eyalm.betteroref.PikudHaorefApi
import com.eyalm.betteroref.R
import java.io.EOFException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            try {
                val response = PikudHaorefApi.retrofitService.getAlertsAsRawString()
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody.isNullOrBlank()) {
                        Log.d("MainActivity", "No active alerts found (empty or whitespace response)")
                        return@launch
                    } else {
                        Log.d("MainActivity", "Response body: $responseBody")
                        try {
                            val gson = Gson()
                            val alerts = gson.fromJson(responseBody, AlertsResponse::class.java)
                            Log.d("MainActivity", "Alerts received: $alerts")
                        } catch (e: EOFException) {
                            Log.d("MainActivity", "No active alerts found (EOFException)")
                        } catch (e: Exception) {
                            Log.e("MainActivity", "Error parsing alerts: ${e.message}", e)
                        }
                    }
                } else {
                    Log.e("MainActivity", "Error: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Exception: ${e.message}", e)
            }
        }
    }
}