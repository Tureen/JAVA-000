package club.tulane.redis.distribute;

/**
 * 库存计数器
 */
public class StoreCounter {

    /**
     * 初始化库存
     * @param jedisClient
     * @param key
     * @param num
     */
    public static void initStore(JedisClient jedisClient, String key, long num){
        jedisClient.set(key, String.valueOf(num));
    }

    /**
     * 扣减库存
     * @param jedisClient
     * @param key
     * @return
     */
    public static boolean decrStore(JedisClient jedisClient, String key){
        final long decr = jedisClient.decr(key);
        return decr >= 0;
    }

    /**
     * 扣减库存 (num个)
     * @param jedisClient
     * @param key
     * @param num
     * @return
     */
    public static boolean decrByStore(JedisClient jedisClient, String key, long num){
        final long decr = jedisClient.decrBy(key, num);
        return decr >= 0;
    }
}
