package com.suman.qchat.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.suman.qchat.BuildConfig;
import com.suman.qchat.R;
import com.suman.qchat.databinding.ActivitySplashScreenBinding;
import com.suman.qchat.utilities.Constants;
import com.suman.qchat.utilities.PreferenceManager;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {
    private static final int TIME_OUT = 3000;
    protected ActivitySplashScreenBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());
        Animation alpha = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
        binding.txtAnimation.startAnimation(alpha);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.progressBar.setVisibility(View.VISIBLE);
                FirebaseFirestore database = FirebaseFirestore.getInstance();
                database.collection(Constants.KEY_VERSION_COLLECTION)
                        .whereGreaterThan(Constants.KEY_VERSION_CODE, BuildConfig.VERSION_CODE)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                                binding.progressBar.setVisibility(View.INVISIBLE);
                                Intent iNext = new Intent(getApplicationContext(), UpdateAppActivity.class);
                                DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                iNext.putExtra(Constants.KEY_VERSION_NAME, documentSnapshot.getString(Constants.KEY_VERSION_NAME));
                                iNext.putExtra(Constants.KEY_VERSION_DATE, documentSnapshot.getString(Constants.KEY_VERSION_DATE));
                                iNext.putExtra(Constants.KEY_VERSION_WHATSNEW, documentSnapshot.getString(Constants.KEY_VERSION_WHATSNEW));
                                iNext.putExtra(Constants.KEY_VERSION_LINK, documentSnapshot.getString(Constants.KEY_VERSION_LINK));
                                startActivity(iNext);
                                finish();
                            } else {
                                binding.progressBar.setVisibility(View.INVISIBLE);
                                boolean checkIsLogin = preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN);
                                boolean checkIsProfileUpdated = preferenceManager.getBoolean(Constants.KEY_IS_PROFILE_UPDATE);
                                if (checkIsLogin && checkIsProfileUpdated) {
                                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                                    finish();
                                } else if (checkIsLogin) {
                                    startActivity(new Intent(SplashScreenActivity.this, UpdateProfileActivity.class));
                                    finish();
                                } else {
                                    startActivity(new Intent(SplashScreenActivity.this, SendOTPActivity.class));
                                    finish();
                                }
                            }
                        });
            }
        }, TIME_OUT);
    }
}