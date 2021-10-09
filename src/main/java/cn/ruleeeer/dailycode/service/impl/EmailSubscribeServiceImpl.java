package cn.ruleeeer.dailycode.service.impl;

import cn.ruleeeer.dailycode.bean.*;
import cn.ruleeeer.dailycode.queue.SendMailQueue;
import cn.ruleeeer.dailycode.bean.po.EmailSubscribe;
import cn.ruleeeer.dailycode.config.ServerInfo;
import cn.ruleeeer.dailycode.mapper.EmailSubscribeMapper;
import cn.ruleeeer.dailycode.service.EmailSubscribeService;
import cn.ruleeeer.dailycode.service.FetchLeetcodeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.util.function.Consumer;


/**
 * @author ruleeeer
 * @date 2021/10/4 15:11
 */
@Service
@Slf4j
public class EmailSubscribeServiceImpl extends ServiceImpl<EmailSubscribeMapper, EmailSubscribe> implements EmailSubscribeService {

    @Autowired
    private EmailSubscribeMapper subscribeMapper;

    @Autowired
    private ServerInfo serverInfo;

    @Autowired
    private FetchLeetcodeService fetchLeetcodeService;

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    @Autowired
    private SendMailQueue sendMailQueue;

    @Override
    public Result subscribe(String email) {

        EmailSubscribe query = subscribeMapper.selectOne(new QueryWrapper<EmailSubscribe>()
                .lambda()
                .eq(EmailSubscribe::getEmail, email));
        if (null != query) {
            return Result.builder()
                    .success(false)
                    .msg(String.format("%s already subscribed", email))
                    .build();
        } else {
//            send verification to confirm subscription
            String link = String.format(MyConstant.TEMPLATE_ENSURE_SUBSCRIBE_LINK, serverInfo.getPublishAddress(), email);
            MailContent ensureEmail = MailContent.builder()
                    .receiver(email)
                    .subject("每日一题确认订阅邮件")
                    .htmlContent(String.format(MyConstant.TEMPLATE_VERIFICATION, link, MyConstant.LINK_GITHUB, MyConstant.LINK_GITHUB))
                    .build();
            try {
                sendMailQueue.queue.put(ensureEmail);
            } catch (InterruptedException e) {
                log.error("queue put error", e);
            }
            return Result.builder()
                    .success(true)
                    .msg("Please check the confirmation subscription email")
                    .build();
        }

    }

    @Override
    public Result ensure(String email) {

        EmailSubscribe query = subscribeMapper.selectOne(new QueryWrapper<EmailSubscribe>()
                .lambda()
                .eq(EmailSubscribe::getEmail, email));
        if (null != query) {
            return Result.builder()
                    .success(true)
                    .msg(String.format("%s already subscribed", email))
                    .build();
        } else {
            EmailSubscribe emailSubscribe = EmailSubscribe.builder()
                    .email(email)
                    .build();
            subscribeMapper.insert(emailSubscribe);

//            send a leetcode email immediately after subscribed
            String redisKey = String.format(MyConstant.REDIS_KEY_SUCCESS_TWO,
                    LocalDate.now().format(MyConstant.FMT),
                    DigestUtils.md5DigestAsHex(email.getBytes()));
            Consumer<MailContent> consumer = notUse -> redisTemplate.opsForValue().set(redisKey, String.valueOf(System.currentTimeMillis()), Duration.ofHours(24)).subscribe();
            fetchLeetcodeService.fetchAndBuild(email)
                    .subscribe(mailContent -> {
                        mailContent.setCallback(consumer);
                        sendMailQueue.put(mailContent);
                    });
            return Result.builder()
                    .success(true)
                    .msg("subscribe success")
                    .build();
        }
    }

    @Override
    public Result unsubscribe(String email) {
//        query is subscribed
        EmailSubscribe query = subscribeMapper.selectOne(new QueryWrapper<EmailSubscribe>()
                .lambda()
                .eq(EmailSubscribe::getEmail, email));
        if (null == query) {
            return Result.builder()
                    .success(true)
                    .msg("you are not subscribed, so there is no need to cancel")
                    .build();
        } else {
//            delete record
            int delete = subscribeMapper.delete(new LambdaQueryWrapper<EmailSubscribe>()
                    .eq(EmailSubscribe::getEmail, email));
            return Result.builder()
                    .success(delete > 0)
                    .msg(delete > 0 ? "you have successfully unsubscribed" : "system error")
                    .build();
        }
    }
}