package com.suman.qchat.utilities;

import java.util.HashMap;

public class Constants {
    public static final String VERIFICATION_KEY = "verificationID";

    public static final String  KEY_VERSION_COLLECTION = "versionControl";
    public static final String KEY_VERSION_CODE = "versionCode";
    public static final String KEY_VERSION_NAME = "versionName";
    public static final String KEY_VERSION_DATE = "versionDate";
    public static final String KEY_VERSION_WHATSNEW = "versionWhatsnew";
    public static final String KEY_VERSION_LINK = "versionLink";

    public static final String KEY_COLLECTION_USERS = "users";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_PHONE = "phone_no";
    public static final String KEY_NAME = "name";
    public static final String KEY_IMAGE = "image";

    public static final String KEY_PREFERENCE_NAME = "chatAppPreference";
    public static final String KEY_IS_SIGNED_IN = "isSignedIn";
    public static final String KEY_IS_PROFILE_UPDATE = "isProfileUpdate";

    public static final String KEY_FCM_TOKEN = "fcmToken";
    public static final String KEY_USER = "user";

    public static final String KEY_CONTACTS = "contacts";

    public static final String KEY_COLLECTION_CHAT = "chat";
    public static final String KEY_SENDER_ID = "senderId";
    public static final String KEY_RECEIVER_ID = "receiverId";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TIMESTAMP = "timestamp";

    public static final String KEY_COLLECTION_CONVERSATIONS = "conversation";
    public static final String KEY_SENDER_NAME = "senderName";
    public static final String KEY_RECEIVER_NAME = "receiverName";
    public static final String KEY_SENDER_IMAGE = "senderImage";
    public static final String KEY_RECEIVER_IMAGE = "receiverImage";
    public static final String KEY_LAST_MESSAGE = "lastMessage";

    public static final String KEY_AVAILABILITY = "availability";

    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";

    public static HashMap<String, String> remoteMsgHeaders = null;
    public static HashMap<String, String> getRemoteMsgHeaders() {
        if (remoteMsgHeaders == null) {
            remoteMsgHeaders = new HashMap<>();
            remoteMsgHeaders.put(REMOTE_MSG_AUTHORIZATION, "key=AAAAXdKfMMs:APA91bHnThPYx9EtXo1YFKV7JfIY00gS1uCRkPThFTEXNASCBShD2vP_ILKN7az5ecdcHF7de-Gn0dMG2IGBF-Do1ij8_OCLVJxd60kzUf7lNHzke8snSDrW4qoLzn3-F5vjl3cDVnCu");
            remoteMsgHeaders.put(REMOTE_MSG_CONTENT_TYPE, "application/json");
        }
        return remoteMsgHeaders;
    }
}
