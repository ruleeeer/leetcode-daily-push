package cn.ruleeeer.dailycode.bean;

import lombok.Builder;
import lombok.Data;

/**
 * @author ruleeeer
 * @date 2021/10/3 16:40
 */
@Data
@Builder
public class MailContent {

    private String receiver;

    private String htmlContent;

    private String subject;
}
