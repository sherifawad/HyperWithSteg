package connection;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;


import com.example.sherifawad.hyperencryption.Password.PasswordActivity;

import Util.NotifyUI;
import Util.Uty;
import algorithm.ecc.ECPoint;
import algorithm.encryption.AESencryption;
import parameters.Client;
import parameters.PrivateKey;
import parameters.PublicKey;
import parameters.publicParameters;

import javax.crypto.Cipher;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;

import static Util.Uty.bytesHash;
import static Util.Uty.currentDate;
import static Util.Uty.mapTOByte;
import static com.example.sherifawad.hyperencryption.CrySteg.CryStegActivity.connectionCheck;
import static com.example.sherifawad.hyperencryption.CrySteg.CryStegActivity.allValuesCheck;
//import static com.example.sherifawad.hyperencryption.CrySteg.CryStegActivity.getCryStegContext;
import static com.example.sherifawad.hyperencryption.MainActivity.getAppContext;
import static com.example.sherifawad.hyperencryption.MainActivity.getPreference;
import static com.example.sherifawad.hyperencryption.MainActivity.setPreference;

public class ClientServer extends Thread{
    private Client client;
    private publicParameters publicParameter;
    private  int serverPort;
    private  String serverName;
    private BigInteger n, d_C;
    private ECPoint P_C1;
    private Socket clientSocket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private BigInteger d_c2;

    public ClientServer(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
        client = new Client();
    }

    public ClientServer() {

    }

    public void run() {
//        try {
//            client = (Client)(Uty.deserialize(getPreference("client")));
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }

