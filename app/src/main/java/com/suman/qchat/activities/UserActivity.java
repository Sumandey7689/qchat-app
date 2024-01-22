package com.suman.qchat.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.suman.qchat.adapters.UserAdapter;
import com.suman.qchat.databinding.ActivityUserBinding;
import com.suman.qchat.listeners.UserListener;
import com.suman.qchat.models.User;
import com.suman.qchat.utilities.Constants;
import com.suman.qchat.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserActivity extends BaseActivity implements UserListener {
    private ActivityUserBinding binding;
    private PreferenceManager preferenceManager;
    private ArrayList<String> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        contactList = new ArrayList<>();

        getContact();
        setListeners();
        getUsers();
    }

    private void getContact() {
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {

            @SuppressLint("Range")
            String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace(" ", "").replace("+91", "");
            if (number.length() == 10) {
                contactList.add(number);
            }
        }
        cursor.close();
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(view -> onBackPressed());
    }

    private void getUsers() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get().addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<User> usersList = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (currentUserId.equals(queryDocumentSnapshot.getId())) {
                                continue;
                            }
                            User user = new User();
                            user.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                            user.phone_no = queryDocumentSnapshot.getString(Constants.KEY_PHONE);
                            user.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                            user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                            user.id = queryDocumentSnapshot.getId();

                            boolean flag = true;

                            for (int i = 0; i < contactList.size(); i++) {
                                if (Objects.equals(user.phone_no, contactList.get(i)) && flag) {
                                    usersList.add(user);
                                    flag = false;
                                }
                            }
                        }
                        contactList.clear();
                        if (usersList.size() > 0) {
                            UserAdapter userAdapter = new UserAdapter(usersList, this);
                            binding.usersRecylerView.setAdapter(userAdapter);
                            binding.usersRecylerView.setVisibility(View.VISIBLE);
                        } else {
                            showErrorMessage();
                        }
                    } else {
                        showErrorMessage();
                    }
                });
    }

    private void showErrorMessage() {
        binding.LottieErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loading(boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
        finish();
    }
}