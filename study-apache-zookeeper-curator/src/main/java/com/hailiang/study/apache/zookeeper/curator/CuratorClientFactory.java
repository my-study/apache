package com.hailiang.study.apache.zookeeper.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * 客户端连接工程类
 */
public class CuratorClientFactory {
	public static final String CONNECT_STRING = "192.168.199.128:2181,192.168.199.129:2181,192.168.199.130:2181";
	
	public static CuratorFramework newClient() {
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, Integer.MAX_VALUE);
		CuratorFramework client = CuratorFrameworkFactory.builder()
//				.connectString("192.168.199.128:2181,192.168.199.129:2181,192.168.199.130:2181")
				.connectString(CONNECT_STRING)
				.sessionTimeoutMs(30000)
				.connectionTimeoutMs(30000)
				.retryPolicy(retryPolicy)
				.namespace("cfg")
				.defaultData(null)
				.build();

		return client;
	}
}
