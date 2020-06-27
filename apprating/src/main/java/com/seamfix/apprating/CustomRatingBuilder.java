package com.seamfix.apprating;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.Keep;

import java.util.ArrayList;
import java.util.List;

@Keep
public class CustomRatingBuilder {

    private static final String NO_VALUE_PROVIDED = "NO_VALUE_PROVIDED";

    private final Context context;
    String playstoreUrl, playStoreTitle, playStoreMessage;
    String titleText;
    Integer backGroundColor, titleColor, ratingStarColor, descriptionColor, buttonColors;
    ArrayList<String> noteDescriptions;

    RatingSetListener ratingSetListener;
    RatingDialogListener ratingDialogListener;
    float threshold = 3;// 3 stars is the default threshold
    boolean cancellable = false;
    int frequency = 3; //3 days is the default frequency interval for every popup.
    String appName = NO_VALUE_PROVIDED;
    String userName = NO_VALUE_PROVIDED;
    String userPhoneNumber = NO_VALUE_PROVIDED;
    String userEmail = NO_VALUE_PROVIDED;
    String userAddress = NO_VALUE_PROVIDED;
    String timestamp = NO_VALUE_PROVIDED;
    String userDeviceName = NO_VALUE_PROVIDED;
    String location = NO_VALUE_PROVIDED;


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



    public AppRatingDialogFragment build() {
        return new AppRatingDialogFragment(context, this);
    }


    public CustomRatingBuilder setAppName(String appName) {
        this.appName = appName;
        return this;
    }

    public CustomRatingBuilder setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public CustomRatingBuilder setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
        return this;
    }

    public CustomRatingBuilder setUserEmail(String userEmail) {
        this.userEmail = userEmail;
        return this;
    }

    public CustomRatingBuilder setUserAddress(String userAddress) {
        this.userAddress = userAddress;
        return this;
    }

    public CustomRatingBuilder setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public CustomRatingBuilder setUserDeviceName(String userDeviceName) {
        this.userDeviceName = userDeviceName;
        return this;
    }

    public CustomRatingBuilder setLocation(String location) {
        this.location = location;
        return this;
    }

    public CustomRatingBuilder setFrequency(int frequency) {
        this.frequency = frequency;
        return this;
    }
}
