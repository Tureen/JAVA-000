package club.tulane.redis.distribute;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.*;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 封装jedis操作
 * Created by Tulane
 */
@Slf4j
@Component
public class JedisClient {

    @Autowired
    private JedisConfig jedisConfig;

    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private static int MAX_WAIT = 15 * 1000;
    //超时时间
    private static int TIMEOUT = 10 * 1000;

    private JedisPool jedisPool = null;

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    /**
     * Jedis实例获取返回码
     *
     * @author jqlin
     */
    public static class JedisStatus {
        /**
         * Jedis实例获取失败
         */
        public static final long FAIL_LONG = -5L;
        /**
         * Jedis实例获取失败
         */
        public static final int FAIL_INT = -5;
        /**
         * Jedis实例获取失败
         */
        public static final String FAIL_STRING = "-5";
    }

    @PostConstruct
    private void initialPool() {
        //Redis服务器IP
        String HOST = jedisConfig.getHost();
        //Redis的端口号
        int PORT = NumberUtils.toInt(jedisConfig.getPort(), 6379);
        //访问密码
        String AUTH = jedisConfig.getPassword();

        try {
            JedisPoolConfig config = new JedisPoolConfig();
            //最大连接数，如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
            config.setMaxTotal(NumberUtils.toInt(jedisConfig.getMaxTotal(), 400));
            //最大空闲数，控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
            config.setMaxIdle(NumberUtils.toInt(jedisConfig.getMaxIdle(), 50));
            //最小空闲数
            config.setMinIdle(NumberUtils.toInt(jedisConfig.getMinIdle(), 10));
            //是否在从池中取出连接前进行检验，如果检验失败，则从池中去除连接并尝试取出另一个
            config.setTestOnBorrow(false);
            //在return给pool时，是否提前进行validate操作
            config.setTestOnReturn(false);
            //在空闲时检查有效性，默认false
            config.setTestWhileIdle(true);
            //表示一个对象至少停留在idle状态的最短时间，然后才能被idle object evitor扫描并驱逐；
            //这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
            config.setMinEvictableIdleTimeMillis(30000);
            //表示idle object evitor两次扫描之间要sleep的毫秒数
            config.setTimeBetweenEvictionRunsMillis(60000);
            //表示idle object evitor每次扫描的最多的对象数
            config.setNumTestsPerEvictionRun(1000);
            //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
            config.setMaxWaitMillis(MAX_WAIT);

            if (StringUtils.isNotBlank(AUTH)) {
                jedisPool = new JedisPool(config, HOST, PORT, TIMEOUT, AUTH);
            } else {
                jedisPool = new JedisPool(config, HOST, PORT, TIMEOUT);
            }
        } catch (Exception e) {
            if (jedisPool != null) {
                jedisPool.close();
            }
            log.error("初始化Redis连接池失败", e);
        }
    }

    /**
     * 在多线程环境同步初始化
     */
    private synchronized void poolInit() {
        if (jedisPool == null) {
            initialPool();
        }
    }

