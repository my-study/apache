package com.hailiang.study.apache.zookeeper.recipes.locks;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import com.hailiang.study.apache.zookeeper.curator.CuratorClientFactory;

public class InterProcessMutexDemo {

	public static void main(String[] args) throws Exception {
		final CuratorFramework client = CuratorClientFactory.newClient();
		client.start();
		
		Thread t1 = new Thread(new Runnable() {
			@Override
			public void run() {
				doWithLock(client, "/lock/inter-process-metex");
			}
		}, "t1");
		
		Thread t2 = new Thread(new Runnable() {
			@Override
			public void run() {
				doWithLock(client, "/lock/inter-process-metex");
			}
		}, "t2");
		
		t1.start();
		t2.start();
	}
	
	
	private static void doWithLock(CuratorFramework client, String lockZnodePath) {
		InterProcessMutex ipm = new InterProcessMutex(client, lockZnodePath);
		try {
			if (ipm.acquire(10, TimeUnit.SECONDS)) {
				System.out.println("thread name = " + Thread.currentThread().getName() + " hold lock");
				TimeUnit.SECONDS.sleep(5);
				System.out.println("thread name = " + Thread.currentThread().getName() + " release lock");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				ipm.release();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
