package com.seamfix.apprating

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

class FeedBackManager {

    companion object{

        fun syncFeedback(context: Context, builder: CustomRatingBuilder, star: Float, comment: String) {
            val feedback = Feedback(System.currentTimeMillis(),
                builder.getPackageName(),
                builder.appName,
                builder.userName,
                builder.userEmail,
                star,
                comment,
                builder.timestamp,
                builder.userDeviceName,
                builder.location
            )

            //set the feedback:
            FeedbackWorker.feedback = feedback

            //sync the feedback:
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val uploadWorkRequest: OneTimeWorkRequest = OneTimeWorkRequestBuilder<FeedbackWorker>()
                .addTag("sync")
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS)
                .build()

            WorkManager
                .getInstance(context)
                .enqueueUniqueWork("sync", ExistingWorkPolicy.APPEND, uploadWorkRequest)
        }
    }
}