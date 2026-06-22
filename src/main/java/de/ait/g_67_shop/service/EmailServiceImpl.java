package de.ait.g_67_shop.service;

import de.ait.g_67_shop.domain.User;
import de.ait.g_67_shop.exceptions.types.EmailSendingException;
import de.ait.g_67_shop.service.interfaces.EmailService;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final Configuration mailConfig;
    private final String host;
    private final String port;
    private final String mailFrom;

    public EmailServiceImpl(JavaMailSender mailSender,
                            Configuration mailConfig,
                            @Value("${server.host}") String host,
                            @Value("${server.port}") String port,
                            @Value("${spring.mail.username}") String mailFrom) {
        this.mailSender = mailSender;
        this.mailConfig = mailConfig;
        this.host = host;
        this.port = port;
        this.mailFrom = mailFrom;

        mailConfig.setDefaultEncoding("UTF-8");
        TemplateLoader loader = new ClassTemplateLoader(EmailServiceImpl.class, "/mail/");
        mailConfig.setTemplateLoader(loader);
    }

    @Override
    public void sendConfirmationEmail(User user) {

    }

    private String generateConfirmationEmail(User user) {

        try {
            Template template = mailConfig.getTemplate("confirm_registration_mail.ftlh");
        } catch (IOException e) {
            throw new EmailSendingException("Email text generation error", e);
        }

        return "";
    }
}
