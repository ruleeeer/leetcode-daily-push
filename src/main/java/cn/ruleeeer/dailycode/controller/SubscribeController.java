package cn.ruleeeer.dailycode.controller;

import cn.ruleeeer.dailycode.bean.Result;
import cn.ruleeeer.dailycode.service.EmailSubscribeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;


/**
 * @author ruleeeer
 * @date 2021/10/2 17:50
 */
@RestController
@RequestMapping("")
@Validated
public class SubscribeController {

    @Autowired
    private EmailSubscribeService emailSubscribeService;

    @GetMapping(value = "subscribe/{email}")
    public Result subscribe(@Email(message = "not a valid email address")
                                    @PathVariable String email) {
        return emailSubscribeService.subscribe(email);
    }

    @GetMapping(value = "ensure/{email}")
    public Result ensureSubscribe(@Email(message = "not a valid email address")
                                          @PathVariable String email) {
        return emailSubscribeService.ensure(email);
    }

    @GetMapping(value = "unsubscribe/{email}")
    public Result unsubscribe(@Email(message = "not a valid email address")
                              @PathVariable String email){
        return emailSubscribeService.unsubscribe(email);
    }


}
