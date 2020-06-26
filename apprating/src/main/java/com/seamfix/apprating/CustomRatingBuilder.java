package com.seamfix.apprating;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.Keep;

import java.util.ArrayList;
import java.util.List;

@Keep
public class CustomRatingBuilder {

    final Context context;
    String playstoreUrl, playStoreTitle, playStoreMessage;
    String titleText;
    Integer backGroundColor, titleColor, ratingStarColor, descriptionColor, buttonColors;
    ArrayList<String> noteDescriptions;
    int days = 1;
    boolean consecutive;
    RatingSetListener ratingSetListener;
    RatingDialogListener ratingDialogListener;
    float threshold = 3;// 3 stars is the default threshold
    boolean cancellable = false;


    public interface RatingSetListener {
        void onRatingSet(AppRatingDialogFragment ratingDialog, float rating, boolean thresholdCleared, String feedback);
    }

    public interface RatingDialogListener {
        void onRatingChanged(float rating, boolean thresholdCleared);
    }

    public CustomRatingBuilder(Context context) {
        this.context = context;
        // Set default PlayStore URL
        this.playstoreUrl = "market://details?id=" + context.getPackageName();

    }

    public CustomRatingBuilder setThreshold(float threshold) {
        this.threshold = threshold;
        return this;
    }

    public CustomRatingBuilder onRatingSet(RatingSetListener ratingSetListener) {
        this.ratingSetListener = ratingSetListener;
        return this;
    }

    public CustomRatingBuilder setPlaystoreUrl(String playstoreUrl) {
        this.playstoreUrl = "market://details?id=" + playstoreUrl;
        return this;
    }


    public CustomRatingBuilder setTitleText(String titleText) {
        this.titleText = titleText;
        return this;
    }

    public CustomRatingBuilder setPlayStoreTitle(String title) {
        this.playStoreTitle = title;
        return this;
    }

    public CustomRatingBuilder setPlayStoreMessage(String message) {
        this.playStoreMessage = message;
        return this;
    }

    public CustomRatingBuilder setBackGroundColor(int color) {
        this.backGroundColor = color;
        return this;
    }

    public CustomRatingBuilder setTitleColor(int color) {
        this.titleColor = color;
        return this;
    }

    public CustomRatingBuilder setRatingStarColor(int color) {
        this.ratingStarColor = color;
        return this;
    }


    public CustomRatingBuilder setDescriptionColor(int color) {
        this.descriptionColor = color;
        return this;
    }


    public CustomRatingBuilder setButtonColor(int color) {
        this.buttonColors = color;
        return this;
    }

    public CustomRatingBuilder setCancellable(boolean cancellable){
        this.cancellable = cancellable;
        return this;
    }

    public CustomRatingBuilder setNoteDescriptionText(List<String> noteDescriptions){
        this.noteDescriptions = new ArrayList<>(noteDescriptions);
        return this;
    }

    public CustomRatingBuilder setSessionCount(int days, boolean consecutive){
        this.days = days;
        this.consecutive = consecutive;
        return this;
    }

    public AppRatingDialogFragment build() {
        return new AppRatingDialogFragment(context, this);
    }
}
