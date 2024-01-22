package com.suman.qchat.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.suman.qchat.R;
import com.suman.qchat.databinding.ActivityVerifyOtpactivityBinding;
import com.suman.qchat.utilities.Constants;
import com.suman.qchat.utilities.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class VerifyOTPActivity extends AppCompatActivity {
    boolean flag = true;
    String verificationID;
    private ActivityVerifyOtpactivityBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyOtpactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());
        binding.textMobile.setText(String.format("+91-%s", getIntent().getStringExtra(Constants.KEY_PHONE)));

        setupOTPInput();

        verificationID = getIntent().getStringExtra(Constants.VERIFICATION_KEY);

        binding.buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.inputCode1.getText().toString().trim().isEmpty() || binding.inputCode2.getText().toString().trim().isEmpty() || binding.inputCode3.getText().toString().trim().isEmpty() || binding.inputCode4.getText().toString().trim().isEmpty() || binding.inputCode5.getText().toString().trim().isEmpty() || binding.inputCode6.getText().toString().trim().isEmpty()) {
                    Toast.makeText(VerifyOTPActivity.this, "Please Enter Valid Code", Toast.LENGTH_SHORT).show();
                    return;
                }
                String code = binding.inputCode1.getText().toString() + binding.inputCode2.getText().toString() + binding.inputCode3.getText().toString() + binding.inputCode4.getText().toString() + binding.inputCode5.getText().toString() + binding.inputCode6.getText().toString();

                if (verificationID != null) {
                    if (verificationID.equals(code)) {
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);

                        loading(true);
                        FirebaseFirestore database = FirebaseFirestore.getInstance();
                        database.collection(Constants.KEY_COLLECTION_USERS)
                                .whereEqualTo(Constants.KEY_PHONE, getIntent().getStringExtra(Constants.KEY_PHONE))
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                        preferenceManager.putBoolean(Constants.KEY_IS_PROFILE_UPDATE, true);
                                        preferenceManager.putString(Constants.KEY_PHONE, getIntent().getStringExtra(Constants.KEY_PHONE));
                                        preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                                        preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                                        preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
                                        Intent iNext = new Intent(getApplicationContext(), MainActivity.class);
                                        iNext.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(iNext);
                                    } else {
                                        loading(false);
                                        Intent iNext = new Intent(VerifyOTPActivity.this, UpdateProfileActivity.class);
                                        preferenceManager.putString(Constants.KEY_PHONE, getIntent().getStringExtra(Constants.KEY_PHONE));
                                        iNext.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(iNext);
                                    }
                                });

                    } else {
                        Toast.makeText(VerifyOTPActivity.this, "Enter Invalid Code", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        new CountDownTimer(30000, 1000) {

            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                ((TextView) findViewById(R.id.textResendOTP)).setText(getString(R.string.remaining) + millisUntilFinished / 1000);
            }

            public void onFinish() {
                ((TextView) findViewById(R.id.textResendOTP)).setText(R.string.resend_otp_new);

                findViewById(R.id.textResendOTP).setOnClickListener(v -> {
                    if (flag) {
                        binding.progressBar.setVisibility(View.VISIBLE);
                        binding.buttonVerify.setVisibility(View.INVISIBLE);
                        String NEWOTP = getOTP();
                        String apiUrl = "https://www.fast2sms.com/dev/bulkV2?authorization=CyUmvbLsAqBKgX6uQSNhazi4OnpjZtDIFETdJ5k9V8HcR2YxMrfFL8rBG9jglbd34UNRKSX0cJk5is16&route=v3&sender_id=FTWSMS&message=" + NEWOTP + "%20Send%20From%20QChat&language=english&flash=0&numbers=" + getIntent().getStringExtra(Constants.KEY_PHONE);
                        AndroidNetworking.initialize(VerifyOTPActivity.this);
                        AndroidNetworking.get(apiUrl).setPriority(Priority.HIGH).build().getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String status = response.getString("return");
                                    if (status.equals("true")) {
                                        binding.progressBar.setVisibility(View.INVISIBLE);
                                        binding.buttonVerify.setVisibility(View.VISIBLE);
                                        verificationID = NEWOTP;
                                    } else {
                                        binding.progressBar.setVisibility(View.INVISIBLE);
                                        binding.buttonVerify.setVisibility(View.VISIBLE);
                                        Toast.makeText(VerifyOTPActivity.this, "OTP Send Failed", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                Toast.makeText(VerifyOTPActivity.this, anError.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        flag = false;
                    }
                });
            }
        }.start();
    }

    private void loading(boolean isLoading) {
        if (isLoading) {
            binding.buttonVerify.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.buttonVerify.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void setupOTPInput() {
        binding.inputCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    binding.inputCode2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.inputCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    binding.inputCode3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.inputCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    binding.inputCode4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.inputCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    binding.inputCode5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.inputCode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    binding.inputCode6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
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