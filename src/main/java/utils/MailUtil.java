package utils;

import entity.LoginHistory;
import java.security.SecureRandom;
import java.util.List;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailUtil {

    private static final String username = "theturtletroopersdat@gmail.com";
    private static final String password = "turtle2020";

    private static Properties createProperties() {
        Properties prop = new Properties(); // TLS
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        return prop;
    }

    private static Session createSession(Properties prop, final String username, final String password) {
        Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        return session;
    }

    public static String generateCode() {
        String result = "";
        for (int i = 0; i < 6; i++) {
            SecureRandom sr = new SecureRandom();
            result += String.valueOf(sr.nextInt(10));
        }
        return result;
    }

    public static boolean sendCode(String email, String code) {
        Properties prop = createProperties();
        Session session = createSession(prop, username, password);
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("from@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("TTT 2-Step: Code");
            message.setText("Hello customer!\n\nHere is your code:\n" + code + "\n\nKinds regards TTT.");

            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public static boolean sendMail(String email, List<LoginHistory> list) {
        Properties prop = createProperties();
        Session session = createSession(prop, username, password);
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("from@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("TTT Login Attempts");
            String msg = "Hello customer!\n\nYou have requested a list of your login attempts, here is your attempts:\n";
            for (int i = 0; i < list.size(); i++) {
                LoginHistory lh = list.get(i);
                msg += i + ". Date:" + lh.getDate().toString() + " - IP:" + lh.getIp() + "\n";
            }
            msg += "\n\nKinds regards TTT.";
            message.setText(msg);

            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}
