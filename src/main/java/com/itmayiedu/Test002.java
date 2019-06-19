package com.itmayiedu;

public class Test002 {

	public static void main(String[] args) {
		SeckillService seckillService = new SeckillService();

		for (int i = 0; i < 100; i++) {
			new Thread(() -> {
				seckillService.seckill();
			}).start();
		}
	}
}