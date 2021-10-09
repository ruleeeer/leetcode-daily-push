package cn.ruleeeer.dailycode.service;

import cn.ruleeeer.dailycode.bean.DailyCode;
import cn.ruleeeer.dailycode.bean.MailContent;
import reactor.core.publisher.Mono;

import java.util.function.Function;

/**
 * @author ruleeeer
 * @date 2021/10/8 19:06
 */
public interface FetchLeetcodeService {

    Mono<DailyCode> fetchDailyTitle();

    Mono<DailyCode> fetchDetailByTitleName(String titleId);

    Mono<DailyCode> fetchDailyCode();

    Mono<MailContent> fetchAndBuild(String email);
}
