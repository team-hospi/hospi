package com.gradproject.hospi.utils;

import java.security.MessageDigest;

public class Encrypt {
    // SHA256 + SALT 암호화
    public static String getEncrypt(String id, String pwd) {

        byte[] salt = id.getBytes();
        String result = "";

        byte[] temp = pwd.getBytes();
        byte[] bytes = new byte[temp.length + salt.length];

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(bytes);

            byte[] b = md.digest();

            StringBuffer sb = new StringBuffer();

            for(int i=0; i<b.length; i++) {
                sb.append(Integer.toString((b[i] & 0xFF) + 256, 16).substring(1));
            }

            result = sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
