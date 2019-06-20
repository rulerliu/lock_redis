package com.itmayiedu;

public class Test003 {

	public static void main(String[] args) {
		SeckillService seckillService = new SeckillService();

		for (int i = 0; i < 100; i++) {
			new Thread(() -> {
				seckillService.seckill2();
			}, "thread:" + i).start();
		}

//		AtomicInteger count = new AtomicInteger(0);
////		int i = count.addAndGet(1); // i=1,count=1
////		int i = count.getAndAdd(1);// i=0,count=1
////		int i = count.incrementAndGet();// i=1,count=1
//		int i = count.getAndIncrement();// i=0,count=1
//		System.out.println(count.getAndIncrement());
//		System.out.println(i);
//		System.out.println(count.intValue());
	}
}