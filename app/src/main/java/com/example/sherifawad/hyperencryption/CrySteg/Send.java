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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.example.sherifawad.hyperencryption.CustomUI.ShowAlert;
import com.example.sherifawad.hyperencryption.R;
import com.example.sherifawad.hyperencryption.Send.SendProcess;
import com.example.sherifawad.hyperencryption.Threads.AsyncHelper;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;

import Util.NotifyUI;

//import static com.example.sherifawad.hyperencryption.CrySteg.CryStegActivity.getCryStegContext;

public class Send extends Fragment {

    FilePickerDialog dialog;

    private Context thiscontext;

    private Button snd_execute_btn;

    private final String TAG = "Send_Fragment";

    private String[] srcFiles;
    private String outDir;
    private  CheckBox checkBox;
    private  String image_path;

    private RelativeLayout layout;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {


        final View view =   inflater.inflate(R.layout.send_frag, container, false);
        thiscontext = container.getContext();
//        sendParEXist((RelativeLayout) view.findViewById(R.id.snd_relativeLayout), false);
        HandleClick handleClick = new HandleClick();
        view.findViewById(R.id.snd_message_btn).setOnClickListener(handleClick);

        Button snd_image_btn = view.findViewById(R.id.snd_image_btn);
        snd_image_btn.setOnClickListener(handleClick);
        checkBox = view.findViewById(R.id.checkBox);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> snd_image_btn.setEnabled(isChecked));


        view.findViewById(R.id.snd_out_btn).setOnClickListener(handleClick);
        snd_execute_btn = (Button) view.findViewById(R.id.snd_execute_btn);
        snd_execute_btn.setOnClickListener(handleClick);

        layout = view.findViewById(R.id.snd_relativeLayout);

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
                    NotifyUI.showToast(thiscontext,"Permission is Required for getting list of files");
                }
            }
        }
    }

    private class HandleClick implements View.OnClickListener{
        @SuppressLint("StaticFieldLeak")
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.snd_message_btn:
                    DialogProperties snd_message = new DialogProperties();
                    snd_message.selection_mode = DialogConfigs.MULTI_MODE;
                    snd_message.selection_type = DialogConfigs.FILE_SELECT;
                    snd_message.root = new File(DialogConfigs.DEFAULT_DIR);
                    snd_message.extensions = null;
                    dialog = new FilePickerDialog(thiscontext, snd_message);
                    dialog.setTitle("Select Secret  Messages");
                    dialog.show();
                    dialog.setDialogSelectionListener(new DialogSelectionListener() {

                        @Override
                        public void onSelectedFilePaths(String[] files) {
                            NotifyUI.showLog(TAG, "onSelectedFilePaths: " + files);
                            srcFiles = files;
                            //files is the array of the paths of files selected by the Application User.
                        }
                    });
                    break;


                case R.id.snd_image_btn:
                    DialogProperties snd_image = new DialogProperties();
                    snd_image.selection_mode = DialogConfigs.SINGLE_MODE;
                    snd_image.selection_type = DialogConfigs.FILE_SELECT;
                    snd_image.root = new File(DialogConfigs.DEFAULT_DIR);
                    snd_image.extensions = new String[]{"png", "jpg", "jpeg"};
                    dialog = new FilePickerDialog(thiscontext, snd_image);
                    dialog.setTitle("Select Carrier Image");
                    dialog.show();
                    dialog.setDialogSelectionListener(new DialogSelectionListener() {

                        @Override
                        public void onSelectedFilePaths(String[] files) {
                            image_path = files[0];

                            NotifyUI.showLog(TAG, "onSelectedFilePaths: " + image_path);
                        }
                    });
                    break;



                case R.id.snd_out_btn:
                    DialogProperties snd_out = new DialogProperties();
                    snd_out.selection_mode = DialogConfigs.SINGLE_MODE;
                    snd_out.selection_type = DialogConfigs.DIR_SELECT;
                    snd_out.root = new File(DialogConfigs.DEFAULT_DIR);
                    snd_out.extensions = null;
                    dialog = new FilePickerDialog(thiscontext, snd_out);
                    dialog.setTitle("Select Output Directory");
                    dialog.show();
                    dialog.setDialogSelectionListener(new DialogSelectionListener() {

                        @Override
                        public void onSelectedFilePaths(String[] files) {
                            outDir = files[0];
                            NotifyUI.showLog(TAG, "onSelectedFilePaths: " + outDir);
                            //files is the array of the paths of files selected by the Application User.
                        }
                    });
                    break;


                case R.id.snd_execute_btn:

                    if (srcFiles == null) {
                        ShowAlert.showAlert(getActivity(), "Please select secret messages");
                        return;
                    }
                    if(outDir == null) {
                        ShowAlert.showAlert(getActivity(), "Please select output directory");
                        return;
                    }
                    if(checkBox.isChecked() && image_path == null){
                        ShowAlert.showAlert(getActivity(), "You Select Steganography" + "\n" + "Please select Carrier Image");
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
                            SendProcess.send(thiscontext, checkBox.isChecked(), image_path, srcFiles, outDir);

                            return super.doInBackground(voids);
                        }
                    }.execute();


//                    ExampleTask task = new ExampleTask(new ExampleTask.TaskListener() {
//                        @Override
//                        public void onFinished(String result) {
//                            // Do Something after the task has finished
//                        }
//                    });
//
//                    task.execute();
//
//                    sendParEXist((RelativeLayout) view.findViewById(R.id.snd_relativeLayout), false);
//                    NotifyUI.showLog(TAG, "onSelectedFilePaths: " + "Clicked");
//                    SendProcess.send();
//                    sendParEXist((RelativeLayout) view.findViewById(R.id.snd_relativeLayout), true);
//
//                    break;
            }
        }
    }


    private void sendParEXist(RelativeLayout layout, boolean enable) {
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
            snd_execute_btn.setEnabled(enable);
        }
    }
}
