package cn.ruleeeer.dailycode.queue;

import cn.ruleeeer.dailycode.bean.MailContent;
import cn.ruleeeer.dailycode.bean.MyConstant;
import cn.ruleeeer.dailycode.util.EmailUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author ruleeeer
 * @date 2021/10/8 19:23
 */
@Component
@Slf4j
public class SendMailQueue {

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    public LinkedBlockingQueue<MailContent> queue = new LinkedBlockingQueue<>();


    public void put(MailContent mailContent) {
        try {
            queue.put(mailContent);
        } catch (InterruptedException e) {
            log.error("put mailContent into queue error, mailContent:{} ", mailContent, e);
        }
    }

    @PostConstruct
    public void init() {
//        use singleton thread to send email
        Runnable task = () -> {
            while (true) {
                StopWatch stopWatch = StopWatch.create();
                MailContent mail = null;
                try {
                    stopWatch.start();
                    mail = queue.take();
                    emailUtil.sendMail(mail);
                    stopWatch.stop();
                    log.info("send mail cost time {} ms , subject:{} ,receiver:{} ", stopWatch.getTime(TimeUnit.MILLISECONDS), mail.getSubject(), mail.getReceiver());
                } catch (Exception e) {
                    if (null != mail) {
                        redisTemplate.opsForList().rightPush(MyConstant.REDIS_KEY_FAILED + "_" + mail.getDate(), mail.toString());
                        log.error("send mail failed , subject:{} ,receiver:{} ", mail.getSubject(), mail.getReceiver(), e);
                    } else {
                        log.error("send mail failed", e);
                    }
                }

//                execute callback
                if (null != mail && null != mail.getCallback()) {
                    try {
                        mail.getCallback().accept(mail);
                    } catch (Exception e) {
                        log.error("mail callback error", e);
                    }
                }
            }
        };

        Thread thread = new Thread(task);
        thread.setName("sendEmailThread--");
        thread.start();
    }
}
