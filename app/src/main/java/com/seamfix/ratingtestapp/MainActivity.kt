package com.seamfix.ratingtestapp

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.seamfix.apprating.AppRatingDialogFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var packageId: String = packageName
        if (packageId.contains(".debug")) {
            packageId = packageId.replace(".debug", "")
        }

        var ratingDialogFragment = AppRatingDialogFragment.Builder(this)
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
            .setButtonColor(R.color.red)

            .setThreshold(3f)
            .build()

        ratingDialogFragment.show(supportFragmentManager, "")
    }


    private fun onRatingSet(ratingDialog: AppRatingDialogFragment,rating: Float, thresholdCleared: Boolean, feedback: String){
        Log.e(MainActivity::class.java.simpleName, "rating: $rating , thresholdCleared: $thresholdCleared, feedback: $feedback")
    }
}
