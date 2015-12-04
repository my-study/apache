package com.hailiang.study.apache.zookeeper.recipes.locks;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.apache.curator.utils.CloseableUtils;

import com.hailiang.study.apache.zookeeper.curator.CuratorClientFactory;

public class InterProcessSemaphoreMutexDemo {

	public static void main(String[] args) throws Exception {
		CuratorFramework client = CuratorClientFactory.newClient();
		client.start();
		final InterProcessSemaphoreMutex ipsm = new InterProcessSemaphoreMutex(client, "/lock/inter-process-semaphore-mutex");
		printProcess(ipsm);
		
		System.out.println("开始获取锁...");
		boolean flag = ipsm.acquire(12, TimeUnit.SECONDS);
		System.out.println(flag ? "成功获得锁" : "未获得锁");
		
		printProcess(ipsm);
		
		TimeUnit.SECONDS.sleep(20);
		
		if (ipsm.isAcquiredInThisProcess()) {
			ipsm.release();
		}
		
		printProcess(ipsm);
		CloseableUtils.closeQuietly(client);
		
	}
	
	private static void printProcess(final InterProcessSemaphoreMutex processSemaphoreMutex) {
		// 在本进程中锁是否激活（是否正在执行）
		System.out.println("isAcquiredInThisProcess：" + processSemaphoreMutex.isAcquiredInThisProcess());
	}
}
