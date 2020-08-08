package com.example.sherifawad.hyperencryption.SecuredPreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class SecurityManager {

    private static final String AndroidKeyStore = "AndroidKeyStore";
    private static final String AES_MODE = "AES/GCM/NoPadding";

    private static SecurityManager sInstance;
    private SecretKey mKey;
    private Context mContext;


    private SecurityManager(Context context) {

        try {

            String androidId = Settings
                    .Secure
                    .getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

            mContext = context.getApplicationContext();

            KeyStore keyStore = KeyStore.getInstance(AndroidKeyStore);
            keyStore.load(null);

            if (!keyStore.containsAlias(androidId)) {
                KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, AndroidKeyStore);
                keyGenerator.init(
                        new KeyGenParameterSpec.Builder(androidId,
                                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                                .setRandomizedEncryptionRequired(false)
                                .build());
                mKey  =  keyGenerator.generateKey();
            }
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException |
                NoSuchProviderException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    public static SecurityManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SecurityManager(context);
        }
        return sInstance;
    }


    public String encryptString(String stringToEncrypt) {
        String output = stringToEncrypt;

        try {
            byte[] clearText = stringToEncrypt.getBytes("UTF8");
            Cipher cipher = Cipher.getInstance(AES_MODE);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, cipher.getIV());
            cipher.init(Cipher.ENCRYPT_MODE, mKey, gcmParameterSpec);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("iv", Base64.encodeToString(cipher.getIV(), Base64.NO_WRAP));
            editor.apply();
            output = new String(Base64.encode(cipher.doFinal(clearText),
                    Base64.NO_WRAP), "UTF8");

        } catch (UnsupportedEncodingException | NoSuchAlgorithmException |
                NoSuchPaddingException | InvalidKeyException |
                IllegalBlockSizeException | InvalidAlgorithmParameterException | BadPaddingException e) {
            e.printStackTrace();
        }
        return output;
    }

    public String decryptString(String stringToDecrypt) {
        String output = stringToDecrypt;
        try {

            byte[] encryptedBytes = Base64.decode(stringToDecrypt, Base64.DEFAULT);
            Cipher cipher = Cipher.getInstance(AES_MODE);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            byte[] iv = Base64.decode(prefs.getString("iv", ""), Base64.NO_WRAP);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, mKey, gcmParameterSpec);
            output = new String(cipher.doFinal(encryptedBytes), "UTF8");

        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException | UnsupportedEncodingException
                | IllegalBlockSizeException | BadPaddingException
                | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return output;
    }

}
