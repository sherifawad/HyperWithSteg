package com.example.sherifawad.hyperencryption.Threads;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.sherifawad.hyperencryption.CustomUI.ProgressDialog;

public class AsyncHelper extends AsyncTask<Void, Void, Void> {


    private RelativeLayout relativelayout;
    private Context context;
    private ProgressDialog progressDialog;


    public AsyncHelper(RelativeLayout relativelayout, Context context) {
        super();
        this.relativelayout = relativelayout;
        this.context = context;
        progressDialog = new ProgressDialog(context);

    }

    @Override
    protected Void doInBackground(Void... voids) {
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.ShowProgressDialog();
        LayoutChildren(relativelayout, false);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        LayoutChildren(relativelayout, true);
        progressDialog.DismissProgressDialog();

    }

    private void LayoutChildren(RelativeLayout layout, boolean state) {
        if (layout != null) {
            for (int i = 0; i < layout.getChildCount(); i++) {
                View child = layout.getChildAt(i);
                child.setEnabled(state);
                if (child instanceof ViewGroup) {
                    ViewGroup group = (ViewGroup) child;
                    for (int j = 0; j < group.getChildCount(); j++) {
                        group.getChildAt(j).setEnabled(state);
                    }
                }

            }
        }
    }



}
