package Util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;


public class NotifyUI {
    private NotifyUI() {}

    private static final Handler s_toastHandler = new Handler(Looper.getMainLooper());


    public static void showToast(final Context context, final String toastMsg) {
        s_toastHandler.post(new Runnable() {
            public void run() {
                try {
                    Toast.makeText(context,
                            toastMsg,
                            Toast.LENGTH_LONG).show();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void showLog(final String tag, final Object message) {
        s_toastHandler.post(new Runnable() {
            public void run() {
                try {
                    Log.d(tag, String.valueOf(message));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
