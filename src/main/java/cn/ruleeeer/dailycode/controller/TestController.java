package cn.ruleeeer.dailycode.controller;

import cn.ruleeeer.dailycode.bean.DailyCode;
import cn.ruleeeer.dailycode.mapper.EmailSubscribeMapper;
import cn.ruleeeer.dailycode.timer.SendEmailTimer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author ruleeeer
 * @date 2021/10/2 19:08
 */
@RestController(value = "test")
public class TestController {


    @Autowired
    private EmailSubscribeMapper emailSubscribeMapper;

    @Autowired
    private SendEmailTimer sendEmailTimer;
}
