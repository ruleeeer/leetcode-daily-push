package cn.ruleeeer.dailycode.service;

import cn.ruleeeer.dailycode.bean.Result;
import cn.ruleeeer.dailycode.bean.po.EmailSubscribe;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author ruleeeer
 * @date 2021/10/4 15:10
 */
public interface EmailSubscribeService extends IService<EmailSubscribe> {
    Result subscribe(String email);

    Result ensure(String email);

    Result unsubscribe(String email);
}
