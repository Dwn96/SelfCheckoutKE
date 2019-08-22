package com.mdg.selfcheckoutke;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;

public class User {
    // Shared Preferences
    private SharedPreferences pref;

    // Editor for Shared preferences
    private SharedPreferences.Editor editor;

    // Context
    private Context _context;

    // Shared pref mode
    private static final int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "selfcheckoutke_pref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    //user id
    private static final String ID = "_id";

    //username
    private static final String KEY_NAME = "username";


    //Email Address
    private static final String KEY_EMAIL = "email";
    //token
    private static final String KEY_TOKEN = "token";
    //current order
    private static final String KEY_CURR_ORDER = "current order";
    private static final String KEY_CURR_ORDER_ID = "order _id";
    private static final String KEY_CURR_TOT_PRICE = "current total";

    // Constructor
    public User(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    }

    /**
     * Create login session
     */
    public void loginUser(String id, String username, String email, String token) {
        editor = pref.edit();
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        //store id in pref
        editor.putString(ID, id);

        // Storing name in pref
        editor.putString(KEY_NAME, username);

        //store email
        editor.putString(KEY_EMAIL, email);

        //store login token
        editor.putString(KEY_TOKEN, token);

        // commit changes
        editor.apply();
    }
    public boolean isNewOrder(){
        return pref.getBoolean(KEY_CURR_ORDER, true);
    }
    public void createOrder(){
        editor = pref.edit();
        editor.putBoolean(KEY_CURR_ORDER, false);
        editor.putInt(KEY_CURR_TOT_PRICE, 0);
        editor.apply();
    }
    public void addOrderId(String id){
        editor = pref.edit();
        editor.putString(KEY_CURR_ORDER_ID, id);
        editor.apply();
    }
    public void addTotal(int total){
        editor = pref.edit();
        editor.putInt(KEY_CURR_TOT_PRICE, total);
        editor.apply();
    }
    public int getCURRTOT() {return pref.getInt(KEY_CURR_TOT_PRICE, 0);}
    public void completeOrder(){
        editor = pref.edit();
        editor.putBoolean(KEY_CURR_ORDER, true);
        editor.remove(KEY_CURR_ORDER_ID);
        editor.apply();
    }
    public String getCurrId(){ return pref.getString(KEY_CURR_ORDER_ID, null);}


    /**
     * Get stored user data
     */
    public String getID() {


        return pref.getString(ID, null);
    }

    public String getUsername() {
        return pref.getString(KEY_NAME, null);
    }

    public String getemail() {
        return pref.getString(KEY_EMAIL, null);
    }

    public String getToken() {
        return pref.getString(KEY_TOKEN, null);
    }

    /**
     * Clear session details
     * logs out user
     */
    public void logoutUser() {
        editor.clear();
        editor.apply();
    }

    /**
     * Quick check for login
     **/
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

}

