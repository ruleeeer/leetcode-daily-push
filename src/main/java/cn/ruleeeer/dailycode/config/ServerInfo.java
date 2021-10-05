package cn.ruleeeer.dailycode.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author ruleeeer
 * @date 2021/10/5 21:46
 */
@Component
@ConfigurationProperties(prefix = "server")
@Data
public class ServerInfo {

    private Integer port;

    private String address;

    private String publishAddress;
}
