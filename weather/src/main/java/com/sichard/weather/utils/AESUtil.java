package com.sichard.weather.utils;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * <br>类描述:
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 18-5-15</b>
 */
public class AESUtil {
    private static String ALGORITHM = "AES/CBC/NoPadding";
    private static String IV = "1234567812345678";
    public static String AES_DECRYPT_KEY = "cqgf971sp394@!#0";

    public static byte[] encrypt2(byte[] dataBytes, String key) throws Exception {
        if (dataBytes != null && dataBytes.length != 0) {
            try {
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                int blockSize = cipher.getBlockSize();
                int plaintextLength = dataBytes.length;
                if (plaintextLength % blockSize != 0) {
                    plaintextLength += blockSize - plaintextLength % blockSize;
                }

                byte[] plaintext = new byte[plaintextLength];
                System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
                byte[] keybytes = key.getBytes("utf-8");
                SecretKeySpec keyspec = new SecretKeySpec(keybytes, "AES");
                IvParameterSpec ivspec = new IvParameterSpec(IV.getBytes("utf-8"));
                cipher.init(1, keyspec, ivspec);
                byte[] encrypted = cipher.doFinal(plaintext);
                return encrypted;
            } catch (Exception var10) {
                var10.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    public static byte[] decrypt2(byte[] encrypted, String key) {
        byte[] original = null;
        if (encrypted != null && encrypted.length != 0) {
            try {
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                byte[] keybytes = key.getBytes("utf-8");
                SecretKeySpec keyspec = new SecretKeySpec(keybytes, "AES");
                IvParameterSpec ivspec = new IvParameterSpec(IV.getBytes("utf-8"));
                cipher.init(2, keyspec, ivspec);
                original = cipher.doFinal(encrypted);
                return original;
            } catch (NoSuchPaddingException var7) {
                var7.printStackTrace();
            } catch (NoSuchAlgorithmException var8) {
                var8.printStackTrace();
            } catch (UnsupportedEncodingException var9) {
                var9.printStackTrace();
            } catch (InvalidAlgorithmParameterException var10) {
                var10.printStackTrace();
            } catch (InvalidKeyException var11) {
                var11.printStackTrace();
            } catch (BadPaddingException var12) {
                var12.printStackTrace();
            } catch (IllegalBlockSizeException var13) {
                var13.printStackTrace();
            }

            return (byte[])original;
        } else {
            return null;
        }
    }

    public static String encodeBase64(byte[] b) {
        return b == null ? null : new String(Base64.encode(b, 1));
    }

    public static byte[] decodeBase64(String encodeStr) {
        if (encodeStr == null) {
            return null;
        } else {
            try {
                byte[] encodeByte = encodeStr.getBytes("utf-8");
                return Base64.decode(encodeByte, 1);
            } catch (Exception var2) {
                return null;
            }
        }
    }
}
