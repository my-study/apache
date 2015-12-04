package com.hailiang.study.apache.zookeeper.recipes.shared;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.shared.SharedCount;
import org.apache.curator.framework.recipes.shared.SharedCountListener;
import org.apache.curator.framework.recipes.shared.SharedCountReader;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.utils.CloseableUtils;

import com.google.common.collect.Lists;
import com.hailiang.study.apache.zookeeper.curator.CuratorClientFactory;

public class SharedCountDemo implements SharedCountListener {
	private static final int QTY = 5;
	private static final String PATH = "/shared-counter";
	
	public static void main(String[] args) throws Exception {
		CuratorFramework client = null;
		ExecutorService exec = null;
		SharedCount sc = null;
		List<SharedCount> scList = null;
		try {
			client = CuratorClientFactory.newClient();
			client.start();
			
			sc = new SharedCount(client, PATH, 0);
			sc.addListener(new SharedCountDemo());
			sc.start();
			
			final Random rand = new Random();
			exec = Executors.newFixedThreadPool(QTY);
			scList = Lists.newArrayList();
			for (int i = 0; i < QTY; i++) {
				final SharedCount count = new SharedCount(client, PATH, 0);
				scList.add(count);
				
				Callable<Void> task = new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						count.start();
						Thread.sleep(rand.nextInt(10000));
						System.out.println("Increment:" + count.trySetCount(count.getVersionedValue(), count.getCount() + rand.nextInt(10)));
						return null;
					}
				};
				exec.submit(task);
			}
		} finally {
			if (exec != null) {
				exec.shutdown();
				exec.awaitTermination(10, TimeUnit.MINUTES);
			}
			if (scList != null && !scList.isEmpty()) {
				for (SharedCount c : scList) {
					CloseableUtils.closeQuietly(c);
				}
			}
			CloseableUtils.closeQuietly(sc);
			CloseableUtils.closeQuietly(client);
		}
		
	}
	

	@Override
	public void stateChanged(CuratorFramework client, ConnectionState newState) {
		System.out.println("State changed: " + newState.toString());
	}

	@Override
	public void countHasChanged(SharedCountReader sharedCount, int newCount) throws Exception {
		System.out.println("Counter's value is changed to " + newCount);        
	}

}
