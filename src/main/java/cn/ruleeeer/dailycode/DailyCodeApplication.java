package cn.ruleeeer.dailycode;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("cn.ruleeeer.dailycode.mapper")
@EnableScheduling
@EnableConfigurationProperties
public class DailyCodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(DailyCodeApplication.class, args);
    }

}
