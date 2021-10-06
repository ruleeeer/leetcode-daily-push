package cn.ruleeeer.dailycode.config;

import cn.ruleeeer.dailycode.bean.DailyCode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;


/**
 * @author ruleeeer
 * @date 2021/10/6 18:25
 */
@Configuration
@Component
public class RedisTemplateConfig {

    @Bean
    ReactiveRedisOperations<String, DailyCode> redisOperations(ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<DailyCode> serializer = new Jackson2JsonRedisSerializer<>(DailyCode.class);
        RedisSerializationContext.RedisSerializationContextBuilder<String, DailyCode> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());
        RedisSerializationContext<String, DailyCode> context = builder.value(serializer).build();
        return new ReactiveRedisTemplate<>(factory, context);
    }

}
