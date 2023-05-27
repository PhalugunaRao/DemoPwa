package com.example.pwaapp

import android.app.Activity
import android.content.res.Resources
import android.view.View
import android.view.Window
import androidx.core.content.res.ResourcesCompat

fun whiteStatus(activity: Activity) {
    activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
}
fun Window.setStatusBarColor(resources: Resources, color: Int) {
    this.statusBarColor = ResourcesCompat.getColor(resources, color, null)
}
