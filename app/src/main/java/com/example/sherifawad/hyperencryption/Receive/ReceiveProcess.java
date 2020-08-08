package com.example.sherifawad.hyperencryption.Receive;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.sherifawad.hyperencryption.CrySteg.Recive;

import Util.Uty;
import algorithm.Algorithm;

import Util.NotifyUI;
import algorithm.Steganograpgy.Extracting;
import algorithm.Steganograpgy.HelperMethods;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

//import static com.example.sherifawad.hyperencryption.CrySteg.CryStegActivity.getCryStegContext;

//public class ReceiveProcess extends Recive {
public class ReceiveProcess {


    private static final String TAG = "Receive Process";

    public static void receive(Context thiscontext, String srcFile, String outDir) {
        String time = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
        HashMap<String, Object> newMap = null;
//        newMap = Uty.fileToMap(srcFile);

        if (srcFile.endsWith(".png") == true) {
            Bitmap bmpImage = BitmapFactory.decodeFile(srcFile);
            String message = Extracting.extractSecretMessage(bmpImage);
            String bits = (String) message;
            byte[] messageBytes = HelperMethods.bitsStreamToByteArray(bits);
            String serialized = new String(messageBytes);
            newMap = (HashMap<String, Object>) Uty.deserialize(serialized);
        } else {
            newMap = Uty.fileToMap(srcFile);
        }

        byte[] plainText = new byte[0];
        try {
            plainText = Algorithm.unSigncryption(newMap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String tempDir_received = thiscontext.getCacheDir().getPath();
        String deOutput = tempDir_received + "/plain_" + time + ".zip";
        if (plainText != null) {
            Uty.writeByteToFile(tempDir_received + "/plain_" + time + ".zip", plainText);
            Uty.unZipIt(deOutput, outDir);
            NotifyUI.showToast(thiscontext, "Success");
        } else {
            NotifyUI.showToast(thiscontext, "Wrong");
            System.out.println(" Empty Message");
        }
    }
}
