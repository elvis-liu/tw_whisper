package com.thoughtworks.trainings;

import org.apache.commons.lang3.StringUtils;
import sun.misc.BASE64Encoder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class WhisperMain
{

    public static final String AUTH_SITE = "http://goo.gl/4J0gy";

    public static void main( String[] args ) throws Exception {
        System.out.print("Hello there, please input your name: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String name = reader.readLine().trim();
        if (StringUtils.isNotBlank(name)) {
            String encodedName = encodeName(name);
            System.out.println("Your pass key is: " + encodedName);
            System.out.println(String.format("Please visit %s for next step.", AUTH_SITE));
        }
    }

    private static String encodeName(String name) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA");
        byte[] digest = messageDigest.digest(name.getBytes("utf-8"));
        BASE64Encoder base64Encoder = new BASE64Encoder();
        return base64Encoder.encode(digest);
    }
}
