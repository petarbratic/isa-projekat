package rs.ac.ftn.isa.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final Environment env;

    public EmailService(JavaMailSender mailSender, Environment env) {
        this.mailSender = mailSender;
        this.env = env;
    }

    public void sendActivationEmail(String to, String activationLink) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(to);
        mail.setFrom(env.getProperty("spring.mail.username"));
        mail.setSubject("Aktivacija naloga");
        mail.setText(
                "Pozdrav,\n\n" +
                        "Kliknite na link da aktivirate nalog:\n" +
                        activationLink + "\n\n" +
                        "Ako niste vi napravili nalog, ignorišite ovu poruku."
        );

        mailSender.send(mail);
    }

    @Async
    public void sendActivationEmailAsync(String to, String activationLink) {
        sendActivationEmail(to, activationLink);
    }
}