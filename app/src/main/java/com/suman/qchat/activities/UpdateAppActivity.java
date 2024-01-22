package com.suman.qchat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.suman.qchat.databinding.ActivityUpdateAppBinding;
import com.suman.qchat.utilities.Constants;

public class UpdateAppActivity extends AppCompatActivity {
    private ActivityUpdateAppBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateAppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String whatsNew = getIntent().getStringExtra(Constants.KEY_VERSION_WHATSNEW).replace("#", "\n");
        binding.version.setText(getIntent().getStringExtra(Constants.KEY_VERSION_NAME));
        binding.date.setText(getIntent().getStringExtra(Constants.KEY_VERSION_DATE));
        binding.whatsnew.setText(whatsNew);
        setListener();
    }

    private void setListener() {
        binding.webupdate.setOnClickListener(view -> openWebPage(getIntent().getStringExtra(Constants.KEY_VERSION_LINK)));
    }

    private void openWebPage(String url) {
        try {
            Uri webpage = Uri.parse(url);
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                webpage = Uri.parse("https://" + url);
            }
            Intent myIntent = new Intent(Intent.ACTION_VIEW, webpage);
            startActivity(myIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application can handle this request. Please install verifyDownload web browser or check your URL.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}