package cn.ruleeeer.dailycode.util;

import cn.ruleeeer.dailycode.bean.DailyCode;
import cn.ruleeeer.dailycode.bean.MailContent;
import cn.ruleeeer.dailycode.bean.MyConstant;
import cn.ruleeeer.dailycode.config.ServerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDate;

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

    public MailContent buildDailyCodeEmail(DailyCode dailyCode, String receiver) {
        String subject = String.format("%s LeetCode每日一题( %s )", LocalDate.now().format(MyConstant.fmt), dailyCode.getTitle());
        String unsubscribeLink = String.format(MyConstant.TEMPLATE_UNSUBSCRIBE_LINK, serverInfo.getPublishAddress(), serverInfo.getPort(), receiver);
        String leetCodeContent = String.format(MyConstant.TEMPLATE_DAILY_CODE, dailyCode.getNumber(), dailyCode.getTitle(), dailyCode.getLevel(), dailyCode.getContent(), dailyCode.getLink(), dailyCode.getLink(),
                unsubscribeLink);
        return MailContent.builder()
                .subject(subject)
                .htmlContent(leetCodeContent)
                .receiver(receiver)
                .build();
    }

    public void buildAndSend(DailyCode dailyCode, String receiver) throws MessagingException {
        sendMail(buildDailyCodeEmail(dailyCode, receiver));
    }
}
