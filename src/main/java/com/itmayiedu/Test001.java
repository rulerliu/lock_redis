/**
 * 功能说明:
 * 功能作者:
 * 创建日期:
 * 版权归属:每特教育|蚂蚁课堂所有 www.itmayiedu.com
 */
package com.itmayiedu;

//什么线程安全问题 在同一个jvm中，多个线程共享同一个全局变量做写的操作的时候，可能会收到其他线程的干扰。
class ThreadDemo implements Runnable {
	// synchronized 至适合于单个jvm
	private static int count;

	public synchronized void run() {
		count();

	}

	private synchronized void count() {
		try {
			Thread.sleep(15);
		} catch (Exception e) {
			// TODO: handle exception
		}
		count++;
		System.out.println(Thread.currentThread().getName() + ",count:" + count);
	}
}

/**
 * 功能说明: <br>
 * 创建作者:每特教育-余胜军<br>
 * 创建时间:2018年8月30日 下午8:46:26<br>
 * 教育机构:每特教育|蚂蚁课堂<br>
 * 版权说明:上海每特教育科技有限公司版权所有<br>
 * 官方网站:www.itmayiedu.com|www.meitedu.com<br>
 * 联系方式:qq644064779<br>
 * 注意:本内容有每特教育学员共同研发,请尊重原创版权
 */
public class Test001 {
	public static void main(String[] args) {

		ThreadDemo threadDemo = new ThreadDemo();
		for (int i = 0; i < 100; i++) {
			Thread thread = new Thread(threadDemo);
			thread.start();
		}
	}
}
