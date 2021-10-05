package cn.ruleeeer.dailycode.bean.emailtask;

import cn.ruleeeer.dailycode.bean.MailContent;
import cn.ruleeeer.dailycode.util.EmailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.mail.MessagingException;

/**
 * @author ruleeeer
 * @date 2021/10/5 14:53
 */
@Component
@Slf4j
public class SendSimpleEmailTask implements Runnable {

    private static EmailUtil util;

    private MailContent mailContent;


    public SendSimpleEmailTask() {
    }

    public SendSimpleEmailTask(MailContent mailContent) {
        this.mailContent = mailContent;
    }

    @Autowired
    public void setEmailUtil(EmailUtil util) {
        SendSimpleEmailTask.util = util;
    }

    @Override
    public void run() {
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            util.sendMail(mailContent);
            stopWatch.stop();
            log.info("send mail success , receiver:{},subject:{},cost time:{}ms", mailContent.getReceiver(), mailContent.getSubject(), stopWatch.getLastTaskTimeMillis());
        } catch (MessagingException e) {
            log.error("send mail failed,mailContent:{}", mailContent);
        }
    }
}
