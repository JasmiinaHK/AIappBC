package ibu.edu.ba.aiapplication.core.api.mail;

public interface MailClient {
    void sendMail(String recipient, String subject, String content);
}
