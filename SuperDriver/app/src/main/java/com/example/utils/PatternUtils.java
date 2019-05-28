package com.example.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternUtils {

    public static boolean checkEmail(String email) {
        boolean flag = false;
        try {
            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    public static boolean checkMobileNumber(String mobileNumber) {
        boolean flag = false;
        try {
            Pattern regex = Pattern.compile("^1[345789]\\d{9}$");
            Matcher matcher = regex.matcher(mobileNumber);
            flag = matcher.matches();
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;

        }
        return flag;
    }

    public static final int VALID = 0;
    public static final int HAS_SPACE = 1;
    public static final int HAS_WORD = 2;
    public static final int LENGTH_INVALID = 3;
    public static final int INVALID_LETTER = 4;
    public static int checkPassWord(String password) {

        if (password.length() < 6 || password.length() > 16) {
            return LENGTH_INVALID;
        }
        //判断是否有空格字符串
        for (int t = 0; t < password.length(); t++) {
            String b = password.substring(t, t + 1);
            if (b.equals(" ")) {
                System.out.println("有空格字符");
                return HAS_SPACE;
            }
        }

        //判断是否有汉字
        int count = 0;
        String regEx = "[\\u4e00-\\u9fa5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(password);
        while (m.find()) {
            for (int i = 0; i <= m.groupCount(); i++) {
                count = count + 1;
            }
        }

        if (count > 0) {
            System.out.println("有汉字");
            return HAS_WORD;
        }


        //判断是否是字母和数字
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (!Character.isLetterOrDigit(c)) {
                return INVALID_LETTER;
            }
        }
        return VALID;
    }
}
