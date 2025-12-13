package com.asdru.cardgame3.system

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager

class BatteryMonitor private constructor(private val context: Context) {

  companion object {
    @Volatile
    private var INSTANCE: BatteryMonitor? = null

    fun getInstance(context: Context): BatteryMonitor {
      return INSTANCE ?: synchronized(this) {
        INSTANCE ?: BatteryMonitor(context.applicationContext).also { INSTANCE = it }
      }
    }
  }

  fun getBatteryPercentage(): Int {
    val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { filter ->
      context.registerReceiver(null, filter)
    }

    return batteryStatus?.let { intent ->
      val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
      val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

      if (level != -1 && scale != -1) {
        (level * 100 / scale.toFloat()).toInt()
      } else {
        0
      }
    } ?: 0
  }
}