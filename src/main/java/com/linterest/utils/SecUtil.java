package com.linterest.utils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
public class SecUtil {

    private static byte[] keybytes = { 0x31, 0x32, 0x45, 0x65, 0x36, 0x1a, 0x3d, 0x4c, 0x31, 0x32, 0x45, 0x65, 0x36, 0x1a, 0x3d, 0x4c };

    public static void main(String[] args) throws Exception {
//        String e1 = encrypt("L0v3p0g0");
//        System.out.println(e1);
//        String e2 = decrypt(e1);
//        System.out.println(e2);
        System.out.println(DigestUtils.md5Hex("1234567"));
    }

    /**
     * 加密
     * @param value
     * @return
     */
    public static String encrypt(String value) {

        String s=null;

        int mode = Cipher.ENCRYPT_MODE;

        try {
            Cipher cipher = initCipher(mode);

            byte[] outBytes = cipher.doFinal(value.getBytes());

            s = String.valueOf(Hex.encodeHex(outBytes));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return s;
    }

    /**
     * 解密
     * @param value
     * @return
     */
    public static String decrypt(String value) {

        String s = null;

        int mode = Cipher.DECRYPT_MODE;

        try {
            Cipher cipher = initCipher(mode);

            byte[] outBytes = cipher.doFinal(Hex.decodeHex(value.toCharArray()));

            s = new String(outBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return s;
    }

    private static Cipher initCipher(int mode) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        Key key = new SecretKeySpec(keybytes, "AES");
        cipher.init(mode, key);
        return cipher;
    }
}