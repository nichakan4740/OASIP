package sit.int221.oasip.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import sit.int221.oasip.emails.EmailService;


@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void sendEmail(String toEmail, String subject, String text){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("oasip.kw2@gmail.com");
        message.setTo(toEmail);
        message.setText(text);
        message.setSubject(subject);
        // Sending the mail
        javaMailSender.send(message);
        System.out.println("sent email success");

    }
}
