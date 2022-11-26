package sit.int221.oasip.emails;

public interface EmailService {
    void sendEmail(String toEmail, String subject, String text);

}
