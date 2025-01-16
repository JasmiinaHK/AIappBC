package ibu.edu.ba.aiapplication.core.api.mail;

import org.springframework.stereotype.Service;

@Service
public class MailgunMailClient implements MailClient {
    @Override
    public void sendMail(String recipient, String subject, String content) {
        System.out.println("Sending email via Mailgun to: " + recipient);
    }
}
