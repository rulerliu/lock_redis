package com.itmayiedu;

import org.apache.commons.lang.StringUtils;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class SeckillService {
	
	private static JedisPool pool = null;
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
	}

	private RedisLock lockRedis = new RedisLock(pool);


	/**
	 * 演示秒杀redis实现分布式锁
	 */
	public void seckill() {
		String identifierValue = "";
		try {
			// 1.获取锁
			identifierValue = lockRedis.getRedisLock("liuwq", 2000L, 5000L);
			if (StringUtils.isBlank(identifierValue)) {
				System.out.println(Thread.currentThread().getName() + ",获取锁失败，因为获取锁时间超时...");
				return;
			}
			System.out.println(Thread.currentThread().getName() + "获取锁成功,锁的id:" + identifierValue + ",count = " + ++count);

			// 处理业务逻辑
			Thread.sleep(500);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 2.释放锁
			lockRedis.unRedisLock("liuwq", identifierValue);
		}

	}
}
