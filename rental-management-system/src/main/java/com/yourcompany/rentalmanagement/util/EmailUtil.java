package com.yourcompany.rentalmanagement.util;
import io.github.cdimascio.dotenv.Dotenv;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtil {
    private static Dotenv dotenv = Dotenv.load();
    private static String fromEmail = dotenv.get("GMAIL_ID");
    private static String password = dotenv.get("GMAIL_PASSWORD");

    public static void sendEmail(String recipient, String subject, String body) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); // SMTP server address
        props.put("mail.smtp.socketFactory.port", "465"); // Port for SSL
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");//SSL Factory Class
        props.put("mail.smtp.auth", "true"); //Enabling SMTP Authentication
        props.put("mail.smtp.port", "465"); //SMTP Port

        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };

        Session session = Session.getDefaultInstance(props, auth);

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
        message.setSubject(subject);
        message.setText(body);

        Transport.send(message);
        System.out.println("Sent message successfully....");
    }
}
