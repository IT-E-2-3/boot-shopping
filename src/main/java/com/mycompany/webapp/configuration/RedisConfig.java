package com.mycompany.webapp.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
	
	/*
	 * @Value("${spring.redis.host}") private String host;
	 * 
	 * @Value("${spring.redis.port}") private int port;
	 */
	
	//레디스 클라이언트와 스프링을 연결시키주는 관리빈
	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		// return new LettuceConnectionFactory(host, port);
		//properites 파일에서 host 와 port를 받는다.
		return new LettuceConnectionFactory();
	}

	//레디스의 연산을 사용할 수 있게 해주는 template를 제공하는 관리빈
	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		//레디스 연결
		redisTemplate.setConnectionFactory(redisConnectionFactory());
		//레디스 와 java 통신을 통해서 직렬화
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());

		return redisTemplate;
	}
}
