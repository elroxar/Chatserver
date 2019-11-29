package util;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Cryptography {

    private static MessageDigest mD;
    private static Cipher cipher;
    private static StringBuilder sB;

    static {
        try {
            mD = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            cipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        sB = new StringBuilder();
    }

    public static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator kPG = KeyPairGenerator.getInstance("DH");
            kPG.initialize(512);
            return kPG.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args)
    {
        KeyPair a = generateKeyPair();
        KeyPair b = generateKeyPair();

        Key s1 = keyAgreement(a.getPrivate(), keyToString(b.getPublic()));
        Key s2 = keyAgreement(b.getPrivate(), keyToString(a.getPublic()));

        System.out.println(keyToString(s1));
        System.out.println(keyToString(s2));
        System.out.println(s1);

        String msg = "Hallo welt!";
        System.out.println(generateMac(s1, msg));
        System.out.println(generateMac(s2, msg));
    }

    public static String keyToString(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static Key keyAgreement(Key ownKey, String receivedKeyString) {
        try {
            byte[] receivedBytes = Base64.getDecoder().decode(receivedKeyString);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(receivedBytes);
            KeyFactory kF = KeyFactory.getInstance("DH");
            PublicKey receivedKey = kF.generatePublic(keySpec);
            KeyAgreement kA = KeyAgreement.getInstance("DH");
            kA.init(ownKey);
            kA.doPhase(receivedKey, true);
            return new SecretKeySpec(kA.generateSecret(), 0, 256 / Byte.SIZE, "AES");
        } catch (NoSuchAlgorithmException | InvalidKeyException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encryptAES(Key key, String str)
    {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] bytes = str.getBytes();
            bytes = cipher.doFinal(bytes);
            return Base64.getEncoder().encodeToString(bytes);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decryptAES(Key key, String str) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] bytes = Base64.getDecoder().decode(str);
            bytes = cipher.doFinal(bytes);
            return new String(bytes);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getSHA(String str)
    {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        bytes = mD.digest(bytes);
        return bytesToHEX(bytes);
    }

    private static String bytesToHEX(byte[] bytes)
    {
        sB.setLength(0);
        for(byte b : bytes)
            sB.append(String.format("%02x", b));
        return sB.toString();
    }

    public static String generateMac(Key key, String msg) {
        try {
            Mac mac = Mac.getInstance("HMACSHA512");
            mac.init(key);
            byte[] bytes = msg.getBytes();
            bytes = mac.doFinal(bytes);
            return Base64.getEncoder().encodeToString(bytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }
}
