/**
 *
 */
package com.itmayiedu;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>Title: RedisLockUtils</p>
 * <p>Description: redis锁工具类 </p>
 * @author liuwq
 * @date 2019年4月2日  下午5:07:06
 */
public class RedisLockUtils {
    private final static Logger logger = LoggerFactory.getLogger(RedisLockUtils.class);

    /**
     * redis 锁超时时间
     */
    public static final int REDIS_LOCK_EXPIRE = 1;
    /**
     * redis 锁重试次数
     */
    public static final int REDIS_LOCK_RETRY_COUNT = 20;
    /**
     * redis 锁每次重试时间间隔
     */
    public static final long REDIS_LOCK_RETRY_INTERVAL_TIME = 100L;

    // redis线程池
    private JedisPool jedisPool;

    public RedisLockUtils(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    /**
     * 加锁
     */
    public boolean lock(final String key, final String value, final int seconds) {
        Jedis conn = jedisPool.getResource();
        boolean result = true;
        if (conn.setnx(key, value) == 1) {
            conn.expire(key, seconds);
        } else {
            result = false; // 锁失败
        }
        return result;
    }

    /**
     * 释放锁
     */
    public void unlock(final String key) {
        Jedis conn = jedisPool.getResource();
        try {
            conn.del(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    public static String getLockKey(String busiCode, String actId, String phoneNo) {
        return busiCode + "_" + actId + "_" + phoneNo;
    }

    public Boolean lockService(String lockKey) {
        return lockService(lockKey, REDIS_LOCK_RETRY_COUNT, REDIS_LOCK_RETRY_INTERVAL_TIME);
    }

    private static ThreadLocal<Integer> threadLocal = new ThreadLocal<>();
    public Boolean lockService(String lockKey, int retryCount, long sleepTime) {
        Jedis conn = jedisPool.getResource();
        AtomicInteger count = new AtomicInteger(0);
        while (true) {
            try {
                String currTime = System.currentTimeMillis() + "";
                threadLocal.set(count.incrementAndGet());
//                System.out.println(Thread.currentThread().getName() + "尝试获取锁...." + count.intValue());
                if (lock(lockKey, currTime, REDIS_LOCK_EXPIRE)) {
                    return true;
                }

//                Thread.sleep(sleepTime);
                if (count.intValue() >= retryCount) {
                    System.out.println("redisKey:,获取锁次数累计超过次" + lockKey + retryCount);
                    String val = conn.get(lockKey);
                    long time = System.currentTimeMillis();
                    if (!StringUtils.isBlank(val)) {

                        if (time - Long.parseLong(val) > REDIS_LOCK_EXPIRE * 1000) {
                            unlock(lockKey);
                        }
                    }
                    return false;
                }

            } catch (Exception e) {
                logger.error("redisKey:{},获取锁失败:" + e.getMessage(), e);
                return false;
            } finally {
                if (conn != null) {
                    conn.close();
                }
            }
        }
    }
}
