package com.itmayiedu.redission;

import org.redisson.Config;
import org.redisson.Redisson;
import org.redisson.RedissonClient;
import org.redisson.core.RLock;

import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: liuwq
 * @date: 2019/6/20 0020 下午 2:29
 * @version: V1.0
 */
public class RedissionConfig {

    public static void main(String[] args) throws Exception {
        Config config = new Config();
        config.useSingleServer().setAddress("127.0.0.1:6379");
        RedissonClient redisson = Redisson.create(config);

        System.out.println(redisson.getConfig().toJSON().toString());


        RLock lock = redisson.getLock("anyLock");

        // 最常见的使用方法

        lock.lock();// 支持过期解锁功能 10秒钟以后自动解锁

        lock.lock(10, TimeUnit.SECONDS);// 无需调用unlock方法手动解锁

        boolean res = lock.tryLock(0, 10, TimeUnit.SECONDS);// 尝试加锁，最多等待100秒，上锁以后10秒自动解锁

        lock.unlock();
    }

}
