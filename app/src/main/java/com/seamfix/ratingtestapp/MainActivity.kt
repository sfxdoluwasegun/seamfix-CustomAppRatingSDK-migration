package com.seamfix.ratingtestapp

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.seamfix.apprating.AppRatingDialogFragment
import com.seamfix.apprating.CustomRatingBuilder

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var packageId: String = packageName
        if (packageId.contains(".debug")) {
            packageId = packageId.replace(".debug", "")
        }

        var ratingDialogFragment = CustomRatingBuilder(this)
            .setTitleText("up")
            .setNoteDescriptionText(listOf("Not Good", "Not Good", "Quite Okay", "Great Stuff!", "Excellent!"))
            .onRatingSet(this::onRatingSet)
            .setPlaystoreUrl(packageId)
            .setPlayStoreTitle("Playstore")
            .setPlayStoreMessage("My message is here")
            .setBackGroundColor(R.color.deep_yellow)
            .setTitleColor(R.color.colorPrimaryDark)
            .setRatingStarColor(Color.MAGENTA)//Must use Color constants
            .setDescriptionColor(R.color.red)
            .setAppName("SAMSUNG 3030")
            .setUserEmail("jeffemuveyan@gmail.com")
            .setButtonColor(R.color.red)
            .setCancellable(true)
            .setThreshold(3f)
            .setFrequency(10)
            .build()

        ratingDialogFragment.show(supportFragmentManager, "")
    }


    private fun onRatingSet(ratingDialog: AppRatingDialogFragment,rating: Float, thresholdCleared: Boolean, feedback: String){
        Log.e(MainActivity::class.java.simpleName, "rating: $rating , thresholdCleared: $thresholdCleared, feedback: $feedback")
    }
}
