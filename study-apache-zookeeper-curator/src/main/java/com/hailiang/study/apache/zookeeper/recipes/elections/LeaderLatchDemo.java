package com.hailiang.study.apache.zookeeper.recipes.elections;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.utils.CloseableUtils;

import com.hailiang.zookeeper.curator.CuratorClientFactory;

public class LeaderLatchDemo {
	public static int count = 0;
	
	public static void main(String[] args) throws Exception {
		CuratorFramework client = CuratorClientFactory.newClient();
		client.start();
		
		LeaderLatch latch = new LeaderLatch(client, "/path");
		latch.start();
		latch.await();
		System.out.println("我启动了...");
		TimeUnit.SECONDS.sleep(30);
		CloseableUtils.closeQuietly(latch);
		CloseableUtils.closeQuietly(client);
	}
}