    /**
     * 同步获取Jedis实例
     *
     * @return Jedis
     */
    public Jedis getJedis() {
        if (jedisPool == null) {
            poolInit();
        }
        Jedis jedis = null;
        try {
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
            }
        } catch (Exception e) {
            log.error("同步获取Jedis实例失败" + e.getMessage(), e);
            jedis.close();
        }
        return jedis;
    }

    /**
     * 设置值
     *
     * @param key
     * @param value
     * @return -5：Jedis实例获取失败<br/>OK：操作成功<br/>null：操作失败
     * @author jqlin
     */
    public String set(String key, String value) {
        try (
                Jedis jedis = getJedis();) {
            if (jedis == null) {
                return JedisStatus.FAIL_STRING;
            }
            return jedis.set(key, value);
        } catch (Exception e) {
            log.error("[redis error] -> 设置值失败");
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置值
     *
     * @param key
     * @param value
     * @param expire 过期时间，单位：秒
     * @return -5：Jedis实例获取失败<br/>OK：操作成功<br/>null：操作失败
     * @author jqlin
     */
    public String set(String key, String value, int expire, TimeUnit timeUnit) {
        try (
                Jedis jedis = getJedis()) {
            if (jedis == null) {
                return JedisStatus.FAIL_STRING;
            }
            if (TimeUnit.MILLISECONDS.equals(timeUnit)) {
                expire = expire / 1000;
            } else if (!TimeUnit.SECONDS.equals(timeUnit)) {
                throw new RuntimeException("TimeUnit时间单位设置错误:请设置milliseconds或seconds");
            }
            return jedis.setex(key, expire, value);
        } catch (Exception e) {
            log.error("[redis error] -> 设置值失败");
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取值
     *
     * @param key
     * @return
     * @author jqlin
     */
    public String get(String key) {
        try (
                Jedis jedis = getJedis()) {
            if (jedis == null) {
                return JedisStatus.FAIL_STRING;
            }
            return jedis.get(key);
        } catch (Exception e) {
            log.error("[redis error] -> 设置值失败");
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置key的过期时间
     *
     * @param key
     * @param -5：Jedis实例获取失败，1：成功，0：失败
     * @return
     * @author jqlin
     */
    public long expire(String key, int seconds) {
        try (
                Jedis jedis = getJedis()) {
            if (jedis == null) {
                return JedisStatus.FAIL_LONG;
            }
            return jedis.expire(key, seconds);
        } catch (Exception e) {
            log.error(String.format("设置key=%s的过期时间失败：" + e.getMessage(), key));
            throw new RuntimeException(e);
        }
    }

    /**
     * 判断key是否存在
     *
     * @param key
     * @return
     * @author jqlin
     */
    public boolean exists(String key) {
        try (
                Jedis jedis = getJedis();) {
            if (jedis == null) {
                log.warn("Jedis实例获取为空");
                return false;
            }
            return jedis.exists(key);
        } catch (Exception e) {
            log.error(String.format("判断key=%s是否存在失败：" + e.getMessage(), key));
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除key
     *
     * @param keys
     * @return -5：Jedis实例获取失败，1：成功，0：失败
     * @author jqlin
     */
    public long del(String... keys) {
        try (
                Jedis jedis = getJedis();) {
            if (jedis == null) {
                return JedisStatus.FAIL_LONG;
            }
            return jedis.del(keys);
        } catch (Exception e) {
            log.error(String.format("删除key=%s失败：" + e.getMessage(), keys));
            throw new RuntimeException(e);
        }
    }

    /**
     * set if not exists，若key已存在，则setnx不做任何操作
     *
     * @param key
     * @param value key已存在，1：key赋值成功
     * @return
     * @author jqlin
     */
    public long setnx(String key, String value) {
        try (
                Jedis jedis = getJedis();) {
            if (jedis == null) {
                return JedisStatus.FAIL_LONG;
            }
            return jedis.setnx(key, value);
        } catch (Exception e) {
            log.error("设置值失败：" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * set if not exists，若key已存在，则setnx不做任何操作
     *
     * @param key
     * @param value  key已存在，1：key赋值成功
     * @param expire 过期时间，单位：秒
     * @return
     * @author jqlin
     */
    public long setnx(String key, String value, int expire) {
        try (
                Jedis jedis = getJedis()) {
            if (jedis == null) {
                return JedisStatus.FAIL_LONG;
            }
            Long result = jedis.setnx(key, value);
            expire(key, expire);
            return result;
        } catch (Exception e) {
            log.error("设置值失败：" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 在列表key的头部插入元素
     *
     * @param key
     * @param values -5：Jedis实例获取失败，>0：返回操作成功的条数，0：失败
     * @return
     * @author jqlin
     */
    public long lpush(String key, String... values) {
        try (
                Jedis jedis = getJedis();) {
            if (jedis == null) {
                return JedisStatus.FAIL_LONG;
            }
            return jedis.lpush(key, values);
        } catch (Exception e) {
            log.error("在列表key的头部插入元素失败：" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 在列表key的尾部插入元素
     *
     * @param key
     * @param values -5：Jedis实例获取失败，>0：返回操作成功的条数，0：失败
     * @return
     * @author jqlin
     */
    public long rpush(String key, String... values) {
        try (
                Jedis jedis = getJedis()) {
            if (jedis == null) {
                return JedisStatus.FAIL_LONG;
            }
            return jedis.rpush(key, values);
        } catch (Exception e) {
            log.error("在列表key的尾部插入元素失败：" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 返回存储在key列表的特定元素
     *
     * @param key
     * @param start 开始索引，索引从0开始，0表示第一个元素，1表示第二个元素
     * @param end   结束索引，-1表示最后一个元素，-2表示倒数第二个元素
     * @return redis client获取失败返回null
     * @author jqlin
     */
    public List<String> lrange(String key, long start, long end) {
        try (
                Jedis jedis = getJedis()) {
            if (jedis == null) {
                return null;
            }
            return jedis.lrange(key, start, end);
        } catch (Exception e) {
            log.error("查询列表元素失败：" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取List缓存对象
     *
     * @param key
     * @param start
     * @param end
     * @return List<T> 返回类型
     * @author jqlin
     */
    public <T> List<T> range(String key, long start, long end, Class<T> clazz) {
        List<String> dataList = lrange(key, start, end);
        if (CollectionUtils.isEmpty(dataList)) {
            return new ArrayList<>();
        }
        return JSON.parseArray(JSON.toJSONString(dataList), clazz);
    }

    /**
     * 获取列表长度
     *
     * @param key -5：Jedis实例获取失败
     * @return
     * @author jqlin
     */
    public long llen(String key) {
        try (
                Jedis jedis = getJedis()) {
            if (jedis == null) {
                return JedisStatus.FAIL_LONG;
            }
            return jedis.llen(key);
        } catch (Exception e) {
            log.error("获取列表长度失败：" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 移除等于value的元素<br/><br/>
     * 当count>0时，从表头开始查找，移除count个；<br/>
     * 当count=0时，从表头开始查找，移除所有等于value的；<br/>
     * 当count<0时，从表尾开始查找，移除count个
     *
     * @param key
     * @param count
     * @param value
     * @return -5:Jedis实例获取失败
     * @author jqlin
     */
    public long lrem(String key, long count, String value) {
        try (
                Jedis jedis = getJedis()) {
            if (jedis == null) {
                return JedisStatus.FAIL_LONG;
            }
            return jedis.lrem(key, count, value);
        } catch (Exception e) {
            log.error("获取列表长度失败：" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 对列表进行修剪
     *
     * @param key
     * @param start
     * @param end
     * @return -5：Jedis实例获取失败，OK：命令执行成功
     * @author jqlin
     */
    public String ltrim(String key, long start, long end) {
        try (
                Jedis jedis = getJedis()) {
            if (jedis == null) {
                return JedisStatus.FAIL_STRING;
            }
            return jedis.ltrim(key, start, end);
        } catch (Exception e) {
            log.error("获取列表长度失败：" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置对象
     *
     * @param key
     * @param obj
     * @return
     * @author jqlin
     */
    public <T> String setObject(String key, T obj) {
        try (
                Jedis jedis = getJedis()) {
            if (jedis == null) {
                return JedisStatus.FAIL_STRING;
            }
            byte[] data = JSON.toJSONBytes(obj);
            return jedis.set(key.getBytes(), data);
        } catch (Exception e) {
            log.error("设置对象失败：" + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取对象
     *
     * @param key
     * @return
     * @author jqlin
     */
    public <T> T getObject(String key) {
        try (
                Jedis jedis = getJedis()) {
            if (jedis == null) {
                return null;
            }
            T result = null;
            byte[] data = jedis.get(key.getBytes());
            if (data != null && data.length > 0) {
                result = (T)JSON.parse(data);
            }
            return result;
        } catch (Exception e) {
            log.error("获取对象失败：" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 缓存Map赋值
     *
     * @param key
     * @param field
     * @param value
     * @return -5：Jedis实例获取失败
     * @author jqlin
     */
    public long hset(String key, String field, String value) {
        try (
                Jedis jedis = getJedis();) {
            if (jedis == null) {
                return JedisStatus.FAIL_LONG;
            }
            return jedis.hset(key, field, value);
        } catch (Exception e) {
            log.error("缓存Map赋值失败：" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取缓存的Map值
     *
     * @param key
     * @return
     */
    public String hget(String key, String field) {
        try (
                Jedis jedis = getJedis();) {
            if (jedis == null) {
                return null;
            }
            return jedis.hget(key, field);
        } catch (Exception e) {
            log.error("获取缓存的Map值失败：" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 缓存Map
     *
     * @param key
     * @param map
     * @return
     */
    public String hmset(String key, Map<String, String> map) {
        try (
                Jedis jedis = getJedis();) {
            if (jedis == null) {
                return JedisStatus.FAIL_STRING;
            }
            return jedis.hmset(key, map);
        } catch (Exception e) {
            log.error("缓存Map赋值失败：" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取map所有的字段和值
     *
     * @param key
     * @return
     * @author jqlin
     */
    public Map<String, String> hgetAll(String key) {
        try (
                Jedis jedis = getJedis()) {
            if (jedis == null) {
                log.warn("Jedis实例获取为空");
                return new HashMap<>();
            }
            return jedis.hgetAll(key);
        } catch (Exception e) {
            log.error("获取map所有的字段和值失败：" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 查看哈希表 key 中，指定的field字段是否存在。
     *
     * @param key
     * @param field
     * @return
     * @author jqlin
     */
    public Boolean hexists(String key, String field) {
        try (
                Jedis jedis = getJedis()) {
            if (jedis == null) {
                log.warn("Jedis实例获取为空");
                return null;
            }
            return jedis.hexists(key, field);
        } catch (Exception e) {
            log.error("查看哈希表field字段是否存在失败：" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取所有哈希表中的字段
     *
     * @param key
     * @return
     * @author jqlin
     */
    public Set<String> hkeys(String key) {
        try (
                Jedis jedis = getJedis();) {
            if (jedis == null) {
                log.warn("Jedis实例获取为空");
                return new HashSet<>();
            }
            return jedis.hkeys(key);
        } catch (Exception e) {
            log.error("获取所有哈希表中的字段失败：" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取所有哈希表中的值
     *
     * @param key
     * @return
     * @author jqlin
     */
    public List<String> hvals(String key) {
        try (
                Jedis jedis = getJedis();) {
            if (jedis == null) {
                log.warn("Jedis实例获取为空");
                return new ArrayList<>();
            }
            return jedis.hvals(key);
        } catch (Exception e) {
            log.error("获取所有哈希表中的值失败：" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 从哈希表 key 中删除指定的field
     *
     * @param key
     * @param
     * @return
     * @author jqlin
     */
    public long hdel(String key, String... fields) {
        try (
                Jedis jedis = getJedis();) {
            if (jedis == null) {
                log.warn("Jedis实例获取为空");
                return JedisStatus.FAIL_LONG;
            }
            return jedis.hdel(key, fields);
        } catch (Exception e) {
            log.error("map删除指定的field失败：" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public long incr(String key){
        try (
                Jedis jedis = getJedis()) {
            if (jedis == null) {
                log.warn("Jedis实例获取为空");
                return JedisStatus.FAIL_LONG;
            }
            return jedis.incr(key);
        } catch (Exception e) {
            log.error("incr失败：" + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public long incrBy(String key, long num){
        try (
                Jedis jedis = getJedis()) {
            if (jedis == null) {
                log.warn("Jedis实例获取为空");
                return JedisStatus.FAIL_LONG;
            }
            return jedis.incrBy(key, num);
        } catch (Exception e) {
            log.error("incrBy失败：" + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public long decr(String key){
        try (
                Jedis jedis = getJedis()) {
            if (jedis == null) {
                log.warn("Jedis实例获取为空");
                return JedisStatus.FAIL_LONG;
            }
            return jedis.decr(key);
        } catch (Exception e) {
            log.error("decr失败：" + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public long decrBy(String key, long num){
        try (
                Jedis jedis = getJedis()) {
            if (jedis == null) {
                log.warn("Jedis实例获取为空");
                return JedisStatus.FAIL_LONG;
            }
            return jedis.decrBy(key, num);
        } catch (Exception e) {
            log.error("decrBy失败：" + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取正则匹配的key (慎用)
     *
     * @param pattern
     * @return
     */
    public Set<String> keys(String pattern) {
        try (
                Jedis jedis = getJedis()) {
            if (jedis == null) {
                log.warn("Jedis实例获取为空");
                return new HashSet<>();
            }
            return jedis.keys(pattern);
        } catch (Exception e) {
            log.error("操作keys失败：" + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 添加地理位置
     * @param key
     * @param longitude
     * @param latitude
     * @param member
     * @return
     */
    public long addGeo(String key, double longitude, double latitude, String member) {
        try (
                Jedis jedis = getJedis()) {
            if (jedis == null) {
                return JedisStatus.FAIL_LONG;
            }
            return jedis.geoadd(key, longitude, latitude, member);
        } catch (Exception e) {
            log.error("操作keys失败：" + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public long addGeo(String key) {
        try (
                Jedis jedis = getJedis()) {
            if (jedis == null) {
                return JedisStatus.FAIL_LONG;
            }
            Map<String, GeoCoordinate> memberCoordinateMap = new HashMap<>();

            return jedis.geoadd(key, memberCoordinateMap);
        } catch (Exception e) {
            log.error("操作keys失败：" + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 发布消息
     * @param channel
     * @param message
     * @return
     */
    public long publish(String channel, String message){
        try (
                Jedis jedis = getJedis()) {
            if (jedis == null) {
                return JedisStatus.FAIL_LONG;
            }
            return jedis.publish(channel, message);
        } catch (Exception e) {
            log.error("publish失败：" + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 订阅消息
     * @param channel
     * @param jedisPubSub
     * @return
     */
    public String subscribe(String channel, JedisPubSub jedisPubSub){
        try (
                Jedis jedis = getJedis()) {
            if (jedis == null) {
                return JedisStatus.FAIL_STRING;
            }
            jedis.subscribe(jedisPubSub, channel);
            return StringUtils.EMPTY;
        } catch (Exception e) {
            log.error("publish失败：" + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
