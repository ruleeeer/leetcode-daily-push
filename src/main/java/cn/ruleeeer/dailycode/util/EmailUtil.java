package cn.ruleeeer.dailycode.util;

import cn.ruleeeer.dailycode.bean.MailContent;
import cn.ruleeeer.dailycode.config.ServerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author ruleeeer
 * @date 2021/10/4 22:41
 */
@Component
public class EmailUtil {


    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ServerInfo serverInfo;

    @Value("${spring.mail.username}")
    private String sender;


    public void sendMail(MailContent mailContent) throws MessagingException {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(sender);
        helper.setTo(mailContent.getReceiver());
        helper.setSubject(mailContent.getSubject());
        helper.setText(mailContent.getHtmlContent(), true);
        mailSender.send(mimeMessage);
    }
}
