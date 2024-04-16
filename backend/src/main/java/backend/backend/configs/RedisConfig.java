package backend.backend.configs;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@Configuration
public class RedisConfig {


    private Logger logger = Logger.getLogger(RedisConfig.class.getName());

    // Injects the properties from application.properties
    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private Integer redisPort;

    @Value("${spring.redis.username}")
    private String redisUsername;

    @Value("${spring.redis.password}")
    private String redisPassword;
    
    @Value("${spring.redis.database}")
    private Integer redisDatabase;

    private JedisConnectionFactory getJedisConnectionFactory(){
        // Create a redis configuration
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisHost);
        config.setPort(redisPort);
        config.setDatabase(redisDatabase);
        if (!"NOT_SET".equals(redisUsername.trim())){
            config.setUsername(redisUsername);
            config.setPassword(redisPassword);
        }

        logger.log(Level.INFO, "Using Redis database %d".formatted(redisPort));

        JedisClientConfiguration jedisClient = JedisClientConfiguration.builder().build();
        JedisConnectionFactory jedisFac = new JedisConnectionFactory(config, jedisClient);
        jedisFac.afterPropertiesSet();
        return jedisFac;
    }
    
    @Bean("redis") // everything with this annotation will come here and look for the annotation
    public RedisTemplate<String, String> createRedisConnection(){
        

        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(getJedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        
        return template;
    }

}
