package bank.transaction.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import bank.transaction.entity.TransactionConfigEntity;

@Configuration
public class RedisConfig {
	
	@Bean
	ReactiveRedisOperations<String, TransactionConfigEntity> redisOperations(ReactiveRedisConnectionFactory factory) {
		Jackson2JsonRedisSerializer<TransactionConfigEntity> serializer = new Jackson2JsonRedisSerializer<>(TransactionConfigEntity.class);

		RedisSerializationContext.RedisSerializationContextBuilder<String, TransactionConfigEntity> builder =
				RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

		RedisSerializationContext<String, TransactionConfigEntity> context = builder.value(serializer).build();

		return new ReactiveRedisTemplate<>(factory, context);
	}

}
