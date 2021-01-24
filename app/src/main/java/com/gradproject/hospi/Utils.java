package com.gradproject.hospi;

import java.security.MessageDigest;

public class Utils {
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

    // 전화번호 자동 하이픈 입력
    public static String phone(String src) {
        if (src == null) {
            return "";
        }
        if (src.length() == 8) {
            return src.replaceFirst("^([0-9]{4})([0-9]{4})$", "$1-$2");
        } else if (src.length() == 12) {
            return src.replaceFirst("(^[0-9]{4})([0-9]{4})([0-9]{4})$", "$1-$2-$3");
        }
        return src.replaceFirst("(^02|[0-9]{3})([0-9]{3,4})([0-9]{4})$", "$1-$2-$3");
    }

    // 빈칸 체크
    public static boolean blankCheck(String str){
        if(str.equals("")){
            return true;
        }else{
            return false;
        }
    }
}
