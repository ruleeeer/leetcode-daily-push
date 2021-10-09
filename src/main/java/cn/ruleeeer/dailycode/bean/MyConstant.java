package cn.ruleeeer.dailycode.bean;

import java.time.format.DateTimeFormatter;

/**
 * @author ruleeeer
 * @date 2021/10/3 16:43
 */
public class MyConstant {

    public static final String REDIS_KEY_SUCCESS_ONE = "success_%s";
    public static final String REDIS_KEY_SUCCESS_TWO = "success_%s_%s";
    public static final String REDIS_KEY_LEETCODE = "leetcode_%s";
    public static final String REDIS_KEY_FAILED = "failed_send_record";
    public static DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    public static String TEMPLATE_VERIFICATION = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
              <meta charset="UTF-8">
            </head>
            <body>
              <p>
                确定订阅LeetCode每日一题吗？
              </p>
              <p>
                请点击下方的订阅链接，如果不是您订阅的，请忽略该邮件
              </p>
                  <a href="%s">点击订阅</a>
              </p>
              </br>
              </br>
              <HR>
              <p>
                github：<a href="%s">%s</a>
              </p>
            </body>
            </html>
            """;

    static public String TEMPLATE_DAILY_CODE = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
              <meta charset="UTF-8">
            </head>
            <body>
              <h4>%s：%s</h4>
              <p>难度：%s</p>
              <div>%s</div>
              <p>
                原题链接：<a href="%s">%s</a>
              </p>
              </br>
              </br>
              <HR>
              <p>
                github：<a href="%s">%s</a>
              </p>
              <p>
                <a href="%s">取消订阅请点击此处</a>
              </p>
            </body>
            </html>
            """;

    public static String TEMPLATE_ENSURE_SUBSCRIBE_LINK = "http://%s/ensure/%s";

    public static String TEMPLATE_UNSUBSCRIBE_LINK = "http://%s/unsubscribe/%s";

    public static String LINK_GITHUB = "https://github.com/ruleeeer/leetcode-daily-push";
}
