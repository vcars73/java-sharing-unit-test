package com.example.belajar.unittest.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
@Slf4j
public class EncryptUtility {

    private EncryptUtility(){
    }

    public String decrypt(String data, String base64PrivateKey) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return decryptChipper(Base64.getDecoder().decode(data.getBytes()), getPrivateKey(base64PrivateKey));
    }

    public String encrypt(String data, String base64PublicKey) throws InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        return Base64.getEncoder().encodeToString(encryptChipper(data.getBytes(), getPublicKey(base64PublicKey)));
    }


    public static PrivateKey getPrivateKey(String base64PrivateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PrivateKey privateKey;
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64PrivateKey.getBytes()));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    public static PublicKey getPublicKey(String base64PublicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PublicKey publicKey;
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    private static String decryptChipper(byte[] data, PrivateKey privateKey) {
        String result = "";
        try {

            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");

            cipher.init(2, privateKey);
            result = new String(cipher.doFinal(data));
        } catch (NoSuchAlgorithmException var3) {
            log.error( "NoSuchAlgorithmException : {}" + var3.getMessage());
        } catch (NoSuchPaddingException var4) {
            log.error("NoSuchPaddingException : {}" + var4.getMessage());
        } catch (InvalidKeyException var5) {
            log.error("InvalidKeyException : {}" + var5.getMessage());
        } catch (IllegalBlockSizeException var6) {
            log.error("IllegalBlockSizeException : {}" + var6.getMessage());
        } catch (BadPaddingException var7) {
            log.error("BadPaddingException : {}" + var7.getMessage());
        }
        return result;
    }

    private static byte[] encryptChipper(byte[] data, PublicKey publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");

        cipher.init(1, publicKey);
        return cipher.doFinal(data);
    }
}
