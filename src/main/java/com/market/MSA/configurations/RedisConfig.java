package com.market.MSA.configurations;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisSerializer<Object> jsonSerializer = redisSerializer();
        
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))  // Thời gian sống của cache
                .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(SerializationPair.fromSerializer(jsonSerializer))
                .disableCachingNullValues();
        
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(config)
                .build();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // String serializer cho key
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // JSON serializer cho value
        RedisSerializer<Object> jsonSerializer = redisSerializer();
        
        // Cấu hình serializers
        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(jsonSerializer);
        
        template.setEnableTransactionSupport(true);
        template.afterPropertiesSet();
        return template;
    }
    
    @Bean
    public RedisSerializer<Object> redisSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();
        
        // Cấu hình để bỏ qua các trường null
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        
        // Bật tính năng lưu thông tin kiểu dữ liệu
        objectMapper.activateDefaultTyping(
            LaissezFaireSubTypeValidator.instance,
            ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.PROPERTY);
        
        // Thêm module để xử lý Page
        objectMapper.registerModule(new PageModule());
        
        // Cấu hình thêm cho việc xử lý các trường không xác định
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }
    
    /**
     * Module để xử lý serialization/deserialization cho Page
     */
    public static class PageModule extends SimpleModule {
        public PageModule() {
            addDeserializer(PageImpl.class, new JsonDeserializer<PageImpl>() {
                @Override
                public PageImpl<?> deserialize(JsonParser p, DeserializationContext ctxt)
                    throws IOException, JsonProcessingException {
                    JsonNode node = p.getCodec().readTree(p);
                    
                    JsonNode content = node.get("content");
                    JsonNode pageable = node.get("pageable");
                    JsonNode total = node.get("totalElements");
                    
                    JavaType type = ctxt.getTypeFactory().constructParametricType(
                        PageImpl.class, 
                        ctxt.getTypeFactory().constructType(Object.class)
                    );
                    
                    return new PageImpl<>(
                        (List<?>) p.getCodec().treeToValue(content, List.class),
                        PageRequest.of(
                            pageable.get("pageNumber").asInt(),
                            pageable.get("pageSize").asInt()
                        ),
                        total.asLong()
                    );
                }
            });
        }
    }
  }
