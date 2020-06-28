package com.seamfix.apprating;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.androidnetworking.AndroidNetworking;

import java.util.ArrayList;
import static com.seamfix.apprating.UtilKt.shouldShowDialog;

public class AppRatingDialogFragment extends DialogFragment implements RatingBar.OnRatingBarChangeListener, View.OnClickListener {

    private Context context;
    private CustomRatingBuilder builder;
    private TextView tvTitle, tvPositive, tvNeutral, tvDescription, tvNoteDescription;
    private RatingBar ratingBar;
    private EditText etFeedback;
    private LinearLayout linearLayout;

    private boolean changeToPlayStoreView = false;
    private boolean cancellable;

    public AppRatingDialogFragment(Context context, CustomRatingBuilder builder) {
        super();
        this.context = context;
        this.builder = builder;
        this.cancellable = builder.cancellable;

        //init Android Networking:
        AndroidNetworking.initialize(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_app_rating, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvTitle = view.findViewById(R.id.titleText);
        tvPositive = view.findViewById(R.id.btn_dialog_positive);
        tvNeutral = view.findViewById(R.id.btn_dialog_neutral);
        ratingBar = view.findViewById(R.id.ratingBar);
        linearLayout = view.findViewById(R.id.layout);
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
        tvNeutral.setOnClickListener(this);
        setCancelable(cancellable);

        //Change the title text from the default if the user set it:
        if(builder.titleText != null && !(builder.titleText.isEmpty())){
            tvTitle.setText(builder.titleText);
        }

        //Change the description to a default if the user did not set it:
        if(builder.noteDescriptions == null || builder.noteDescriptions.size() < 5){
            ArrayList<String> list = new ArrayList<>();
            list.add("Not Good");
            list.add("Not Good");
            list.add("Quite Okay");
            list.add("Great Stuff!");
            list.add("Excellent!");
            builder.noteDescriptions = list;
        }

        //Change the background color if the user set it
        if(builder.backGroundColor != null){
            linearLayout.setBackgroundColor(getResources().getColor(builder.backGroundColor));
        }

        //Change the title color if the user set it
        if(builder.titleColor != null){
            tvTitle.setTextColor(getResources().getColor(builder.titleColor));
        }

        //Change the description color if the user set it
        if(builder.descriptionColor != null){
            tvNoteDescription.setTextColor(getResources().getColor(builder.descriptionColor));
        }

        //Change the color of all buttons if the user set it
        if(builder.buttonColors != null){
            tvNeutral.setTextColor(getResources().getColor(builder.buttonColors));
            tvPositive.setTextColor(getResources().getColor(builder.buttonColors));
        }

        //Change the rating start color if the user set it
        if(builder.ratingStarColor != null){
            LayerDrawable layerDrawable = (LayerDrawable) ratingBar.getProgressDrawable();
            DrawableCompat.setTint(DrawableCompat.wrap(layerDrawable.getDrawable(0)),
                    Color.LTGRAY);  // Empty star color (the initial state)
            DrawableCompat.setTint(DrawableCompat.wrap(layerDrawable.getDrawable(1)),
                    builder.ratingStarColor); // Partial star color
            DrawableCompat.setTint(DrawableCompat.wrap(layerDrawable.getDrawable(2)),
                    builder.ratingStarColor); // Full star color
        }
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        //A callback each time the user changes the rating
        if (builder.ratingDialogListener != null){
            builder.ratingDialogListener.onRatingChanged(rating, rating >= builder.threshold);
        }
        if (rating > 0 && rating <= 5){
            tvNoteDescription.setText(builder.noteDescriptions.get(Math.round(rating - 1)));

            if (rating < builder.threshold){
                etFeedback.setVisibility(View.VISIBLE);
            } else {
                etFeedback.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        if (id == R.id.btn_dialog_positive) {//user clicks RATE_NOW_BUTTON
            float rating = ratingBar.getRating();

            if (rating < 0.5) {
                //the user did not set any rating star at all, so:
                Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
                tvPositive.startAnimation(shake);
                return;
            }


            if(rating < builder.threshold){
                //the user set a low rating, a text view must have already appeared to collect the
                //user's negative feedback.

                //So now we get the negative feedback:
                String feedback = getPoorFeedBack(etFeedback.getText().toString().trim());

                //Sync the users feedback:
                syncFeedback(rating, feedback);
                dismiss();

                new AlertDialog.Builder(requireContext()).setTitle("Feedback sent")
                        .setMessage("Thanks for the feedback.").show();
            }


            if(rating >= builder.threshold && !changeToPlayStoreView){
                //the user has just set a high rating, He should now be prompted to visit play store.

                //But before that, lets get and sync the user's feedback:
                String feedback = String.valueOf(rating) + " stars given!";

                //Sync the users feedback:
                syncFeedback(rating, feedback);

                //Now prompt the user to visit play store and rate the app there:
                tvDescription.setVisibility(View.VISIBLE);
                etFeedback.setVisibility(View.GONE);
                tvTitle.setText(R.string.rate_us_on_playstore);
                tvPositive.setText(R.string.continue_text);
                ratingBar.setVisibility(View.GONE);
                tvNoteDescription.setVisibility(View.GONE);

                //Change the title text to what was set by user as playstore title:
                if(builder.playStoreTitle != null && !(builder.playStoreTitle.isEmpty())){
                    tvTitle.setText(builder.playStoreTitle);
                }

                //Change the title text to what was set by user as playstore title:
                if(builder.playStoreMessage != null && !(builder.playStoreMessage.isEmpty())){
                    tvDescription.setText(builder.playStoreMessage);
                }

                //Change the description color if the user set it
                if(builder.descriptionColor != null){
                    tvDescription.setTextColor(getResources().getColor(builder.descriptionColor));
                }

                changeToPlayStoreView = true;
                return;
            }

            if (rating >= builder.threshold && changeToPlayStoreView){
                //the user has agreed to rate the app on playstore:
                openPlayStore();

                //after you have opened playstore, you should reset this value:
                changeToPlayStoreView = false;
            }

        } else if (id == R.id.btn_dialog_neutral) {
            dismiss();
        }
    }


    private String getPoorFeedBack(String feedback) {
        if(feedback == null || feedback.isEmpty()){
            return "No comment provided by user";
        }else {
            return feedback;
        }
    }


    private void syncFeedback(float star, String comment) {
        //emit the callback
        if(builder.ratingSetListener != null){
            builder.ratingSetListener.onRatingSet(
                    this,
                    ratingBar.getRating(),
                    star >= builder.threshold,
                    comment);
        }
        FeedBackManager.Companion.syncFeedback(context, builder,star, comment);
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
        if (context != null && shouldShowDialog(context, builder.frequency)) {
            super.show(manager, tag);
        }
    }
}
