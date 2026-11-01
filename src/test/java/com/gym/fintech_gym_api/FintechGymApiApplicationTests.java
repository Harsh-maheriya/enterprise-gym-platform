package com.gym.fintech_gym_api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class FintechGymApiApplicationTests {

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Test
	void contextLoads() {
	}

	@Test
	void testRedisConnection() {
		redisTemplate.opsForValue().set("ping", "pong");
		String value = redisTemplate.opsForValue().get("ping");
		Assertions.assertEquals("pong", value);
	}

}
