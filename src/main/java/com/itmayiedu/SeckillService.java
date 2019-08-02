package com.itmayiedu;

import com.itmayiedu.redission.RedissionUtils;
import org.apache.commons.lang.StringUtils;
import org.redisson.Config;
import org.redisson.RedissonClient;
import org.redisson.SingleServerConfig;
import org.redisson.core.RLock;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SeckillService {
	
	private static JedisPool pool = null;
	private static Config config2 = null;
	public static int count = 0;

	static {
		JedisPoolConfig config = new JedisPoolConfig();
		// 设置最大连接数
		config.setMaxTotal(200);
		// 设置最大空闲数
		config.setMaxIdle(8);
		// 设置最大等待时间
		config.setMaxWaitMillis(1000 * 100);
		// 在borrow一个jedis实例时，是否需要验证，若为true，则所有jedis实例均是可用的
		config.setTestOnBorrow(true);
		pool = new JedisPool(config, "localhost", 6379, 3000);
//		pool = new JedisPool(config, "localhost", 6379, 3000, "123456");


		// redisson配置
		config2 = new Config();
		SingleServerConfig singleSerververConfig = config2.useSingleServer();
		singleSerververConfig.setAddress("127.0.0.1:6379");
//        singleSerververConfig.setPassword("redis");
	}

	private RedisLock lockRedis = new RedisLock(pool);


	/**
	 * 方式一: redis实现分布式锁
	 */
	public void seckill() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss SSS");
		String identifierValue = "";
		try {
			// 1.获取锁
			identifierValue = lockRedis.getRedisLock("liuwq", 2000L, 2000L);
			if (StringUtils.isBlank(identifierValue)) {
				System.out.println(Thread.currentThread().getName() + ",获取锁失败，因为获取锁时间超时...");
				return;
			}
			System.out.println(Thread.currentThread().getName() + "获取锁成功,锁的id:" + identifierValue + ",count = " + ++count + ",时间：" + sdf.format(new Date()));

			// 处理业务逻辑
			Thread.sleep(500);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 2.释放锁
			lockRedis.unRedisLock("liuwq", identifierValue);
		}

	}


	RedisLockUtils lockUtils = new RedisLockUtils(pool);
	/**
	 * 方式二: redis实现分布式锁
	 */
	public void seckill2() {
		String lockKey = RedisLockUtils.getLockKey("BALANCE_SEND_LOCK", "1111","2222");
		if (!lockUtils.lockService(lockKey)) {
			System.out.println(Thread.currentThread().getName() + ",获取锁失败，因为获取锁累计重试次数已经超过20次，时间超时...");
			return;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss SSS");
		System.out.println(Thread.currentThread().getName() + "获取锁成功," + ",count = " + ++count + ",时间：" + sdf.format(new Date()));

		// 处理业务逻辑
		try {

		} catch (Exception e) {

		} finally {
			lockUtils.unlock(lockKey);
		}
	}


	private RedissonClient redissonClient = RedissionUtils.getInstance().getRedisson(config2);

	/**
	 * 方式三: redisdion实现分布式锁
	 */
	public void seckill3() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss SSS");
		// 1.获取锁
		RLock lock = redissonClient.getLock("lock");
		try {
			lock.lock();
			System.out.println(Thread.currentThread().getName() + "获取锁成功," + ",count = " + ++count + ",时间：" + sdf.format(new Date()));

			// 处理业务逻辑
			Thread.sleep(100);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 2.释放锁
			lock.unlock();
		}

	}
}
