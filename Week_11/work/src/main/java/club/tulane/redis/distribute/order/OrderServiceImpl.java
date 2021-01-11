package club.tulane.redis.distribute.order;

import club.tulane.redis.distribute.JedisClient;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户订单服务
 */
@Service
public class OrderServiceImpl {

    @Autowired
    private JedisClient jedisClient;

    public void submitOrder(OrderInfo orderInfo){
        // 存储数据
        // saveDB(orderInfo);
        // 调用MQ, 通知订单提交
        jedisClient.publish(MQConstants.MQ_BY_ORDER, JSON.toJSONString(orderInfo));
    }


}
