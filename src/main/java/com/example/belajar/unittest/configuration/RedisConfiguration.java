package com.example.belajar.unittest.configuration;

import io.lettuce.core.RedisClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
* @author  Adinandra Dharmasurya
* @version 1.0
* @since   2020-09-24
*/
@Configuration
@ConfigurationProperties(prefix = "redis")
public class RedisConfiguration {
    
    private String host;
    private String password;
    private Integer port;

    public void setHost(String host){
        this.host = host;
    }

    public String getHost(){
        return this.host;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getPassword(){
        return this.password;
    }

    public void setPort(Integer port){
        this.port = port;
    }

    public Integer getPort(){
        return this.port;
    }

    @Bean(name="redisClient")
    public RedisClient getRedisClient(){
        StringBuilder propBuilder = new StringBuilder();
        propBuilder.append("redis://");
        propBuilder.append(this.getPassword());
        propBuilder.append("@");
        propBuilder.append(this.getHost());
        propBuilder.append(":");
        propBuilder.append(this.getPort());
        propBuilder.append("/0");
        return RedisClient.create(propBuilder.toString());
    }

}
