package cn.ruleeeer.dailycode.bean;

import lombok.Builder;
import lombok.Data;

/**
 * @author ruleeeer
 * @date 2021/10/4 15:07
 */
@Data
@Builder
public class Result {

    private boolean success;

    private String msg;
}
