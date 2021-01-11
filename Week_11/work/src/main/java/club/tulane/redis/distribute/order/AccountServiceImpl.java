package club.tulane.redis.distribute.order;

import club.tulane.redis.distribute.JedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户账户服务
 */
@Service
public class AccountServiceImpl {

    @Autowired
    private JedisClient jedisClient;

    public void updateAccount(OrderInfo orderInfo){

    }
}
