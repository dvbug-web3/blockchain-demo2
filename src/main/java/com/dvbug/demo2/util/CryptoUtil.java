/*
 * Copyright (C) Vito
 * By Vito on 2022/1/11 11:01
 */
package com.dvbug.demo2.util;

import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class CryptoUtil {
    public static String sha256(String src) {
        MessageDigest messageDigest;
        String result = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(src.getBytes(StandardCharsets.UTF_8));
            result = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            System.err.println("get sha256 failed, " + e.getMessage());
        }
        return result;
    }

    public static String md5(String src) {
        return DigestUtils.md5DigestAsHex(src.getBytes(StandardCharsets.UTF_8)).substring(4);
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String byte2Hex(byte[] data) {
        StringBuilder build = new StringBuilder();
        String temp;
        for (byte datum : data) {
            temp = Integer.toHexString(datum & 0xFF);
            if (temp.length() == 1) {
                build.append("0");
            }
            build.append(temp);
        }
        return build.toString();
    }
}
