package cn.ruleeeer.dailycode.bean.emailtask;

import cn.ruleeeer.dailycode.bean.DailyCode;
import cn.ruleeeer.dailycode.bean.MailContent;
import cn.ruleeeer.dailycode.bean.MyConstant;
import cn.ruleeeer.dailycode.bean.Result;
import cn.ruleeeer.dailycode.util.EmailUtil;
import cn.ruleeeer.dailycode.util.FetchUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.StopWatch;
import reactor.core.publisher.Mono;

import javax.mail.MessagingException;
import java.time.Duration;
import java.util.ArrayList;

/**
 * @author ruleeeer
 * @date 2021/10/5 14:53
 */
@Component
@Slf4j
public class SendDailyCodeEmailTask implements Runnable {

    private static EmailUtil emailUtil;

    private static FetchUtil fetchUtil;

    private static ReactiveRedisTemplate<String, DailyCode> redisTemplate;

    private static ReactiveRedisTemplate<String, String> redisStringTemplate;

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

    @Autowired
    public void setRedis(ReactiveRedisTemplate<String, DailyCode> redis) {
        SendDailyCodeEmailTask.redisTemplate = redis;
    }

    @Autowired
    public void setRedisStringTemplate(ReactiveRedisTemplate<String, String> redisStringTemplate) {
        SendDailyCodeEmailTask.redisStringTemplate = redisStringTemplate;
    }

    @Override
    public void run() {
        String key = String.format(MyConstant.REDIS_KEY_LEETCODE, date);
//        why the subscribe method will never be triggered when using get to obtain a non-existent key?
        ArrayList<String> array = new ArrayList<>(1);
        array.add(key);
        ReactiveValueOperations<String, DailyCode> opsForValue = redisTemplate.opsForValue();
        ReactiveValueOperations<String, String> stringOpsForValue = redisStringTemplate.opsForValue();
        opsForValue.multiGet(array)
                .flatMap(item -> (item.isEmpty() || null == item.get(0)) ? fetchUtil.fetchDailyCode() : Mono.just(item.get(0)))
                .subscribe(item -> {
//                    set redis cache
                    opsForValue.setIfAbsent(key, item, Duration.ofHours(24)).subscribe();
                    String md5Email = DigestUtils.md5DigestAsHex(email.getBytes());

                    StopWatch stopWatch = new StopWatch();
                    stopWatch.start();
                    try {
                        emailUtil.buildAndSend(item, email);
//                        set has send flag
                        stringOpsForValue.set(
                                String.format(MyConstant.REDIS_KEY_SUCCESS_TWO, date, md5Email),
                                String.valueOf(System.currentTimeMillis()), Duration.ofHours(24)).subscribe();
                    } catch (MessagingException e) {
                        log.error("send mail failed , receiver : {}", email, e);
//                        redis record failed
                        redisTemplate.opsForList().rightPush(MyConstant.REDIS_KEY_FAILED + "_" + date, item);
                    }
                    stopWatch.stop();
                    log.info("send mail success , receiver : {}  ", email);
                });
    }
}
