package com.example.designpoc.ui

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import com.example.designpoc.R.id
import com.example.designpoc.R.layout
import com.example.designpoc.utils.replaceFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)

        supportFragmentManager.replaceFragment(
            this,
            MainFragment(),
            id.fragmentContainer,
            true
        )
    }

    override fun attachBaseContext(newBase: Context?) {
        val newOverride = Configuration(newBase?.resources?.configuration)
        try {
            newOverride.fontScale = when {
                newOverride.fontScale < 1.0 -> 1.0f
                newOverride.fontScale > 1.15 -> 1.15f
                else -> newOverride.fontScale
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            newOverride.densityDpi = when {
                newOverride.densityDpi < DisplayMetrics.DENSITY_DEVICE_STABLE -> DisplayMetrics.DENSITY_DEVICE_STABLE
                newOverride.densityDpi > DisplayMetrics.DENSITY_DEVICE_STABLE -> getNearestScreenValue()
                else -> newOverride.densityDpi
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        applyOverrideConfiguration(newOverride)
        super.attachBaseContext(newBase)
    }

    private fun getNearestScreenValue(): Int {
        val sizes: List<Int> = listOf(
            DisplayMetrics.DENSITY_LOW,
            DisplayMetrics.DENSITY_MEDIUM,
            DisplayMetrics.DENSITY_TV,
            DisplayMetrics.DENSITY_HIGH,
            DisplayMetrics.DENSITY_280,
            DisplayMetrics.DENSITY_XHIGH,
            DisplayMetrics.DENSITY_360,
            DisplayMetrics.DENSITY_400,
            DisplayMetrics.DENSITY_420,
            DisplayMetrics.DENSITY_XXHIGH,
            DisplayMetrics.DENSITY_560,
            DisplayMetrics.DENSITY_XXXHIGH,
        )

        return sizes.filter { it > DisplayMetrics.DENSITY_DEVICE_STABLE }.minOf { it }
    }
}