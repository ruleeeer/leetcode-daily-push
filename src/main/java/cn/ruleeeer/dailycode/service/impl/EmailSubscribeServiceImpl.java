package cn.ruleeeer.dailycode.service.impl;

import cn.ruleeeer.dailycode.bean.*;
import cn.ruleeeer.dailycode.bean.emailtask.SendDailyCodeEmailTask;
import cn.ruleeeer.dailycode.bean.emailtask.SendSimpleEmailTask;
import cn.ruleeeer.dailycode.bean.po.EmailSubscribe;
import cn.ruleeeer.dailycode.mapper.EmailSubscribeMapper;
import cn.ruleeeer.dailycode.service.EmailSubscribeService;
import cn.ruleeeer.dailycode.util.EmailUtil;
import cn.ruleeeer.dailycode.util.FetchUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.time.LocalDate;

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
    private EmailUtil emailUtil;

    @Autowired
    private FetchUtil fetchUtil;

    @Autowired
    private ThreadPoolTaskExecutor sendEmailThreadPool;

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
            String link = String.format("http://localhost:8080/ensure/%s", email);
            MailContent ensureEmail = MailContent.builder()
                    .receiver(email)
                    .subject("确认订阅LeetCode每日一题?")
                    .htmlContent(String.format(MyConstant.TEMPLATE_VERIFICATION, link))
                    .build();
            sendEmailThreadPool.submit(new SendSimpleEmailTask(ensureEmail));
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
//            send a leetcode email immediately after subscribed
            subscribeMapper.insert(emailSubscribe);
            String date = LocalDate.now().format(MyConstant.fmt);
            sendEmailThreadPool.submit(new SendDailyCodeEmailTask(date, email));
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