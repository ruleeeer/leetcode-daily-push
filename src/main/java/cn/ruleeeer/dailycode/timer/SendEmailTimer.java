package cn.ruleeeer.dailycode.timer;

import cn.ruleeeer.dailycode.bean.MyConstant;
import cn.ruleeeer.dailycode.bean.emailtask.SendDailyCodeEmailTask;
import cn.ruleeeer.dailycode.bean.po.EmailSubscribe;
import cn.ruleeeer.dailycode.service.EmailSubscribeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import java.time.LocalDate;
import java.util.List;


/**
 * @author ruleeeer
 * @date 2021/10/2 17:55
 */
@Component
@Slf4j
public class SendEmailTimer {

    @Autowired
    private EmailSubscribeService emailSubscribeService;

    @Autowired
    private ThreadPoolTaskExecutor sendEmailThreadPool;

    @Scheduled(cron = "0 0 8 * * ?")
    public void sendEmail() {

        List<EmailSubscribe> list = emailSubscribeService.list();
        String date = LocalDate.now().format(MyConstant.fmt);
        if (!list.isEmpty()) {
            for (EmailSubscribe emailSubscribe : list) {
                SendDailyCodeEmailTask task = new SendDailyCodeEmailTask(date, emailSubscribe.getEmail());
                ListenableFuture<Void> future = sendEmailThreadPool.submitListenable(task);
                //                    record success
//                future.addCallback(System.out::println, ex -> {
//                    System.out.println(ex);
//                });
            }
        }
    }
}
