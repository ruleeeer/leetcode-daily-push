package cn.ruleeeer.dailycode.service.impl;

import cn.ruleeeer.dailycode.bean.DailyCode;
import cn.ruleeeer.dailycode.bean.MailContent;
import cn.ruleeeer.dailycode.bean.MyConstant;
import cn.ruleeeer.dailycode.config.ServerInfo;
import cn.ruleeeer.dailycode.service.FetchLeetcodeService;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.collections4.CollectionUtils;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author ruleeeer
 * @date 2021/10/5 12:30
 */
@Component
public class FetchLeetCodeServiceImpl implements FetchLeetcodeService {


    @Autowired
    private ReactiveRedisTemplate<String, DailyCode> redisTemplate;

    @Autowired
    private ServerInfo serverInfo;


    @Override
    public Mono<DailyCode> fetchDailyTitle() {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://leetcode-cn.com")
                .build();
        String query = """
                {
                    "operationName": "questionOfToday",
                    "variables": {},
                    "query": "query questionOfToday { todayRecord {   question {     questionFrontendId     questionTitleSlug     __typename   }   lastSubmission {     id     __typename   }   date   userStatus   __typename }}"
                }
                """;
        return webClient.post()
                .uri("graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(query))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(item -> Optional.ofNullable(item)
                        .map(me -> me.get("data"))
                        .map(data -> data.get("todayRecord"))
                        .map(todayRecord -> todayRecord.get(0))
                        .map(questionInfo -> questionInfo.get("question"))
                        .map(question -> {
                            String number = question.get("questionFrontendId").asText();
                            String titleName = question.get("questionTitleSlug").asText();
                            return DailyCode.builder().number(number).title(titleName).build();
                        }).orElse(null));
    }

    @Override
    public Mono<DailyCode> fetchDetailByTitleName(String titleId) {
        if (!titleId.isEmpty()) {
            WebClient webClient = WebClient.builder()
                    .baseUrl("https://leetcode-cn.com")
                    .build();
            String query = String.format("""
                    {
                             "operationName": "questionData",
                             "variables": {
                                 "titleSlug": "%s"
                             },
                             "query": "query questionData($titleSlug: String!) {  question(titleSlug: $titleSlug) {    questionId    questionFrontendId    boundTopicId    title    titleSlug    content    translatedTitle    translatedContent    isPaidOnly    difficulty    likes    dislikes    isLiked    similarQuestions    contributors {      username      profileUrl      avatarUrl      __typename    }    langToValidPlayground    topicTags {      name      slug      translatedName      __typename    }    companyTagStats    codeSnippets {      lang      langSlug      code      __typename    }    stats    hints    solution {      id      canSeeDetail      __typename    }    status    sampleTestCase    metaData    judgerAvailable    judgeType    mysqlSchemas    enableRunCode    envInfo    book {      id      bookName      pressName      source      shortDescription      fullDescription      bookImgUrl      pressImgUrl      productUrl      __typename    }    isSubscribed    isDailyQuestion    dailyRecordStatus    editorType    ugcQuestionId    style    __typename  }}"
                    }      
                    """, titleId);
            return webClient.post()
                    .uri("graphql")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(query))
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .timeout(Duration.ofMinutes(1))
                    .map(item -> Optional.of(item)
                            .map(me -> item.get("data"))
                            .map(data -> data.get("question"))
                            .map(question -> {
                                String number = question.get("questionFrontendId").asText();
                                String title = question.get("translatedTitle").asText();
                                String content = question.get("translatedContent").asText();
                                Parser parser = Parser.builder().build();
                                Node document = parser.parse(content);
                                HtmlRenderer renderer = HtmlRenderer.builder().build();
                                String render = renderer.render(document);
                                String level = question.get("difficulty").asText();
                                return DailyCode.builder()
                                        .number(number)
                                        .title(title)
                                        .content(StringUtils.hasText(render) ? render : content)
                                        .level(level)
                                        .link(String.format("https://leetcode-cn.com/problems/%s", titleId))
                                        .build();
                            }).orElse(null));
        } else {
            return null;
        }
    }

    @Override
    public Mono<DailyCode> fetchDailyCode() {
        String date = LocalDate.now().format(MyConstant.FMT);
        String key = String.format(MyConstant.REDIS_KEY_LEETCODE, date);
        List<String> onlyKey = new ArrayList<>(1);
        onlyKey.add(key);
        return redisTemplate.opsForValue().multiGet(onlyKey)
                .flatMap(results -> {
                    if (CollectionUtils.isEmpty(results) || null == results.get(0)) {
//                        dont hit cache
                        Mono<DailyCode> dailyCodeMono = fetchDailyTitle()
                                .flatMap(title -> fetchDetailByTitleName(title.getTitle()));
//                        set redis cache
                        dailyCodeMono.subscribe(dailyCode -> redisTemplate.opsForValue().setIfAbsent(key, dailyCode, Duration.ofHours(24)).subscribe());
                        return dailyCodeMono;
                    } else {
//                        hit cache
                        return Mono.just(results.get(0));
                    }
                });
    }

    @Override
    public Mono<MailContent> fetchAndBuild(String email) {
        return fetchDailyCode().map(dailyCode -> {
            String subject = String.format("%s LeetCode每日一题( %s )", LocalDate.now().format(MyConstant.FMT), dailyCode.getTitle());
            String unsubscribeLink = String.format(MyConstant.TEMPLATE_UNSUBSCRIBE_LINK, serverInfo.getPublishAddress(), email);
            String leetCodeContent = String.format(MyConstant.TEMPLATE_DAILY_CODE,
                    dailyCode.getNumber(),
                    dailyCode.getTitle(),
                    dailyCode.getLevel(),
                    dailyCode.getContent(),
                    dailyCode.getLink(),
                    dailyCode.getLink(),
                    MyConstant.LINK_GITHUB,
                    MyConstant.LINK_GITHUB,
                    unsubscribeLink
            );
            return MailContent.builder()
                    .subject(subject)
                    .receiver(email)
                    .htmlContent(leetCodeContent)
                    .build();
        });
    }

}
