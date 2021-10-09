package cn.ruleeeer.dailycode.timer;

import cn.ruleeeer.dailycode.bean.MailContent;
import cn.ruleeeer.dailycode.bean.MyConstant;
import cn.ruleeeer.dailycode.queue.SendMailQueue;
import cn.ruleeeer.dailycode.bean.po.EmailSubscribe;
import cn.ruleeeer.dailycode.service.EmailSubscribeService;
import cn.ruleeeer.dailycode.service.FetchLeetcodeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;


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
    private FetchLeetcodeService fetchLeetcodeService;

    @Autowired
    private SendMailQueue sendMailQueue;

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    @Scheduled(cron = "0 0 8 * * ?")
    public void sendEmail() {

        List<EmailSubscribe> list = emailSubscribeService.list();
        String date = LocalDate.now().format(MyConstant.FMT);
        if (!list.isEmpty()) {
            ReactiveValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
            List<String> onlyOneKey = new ArrayList<>(1);
            for (EmailSubscribe emailSubscribe : list) {
                String email = emailSubscribe.getEmail();
                onlyOneKey.clear();
//                redis key
                String redisKey = String.format(MyConstant.REDIS_KEY_SUCCESS_TWO, date, DigestUtils.md5DigestAsHex(email.getBytes()));
                onlyOneKey.add(redisKey);
//                put to queue
                Consumer<MailContent> put2Queue = mailContent -> opsForValue.multiGet(onlyOneKey)
                        .subscribe(results -> {
                            if (StringUtils.isEmpty(results.get(0))) {
//                              send successful callback:set redis send flag
                                String timeStamp = String.valueOf(System.currentTimeMillis());
                                mailContent.setCallback(notUse -> opsForValue.set(redisKey, timeStamp, Duration.ofHours(24)).subscribe());
                                try {
//                                    send to queue
                                    sendMailQueue.queue.put(mailContent);
                                } catch (Exception e) {
                                    log.error("put emailTask to queue failed, subject:{} receiver:{} ",
                                            mailContent.getSubject(),
                                            mailContent.getReceiver());
                                }
                            } else {
                                log.info("email :{} has been sent today, no more", email);
                            }

                        });

                fetchLeetcodeService
                        .fetchAndBuild(email)
                        .subscribe(put2Queue);
            }
        }
    }
}
