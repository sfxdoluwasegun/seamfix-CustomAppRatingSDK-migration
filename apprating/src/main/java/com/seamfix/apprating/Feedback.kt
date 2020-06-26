package com.seamfix.apprating

import androidx.annotation.Keep

@Keep
//App Name|Name|Email Address|Rating |Comment|Timestamp|Device|location
class Feedback(val appName: String,
               val userName: String,
               val emailAddress: String,
               val rating: Float,
               val comment: String,
               val timestamp: Long,
               val device: String,
               val userLocation: String)