package cn.ruleeeer.dailycode.bean.emailtask;

import cn.ruleeeer.dailycode.bean.DailyCode;
import cn.ruleeeer.dailycode.util.EmailUtil;
import cn.ruleeeer.dailycode.util.FetchUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.mail.MessagingException;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * @author ruleeeer
 * @date 2021/10/5 14:53
 */
@Component
@Slf4j
public class SendDailyCodeEmailTask implements Callable<Void> {

    private static EmailUtil emailUtil;

    private static FetchUtil fetchUtil;

    private String date;

    private String email;

    public SendDailyCodeEmailTask(String date, String email) {
        this.date = date;
        this.email = email;
    }

    public SendDailyCodeEmailTask() {
    }

    @Autowired
    public void setEmailUtil(EmailUtil util) {
        SendDailyCodeEmailTask.emailUtil = util;
    }

    @Autowired
    private void setFetchUtil(FetchUtil util) {
        SendDailyCodeEmailTask.fetchUtil = util;
    }

    @Override
    public Void call() throws Exception {
        DailyCode dailyCode = fetchUtil.cache.get(date);
        if (null != dailyCode) {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            emailUtil.buildAndSend(dailyCode, email);
            stopWatch.stop();
            log.info("send mail success , receiver : {}  ", email);

        } else {
            fetchUtil.fetchDailyCode().subscribe(item -> {
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                fetchUtil.cache.putIfAbsent(date, item);
                try {
                    emailUtil.buildAndSend(item, email);
                    log.info("send mail success , receiver : {}  ", email);
                } catch (MessagingException e) {
                    log.error("send email failed, emailContent : {}", item, e);
                }
            });
        }
        return null;
    }
}
