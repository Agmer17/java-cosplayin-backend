package cosplayin.app.config;

import java.time.Duration;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

@Configuration
public class RedisStorageConfiguration {

        private PolymorphicTypeValidator serializerValidator() {
                return BasicPolymorphicTypeValidator.builder()
                                .allowIfSubType(Object.class)
                                .build();
        }

        @Bean
        public GenericJacksonJsonRedisSerializer genericRedisSerializer() {
                return GenericJacksonJsonRedisSerializer.builder()
                                .enableDefaultTyping(serializerValidator())
                                .build();
        }

        @Bean
        RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory,
                        GenericJacksonJsonRedisSerializer serializer) {
                RedisTemplate<String, Object> template = new RedisTemplate<>();
                template.setConnectionFactory(factory);
                template.setKeySerializer(new StringRedisSerializer());
                template.setHashKeySerializer(new StringRedisSerializer());
                template.setValueSerializer(serializer);
                template.setHashValueSerializer(serializer);
                template.afterPropertiesSet();
                return template;
        }

        @Bean
        CacheManager cacheManager(RedisConnectionFactory factory, GenericJacksonJsonRedisSerializer serializer) {
                RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(60))
                                .serializeKeysWith(
                                                RedisSerializationContext.SerializationPair
                                                                .fromSerializer(new StringRedisSerializer()))
                                .serializeValuesWith(
                                                RedisSerializationContext.SerializationPair
                                                                .fromSerializer(serializer))
                                .disableCachingNullValues();

                return RedisCacheManager.builder(factory)
                                .cacheDefaults(defaultConfig)
                                .build();
        }

}
