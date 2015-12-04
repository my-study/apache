package com.hailiang.study.apache.zookeeper.recipes.barrier;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;

import com.hailiang.zookeeper.curator.CuratorClientFactory;

public class DistributedBarrierDemo {
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
		DistributedBarrier controlBarrier = new DistributedBarrier(client, PATH);
		controlBarrier.setBarrier();
		
		for (int i = 0; i < QTY; i++) {
			final DistributedBarrier barrier = new DistributedBarrier(client, PATH);
			final int idx = i;
			Callable<Void> task = new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					Thread.sleep((long) (3 * Math.random()));
					System.out.println("客户端#" + idx + "等待barrier(栅栏)");
					barrier.waitOnBarrier();
					System.out.println("客户端#" + idx + "开始...");
					return null;
				}
			};
			exec.submit(task);
		}
		Thread.sleep(10000);
        System.out.println("所有的Barrier已准备就绪");
		controlBarrier.removeBarrier();
		
		exec.shutdown();
		exec.awaitTermination(5, TimeUnit.MINUTES);
	}
	
}
