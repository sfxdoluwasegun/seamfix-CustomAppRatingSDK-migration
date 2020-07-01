package com.seamfix.apprating

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.ANResponse
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import org.json.JSONArray


class FeedbackWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    companion object{
        var feedback: Feedback? = null
        const val BASE_URL = "192.168.43.222"
    }

    override fun doWork(): Result {

        Log.e("SDK", "Feedback: ${feedback.toString()}")


        val request = AndroidNetworking.post("localhost:8080/saveFeedback")
            .setTag("syncing...")
            .addBodyParameter(feedback)
            .setPriority(Priority.LOW)
            .build()

        val response = request.executeForObject(FeedbackResponse::class.java)

        return if (response.isSuccess) {
            Result.success()
        } else {
            //handle error
            Result.retry()
        }
    }
}