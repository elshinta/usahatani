package com.example.usahatanipadi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import java.util.HashMap;

public class UserSessionManager {

        // Shared Preferences reference
        SharedPreferences pref;

        // Editor reference for Shared preferences
        SharedPreferences.Editor editor;

        // Context
        Context _context;

        // Shared pref mode
        int PRIVATE_MODE = 0;

        // Sharedpref file name
        private static final String PREFER_NAME = "AndroidExamplePref";

        // All Shared Preferences Keys
        private static final String IS_USER_LOGIN = "IsUserLoggedIn";

        // Email address (make variable public to access from outside)
        public static final String KEY_NAMA = "nama_pengguna";
    // Email address (make variable public to access from outside)
    public static final String KEY_ID = "id";

        // Constructor
        public UserSessionManager(Context context){
            this._context = context;
            pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
            editor = pref.edit();
        }

        //Create login session
        public void createUserLoginSession(String nama_pengguna, String id){
            // Storing login value as TRUE
            editor.putBoolean(IS_USER_LOGIN, true);

            // Storing name in pref
            editor.putString(KEY_NAMA, nama_pengguna);
            // Storing name in pref
            editor.putString(KEY_ID, id);

            // commit changes
            editor.commit();
        }

    public void createUserRegisterSession(String nama_pengguna, String id){

        // Storing name in pref
        editor.putString(KEY_NAMA, nama_pengguna);
        // Storing name in pref
        editor.putString(KEY_ID, id);

        // commit changes
        editor.commit();
    }

        /**
         * Check login method will check user login status
         * If false it will redirect user to login page
         * Else do anything
         * */
        public boolean checkLogin(){
            // Check login status
            if(!this.isUserLoggedIn()){

                // user is not logged in redirect him to Login Activity
                Intent i = new Intent(_context, login.class);

                // Closing all the Activities from stack
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                // Add new Flag to start new Activity
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                // Staring Login Activity
                _context.startActivity(i);

                return true;
            }
            return false;
        }



        /**
         * Get stored session data
         * */
        public HashMap<String, String> getUserDetails(){

            //Use hashmap to store user credentials
            HashMap<String, String> user = new HashMap<String, String>();

            // user email id
            user.put(KEY_NAMA, pref.getString(KEY_NAMA, null));
            user.put(KEY_ID, pref.getString(KEY_ID, null));

            // return user
            return user;
        }

        /**
         * Clear session details
         * */
        public void logoutUser(){

            // Clearing all user data from Shared Preferences
            editor.clear();
            editor.commit();

            // After logout redirect user to Login Activity
            Intent i = new Intent(_context, login.class);

            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }


        // Check for login
        public boolean isUserLoggedIn(){
            return pref.getBoolean(IS_USER_LOGIN, false);
        }
    }
