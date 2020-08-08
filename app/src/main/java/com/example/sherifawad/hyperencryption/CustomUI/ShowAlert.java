package com.example.sherifawad.hyperencryption.CustomUI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;

public class ShowAlert {

    public static void showAlert(Activity activity, String message) {

        TextView title = new TextView(activity);
        // You Can Customise your Title here
        title.setText("Error");
        //title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);


        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCustomTitle(title);
        //builder.setIcon(R.drawable.alert_36);

        builder.setMessage(message);

        builder.setCancelable(false);
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();

            }

        });

        AlertDialog alert = builder.show();
        TextView messageText = (TextView)alert.findViewById(android.R.id.message);
        messageText.setGravity(Gravity.CENTER);
        messageText.setTextColor(Color.RED);

    }
}

