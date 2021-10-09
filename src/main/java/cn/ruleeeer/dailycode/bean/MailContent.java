package cn.ruleeeer.dailycode.bean;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.function.Consumer;

/**
 * @author ruleeeer
 * @date 2021/10/3 16:40
 */
@Data
@Builder
@ToString
public class MailContent {

    private String receiver;

    private String date;

    private String htmlContent;

    private String subject;

    /**
     * send email success callback;
     */
    private Consumer<MailContent> callback;


}
