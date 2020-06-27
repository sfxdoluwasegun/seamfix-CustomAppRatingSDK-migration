package com.seamfix.apprating

import android.util.Log

class FeedBackManager {

    companion object{

        fun syncFeedback(builder: CustomRatingBuilder, star: Float, comment: String) {
            val feedback = Feedback(
                builder.appName,
                builder.userName,
                builder.userEmail,
                star,
                comment,
                builder.timestamp,
                builder.userDeviceName,
                builder.location)

            Log.e("SDK", feedback.toString())
        }
    }
}