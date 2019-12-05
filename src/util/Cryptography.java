package util;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * a helper class to encrypt and decrypt messages
 *
 * @author Stefan Christian Kohlmeier
 * @version 05.12.2019
 */
public class Cryptography {

    private static MessageDigest messageDigest;
    private static Cipher cipher;
    private static StringBuilder stringBuilder;
    private static SecureRandom secureRandom;

    static {
        try {
            messageDigest = MessageDigest.getInstance("SHA-512");
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
        stringBuilder = new StringBuilder();
        secureRandom = new SecureRandom();
    }

    /**
     * generates a key pair
     *
     * @return key pair
     */
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

    /**
     * converts a key to a string
     *
     * @param key key
     * @return key converted to Base64 String
     */
    public static String keyToString(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    /**
     * processes the Diffie-Hellman key agreement
     *
     * @param ownKey            own key
     * @param receivedKeyString received key as a string
     * @return the final key
     */
    public static SecretKey keyAgreement(Key ownKey, String receivedKeyString) {
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

    /**
     * encrypts a string using the aes encryption
     *
     * @param key encryption key
     * @param str string
     * @return encrypted String in Base64 format
     */
    public static String encryptAES(SecretKey key, String str) {
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

    /**
     * decrypts a string using the aes encryption
     *
     * @param key decryption key
     * @param str string in Base64 format
     * @return decrypted String
     */
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

    /**
     * generates a salt for SHA hashing
     *
     * @return 16 Byte salt as a Base64 String
     */
    public static String generateSalt() {
        byte[] bytes = new byte[16];
        secureRandom.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * generates the SHA-value of a String
     *
     * @param salt salt in Base64 format
     * @param str  string
     * @return SHA-value as a Base64 String
     */
    public static String getSHA(String salt, String str) {
        byte[] bytes = str.getBytes();
        messageDigest.update(Base64.getDecoder().decode(salt));
        bytes = messageDigest.digest(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * generates a Message Authentication Certification code for a message
     *
     * @param key key
     * @param msg message
     * @return MAC as a Base64 String
     */
    public static String generateMac(SecretKey key, String msg) {
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
