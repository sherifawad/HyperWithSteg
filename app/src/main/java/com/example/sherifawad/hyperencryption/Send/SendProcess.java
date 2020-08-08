package com.example.sherifawad.hyperencryption.Send;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.sherifawad.hyperencryption.CrySteg.Send;

import Util.Uty;
import algorithm.Algorithm;
import algorithm.Steganograpgy.Embedding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

//import static Util.Uty.currentDate;
//import static com.example.sherifawad.hyperencryption.CrySteg.CryStegActivity.contextCrySteg;
//import static com.example.sherifawad.hyperencryption.CrySteg.CryStegActivity.getCryStegContext;


public class SendProcess extends Send {


    private final String TAG = "Send_Process";


    public static void send(Context thiscontext, boolean checked, String image_path, String[] srcFiles, String outDir){

        String time = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());

        String tempDir = thiscontext.getCacheDir().getPath();

        HashMap<String, Object>  hmap = null;
        String serialized = null;
        try {
            hmap = Algorithm.signcryption(srcFiles, tempDir);
            serialized = Uty.serialize(hmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(hmap == null)
            return;

        if (checked){

            Bitmap bmpImageCarrier = BitmapFactory.decodeFile(image_path);

            Bitmap bmpImage = Embedding.embedSecretText(serialized, bmpImageCarrier);

            OutputStream fOut = null;
            try {
                File picFile = new File(outDir, "outfile_" + time + ".png");
                fOut = new FileOutputStream(picFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return;
            }
            bmpImage.compress(Bitmap.CompressFormat.PNG, 100, fOut);

            try {
                fOut.flush();
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            bmpImage.recycle();
        }else {
            try {
                File textFile = new File(outDir, "outfile_" + time + ".txt");
                Uty.WriteMapToFile(hmap, textFile);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

        }

//        if (srcFiles == null) {
//            NotifyUI.showToast(getCryStegContext(), "Please select secret messages");
//            return;
//        }
//        if(outDir == null) {
//            NotifyUI.showToast(getCryStegContext(), "Please select output directory");
//
//            return;
//        }
//        if(checkBox.isChecked() && image_path == null){
//            NotifyUI.showToast(getCryStegContext(), "You Select Steganography ");
//            NotifyUI.showToast(getCryStegContext(), "Please select Carrier Image");
//            return;
//        }

//        else{
//            String tempDir = null;
//            tempDir = getCryStegContext().getCacheDir().getPath();
//
//            HashMap<String, byte[]> hmap = null;
//            try {
//                hmap = Algorithm.signcryption(srcFiles, tempDir);
//                System.out.println(hmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            NotifyUI.showLog(TAG, "hashmap created");
////            Gson gson = new Gson();
////            String serialized = gson.toJson(hmap);
////            NotifyUI.showLog(TAG, "gson created");
////            NotifyUI.showLog(TAG, "gson " + serialized);
//
//            if(outDir == null) {
//                NotifyUI.showToast(getCryStegContext(), "Please select output directory");
//
//                return;
//            }
//            else if (checkBox.isChecked() == true){
//                if(image_path == null){
//                    NotifyUI.showToast(getCryStegContext(), "You Select Steganography ");
//                    NotifyUI.showToast(getCryStegContext(), "Please select Carrier Image");
//                    return;
//                }
//                else {
//
//                    String serialized = null;
//                    try {
//                        serialized = Uty.serialize(hmap);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
////                    snd_execute_btn.setProgress(50);
//                    File outFile = new File(outDir, "outfile_" + time + ".png");
//                    NotifyUI.showLog(TAG, "Start Steganogrphy");
//
//                    Bitmap bmpImage = Embedding.embedSecretText(serialized, bmpImageCarrier);
//
//                    NotifyUI.showLog(TAG, "Finished Steganogrphy");
//
//                    OutputStream fOut = null;
//                    NotifyUI.showLog(TAG, "create file Steganogrphy");
//
//                    File file = new File(outDir, "outfile_" + time + ".png");
//                    try {
//                        fOut = new FileOutputStream(file);
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                    NotifyUI.showLog(TAG, "Start CompressFormat.PNG");
//                    bmpImage.compress(Bitmap.CompressFormat.PNG, 100, fOut);
//                    NotifyUI.showLog(TAG, "finish CompressFormat.PNG");
//
//                    try {
//                        fOut.flush();
//                        fOut.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    bmpImage.recycle();
//                    NotifyUI.showLog(TAG, "Secret encoded successfully and image saved");
//
//                }
//
//            }
//            else {
//
//                NotifyUI.showLog(TAG, "Creat file" );
//                System.out.println();
//                Uty.WriteMapToFile(hmap, outDir);
//
//            }
//            NotifyUI.showToast(getCryStegContext(), "Secret encoded successfully and text file saved");
//
//        }

    }
}
