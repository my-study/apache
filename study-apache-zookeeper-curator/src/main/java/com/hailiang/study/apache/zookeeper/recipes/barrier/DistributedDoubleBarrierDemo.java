package com.hailiang.study.apache.zookeeper.recipes.barrier;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;

import com.hailiang.study.apache.zookeeper.curator.CuratorClientFactory;

public class DistributedDoubleBarrierDemo {
	private static final int QTY = 5;
	private static final String PATH = "/barrier";
	
	public static void main(String[] args) throws Exception {
		CuratorFramework client = CuratorClientFactory.newClient();
		client.getCuratorListenable().addListener(new CuratorListener() {
			@Override
			public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {
				System.out.println("CuratorEvent: " + event.getType().name());
			}
		});
		client.start();
		
		ExecutorService exec = Executors.newFixedThreadPool(QTY);
		
		for (int i = 0; i < QTY; i++) {
			final DistributedDoubleBarrier barrier = new DistributedDoubleBarrier(client, PATH, QTY);
			final int idx = i;
			Callable<Void> task = new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					Thread.sleep((long) (3 * Math.random()));
					System.out.println("客户端#" + idx + " enter");
					barrier.enter();
					System.out.println("客户端#" + idx + " begin");
					Thread.sleep((long) (3000 * Math.random()));
					barrier.leave();
					System.out.println("客户端#" + idx + " leave");
					return null;
				}
			};
			exec.submit(task);
		}
		
		exec.shutdown();
		exec.awaitTermination(5, TimeUnit.MINUTES);
	}
	
}
