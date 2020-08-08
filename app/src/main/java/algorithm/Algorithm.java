package algorithm;

import Util.Uty;

import algorithm.ecc.ECPoint;
import connection.ClientServer;
import parameters.Client;
import parameters.PrivateKey;
import parameters.PublicKey;
import parameters.publicParameters;


import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;

import static Util.Uty.byteToMap;
import static Util.Uty.currentDate;
import static Util.Uty.mapTOByte;
import static algorithm.encryption.EncUtil.readBytes;

//import static androidx.constraintlayout.Constraints.TAG;
import static com.example.sherifawad.hyperencryption.MainActivity.getPreference;


public class Algorithm {


//    private static ClientServer bankClient;
    private static publicParameters publicParameter;

    public Algorithm(ClientServer bankClient) {
//        this.bankClient = bankClient;

                publicParameter = (publicParameters) Uty.deserialize(getPreference("publicParameter"));
        System.out.println("publicParameter is " + publicParameter);
    }

    public static HashMap<String, Object> signcryption(String[] srcFiles, String tempDir) throws IOException {
        publicParameter = (publicParameters) Uty.deserialize(getPreference("publicParameter"));

//        System.out.println("publicParameters.getOrder() " + bankClient.getPublicParameter().getOrder());
        BigInteger r_c ;
//        do {
//            r_c = Uty.randomBig(bankClient.getPublicParameter().getOrder());
//        } while (r_c == null);
//        System.out.println("R_C " + r_c);
        r_c = new BigInteger("907537726282672001737046682329929935372443483351");
//        ECPoint z_c = bankClient.getPublicParameter().getCurve().multiply(bankClient.getPublicParameter().getBasePoint(), r_c);
        ECPoint z_c = publicParameter.getCurve().multiply(publicParameter.getBasePoint(), r_c);
        ECPoint K_1 = null;
//        K_1 = bankClient.getPublicParameter().getCurve().multiply (((Client)(Uty.deserialize(getPreference("client")))).getPointOfBankPartialKey(), r_c);
//        K_1 = publicParameter.getCurve().multiply (((Client)(Uty.deserialize(getPreference("client")))).getPointOfBankPartialKey(), r_c);
        K_1 = publicParameter.getCurve().multiply ((ECPoint)Uty.deserialize(getPreference("PointOfBankPartialKey")), r_c);
        System.out.println("K_1 is " + K_1);
        byte[] Key = K_1.x.toByteArray();

        HashMap<String,Object> AccountHshMap = new HashMap<>();
        AccountHshMap.put("IDC", ((Client)(Uty.deserialize(getPreference("client")))).getClientID());
        AccountHshMap.put("t", currentDate());
        AccountHshMap.put("acc", ((Client)(Uty.deserialize(getPreference("client")))).getAccountNumber());
        byte[] cipherAccount = Uty.xorWithKey(mapTOByte(AccountHshMap), K_1.y.toByteArray());

        String pathFile = tempDir + "/Source.zip";
        Uty.zip(pathFile, srcFiles);

        byte[] cipherText = new byte[0];
        try {
//            cipherText = AESencryption.crypt(readBytes(new File(pathFile)), Key);
            cipherText = Uty.xorWithKey(readBytes(new File(pathFile)), Key);

        } catch (Exception e) {
            e.printStackTrace();
        }

//        byte[] IDc = new byte[0];
//        IDc = Uty.xorWithKey(((Client)(Uty.deserialize(getPreference("client")))).getClientID().getBytes(), K_1.y.toByteArray());


        byte[] h_c_data = Uty.byteConcatenate(Arrays.asList(cipherAccount,
                cipherText,
                ((Client)(Uty.deserialize(getPreference("client")))).getClientID().getBytes(),
                z_c.toString().getBytes(),
//                ((Client)(Uty.deserialize(getPreference("client")))).getPublicKey().getXCoordinates().toString().getBytes(),
                ((PublicKey)Uty.deserialize(getPreference("fullPublicKey"))).getXCoordinates().toString().getBytes(),
                publicParameter.getBankPublicKey().getXCoordinates().toString().getBytes(),
                (publicParameter.getKgcPublic().toString().getBytes())));
//                (bankClient.getPublicParameter().getKgcPublic().toString().getBytes())));

        BigInteger h_c = Uty.bytesHash(h_c_data);

//        BigInteger S = (r_c.subtract(h_c.multiply(((Client)(Uty.deserialize(getPreference("client")))).getPrivateKey().getXCoordinates().add
//                (((Client)(Uty.deserialize(getPreference("client")))).getPrivateKey().getYCoordinate())))).mod
//                (publicParameter.getOrder());
        BigInteger S = (r_c.subtract(h_c.multiply(((PrivateKey)Uty.deserialize(getPreference("fullPrivateKey"))).getXCoordinates()
                .add(((PrivateKey)Uty.deserialize(getPreference("fullPrivateKey"))).getYCoordinate()))))
                .mod(publicParameter.getOrder());
//                (bankClient.getPublicParameter().getOrder());

//        System.out.println("S " + S);

//        ECPoint R = bankClient.getPublicParameter().getCurve().multiply(bankClient.getPublicParameter().getCurve().add
        ECPoint R = publicParameter.getCurve().multiply(publicParameter.getCurve()
                .add(((PublicKey)Uty.deserialize(getPreference("fullPublicKey"))).getXCoordinates(),
//                (((Client)(Uty.deserialize(getPreference("client")))).getPublicKey().getXCoordinates(),
//                        ((Client)(Uty.deserialize(getPreference("client")))).getPointOfClientPartialKey()), h_c);
                        ((ECPoint)Uty.deserialize(getPreference("PointOfClientPartialKey")))), h_c);


        System.out.println("cipher is " + Arrays.toString(cipherText));
        System.out.println("IDcBc is " +  ((Client)(Uty.deserialize(getPreference("client")))).getClientID());
        System.out.println("z_sb is " + z_c);
        System.out.println("clientInfo.getPublicKey().getXCoordinates() is " + (((PublicKey)Uty.deserialize(getPreference("fullPublicKey"))).getXCoordinates()));
        System.out.println("bankClient.getBankParameter().getPublicKey().getXCoordinates() is " + publicParameter.getBankPublicKey().getXCoordinates());
        System.out.println("bankClient.getPublicParameter().getKgcPublic() is " + (publicParameter.getKgcPublic()));
        System.out.println("h_b is " + h_c);
        System.out.println("pointOfClientPartialKey is " + ((Client)(Uty.deserialize(getPreference("client")))).getPointOfClientPartialKey());
        System.out.println("R_b is " + R);
//            plainText = null;


        HashMap<String, Object>  hshMap = new HashMap<>();
        hshMap.put("S", S);
        hshMap.put("R", R);
        hshMap.put("c", cipherText);
        System.out.println("Cipher " + cipherText);
        hshMap.put("ACC", cipherAccount);


//        byte[] IDcBc = Uty.xorWithKey(hshMap.get("IDcB"), K_1.y.toByteArray());
//        String receivedID = new String(IDcBc);
//        System.out.println("receivedID " + receivedID);

        return hshMap;
    }

