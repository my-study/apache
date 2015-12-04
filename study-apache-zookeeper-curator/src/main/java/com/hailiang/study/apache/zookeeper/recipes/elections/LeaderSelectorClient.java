package com.hailiang.study.apache.zookeeper.recipes.elections;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;

public class LeaderSelectorClient extends LeaderSelectorListenerAdapter implements Closeable {
	
	private final String name;
	private final LeaderSelector leaderSelector;
	private static final String PATH = "/leaderselector";
	
	public LeaderSelectorClient(CuratorFramework client, String name) {
		this.name = name;
		this.leaderSelector = new LeaderSelector(client, PATH, this);
		this.leaderSelector.autoRequeue();
	}
	
	public void start() throws IOException {
		leaderSelector.start();
	}

	/**
	 * client成为Leader后会调用此方法
	 */
	@Override
	public void takeLeadership(CuratorFramework arg0) throws Exception {
		int waitSeconds = (int) (Math.random() * 5) + 1;
		System.out.println(name + "是当前的leader");
		try {
			Thread.sleep(TimeUnit.SECONDS.toMillis(waitSeconds));
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
		} finally {
			System.out.println(name + " 让出领导权\n");
		}
	}

	@Override
	public void close() throws IOException {
		leaderSelector.close();
	}

}