        try {
            this.clientSocket = new Socket(serverName, serverPort);
            this.objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
            connectionCheck = true;
            process();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void process() {
        send("Public", null);
        startMessageReader();
//        readMessageLoop();
    }

    private void clintKeyGeneration() throws IOException {
        do {
            try {
//                do {
//                    d_C = Uty.randomBig(publicParameter.getOrder());
//                } while (d_C == null);
//                System.out.println("DC " + d_C);
                d_C = new BigInteger("865049205761779596314415597297818988832033078586");
                P_C1 = publicParameter.getCurve().multiply(publicParameter.getBasePoint(), d_C);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } while (P_C1.isPointOfInfinity() || publicParameter.getBasePoint() == P_C1
                || P_C1 == new ECPoint(0, 0));
        handleAuthentication();
    }

    public ECPoint getPartialPublicKey() {
        return P_C1;
    }

    public BigInteger getPrivate() {
        return d_C;
    }


    private void send(String Command, Object messageBody){
        Message message = null;
        if (((Client)(Uty.deserialize(getPreference("client")))).getPublicKey() != null) {
            message = new Message(Command, ((Client)(Uty.deserialize(getPreference("client")))).getClientID(), ((Client)(Uty.deserialize(getPreference("client")))).getPublicKey(), messageBody);
        } else if (this.getPartialPublicKey() != null){
            message = new Message(Command, ((Client)(Uty.deserialize(getPreference("client")))).getClientID(), null, messageBody);
        } else {
            message = new Message(Command, ((Client)(Uty.deserialize(getPreference("client")))).getClientID(), null, messageBody);
        }
        System.out.println("send Message " + message.toString());
        try {
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
            System.out.println("Message has been send");
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void startMessageReader() {
        Thread t = new Thread("MessageReader") {
            @Override
            public void run() {
                readMessageLoop();
            }
        };
        t.start();
    }

    private void readMessageLoop() {
//        System.out.println(((Client)(Uty.deserialize(getPreference("client")))).getAccountNumber());
        Message message;
        try {
            while ((message = Message.class.cast(objectInputStream.readObject())) != null){
                try {
                    String cmd = message.getCommand();
                    if ("public".equalsIgnoreCase(cmd)) {
                        System.out.println(" public command received");
                        handlePublicParameters(message);
                    } else if ("publicU".equalsIgnoreCase(cmd)) {
                        System.out.println("publicU command received");
                        rehandlePublicParameters(message);
                    }else if ("key".equalsIgnoreCase(cmd)) {
                        System.out.println("key command received");
                        handleKeyGeneration(message);
                    }  else if ("handleAuthError".equalsIgnoreCase(cmd)) {
                        handleAuthentication();
                    }  else if ("ErrorAuth".equalsIgnoreCase(cmd)) {
                        WrongAuthPass();
                    }  else if ("Error".equalsIgnoreCase(cmd)) {
                        handleErrorMessages(message);
                    } else {
                        System.out.println("CommandError");
                        send("Error", "CommandError");
                    }
                } catch (IOException e) {
                    send("Error", "FormatError");
                }
            }

        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

    }

    private void WrongAuthPass() {
        Intent intentCoreActivity = new Intent(getAppContext(), PasswordActivity.class);
        getAppContext().startActivity(intentCoreActivity);
    }


    private void handleErrorMessages(Message message) {
        System.out.println("-------------------");
        System.out.println("ID " + message.getClientID());
        System.out.println("PublicKey " + message.getPublicKey());
        System.out.println("MessageBody " + message.getMessageBody());
        System.out.println("-------------------");
    }

    private void handleKeyGeneration(Message message) throws IOException {
        if ("KGCServer".equals(message.getClientID())) {
            generationProcess((byte[]) message.getMessageBody());
        }
    }

    private void generationProcess(byte[] receivedMap) throws IOException {
        try {
            Client client = (Client) Uty.deserialize(getPreference("client"));
            byte[] receivedHashMap = AESencryption.gcmMode(
                    Cipher.DECRYPT_MODE,
                    ((client).getClientID()).getBytes(),
                    (client).getPassword(),
                    receivedMap);

//            byte[] receivedHashMap = AESencryption.gcmMode(
//                    Cipher.DECRYPT_MODE,
//                    "51".getBytes(),
//                    "a2".toCharArray(),
//                    receivedMap);

//            System.out.println("receivedHashMap is " + receivedHashMap);
            HashMap<String, Object> hashMap = Uty.byteToMap(receivedHashMap);
            String receivedTimeStamp = (String) hashMap.get("t");
//            System.out.println("receivedName is " + receivedTimeStamp);
//            System.out.println("Name is " + ((Client)(Uty.deserialize(getPreference("client")))).getName());
//            if (currentDate().compareTo(receivedTimeStamp)<0)
//                return;

                System.out.println(" is true" );
                d_c2 = (BigInteger) hashMap.get("PartialKey");
                ECPoint R_C = (ECPoint) hashMap.get("PublicKey_YCoordinate");
                byte[] enConcatenateClient = Uty.byteConcatenate(Arrays.asList(
                        ((Client)(Uty.deserialize(getPreference("client")))).getClientID().getBytes(),
                        publicParameter.getKgcPublic().toString().getBytes(),
                        R_C.toString().getBytes(),
                        this.getPartialPublicKey().toString().getBytes()));

                BigInteger enHashClient = bytesHash(enConcatenateClient);
                ECPoint v1 = publicParameter.getCurve()
                        .add(R_C, publicParameter.getCurve()
                                .multiply(publicParameter.getKgcPublic(), enHashClient));

                ECPoint v2 = publicParameter.getCurve()
                        .multiply(publicParameter.getBasePoint(), d_c2);

                System.out.println("v1 is " + v1);
                System.out.println("v2 is " + v2);

                if (v1.toString().compareTo(v2.toString()) == 0) {
                    PrivateKey fullPrivateKey = new PrivateKey(this.getPrivate(), d_c2);

                    setPreference("fullPrivateKey", Uty.serialize(fullPrivateKey));
//                    ((Client)(Uty.deserialize(getPreference("client")))).setPrivateKey(fullPrivateKey);
                    System.out.println("fullPrivateKey is " + ((Client)(Uty.deserialize(getPreference("client")))).getPrivateKey());
                    System.out.println("fullPrivateKey is " + ((PrivateKey)Uty.deserialize(getPreference("fullPrivateKey"))));

                    PublicKey fullPublicKey = new PublicKey(this.getPartialPublicKey(), R_C);

                    setPreference("fullPublicKey", Uty.serialize( fullPublicKey));

//                    ((Client)(Uty.deserialize(getPreference("client")))).setPublicKey(fullPublicKey);

//                    System.out.println("fullPrivateKey is " + fullPrivateKey);
//                    System.out.println("fullPublicKey is " + ((Client)(Uty.deserialize(getPreference("client")))).getPublicKey());

                    pointOfClientPartialKey(d_c2);
                    allValuesCheck = true;
//                    dismissProgressDialog(getCryStegContext());
                    System.out.println("allValuesCheck # " + allValuesCheck);

                } else {
                    System.out.println("Wrong Key");
                    send("Error", "WrongKey");
                }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            send("Error", "DecryptionFailed");
        }
    }

    private void handleAuthentication() throws IOException {
        if(this.getPartialPublicKey() == null || ((Client)(Uty.deserialize(getPreference("client")))).getName() == null)
            return;

        HashMap<String, Object> AccountHshMap = new HashMap<>();
        AccountHshMap.put("pc1", this.getPartialPublicKey());
        AccountHshMap.put("t", currentDate());
        AccountHshMap.put("name",
                (((Client)(Uty.deserialize(getPreference("client")))).getName()).hashCode());
        System.out.println("ID " + ((Client)(Uty.deserialize(getPreference("client")))).getClientID());
        System.out.println("Pass " + Arrays.toString(( (Client)(Uty.deserialize(getPreference("client")))).getPassword()));

        byte[] Encryption = (AESencryption.gcmMode
                (Cipher.ENCRYPT_MODE,
                        (((Client)(Uty.deserialize(getPreference("client")))).getClientID()).getBytes(),
                        ((Client)(Uty.deserialize(getPreference("client")))).getPassword(),
                        mapTOByte(AccountHshMap)));

//        byte[] Encryption = (AESencryption.gcmMode
//                (Cipher.ENCRYPT_MODE,
//                        "51".getBytes(),
//                        "a2".toCharArray(),
//                        mapTOByte(AccountHshMap)));

        System.out.println("Encryption " + Encryption);

//        byte[] Decryption = (AESencryption.gcmMode
//                        (Cipher.DECRYPT_MODE, (((Client)(Uty.deserialize(getPreference("client")))).getClientID()).getBytes(),
//                                ((Client)(Uty.deserialize(getPreference("client")))).getPassword(),
//                                Base64.decode(Encryption, Base64.NO_WRAP)));
//
//        HashMap<String, Object> receivedAccountHashMap = Uty.byteToMap(Decryption);
//
//        if (receivedAccountHashMap.equals(AccountHshMap)){
//            System.out.println("true");
//        }else {
//            System.out.println("false");
//        }
        send("auth", Encryption);
    }

    private void rehandlePublicParameters(Message message) {
        publicParameters received_publicParameter = (publicParameters) message.getMessageBody();
        publicParameter = new publicParameters();
        publicParameter = received_publicParameter;
//                        System.out.println("New publicParameter" + publicPars);
        try {
            setPreference("publicParameter", Uty.serialize(publicParameter));
            System.out.println("Resaved to preference");
            System.out.println(publicParameter);
            pointOfBankPartialKey(publicParameter);
            clintKeyGeneration();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void handlePublicParameters(Message message) {
        if ("KGCServer".equalsIgnoreCase(message.getClientID())) {

            publicParameters received_publicParameter = (publicParameters) message.getMessageBody();
//            publicParameters received_publicParameter = publicParameter.getClass().cast(message.getMessageBody());
//            System.out.println("Existed publicParameter " + publicParameter);
//            System.out.println("Received publicParameter " + received_publicParameter);
            publicParameters publicPars = null;
            try {
                if (getPreference("publicParameter") != null) {
                    publicPars = (publicParameters) Uty.deserialize(getPreference("publicParameter"));
                }
//                publicPars = (publicParameters) Uty.deserialize(getPreference("publicParameter"));

                System.out.println("Saved  publicParameter" + publicPars);

                if (received_publicParameter.equal(publicPars)) {
                    publicParameter = publicPars;
//                    if (((PrivateKey)Uty.deserialize(getPreference("fullPrivateKey"))) != null){
                    if (getPreference("fullPrivateKey") != null){
                        allValuesCheck = true;
                        return;
                    }
//            if (parametersCeckEquality(received_publicParameter, publicPars)) {
                    System.out.println("Using Saved publicParameter");
//                    dismissProgressDialog(getCryStegContext());


                }else {
                        publicParameter = new publicParameters();
                        publicParameter = received_publicParameter;
//                        System.out.println("New publicParameter" + publicPars);
                        setPreference("publicParameter", Uty.serialize(publicParameter));
                        System.out.println("Saved to preference");

                }
                System.out.println(publicParameter);
                pointOfBankPartialKey(publicParameter);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
//                if (!(getPreference("fullPrivateKey").isEmpty()))
//                    return;

                clintKeyGeneration();
//                if(getPreference("fullPrivateKey").isEmpty() || d_c2 == null)
//                    return;
//                allValuesCheck = true;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    private void pointOfBankPartialKey(parameters.publicParameters Parameters) {
        byte[] h_c_data = Uty.byteConcatenate(Arrays.asList(publicParameter.getBankID().getBytes(),
                publicParameter.getKgcPublic().toString().getBytes(),
                publicParameter.getBankPublicKey().getYCoordinate().toString().getBytes(),
                publicParameter.getBankPublicKey().getXCoordinates().toString().getBytes()));
        BigInteger h_c = bytesHash(h_c_data);
        System.out.println("h_c " + h_c);

        ECPoint p_b2 = publicParameter.getCurve()
                .add(publicParameter.getBankPublicKey().getYCoordinate(),
                publicParameter.getCurve()
                        .multiply(publicParameter.getKgcPublic(), h_c));

        try {
            setPreference("PointOfBankPartialKey", Uty.serialize(p_b2));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ((Client)(Uty.deserialize(getPreference("client")))).setPointOfBankPartialKey(p_b2);

//        ((Client)(Uty.deserialize(getPreference("client")))).setPointOfBankPartialKey(p_b2);
//        Client c = (Client)Uty.deserialize(getPreference("client"));
//        System.out.println("getClient " + c.getPointOfBankPartialKey());
//        System.out.println("p_b2 " + p_b2);
    }

    private void pointOfClientPartialKey(BigInteger partialPrivateKey) {
        try {
            ECPoint p_c2 = publicParameter.getCurve()
                    .multiply(publicParameter.getBasePoint(), partialPrivateKey);
            ((Client)(Uty.deserialize(getPreference("client")))).setPointOfClientPartialKey(p_c2);
            setPreference("PointOfClientPartialKey", Uty.serialize(p_c2));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public publicParameters getPublicParameter() {
        return publicParameter;
    }

//    public Client getClientParameter() {
//        return client;
//    }

    private boolean parametersCeckEquality(publicParameters a, publicParameters b) {


        if (a == null || b == null ) {
            return false;
        } else if (a.getBankAddress().equals(b.getBankAddress()) && a.getBankID().equals(b.getBankID()) &&
                a.getBankPort() == b.getBankPort() && a.getBankPublicKey().equals(b.getBankPublicKey()) &&
                a.getBasePoint().equals(b.getBasePoint()) && a.getCurve().equals(b.getCurve()) &&
                a.getKgcPublic().equals(b.getKgcPublic()) && a.getOrder().equals(b.getOrder())) {
            return true;
        }else {
            return false;
        }
    }
}
