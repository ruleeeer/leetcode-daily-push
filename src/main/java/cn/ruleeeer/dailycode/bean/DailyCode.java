package cn.ruleeeer.dailycode.bean;

import lombok.Builder;
import lombok.Data;

/**
 * @author ruleeeer
 * @date 2021/10/2 17:54
 */
@Data
@Builder
public class DailyCode {

    private String number;

    private String title;

    private String content;

    private String level;

    private String link;
}
