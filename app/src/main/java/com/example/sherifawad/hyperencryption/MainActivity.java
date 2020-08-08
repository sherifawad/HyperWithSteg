package com.example.sherifawad.hyperencryption;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.sherifawad.hyperencryption.PIN.CreatePIN;
import com.example.sherifawad.hyperencryption.PIN.EnterPIN;
import com.example.sherifawad.hyperencryption.Password.PasswordActivity;

import static Util.Uty.encryptDecrypt;

public class MainActivity extends AppCompatActivity implements CreatePIN.Callback {
    private static SharedPreferences preferences;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity.context = getApplicationContext();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                public void run() {
                                    if (preferences.getString("PIN", "").equals("")) {
//                                        Intent intentCoreActivity = new Intent(getApplicationContext(), PasswordActivity.class);
//                                        startActivity(intentCoreActivity);
                                        CreatePIN dialog = new CreatePIN();
                                        dialog.show(ft, dialog.TAG);


                                    } else {
                                        EnterPIN dialog = new EnterPIN();
                                        dialog.show(ft, dialog.TAG);
                                    }
                                }
                            }, 5
        );
    }

    public static Context getAppContext() {
        return MainActivity.context;
    }

    public static void setPreference(String Name, String value) {

        preferences.edit().putString(Name, encryptDecrypt(value, preferences.getString("PIN", "")) ).apply();
    }
    public static String getPreference(String Name) {

        if (preferences.contains(Name)) {
            return encryptDecrypt(preferences.getString(Name,""), preferences.getString("PIN", ""));
        } else {
            return null;
        }
    }

    @Override
    public void setData(String PIN) {
        preferences.edit().putString("PIN", PIN).apply();
        System.out.println("PIN is" + preferences.getString("PIN", ""));
        Intent intentCoreActivity = new Intent(getApplicationContext(), PasswordActivity.class);
        startActivity(intentCoreActivity);
    }
}
