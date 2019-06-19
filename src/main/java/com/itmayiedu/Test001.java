/**
 * 功能说明:
 * 功能作者:
 * 创建日期:
 * 版权归属:每特教育|蚂蚁课堂所有 www.itmayiedu.com
 */
package com.itmayiedu;

import java.util.concurrent.locks.ReentrantLock;

//什么线程安全问题 在同一个jvm中，多个线程共享同一个全局变量做写的操作的时候，可能会收到其他线程的干扰。
class ThreadDemo implements Runnable {

	ReentrantLock lock = new ReentrantLock();

	// synchronized 只适合于单个jvm
	private static int count;

	public void run() {
		count();
	}

//	private synchronized void count() {
	private void count() {
//		lock.lock();
		try {
			Thread.sleep(15);
			count++;
			System.out.println(Thread.currentThread().getName() + ",count:" + count);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
}

public class Test001 {
	public static void main(String[] args) {

		ThreadDemo threadDemo = new ThreadDemo();
		for (int i = 0; i < 100; i++) {
			Thread thread = new Thread(threadDemo);
			thread.start();
		}
	}
}
