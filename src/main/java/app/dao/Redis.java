package app.dao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
public class Redis {
    @Value("${redis_ip}")
    private String redis_ip;
    
    public Jedis connect(){
       Jedis redis = new Jedis(redis_ip); 
       return redis;
    }
    
}
