package com.example.belajar.unittest.util;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CacheUtility {

    @Autowired
    private RedisClient redisClient;

    public void delete(String prefix, String key){
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> syncCommand = connection.sync();
        String pair = prefix + ":" + key;
        syncCommand.del(pair);
        connection.close();
    }

    public void set(String prefix, String key, String value, Integer expiration){
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> syncCommand = connection.sync();
        String pair = prefix + ":" + key;
        String existingCache = syncCommand.get(pair);
        if(StringUtils.isNotEmpty(existingCache)){
            syncCommand.del(pair);
        }
        syncCommand.set(pair, value);
        if(ObjectUtils.isNotEmpty(expiration)){
            syncCommand.expire(pair, expiration);
        }
        connection.close();
    }

    public String get(String prefix, String key){
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> syncCommand = connection.sync();
        String pair = prefix + ":" + key;
        String value = syncCommand.get(pair);
        if(ObjectUtils.isEmpty(value)){
            return null;
        }
        connection.close();
        return value;
    }
    
}
