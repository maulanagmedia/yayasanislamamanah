package co.id.gmedia.coremodul;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "GmediaPerkasaApp";

    // All Shared Preferences Keys
    public static final String TAG_USERNAME = "username";
    public static final String TAG_PASSWORD= "password";
    public static final String TAG_NIK = "nik";
    public static final String TAG_NAMA= "nama";
    public static final String TAG_LEVEL= "level";
    public static final String TAG_LEVELNAMA= "level_nama";

    // Constructor
    public SessionManager(Context context){
        this.context = context;
        pref = this.context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String username, String nik, String nama, String level, String levelNama){

        editor.putString(TAG_USERNAME, username);

        editor.putString(TAG_NIK, nik);

        editor.putString(TAG_NAMA, nama);

        editor.putString(TAG_LEVEL, level);

        editor.putString(TAG_LEVELNAMA, levelNama);

        // commit changes
        editor.commit();
    }

    public void savePassword(String password){

        editor.putString(TAG_PASSWORD, password);

        editor.commit();
    }

    /**
     * Get stored session data
     * */

    public String getUserInfo(String key){
        return pref.getString(key, "");
    }

    public String getUsername(){
        return pref.getString(TAG_USERNAME, "");
    }

    public String getNik(){
        return pref.getString(TAG_NIK, "");
    }

    public String getNama(){
        return pref.getString(TAG_NAMA, "");
    }

    public String getLevel(){
        return pref.getString(TAG_LEVEL, "");
    }

    public String getLevelNama(){
        return pref.getString(TAG_LEVELNAMA, "");
    }

    /**
     * Clear session details
     * */
    public void logoutUser(Intent logoutIntent){

        // Clearing all data from Shared Preferences
        try {
            editor.clear();
            editor.commit();
        }catch (Exception e){
            e.printStackTrace();
        }

        logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(logoutIntent);
        ((Activity)context).finish();
        ((Activity)context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        if(getUsername().isEmpty()){
            return false;
        }else{
            return true;
        }
        /*return pref.getBoolean(IS_LOGIN, false);*/
    }
}
