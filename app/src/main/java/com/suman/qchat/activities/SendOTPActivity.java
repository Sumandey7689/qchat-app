package com.suman.qchat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.suman.qchat.databinding.ActivitySendOtpactivityBinding;
import com.suman.qchat.utilities.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class SendOTPActivity extends AppCompatActivity {
    protected ActivitySendOtpactivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySendOtpactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonGetOTP.setOnClickListener(view -> {

            if (binding.inputMobile.getText().toString().trim().isEmpty() || binding.inputMobile.getText().toString().length() != 10) {
                Toast.makeText(SendOTPActivity.this, "Enter Mobile Number", Toast.LENGTH_SHORT).show();
                return;
            }
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.buttonGetOTP.setVisibility(View.INVISIBLE);

            String OTP = getOTP();
            String apiUrl = "https://www.fast2sms.com/dev/bulkV2?authorization=CyUmvbLsAqBKgX6uQSNhazi4OnpjZtDIFETdJ5k9V8HcR2YxMrfFL8rBG9jglbd34UNRKSX0cJk5is16&route=v3&sender_id=FTWSMS&message=" + OTP + "%20Send%20From%20QChat&language=english&flash=0&numbers=" + binding.inputMobile.getText().toString();
            AndroidNetworking.initialize(SendOTPActivity.this);
            AndroidNetworking.get(apiUrl).setPriority(Priority.HIGH).build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String status = response.getString("return");
                        if (status.equals("true")) {
                            binding.progressBar.setVisibility(View.INVISIBLE);
                            binding.buttonGetOTP.setVisibility(View.VISIBLE);
                            Intent iNext = new Intent(SendOTPActivity.this, VerifyOTPActivity.class);
                            iNext.putExtra(Constants.KEY_PHONE, binding.inputMobile.getText().toString());
                            iNext.putExtra(Constants.VERIFICATION_KEY, OTP);
                            startActivity(iNext);
                        } else {
                            binding.progressBar.setVisibility(View.INVISIBLE);
                            binding.buttonGetOTP.setVisibility(View.VISIBLE);
                            Toast.makeText(SendOTPActivity.this, "OTP Send Failed", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(ANError anError) {
                    Toast.makeText(SendOTPActivity.this, anError.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private String getOTP() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        String OTP = String.valueOf(number);
        while (OTP.length() != 6) {
            getOTP();
        }
        return OTP;
    }
}