package com.example.test.nuvoco3.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.test.nuvoco3.signup.ObjectSerializer;

import java.util.ArrayList;

import static com.example.test.nuvoco3.helpers.Contract.USER_ADDRESS;
import static com.example.test.nuvoco3.helpers.Contract.USER_AGE;
import static com.example.test.nuvoco3.helpers.Contract.USER_AREA;
import static com.example.test.nuvoco3.helpers.Contract.USER_CITY;
import static com.example.test.nuvoco3.helpers.Contract.USER_DEPARTMENT;
import static com.example.test.nuvoco3.helpers.Contract.USER_EMAIL;
import static com.example.test.nuvoco3.helpers.Contract.USER_ID;
import static com.example.test.nuvoco3.helpers.Contract.USER_NAME;
import static com.example.test.nuvoco3.helpers.Contract.USER_PASSWORD;
import static com.example.test.nuvoco3.helpers.Contract.USER_PHONE_NO;
import static com.example.test.nuvoco3.helpers.Contract.USER_PIN;
import static com.example.test.nuvoco3.helpers.Contract.USER_STATUS;
import static com.example.test.nuvoco3.helpers.Contract.USER_TOKEN;
import static com.example.test.nuvoco3.signup.LoginActivity.TAG;

public class UserInfoHelper {
    private ArrayList<String> userArrayList;
    private SharedPreferences sharedPreferences;
    private Context mContext;

    public UserInfoHelper(Context mContext) {
        this.mContext = mContext;
    }

    public String getUserInfo(int mInfoType) {
        userArrayList = new ArrayList<>();
        sharedPreferences = mContext.getSharedPreferences("com.example.test.nuvoco3", Context.MODE_PRIVATE);

        userArrayList = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("CustomerData", ObjectSerializer.serialize(new ArrayList<String>())));
        String mUserInfo;
        mUserInfo = userArrayList.get(mInfoType) + "";
        return mUserInfo;
    }

    public String getUserAddress() {
        userArrayList = new ArrayList<>();
        sharedPreferences = mContext.getSharedPreferences("com.example.test.nuvoco3", Context.MODE_PRIVATE);

        userArrayList = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("CustomerData", ObjectSerializer.serialize(new ArrayList<String>())));
        String mUserInfo;
        mUserInfo = userArrayList.get(USER_ADDRESS) + "";
        return mUserInfo;
    }

    public String getUserArea() {
        userArrayList = new ArrayList<>();
        sharedPreferences = mContext.getSharedPreferences("com.example.test.nuvoco3", Context.MODE_PRIVATE);

        userArrayList = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("CustomerData", ObjectSerializer.serialize(new ArrayList<String>())));
        String mUserInfo;
        mUserInfo = userArrayList.get(USER_AREA) + "";
        return mUserInfo;
    }

    public String getUserCty() {
        userArrayList = new ArrayList<>();
        sharedPreferences = mContext.getSharedPreferences("com.example.test.nuvoco3", Context.MODE_PRIVATE);

        userArrayList = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("CustomerData", ObjectSerializer.serialize(new ArrayList<String>())));
        String mUserInfo;
        mUserInfo = userArrayList.get(USER_CITY) + "";
        return mUserInfo;
    }

    public String getUserAge() {
        userArrayList = new ArrayList<>();
        sharedPreferences = mContext.getSharedPreferences("com.example.test.nuvoco3", Context.MODE_PRIVATE);

        userArrayList = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("CustomerData", ObjectSerializer.serialize(new ArrayList<String>())));
        String mUserInfo;
        mUserInfo = userArrayList.get(USER_AGE) + "";
        return mUserInfo;
    }

    public String getUserDepartment() {
        userArrayList = new ArrayList<>();
        sharedPreferences = mContext.getSharedPreferences("com.example.test.nuvoco3", Context.MODE_PRIVATE);

        userArrayList = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("CustomerData", ObjectSerializer.serialize(new ArrayList<String>())));
        String mUserInfo;
        mUserInfo = userArrayList.get(USER_DEPARTMENT) + "";
        return mUserInfo;
    }

    public String getUserEmail() {
        userArrayList = new ArrayList<>();
        sharedPreferences = mContext.getSharedPreferences("com.example.test.nuvoco3", Context.MODE_PRIVATE);

        userArrayList = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("CustomerData", ObjectSerializer.serialize(new ArrayList<String>())));
        String mUserInfo;
        mUserInfo = userArrayList.get(USER_EMAIL) + "";
        for (int i = 0; i < 13; i++) {

            Log.i(TAG, "getUserEmail: " + userArrayList.get(i));
        }
        return mUserInfo;
    }

    public String getUserId() {
        userArrayList = new ArrayList<>();
        sharedPreferences = mContext.getSharedPreferences("com.example.test.nuvoco3", Context.MODE_PRIVATE);

        userArrayList = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("CustomerData", ObjectSerializer.serialize(new ArrayList<String>())));
        String mUserInfo;
        mUserInfo = userArrayList.get(USER_ID) + "";
        return mUserInfo;
    }

    public String getUserName() {
        userArrayList = new ArrayList<>();
        sharedPreferences = mContext.getSharedPreferences("com.example.test.nuvoco3", Context.MODE_PRIVATE);

        userArrayList = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("CustomerData", ObjectSerializer.serialize(new ArrayList<String>())));
        String mUserInfo;
        mUserInfo = userArrayList.get(USER_NAME) + "";
        return mUserInfo;
    }

    public String getUserPhone() {
        userArrayList = new ArrayList<>();
        sharedPreferences = mContext.getSharedPreferences("com.example.test.nuvoco3", Context.MODE_PRIVATE);

        userArrayList = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("CustomerData", ObjectSerializer.serialize(new ArrayList<String>())));
        String mUserInfo;
        mUserInfo = userArrayList.get(USER_PHONE_NO) + "";
        return mUserInfo;
    }

    public String getUserStatus() {
        userArrayList = new ArrayList<>();
        sharedPreferences = mContext.getSharedPreferences("com.example.test.nuvoco3", Context.MODE_PRIVATE);

        userArrayList = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("CustomerData", ObjectSerializer.serialize(new ArrayList<String>())));
        String mUserInfo;
        mUserInfo = userArrayList.get(USER_STATUS) + "";
        return mUserInfo;
    }

    public String getUserPin() {
        userArrayList = new ArrayList<>();
        sharedPreferences = mContext.getSharedPreferences("com.example.test.nuvoco3", Context.MODE_PRIVATE);

        userArrayList = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("CustomerData", ObjectSerializer.serialize(new ArrayList<String>())));
        String mUserInfo;
        mUserInfo = userArrayList.get(USER_PIN) + "";
        return mUserInfo;
    }

    public String getUserPassword() {
        userArrayList = new ArrayList<>();
        sharedPreferences = mContext.getSharedPreferences("com.example.test.nuvoco3", Context.MODE_PRIVATE);

        userArrayList = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("CustomerData", ObjectSerializer.serialize(new ArrayList<String>())));
        String mUserInfo;
        mUserInfo = userArrayList.get(USER_PASSWORD) + "";
        return mUserInfo;
    }

    public String getUserToken() {
        userArrayList = new ArrayList<>();
        sharedPreferences = mContext.getSharedPreferences("com.example.test.nuvoco3", Context.MODE_PRIVATE);

        userArrayList = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("CustomerData", ObjectSerializer.serialize(new ArrayList<String>())));
        String mUserInfo;
        mUserInfo = userArrayList.get(USER_TOKEN) + "";
        return mUserInfo;
    }



    public static boolean isEmailValid(String mEmail) {
        return mEmail.contains("@");
    }
}
