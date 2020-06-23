package com.seamfix.apprating;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AppRatingDialogFragment extends DialogFragment implements RatingBar.OnRatingBarChangeListener, View.OnClickListener {

    private static final String SESSION_COUNT = "session_count";
    private static final String LAST_COUNT_TIME = "last_count_time";
    private static final String SHOW_NEVER = "show_never";
    private String myPrefs = "RatingDialog";
    private SharedPreferences sharedpreferences;

    private Context context;
    private Builder builder;
    private TextView tvTitle, tvNegative, tvPositive, tvNeutral, tvDescription, tvNoteDescription;
    private RatingBar ratingBar;
    private EditText etFeedback;

    private float threshold;
    private int session;
    private boolean thresholdPassed = true;
    private boolean ratingSet = false;
    private boolean cancellable;

    public AppRatingDialogFragment(Context context, Builder builder) {
        super();
        this.context = context;
        this.builder = builder;

        this.session = builder.days;
        this.threshold = builder.threshold;
        this.cancellable = builder.cancellable;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_app_rating, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeSharedPreferences();

        tvTitle = view.findViewById(R.id.titleText);
        tvNegative = view.findViewById(R.id.btn_dialog_negative);
        tvPositive = view.findViewById(R.id.btn_dialog_positive);
        tvNeutral = view.findViewById(R.id.btn_dialog_neutral);
        ratingBar = view.findViewById(R.id.ratingBar);
        tvDescription = view.findViewById(R.id.descriptionText);
        tvNoteDescription = view.findViewById(R.id.noteDescriptionText);
        etFeedback = view.findViewById(R.id.commentEditText);

        init();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if(dialog.getWindow() !=null) {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return dialog;
    }

    private void init() {

        ratingBar.setOnRatingBarChangeListener(this);
        tvPositive.setOnClickListener(this);
        tvNegative.setOnClickListener(this);
        tvNeutral.setOnClickListener(this);
        setCancelable(cancellable);

        if (session == 1) {
            tvNegative.setVisibility(View.GONE);
        }
    }

    private void initializeSharedPreferences(){
        if (sharedpreferences == null){
            sharedpreferences = context.getSharedPreferences(myPrefs, Context.MODE_PRIVATE);
        }
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        if (builder.ratingDialogListener != null){
            builder.ratingDialogListener.onRatingSelected(rating, rating >= threshold);
        } else {
            if (rating > 0 && rating <= 5){
                tvNoteDescription.setText(builder.noteDescriptions.get(Math.round(rating - 1)));

                if (rating <= builder.threshold){
                    etFeedback.setVisibility(View.VISIBLE);
                } else {
                    etFeedback.setVisibility(View.GONE);
                }
            }
        }



    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        if (id == R.id.btn_dialog_positive) {
            float rating = ratingBar.getRating();

            if (rating < 0.5) {
                Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
                tvPositive.startAnimation(shake);
                return;
            }


            String feedback = etFeedback.getText().toString().trim();
            builder.ratingSetListener.onRatingSet(
                    this, ratingBar.getRating(), thresholdPassed, feedback);

            if (!ratingSet && ratingBar.getRating() >= threshold) {
                tvDescription.setVisibility(View.VISIBLE);
                tvNegative.setVisibility(View.VISIBLE);
                etFeedback.setVisibility(View.GONE);
                tvNeutral.setVisibility(View.GONE);
                tvTitle.setText(R.string.rate_us_on_playstore);
                tvPositive.setText(R.string.continue_text);
                tvNegative.setText(R.string.skip);
                ratingBar.setVisibility(View.GONE);
                tvNoteDescription.setVisibility(View.GONE);
                showNever();

            } else if (ratingSet) {
                dismiss();
                openPlayStore();

            } else {
                dismiss();
            }

            ratingSet = true;
        } else if (id == R.id.btn_dialog_negative) {
            initializeCount(false);
            dismiss();
        } else if (id == R.id.btn_dialog_neutral) {
            showNever();
        }

    }

    private void openPlayStore() {
        final Uri marketUri = Uri.parse(builder.playstoreUrl);
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, marketUri));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, R.string.couldnt_find_playstore, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (shouldShowRatingDialog()){
            super.show(manager, tag);
        }

    }

    private boolean shouldShowRatingDialog() {

        initializeSharedPreferences();

        if (sharedpreferences.getBoolean(SHOW_NEVER, false)) {
            return false;
        }

        if (session == 1) {
            return true;
        }

        int count = sharedpreferences.getInt(SESSION_COUNT, 1);
        long lastCountTime = sharedpreferences.getLong(LAST_COUNT_TIME, 0);

        if (DateUtils.isToday(lastCountTime)){
            return false;
        } else if (DateUtils.isToday(lastCountTime + DateUtils.DAY_IN_MILLIS)){
            return checkIfSessionMatchesCount(++count);

        } else if (!builder.consecutive){
            return checkIfSessionMatchesCount(++count);

        } else {
            initializeCount(true);
            return false;

        }


    }

    private void initializeCount(boolean includeSession) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(SESSION_COUNT, includeSession ? 1 : 0);
        editor.putLong(LAST_COUNT_TIME, Calendar.getInstance().getTimeInMillis());
        editor.apply();
    }

    private boolean checkIfSessionMatchesCount(int count) {
        if (session <= count) {
            initializeCount(false);
            return true;
        } else {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt(SESSION_COUNT, count);
            editor.putLong(LAST_COUNT_TIME, Calendar.getInstance().getTimeInMillis());
            editor.apply();
            return false;
        }
    }

    private void showNever() {
        sharedpreferences = context.getSharedPreferences(myPrefs, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(SHOW_NEVER, true);
        editor.apply();
    }

    public static class Builder {

        private final Context context;
        private String playstoreUrl;
        private ArrayList<String> noteDescriptions;
        private int days = 1;
        private boolean consecutive;
        private RatingSetListener ratingSetListener;
        private RatingDialogListener ratingDialogListener;
        private Drawable drawable;
        private float threshold = 3;
        private boolean cancellable = false;

        public interface RatingThresholdClearedListener {
            void onThresholdCleared(AppRatingDialogFragment ratingDialog, float rating, boolean thresholdCleared);
        }

        public interface RatingSetListener {
            void onRatingSet(AppRatingDialogFragment ratingDialog, float rating, boolean thresholdCleared, String feedback);
        }

        public interface RatingDialogFormListener {
            void onFormSubmitted(String feedback);
        }

        public interface RatingDialogListener {
            void onRatingSelected(float rating, boolean thresholdCleared);
        }

        public Builder(Context context) {
            this.context = context;
            // Set default PlayStore URL
            this.playstoreUrl = "market://details?id=" + context.getPackageName();

        }

        public Builder setThreshold(float threshold) {
            this.threshold = threshold;
            return this;
        }

        public Builder onRatingSet(RatingSetListener ratingSetListener) {
            this.ratingSetListener = ratingSetListener;
            return this;
        }

        public Builder onRatingChanged(RatingDialogListener ratingDialogListener) {
            this.ratingDialogListener = ratingDialogListener;
            return this;
        }

        public Builder setPlaystoreUrl(String playstoreUrl) {
            this.playstoreUrl = "market://details?id=" + playstoreUrl;
            return this;
        }

        public Builder setCancellable(boolean cancellable){
            this.cancellable = cancellable;
            return this;
        }

        public Builder setNoteDescriptionText(List<String> noteDescriptions){
            this.noteDescriptions = new ArrayList<>(noteDescriptions);
            return this;
        }

        public Builder setSessionCount(int days, boolean consecutive){
            this.days = days;
            this.consecutive = consecutive;
            return this;
        }

        public AppRatingDialogFragment build() {
            return new AppRatingDialogFragment(context, this);
        }
    }
}
