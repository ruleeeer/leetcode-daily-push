package cn.ruleeeer.dailycode.timer;

import cn.ruleeeer.dailycode.bean.MyConstant;
import cn.ruleeeer.dailycode.bean.emailtask.SendDailyCodeEmailTask;
import cn.ruleeeer.dailycode.bean.po.EmailSubscribe;
import cn.ruleeeer.dailycode.service.EmailSubscribeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.time.LocalDate;
import java.util.ArrayList;
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

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    @Scheduled(cron = "0 0 8 * * ?")
    public void sendEmail() {

        List<EmailSubscribe> list = emailSubscribeService.list();
        String date = LocalDate.now().format(MyConstant.fmt);
        if (!list.isEmpty()) {
            ReactiveValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
            List<String> containOneKey = new ArrayList<>(1);
            for (EmailSubscribe emailSubscribe : list) {
                String email = emailSubscribe.getEmail();
                String key = String.format(MyConstant.REDIS_KEY_SUCCESS_TWO, date, DigestUtils.md5DigestAsHex(email.getBytes()));
                containOneKey.clear();
                System.out.println(key);
                containOneKey.add(key);
                opsForValue.multiGet(containOneKey)
                        .subscribe(item -> {
                            String result = item.get(0);
                            if (null == result || result.isEmpty()) {
//                                set send email task
                                sendEmailThreadPool.submit(new SendDailyCodeEmailTask(date, email));
                            }
                        });
            }
        }
    }
}
