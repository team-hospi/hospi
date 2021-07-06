package com.gradproject.hospi.utils;

import com.gradproject.hospi.BuildConfig;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Random;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class GMailSender extends javax.mail.Authenticator{
    private final String user ;
    private final String password ;
    private final Session session;
    private final String emailCode;

    public GMailSender() {
        this.user = BuildConfig.SMTP_EMAIL;
        this.password = BuildConfig.SMTP_PWD;
        emailCode = createEmailCode();
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        final String mailhost = "smtp.gmail.com";
        props.setProperty("mail.host", mailhost);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        //구글에서 지원하는 smtp 정보를 받아와 MimeMessage 객체에 전달해준다.
        session = Session.getDefaultInstance(props, this);
    }

    public String getEmailCode() {
        return emailCode;
    } //생성된 이메일 인증코드 반환

    private String createEmailCode() { //이메일 인증코드 생성
        String[] str = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        StringBuilder newCode = new StringBuilder();

        Random rnd = new Random();
        rnd.setSeed(System.currentTimeMillis());

        for (int x = 0; x < 6; x++) {
            int random = rnd.nextInt(str.length);
            newCode.append(str[random]);
        }

        return newCode.toString();
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        //해당 메서드에서 사용자의 계정(id & password)을 받아 인증받으며 인증 실패시 기본값으로 반환됨.
        return new PasswordAuthentication(user, password);
    }

    public synchronized void sendMail(String recipients) throws Exception {
        final String subject = "Hospi 이메일 인증입니다.";
        final String body = "Hospi 이메일 인증코드는 [" + getEmailCode() + "] 입니다.\n"
                                + "본인이 요청하신게 아니라면 이 메일은 무시하셔도 됩니다.";
        MimeMessage message = new MimeMessage(session);
        DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain")); //본문 내용을 byte단위로 쪼개어 전달
        message.setSender(new InternetAddress(user));  //본인 이메일 설정
        message.setSubject(subject); //해당 이메일의 본문 설정
        message.setDataHandler(handler);
        if (recipients.indexOf(',') > 0)
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
        else
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
        Transport.send(message); //메시지 전달
    }

    public static class ByteArrayDataSource implements DataSource {
        private final byte[] data;
        private String type;

        public ByteArrayDataSource(byte[] data, String type) {
            super();
            this.data = data;
            this.type = type;
        }

        public ByteArrayDataSource(byte[] data) {
            super();
            this.data = data;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContentType() {
            if (type == null)
                return "application/octet-stream";
            else
                return type;
        }

        public InputStream getInputStream() {
            return new ByteArrayInputStream(data);
        }

        public String getName() {
            return "ByteArrayDataSource";
        }

        public OutputStream getOutputStream() throws IOException {
            throw new IOException("Not Supported");
        }
    }
}