    public static byte[] unSigncryption(HashMap<String, Object> HashMap) throws IOException {
        publicParameter = (publicParameters) Uty.deserialize(getPreference("publicParameter"));

//        BigInteger r_x = new BigInteger(1, HashMap.get("r_x"));
//        BigInteger r_y = new BigInteger(1, HashMap.get("r_y"));
//        BigInteger Sr = new BigInteger(1, HashMap.get("s"));
//        ECPoint Rr = new ECPoint(r_x, r_y);

        BigInteger Sr = (BigInteger) HashMap.get("S");
        ECPoint Rr = (ECPoint) HashMap.get("R");

//        ECPoint z_sb = bankClient.getPublicParameter().getCurve().add(bankClient.getPublicParameter().getCurve().multiply(bankClient.getPublicParameter().getBasePoint(), Sr), Rr);
        ECPoint z_sb = publicParameter.getCurve()
                .add(publicParameter.getCurve().multiply(publicParameter.getBasePoint(), Sr)
                        , Rr);

        ECPoint K_1b = null;
//        K_1b = bankClient.getPublicParameter().getCurve().multiply(z_sb, ((Client)(Uty.deserialize(getPreference("client")))).getPrivateKey().getYCoordinate());
//        K_1b = publicParameter.getCurve().multiply(z_sb, ((Client)(Uty.deserialize(getPreference("client")))).getPrivateKey().getYCoordinate());
        K_1b = publicParameter.getCurve()
                .multiply(z_sb, ((PrivateKey)Uty.deserialize(getPreference("fullPrivateKey"))).getYCoordinate());

        byte[] cipherAccount = Uty.xorWithKey((byte[]) HashMap.get("ACC"), K_1b.y.toByteArray());

        HashMap<String, Object> AccountHshMap = byteToMap(cipherAccount);

//        if (Uty.currentDate().compareTo((String) AccountHshMap.get("t")) < 0)
//            return null;

        String receivedID = (String) AccountHshMap.get("IDb");

        System.out.println("receivedID " + receivedID);
//
        if (receivedID == null)
            return null;

        if (!(receivedID.equals(publicParameter.getBankID())))
            return null;


//        byte[] IDcBc = Uty.xorWithKey(HashMap.get("IDcB"), K_1b.y.toByteArray());
//        byte[] IDcBc = Uty.xorWithKey(HashMap.get("IDcB"), K_1b.y.toByteArray());
//        String receivedID = new String(IDcBc);

//        System.out.println("IDcB " + receivedID);
//        System.out.println("getBankID " + bankClient.getPublicParameter().getBankID());
        byte[] plainText = null;
//        if ((Base64.encodeToString(IDcBc, Base64.NO_WRAP)).equals(bankClient.getPublicParameter().getBankID()) ) {
//        if ((receivedID).equals(bankClient.getPublicParameter().getBankID()) ) {
        if ((receivedID).equals(publicParameter.getBankID()) ) {
            byte[] h_b_data = new byte[0];
            ECPoint R_b = null;
            h_b_data = Uty.byteConcatenate(Arrays.asList((byte[]) HashMap.get("ACC"),
                    (byte[]) HashMap.get("c"),
                    publicParameter.getBankID().getBytes(),
                    z_sb.toString().getBytes(),
//                    bankClient.getPublicParameter().getBankPublicKey().getXCoordinates().toString().getBytes(),
                    ((PublicKey)Uty.deserialize(getPreference("fullPublicKey"))).getXCoordinates().toString().getBytes(),
                    publicParameter.getBankPublicKey().getXCoordinates().toString().getBytes(),
//                    ((ECPoint) (Uty.deserialize(getPreference("PointOfClientPartialKey")))).toString().getBytes(),
//                    ((Client) (Uty.deserialize(getPreference("client")))).getPointOfClientPartialKey().toString().getBytes(),

//                    bankClient.getPublicParameter().getKgcPublic().toString().getBytes()));
                    publicParameter.getKgcPublic().toString().getBytes()));

            BigInteger h_b = Uty.bytesHash(h_b_data);


//            R_b = bankClient.getPublicParameter().getCurve().multiply(bankClient.getPublicParameter().getCurve().add(bankClient.getPublicParameter().getBankPublicKey().getXCoordinates(),
            R_b = publicParameter.getCurve()
                    .multiply(publicParameter.getCurve()
                            .add(publicParameter.getBankPublicKey().getXCoordinates(),
//                                    ((Client) (Uty.deserialize(getPreference("client")))).getPointOfBankPartialKey()), h_b);
                                    ((ECPoint)Uty.deserialize(getPreference("PointOfBankPartialKey"))))
                            , h_b);

            System.out.println("K_1b " + K_1b);
            System.out.println("cipher is " + Arrays.toString((byte[])HashMap.get("c")));
            System.out.println("z_sb is " + z_sb);
            System.out.println("clientInfo.getPublicKey().getXCoordinates() is " + ((PublicKey)Uty.deserialize(getPreference("fullPublicKey"))).getXCoordinates());
            System.out.println("bankClient.getBankParameter().getPublicKey().getXCoordinates() is " + publicParameter.getBankPublicKey().getXCoordinates());
            System.out.println("bankClient.getPublicParameter().getKgcPublic() is " + publicParameter.getKgcPublic());
            System.out.println("h_b is " + h_b);
            System.out.println("PointOfBankPartialKey is " + ((ECPoint)Uty.deserialize(getPreference("PointOfBankPartialKey"))));
            System.out.println("R_b is " + R_b);
            System.out.println("Rr is " + Rr);
//            plainText = null;

//            plainText = null;
            System.out.println("R_b " + R_b);
            System.out.println("K_1b.x " + Arrays.toString(K_1b.x.toByteArray()));
            if (R_b.toString().equals(Rr.toString())) {
                System.out.println("Is Equal");
                byte[] receivedKey = K_1b.x.toByteArray();
//                NotifyUI.showLog(TAG, "receiveKey " + Arrays.toString(receivedKey));
//                NotifyUI.showLog(TAG, "receive Cipher " + Arrays.toString((byte[]) HashMap.get("c")));
                plainText = Uty.xorWithKey((byte[]) HashMap.get("c"), receivedKey);
//                plainText = AESencryption.crypt(HashMap.get("c"), receivedKey);
            }
        }
        return plainText;
    }



}
