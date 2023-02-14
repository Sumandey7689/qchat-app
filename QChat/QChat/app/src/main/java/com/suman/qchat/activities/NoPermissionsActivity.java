package com.suman.qchat.activities;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.suman.qchat.R;
import com.suman.qchat.databinding.ActivityNoPermissionsBinding;

public class NoPermissionsActivity extends AppCompatActivity {
    protected ActivityNoPermissionsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoPermissionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Animation alpha = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
        binding.txtAnimation.startAnimation(alpha);
    }
}