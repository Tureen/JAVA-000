package club.tulane.redis.distribute;
import redis.clients.jedis.Jedis;

import java.util.Collections;

/**
 * 分布式锁
 * Created by Tulane
 */
public class DistributeLock {

    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";     //  NX 代表只在键不存在时，才对键进行设置操作
    private static final String SET_WITH_EXPIRE_TIME = "PX"; // PX 5000 设置键的过期时间为5000毫秒

    /**
     * 尝试获取分布式锁
     * @param jedis Redis客户端
     * @param lockKey 锁
     * @param requestId 请求标识
     * @param expireTime 超期时间
     * @return 是否获取成功
     */
    public static boolean tryGetDistributedLock(Jedis jedis, String lockKey, String requestId, int expireTime) {

        /**
         * 1、把key、value set到redis中，隐含覆盖，默认的ttl是-1（永不过期）
         *
         * 2、根据第三个参数，把key、value set到redis中
         *     nx ： not exists, 只有key 不存在时才把key value set 到redis
         *     xx ： is exists ，只有 key 存在是，才把key value set 到redis
         *
         * 3、4 和2 就相同，只是多加了个过期时间
         *      expx参数有两个值可选 ：
         *           ex ： seconds 秒
         *           px :   milliseconds 毫秒
         *      使用其他值，抛出 异常 ： redis.clients.jedis.exceptions.JedisDataException : ERR syntax error
         */
        try {
            String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);

            if (LOCK_SUCCESS.equals(result)) {
                return true;
            }
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    private static final Long RELEASE_SUCCESS = 1L;

    /**
     * 释放分布式锁
     * @param jedis Redis客户端
     * @param lockKey 锁
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    public static boolean releaseDistributedLock(Jedis jedis, String lockKey, String requestId) {
        try {
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));

            if (RELEASE_SUCCESS.equals(result)) {
                return true;
            }
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }
}

