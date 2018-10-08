/**
 * 功能说明:
 * 功能作者:
 * 创建日期:
 * 版权归属:每特教育|蚂蚁课堂所有 www.itmayiedu.com
 */
package com.itmayiedu;

import java.util.UUID;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 功能说明: <br>
 * 创建作者:每特教育-余胜军<br>
 * 创建时间:2018年8月30日 下午9:45:40<br>
 * 教育机构:每特教育|蚂蚁课堂<br>
 * 版权说明:上海每特教育科技有限公司版权所有<br>
 * 官方网站:www.itmayiedu.com|www.meitedu.com<br>
 * 联系方式:qq644064779<br>
 * 注意:本内容有每特教育学员共同研发,请尊重原创版权
 */
public class LockRedis {
	
	// redis线程池
	private JedisPool jedisPool;
	
	// 同时在redis上创建相同的一个key 相同key 名称
	private String redislockKey = "redis_lock";

	public LockRedis(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}
	// redis 以key （redislockKey） 和value（随机不能够重复数字 锁的id）方式进行存储

	// redis实现分布式锁 有两个超时 时间问题
	/**
	 * 两个超时时间含义：<br>
	 * 1.在获取锁之前的超时时间----在尝试获取锁的时候，如果在规定的时间内还没有获取锁，直接放弃。<br>
	 * 2.在获取锁之后的超时时间---当获取锁成功之后，对应的key 有对应有效期，对应的key 在规定时间内进行失效
	 */

	/**
	 * acquireTimeout
	 * 
	 * @param acquireTimeout
	 *            在获取锁之前的超时时间
	 * @param timeOut
	 *            在获取锁之后的超时时间
	 */
	// 基于redis实现分布式锁代码思路 核心方法 获取锁 、释放锁
	public String getRedisLock(Long acquireTimeout, Long timeOut) {
		Jedis conn = null;

		try {
			// 1.建立redis连接
			conn = jedisPool.getResource();
			// 2.定义 redis 对应key 的value值( uuid) 作用 释放锁 随机生成value
			String identifierValue = UUID.randomUUID().toString();

			// 3.定义在获取锁之后的超时时间
			int expireLock = (int) (timeOut / 1000);// 以秒为单位
			// 4.定义在获取锁之前的超时时间
			// 5.使用循环机制 如果没有获取到锁，要在规定acquireTimeout时间 保证重复进行尝试获取锁（乐观锁）
			// 使用循环方式重试的获取锁
			Long endTime = System.currentTimeMillis() + acquireTimeout;
			while (System.currentTimeMillis() < endTime) {
				// 获取锁
				// 6.使用setnx命令插入对应的redislockKey ，如果返回为1 成功获取锁
				if (conn.setnx(redislockKey, identifierValue) == 1) {
					System.out.println("############获取锁成功#############");
					// 设置对应key的有效期
					conn.expire(redislockKey, expireLock);
					return identifierValue;
				}

				// 为什么获取锁之后，还要设置锁的超时时间 目的是为了防止死锁
				// zookeeper实现分布式锁通过什么方式 防止死锁 设置session 有效期
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
		return null;

	}

	// 如果直接使用 conn.del(redislockKey); 保证对应是自己的创建redislockKey 删除对应自己的。

	// 释放redis锁
	public void unRedisLock(String identifierValue) {
		Jedis conn = null;
		// 1.建立redis连接
		conn = jedisPool.getResource();
		try {
			// 如果该锁的id 等于identifierValue 是同一把锁情况才可以删除
			if (conn.get(redislockKey).equals(identifierValue)) {
				//System.out.println(Thread.currentThread().getName() + ",identifierValue:" + identifierValue);
				System.out.println("##########释放锁成功##########");
				conn.del(redislockKey);
			}
		} catch (Exception e)

		{
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
		// 释放锁有两种 key自动有有效期
		// 整个程序执行完毕情况下，删除对应key

	}
}
