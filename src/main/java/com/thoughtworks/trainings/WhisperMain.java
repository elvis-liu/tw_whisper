package com.thoughtworks.trainings;

import com.google.common.base.Joiner;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import sun.misc.BASE64Encoder;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

public class WhisperMain {

    public static final String AUTH_SITE = "http://goo.gl/4J0gy";
    public static final String EMAIL_FROM = "tw.cd.training@gmail.com";
    public static final String EMAIL_TO = "tw.cd.training@gmail.com";
    public static final String EMAIL_SUBJECT = "Passkey generated notification";
    public static final String EMAIL_USER = "tw.cd.training";
    public static final String EMAIL_PASSWORD = "xL3Xa240EEbaHJz3iD";

    public static void main(String[] args) throws Exception {
        System.out.print("Hello there, please input your name: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String name = reader.readLine().trim();
        if (StringUtils.isNotBlank(name)) {
            byte[] seed = getPassKeySeed(name);
            String passKey = generatePassKey(seed);
            System.out.println("Your pass key is: " + passKey);
            System.out.println(String.format("Please visit %s for next step.", AUTH_SITE));
            sendNotificationMail(name, getMacAddress(), passKey);
        }
    }

    private static void sendNotificationMail(String name, byte[] macAddress, String passKey) throws MessagingException {
        String content = Joiner.on(" : ").join(name, Hex.encodeHexString(macAddress), passKey);
        sendMail(content);
    }

    private static byte[] getPassKeySeed(String name) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bos.write(getMacAddress());
        bos.write(name.getBytes("utf-8"));
        return bos.toByteArray();
    }

    private static String generatePassKey(byte[] seed) throws NoSuchAlgorithmException, IOException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA");
        byte[] digest = messageDigest.digest(seed);
        BASE64Encoder base64Encoder = new BASE64Encoder();
        return base64Encoder.encode(digest);
    }

    private static byte[] getMacAddress() throws IOException {
        InetAddress ip;
        ip = InetAddress.getLocalHost();
        NetworkInterface networkInterface = NetworkInterface.getByInetAddress(ip);
        return networkInterface.getHardwareAddress();
    }

    private static void sendMail(String content) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(EMAIL_USER, EMAIL_PASSWORD);
                    }
                });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(EMAIL_FROM));
        message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(EMAIL_TO));
        message.setSubject(EMAIL_SUBJECT);
        message.setText(content);

        Transport.send(message);
    }
}
