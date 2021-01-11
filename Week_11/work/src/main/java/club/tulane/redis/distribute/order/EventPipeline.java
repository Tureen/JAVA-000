package club.tulane.redis.distribute.order;

import club.tulane.redis.distribute.JedisClient;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPubSub;

import javax.annotation.PostConstruct;

@Component
public class EventPipeline {

    @Autowired
    private JedisClient jedisClient;
    @Autowired
    private AccountServiceImpl accountService;

    /**
     * 此方法调用一次, 监听订单提交
     */
    @PostConstruct
    public void listen(){
        jedisClient.subscribe(MQConstants.MQ_BY_ORDER, new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                if(channel.equals(MQConstants.MQ_BY_ORDER)){
                    final OrderInfo orderInfo = JSON.parseObject(message, OrderInfo.class);
                    accountService.updateAccount(orderInfo);
                }
            }
        });
    }
}
