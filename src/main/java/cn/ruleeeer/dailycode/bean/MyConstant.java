package cn.ruleeeer.dailycode.bean;

import java.time.format.DateTimeFormatter;

/**
 * @author ruleeeer
 * @date 2021/10/3 16:43
 */
public class MyConstant {

    static public DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    static public String TEMPLATE_VERIFICATION = """
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
              <p>
                <h4>
                  <a href="%s">点击订阅</a>
                </h4>
              </div>
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
              <div>
                <h4>
                  <a href="%s">原题链接：%s</a>
                </h4>
                <h4>
                  <a href="%s">取消订阅请点击此处</a>
                </h4>
              </div>
            </body>
            </html>
            """;
}
