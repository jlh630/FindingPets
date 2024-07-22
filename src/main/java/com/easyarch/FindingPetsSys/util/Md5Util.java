package com.easyarch.FindingPetsSys.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Util {
    static final char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};


    public static String encode(String str) {
        MessageDigest md5 = null;

        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException var10) {
            return null;
        }

        md5.update(str.getBytes());
        byte[] digest = md5.digest();
        int j = digest.length;
        char[] retStr = new char[j * 2];
        int k = 0;

        for (byte byte0 : digest) {
            retStr[k++] = hexDigits[byte0 >>> 4 & 15];
            retStr[k++] = hexDigits[byte0 & 15];
        }

        return new String(retStr);
    }
}
