package com.itmayiedu.redission;

import org.redisson.Config;
import org.redisson.Redisson;
import org.redisson.RedissonClient;
import org.redisson.SingleServerConfig;
import org.redisson.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: liuwq
 * @date: 2019/6/20 0020 下午 2:41
 * @version: V1.0
 */
public class RedissionUtils {

    private static Logger logger = LoggerFactory.getLogger(RedissionUtils.class);

    private static RedissionUtils redissionUtils;

    private RedissionUtils() {
    }

    /**
     * 提供单例模式
     *
     * @return
     */
    public static RedissionUtils getInstance() {
        if (redissionUtils == null) {
            synchronized (RedissionUtils.class) {
                if (redissionUtils == null) {
                    redissionUtils = new RedissionUtils();
                }
            }
        }
        return redissionUtils;
    }


    /**
     * 使用config创建Redisson
     * Redisson是用于连接Redis Server的基础类
     *
     * @param config
     * @return
     */
    public RedissonClient getRedisson(Config config) {
        RedissonClient redisson = Redisson.create(config);
        logger.info("成功连接Redis Server");
        return redisson;
    }

    /**
     * 使用ip地址和端口创建Redisson
     *
     * @param ip
     * @param port
     * @return
     */
    public RedissonClient getRedisson(String ip, String port) {
        Config config = new Config();
        config.useSingleServer().setAddress(ip + ":" + port);
        RedissonClient redisson = getRedisson(config);
        logger.info("成功连接Redis Server" + "\t" + "连接" + ip + ":" + port + "服务器");
        return redisson;
    }

    /**
     * 关闭Redisson客户端连接
     *
     * @param redisson
     */
    public void closeRedisson(RedissonClient redisson) {
        redisson.shutdown();
        logger.info("成功关闭Redis Client连接");
    }

    /**
     * 获取字符串对象
     *
     * @param redisson
     * @param objectName
     * @return
     */
    public <T> RBucket<T> getRBucket(RedissonClient redisson, String objectName) {
        RBucket<T> bucket = redisson.getBucket(objectName);
        return bucket;
    }

    /**
     * 获取Map对象
     *
     * @param redisson
     * @param objectName
     * @return
     */
    public <K, V> RMap<K, V> getRMap(RedissonClient redisson, String objectName) {
        RMap<K, V> map = redisson.getMap(objectName);
        return map;
    }

    /**
     * 获取有序集合
     *
     * @param redisson
     * @param objectName
     * @return
     */
    public <V> RSortedSet<V> getRSortedSet(RedissonClient redisson, String objectName) {
        RSortedSet<V> sortedSet = redisson.getSortedSet(objectName);
        return sortedSet;
    }

    /**
     * 获取无序集合
     *
     * @param redisson
     * @param objectName
     * @return
     */
    public <V> RSet<V> getRSet(RedissonClient redisson, String objectName) {
        RSet<V> rSet = redisson.getSet(objectName);
        return rSet;
    }

    /**
     * 获取列表
     *
     * @param redisson
     * @param objectName
     * @return
     */
    public <V> RList<V> getRList(RedissonClient redisson, String objectName) {
        RList<V> rList = redisson.getList(objectName);
        return rList;
    }

    /**
     * 获取队列
     *
     * @param redisson
     * @param objectName
     * @return
     */
    public <V> RQueue<V> getRQueue(RedissonClient redisson, String objectName) {
        RQueue<V> rQueue = redisson.getQueue(objectName);
        return rQueue;
    }

    /**
     * 获取双端队列
     *
     * @param redisson
     * @param objectName
     * @return
     */
    public <V> RDeque<V> getRDeque(RedissonClient redisson, String objectName) {
        RDeque<V> rDeque = redisson.getDeque(objectName);
        return rDeque;
    }

    /**
     * 此方法不可用在Redisson 1.2 中
     * 在1.2.2版本中 可用
     * @param redisson
     * @param objectName
     * @return
     */
    /**
     public <V> RBlockingQueue<V> getRBlockingQueue(RedissonClient redisson,String objectName){
     RBlockingQueue rb=redisson.getBlockingQueue(objectName);
     return rb;
     }*/

    /**
     * 获取锁
     *
     * @param redisson
     * @param objectName
     * @return
     */
    public RLock getRLock(RedissonClient redisson, String objectName) {
        RLock rLock = redisson.getLock(objectName);
        return rLock;
    }

    /**
     * 获取原子数
     *
     * @param redisson
     * @param objectName
     * @return
     */
    public RAtomicLong getRAtomicLong(RedissonClient redisson, String objectName) {
        RAtomicLong rAtomicLong = redisson.getAtomicLong(objectName);
        return rAtomicLong;
    }

    /**
     * 获取记数锁
     *
     * @param redisson
     * @param objectName
     * @return
     */
    public RCountDownLatch getRCountDownLatch(RedissonClient redisson, String objectName) {
        RCountDownLatch rCountDownLatch = redisson.getCountDownLatch(objectName);
        return rCountDownLatch;
    }

    /**
     * 获取消息的Topic
     *
     * @param redisson
     * @param objectName
     * @return
     */
    public <M> RTopic<M> getRTopic(RedissonClient redisson, String objectName) {
        RTopic<M> rTopic = redisson.getTopic(objectName);
        return rTopic;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        // redisson配置
        Config config = new Config();
        SingleServerConfig singleSerververConfig = config.useSingleServer();
        singleSerververConfig.setAddress("127.0.0.1:6379");
//        singleSerververConfig.setPassword("redis");

        // redisson客户端
        RedissonClient redissonClient = RedissionUtils.getInstance().getRedisson(config);

        // 测试连接
        System.out.println(redissonClient.getConfig().toJSON().toString());

        // 获取字符串对象
        RBucket<Object> rBucket = RedissionUtils.getInstance().getRBucket(redissonClient, "name");
        rBucket.set("liuwq2222222");
        rBucket.expire(5, TimeUnit.SECONDS);
        System.out.println(rBucket.get());

        while (true) {
            RLock lock = redissonClient.getLock("lock");
            try {
                // 第一个参数代表等待时间，第二是代表超过时间释放锁，第三个代表设置的时间制
//                boolean res = lock.tryLock(0, 10, TimeUnit.SECONDS);
//                if (res) {
//                    // 执行业务逻辑
//                }

                lock.lock();
                System.out.println("获取锁成功:" + System.currentTimeMillis());
                // 处理业务逻辑
                Thread.sleep(100);
            } finally {
//                lock.unlock();
            }
        }

    }

}
