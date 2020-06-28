package com.seamfix.apprating

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters


class FeedbackWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    companion object{
        var feedback: Feedback? = null
    }

    override fun doWork(): Result {

        Log.e("SDK", "Feedback: ${feedback.toString()}")

        /*if(feedback != null) {

            AndroidNetworking.post("https://fierce-cove-29863.herokuapp.com/createUser")
                .addBodyParameter(feedback) // posting java object
                .setTag("sync")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(object : JSONArrayRequestListener {
                    override fun onResponse(response: JSONArray) { // do anything with response

                    }

                    override fun onError(error: ANError) { // handle error
                        Log.e("SDK", "Failed to sync")
                    }
                })
        }*/

        return Result.success()
    }
}