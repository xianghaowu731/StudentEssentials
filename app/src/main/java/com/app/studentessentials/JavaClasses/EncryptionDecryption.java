package com.app.studentessentials.JavaClasses;

import android.util.Base64;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class EncryptionDecryption
{
    private final static String RSA = "RSA";

    public static PublicKey pu_k;

    public static PrivateKey Pr_k;



    public static void generateKey() throws Exception {
//        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
//        generator.initialize(512);
//        KeyPair keyPair = generator.generateKeyPair();
//
//        byte[] privateKeyEnc = keyPair.getPrivate().getEncoded();
//        byte[] publicKeyEnc = keyPair.getPublic().getEncoded();
//        String publicKey = new String(Base64.encode(publicKeyEnc,0));
//        String privateKey = new String(Base64.encode(privateKeyEnc,0));
//        System.out.println("publicKey   "+publicKey);
//        System.out.println("privateKey   "+privateKey);

        String publicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAMIimyByxogkJLiru7IwGH6OYzoCO3Re5I+fGHVRovia\n" +
                "              vCa5ghBwlI0QZkseQzMVG6wdLKa3KBtnwgFNP8KrackCAwEAAQ==";
        String privateKey = "MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAwiKbIHLGiCQkuKu7sjAYfo5jOgI7\n" +
                "              dF7kj58YdVGi+Jq8JrmCEHCUjRBmSx5DMxUbrB0sprcoG2fCAU0/wqtpyQIDAQABAkEAqc5zOdKq\n" +
                "              7PIQXKd6KcX/5tLM4DSLpKJL8YQLdLLPhfrqVQov6vF4Jd5+XpYOANyLuDHoh+RWPa5Kpn5XLEoe\n" +
                "              AQIhAPge5GEnjO0cj4qgn3rAT3/u21BTl4Oui4hjv1k0bXBxAiEAyEzWH+eB8go0tJgO8BL6JFZS\n" +
                "              PPUfZomZnG6fb1wFutkCIEOnuMI9FJI67XDxZ0sDUSojKS33/SJs36Mq/6wFPaohAiA9qSG1uqAP\n" +
                "              McMd4T2f/EeTDhvM9izPdQab2JYuQubkqQIhAJqMAazEw2LOp45Rt0KfB195KWYOcPO6OXAui5/l\n" +
                "              Qkg1";

        byte[] publicKeyBytes = Base64.decode(publicKey,0); //rivert back to byte stream
        X509EncodedKeySpec keySpec1 = new X509EncodedKeySpec(publicKeyBytes); //get the keyspec back
        KeyFactory fact1 = KeyFactory.getInstance("RSA"); //specify the required algo and security provider
        pu_k = fact1.generatePublic(keySpec1);

        byte[] privateKeyBytes = Base64.decode(privateKey,0); //rivert back to byte stream
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes); //get the keyspec back
        KeyFactory fact = KeyFactory.getInstance("RSA"); //specify the required algo and security provider
        Pr_k = fact.generatePrivate(keySpec);
    }



    private static byte[] encrypt(String text, PublicKey pubRSA) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.ENCRYPT_MODE, pubRSA);
        return cipher.doFinal(text.getBytes());
    }



    public final static String encrypt(String text) {
        try {
            return byte2hex(encrypt(text, pu_k));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public final static String decrypt(String data) {
        try {
            return new String(decrypt(hex2byte(data.getBytes())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private static byte[] decrypt(byte[] src) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.DECRYPT_MODE, Pr_k);
        return cipher.doFinal(src);
    }


    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            if (stmp.length() == 1)
                hs += ("0" + stmp);
            else
                hs += stmp;
        }
        return hs.toUpperCase();
    }



    public static byte[] hex2byte(byte[] b) {
        if ((b.length % 2) != 0)
            throw new IllegalArgumentException("hello");
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }
}
