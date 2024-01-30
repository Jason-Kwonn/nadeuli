package kr.nadeuli.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
@EnableRedisRepositories
public class RedisConfig {
  private final RedisProperties redisProperties;

  // Redis 연결 팩토리를 생성하는 빈 메서드
  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    return new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
  }

  // RedisTemplate을 생성하여 Redis와의 상호작용을 담당하는 빈 메서드
  @Bean
  public RedisTemplate<String, Object> redisTemplate() {
    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(redisConnectionFactory());

    // Redis에서 사용되는 Key에 대한 직렬화 방식을 설정 (문자열을 사용)
    redisTemplate.setKeySerializer(new StringRedisSerializer());

    // Redis에서 사용되는 Value에 대한 직렬화 방식을 설정 (문자열을 사용)
    redisTemplate.setValueSerializer(new StringRedisSerializer());

    return redisTemplate;
  }
}