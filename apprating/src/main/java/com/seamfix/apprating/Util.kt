package com.seamfix.apprating

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.fragment.app.FragmentActivity
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

private const val NEXT_POPUP_TIME = "NEXT_POPUP_TIME"
private const val NO_VALUE_SET = -1L

private fun getSimpleDateFormat(timestampValue: Long): String {
    val dateValue = Date(timestampValue)
    val dateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
    val symbols = DateFormatSymbols(Locale.getDefault())
    dateFormat.dateFormatSymbols = symbols
    return dateFormat.format(dateValue)
}


/*** Determines the next time the SDK should show depending on the frequency  */
private fun getNextPopupTime(currentTime: Long, frequency: Int): Long {
    //If, for example, the frequency is 3, the next popup time should be 3 days from the current time.
    val currentDate = Date(currentTime)
    val calendar = Calendar.getInstance()
    calendar.time = currentDate
    calendar.add(Calendar.DATE, frequency)

    val nextPopupDate = calendar.time

    return nextPopupDate.time
}


/** Gets the next popup time and saves it ***/
private fun getAndSaveNextPopupTime(sharedPref: SharedPreferences, currentTime: Long, frequency: Int){
    val editor = sharedPref.edit()
    val nextTime = getNextPopupTime(currentTime, frequency)
    editor.putLong(NEXT_POPUP_TIME, nextTime)
    editor.apply()

    Log.e(
        "SDK",
        "Current date: ${getSimpleDateFormat(currentTime)}, " +
                "Next pop up date date: ${getSimpleDateFormat(nextTime)}"
    )
}


/*** Determines whether to show the dialog or not  */
fun shouldShowDialog(context: Context, frequency: Int): Boolean {

    val sharedPref: SharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
    //How do we know whether to show the popup?
    //The logical thing to do is to check if we have reached the allowed time to show a popup:
    //Read the nextPopupTime value (if it exists)
    val nextPopupTime = sharedPref.getLong(NEXT_POPUP_TIME, NO_VALUE_SET)

    if (nextPopupTime == NO_VALUE_SET) { //this means that the SDK has never launched on the app before.
        //So we can freely launch the SDK now:
        //But first, use our currentTime to calculate the next 'nextPopupTime' then save it:
        Log.e("SDK", "SDK's first launch!")
        getAndSaveNextPopupTime(sharedPref, System.currentTimeMillis(), frequency)
        return true
    } else { //This means a time had been set for the SDK to launch. If we are equal to or ahead
        //of this time, we can launch the SDK:
        val currentTime = System.currentTimeMillis()
        return if (currentTime >= nextPopupTime) { //Launch SDK
            //But first, use our currentTime to calculate the next 'nextPopupTime' then save it:
            getAndSaveNextPopupTime(sharedPref, currentTime, frequency)
            true
        }else {
            Log.e("SDK", "Next popup time has not been reached \n " +
                    "Current time = ${getSimpleDateFormat(currentTime)} \n " +
                    "Next time is ${getSimpleDateFormat(nextPopupTime)}")
            false
        }
    }
}
