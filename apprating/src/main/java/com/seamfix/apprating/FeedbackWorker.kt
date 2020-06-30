package com.seamfix.apprating

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import org.json.JSONArray


class FeedbackWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    
    companion object{
        var feedback: Feedback? = null
    }

    override fun doWork(): Result {

        Log.e("SDK", "Feedback: ${feedback.toString()}")

        if(feedback != null) {

            var request = AndroidNetworking.post("https://fierce-cove-29863.herokuapp.com/createUser")
                .addBodyParameter(feedback) // posting java object
                .setTag("sync")
                .setPriority(Priority.HIGH)
                .build()
            
            val response = request.executeForJSONArray()
            return if(response.isSuccess){
                Result.success()
            }else{
                Result.retry()
            }
        }

        return Result.retry()
    }
}