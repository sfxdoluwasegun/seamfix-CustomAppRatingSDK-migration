package com.seamfix.apprating

import android.content.pm.PackageManager
import androidx.annotation.Keep

@Keep
//App Name|Name|Email Address|Rating |Comment|Timestamp|Device|location
data class Feedback(val id: Long,
                    val packageName: String,
                    val appName: String,
                   val userName: String,
                   val emailAddress: String,
                   val rating: Float,
                   val comment: String,
                   val timestamp: Long,
                   val device: String,
                   val userLocation: String)