package com.suffhillrabbitfarm.rabbitinfosystem.data;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.suffhillrabbitfarm.rabbitinfosystem.models.UserModel;

public class LoginData {
    Context context;

    public LoginData(Context context) {
        this.context = context;
    }

    public void setAuth(UserModel userModel) {
        SharedPreferences sh = context.getSharedPreferences("loginFile", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sh.edit();
        myEdit.putString("userName", userModel.getUserName());
        myEdit.putString("email", userModel.getEmail());
        myEdit.putString("phoneNumber", userModel.getPhoneNumber());
        myEdit.putString("accountType", userModel.getAccountType());
        myEdit.apply();
    }

    public UserModel getLoginType() {
        UserModel userModel = new UserModel();
        SharedPreferences sh = context.getSharedPreferences("loginFile", MODE_PRIVATE);
        userModel.setUserName(sh.getString("userName", ""));
        userModel.setEmail(sh.getString("email", ""));
        userModel.setPhoneNumber(sh.getString("phoneNumber", ""));
        userModel.setAccountType(sh.getString("accountType", ""));
        return userModel;
    }

    // New method to store user UID (since we no longer have Firebase Auth)
    public void setUserUid(String uid) {
        SharedPreferences sh = context.getSharedPreferences("loginFile", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sh.edit();
        myEdit.putString("userUid", uid);
        myEdit.apply();
    }

    // New method to get user UID
    public String getUserUid() {
        SharedPreferences sh = context.getSharedPreferences("loginFile", MODE_PRIVATE);
        return sh.getString("userUid", "");
    }

    // Method to check if user is logged in
    public boolean isLoggedIn() {
        SharedPreferences sh = context.getSharedPreferences("loginFile", MODE_PRIVATE);
        String uid = sh.getString("userUid", "");
        return !uid.isEmpty();
    }

    // Method to clear login data (logout)
    public void clearLoginData() {
        SharedPreferences sh = context.getSharedPreferences("loginFile", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sh.edit();
        myEdit.clear();
        myEdit.apply();
    }
}