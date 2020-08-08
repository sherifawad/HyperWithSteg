package com.example.sherifawad.hyperencryption.CrySteg;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.sherifawad.hyperencryption.CustomUI.ShowAlert;
import com.example.sherifawad.hyperencryption.R;
import com.example.sherifawad.hyperencryption.Receive.ReceiveProcess;
import com.example.sherifawad.hyperencryption.Threads.AsyncHelper;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;

//import static com.example.sherifawad.hyperencryption.CrySteg.CryStegActivity.getCryStegContext;

public class Recive extends Fragment {

    private FilePickerDialog dialog;

    private Context thiscontext;

    private static final String TAG = "Receive Fragment";

    private String srcFile;
    private String outDir;
    private Button rcv_execute_btn;

    private RelativeLayout layout;

    @Nullable
    @Override
    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {


        final View view =  inflater.inflate(R.layout.receive_frag, container, false);
//        rcvParEXist((RelativeLayout) view.findViewById(R.id.rcv_relativeLayout), false);
        thiscontext = container.getContext();

        HandleClick handleClick = new HandleClick();
        view.findViewById(R.id.rcv_mesg_btn).setOnClickListener(handleClick);
        view.findViewById(R.id.rcv_out_btn).setOnClickListener(handleClick);
        rcv_execute_btn = (Button) view.findViewById(R.id.rcv_execute_btn);
        rcv_execute_btn.setOnClickListener(handleClick);
//        view.findViewById(R.id.rcv_execute_btn).setOnClickListener(handleClick);

        layout = view.findViewById(R.id.rcv_relativeLayout);

        return view;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case FilePickerDialog.EXTERNAL_READ_PERMISSION_GRANT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(dialog!=null)
                    {   //Show dialog if the read permission has been granted.
                        dialog.show();
                    }
                }
                else {
                    //Permission has not been granted. Notify the user.
                    Toast.makeText(thiscontext,"Permission is Required for getting list of files",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private class HandleClick implements View.OnClickListener {
        @SuppressLint({"LongLogTag", "StaticFieldLeak"})
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.rcv_mesg_btn:
                    DialogProperties rcv_mesg = new DialogProperties();
                    rcv_mesg.selection_mode = DialogConfigs.SINGLE_MODE;
                    rcv_mesg.selection_type = DialogConfigs.FILE_SELECT;
                    rcv_mesg.root = new File(DialogConfigs.DEFAULT_DIR);
                    rcv_mesg.extensions = new String[]{"png", "txt"};
                    dialog = new FilePickerDialog(thiscontext, rcv_mesg);
                    dialog.setTitle("Select Received Message");
                    dialog.show();
                    dialog.setDialogSelectionListener(new DialogSelectionListener() {

                        @SuppressLint("LongLogTag")
                        @Override
                        public void onSelectedFilePaths(String[] files) {
                            Log.d(TAG, "onSelectedFilePaths: " + files);
                            srcFile = files[0];
//                            bmpImage = BitmapFactory.decodeFile(srcFile);

                            //files is the array of the paths of files selected by the Application User.
                        }
                    });
                    break;

                    case R.id.rcv_out_btn:
                    DialogProperties rcv_out = new DialogProperties();
                    rcv_out.selection_mode = DialogConfigs.SINGLE_MODE;
                    rcv_out.selection_type = DialogConfigs.DIR_SELECT;
                    rcv_out.root = new File(DialogConfigs.DEFAULT_DIR);
                    rcv_out.extensions = null;
                    dialog = new FilePickerDialog(thiscontext, rcv_out);
                    dialog.setTitle("Select Received Message");
                    dialog.show();
                    dialog.setDialogSelectionListener(new DialogSelectionListener() {

                        @SuppressLint("LongLogTag")
                        @Override
                        public void onSelectedFilePaths(String[] files) {
                            Log.d(TAG, "onSelectedFilePaths: " + files);
                            outDir = files[0];
                            //files is the array of the paths of files selected by the Application User.
                        }
                    });
                    break;


                case R.id.rcv_execute_btn:
                    Log.d(TAG, "onSelectedFilePaths: " + "Clicked");

                    if (srcFile == null) {
                        ShowAlert.showAlert(getActivity(), "PSelect the received Message");
                        return;
                    }

                    if (outDir == null) {
                        ShowAlert.showAlert(getActivity(), "Please select output directory");
                        return;
                    }

                    new AsyncHelper(layout, thiscontext) {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            if (Build.VERSION.SDK_INT >= 23) {
                                int permissionCheck = ContextCompat.checkSelfPermission(thiscontext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                }
                            }
                            ReceiveProcess.receive(thiscontext, srcFile, outDir);
                            return super.doInBackground(voids);
                        }
                    }.execute();


//                    rcvParEXist((RelativeLayout) view.findViewById(R.id.rcv_relativeLayout), false);
////                    rcv_execute_btn.startAnimation();
////                    new ReceiveExecuteTask().execute();
//                    ReceiveProcess.receive();
//                    rcvParEXist((RelativeLayout) view.findViewById(R.id.rcv_relativeLayout), true);
//
//                    break;
            }
        }
    }

    private void rcvParEXist(RelativeLayout layout, boolean enable) {
        if (layout != null) {
            for (int i = 0; i < layout.getChildCount(); i++) {
                View child = layout.getChildAt(i);
                child.setEnabled(enable);
                if (child instanceof ViewGroup) {
                    ViewGroup group = (ViewGroup) child;
                    for (int j = 0; j < group.getChildCount(); j++) {
                        group.getChildAt(j).setEnabled(enable);
                    }
                }
            }
        }else{
            rcv_execute_btn.setEnabled(enable);
        }
    }
}
