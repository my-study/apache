package com.hailiang.study.apache.zookeeper.recipes.locks;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMultiLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;

import com.hailiang.study.apache.zookeeper.curator.CuratorClientFactory;

public class InterProcessMultiLockDemo {

	private static final String PATH1 = "/lock/multi-lock1";
	private static final String PATH2 = "/lock/multi-lock2";
	
	public static void main(String[] args) throws Exception {
		FakeLimitedResource resource = new FakeLimitedResource();
		
		CuratorFramework client = CuratorClientFactory.newClient();
		client.start();
		
		InterProcessLock lock1 = new InterProcessMutex(client, PATH1);
		InterProcessLock lock2 = new InterProcessSemaphoreMutex(client, PATH2);
		
		InterProcessMultiLock lock = new InterProcessMultiLock(Arrays.asList(lock1, lock2));
		
		if (!lock.acquire(10, TimeUnit.SECONDS)) {
			throw new IllegalStateException("could not acquire the lock");
		}
		System.out.println("has the lock");
		
		System.out.println("has the lock1: " + lock1.isAcquiredInThisProcess());
		System.out.println("has the lock2: " + lock2.isAcquiredInThisProcess());
		
		try {
			resource.use();
		} finally {
			System.out.println("releasing the lock");
			lock.release();
		}
		
		System.out.println("has the lock1: " + lock1.isAcquiredInThisProcess());
		System.out.println("has the lock2: " + lock2.isAcquiredInThisProcess());
	}
	
	
}
