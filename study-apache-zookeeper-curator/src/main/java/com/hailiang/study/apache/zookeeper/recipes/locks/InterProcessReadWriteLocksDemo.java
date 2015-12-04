package com.hailiang.study.apache.zookeeper.recipes.locks;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.apache.curator.utils.CloseableUtils;

import com.hailiang.study.apache.zookeeper.curator.CuratorClientFactory;

public class InterProcessReadWriteLocksDemo {
	private final InterProcessReadWriteLock lock;
	private final InterProcessLock readLock;
	private final InterProcessLock writeLock;
	private final FakeLimitedResource resource;
	private final String clientName;
	
	public InterProcessReadWriteLocksDemo(CuratorFramework client, String lockPath, FakeLimitedResource resource, String clientName) {
		this.resource = resource;
		this.clientName = clientName;
		lock = new InterProcessReadWriteLock(client, lockPath);
		readLock = lock.readLock();
		writeLock = lock.writeLock();
	}
	
	public void doWork(long time, TimeUnit unit) throws Exception {
		if (!writeLock.acquire(time, unit)) {
			throw new IllegalStateException(clientName + " could not acquire the writeLock");
		}
		System.out.println(clientName + " has the writeLock");
		
		if (!readLock.acquire(time, unit)) {
			throw new IllegalStateException(clientName + " could not acquire the readLock");
		}
		System.out.println(clientName + " has the readLock");
		
		try {
			resource.use();
		} finally {
			System.out.println(clientName + " releasing the lock");
			readLock.release();
			writeLock.release();
		}
	}
	
	public static void main(String[] args) throws Exception {
		final FakeLimitedResource resource = new FakeLimitedResource();
		int poolCount = 30;
		ExecutorService exec = Executors.newFixedThreadPool(poolCount);
		
		for (int i = 0; i < poolCount; i++) {
			final int index = i;
			Callable<Void> task = new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					CuratorFramework client = CuratorClientFactory.newClient();
					try {
						client.start();
						
						final InterProcessReadWriteLocksDemo readWriteLock = new InterProcessReadWriteLocksDemo(client, "/lock/read-write", resource,  "Client-" + index);
						for (int j = 0; j < 10000000; j++) {
							readWriteLock.doWork(10, TimeUnit.SECONDS);
						}
					} catch (Throwable e) {
						e.printStackTrace();
					} finally {
						CloseableUtils.closeQuietly(client);
					}
					return null;
				}
			};
			exec.submit(task);
		}
		exec.shutdown();
		exec.awaitTermination(10, TimeUnit.MINUTES);
	}
	
}
