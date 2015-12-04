package com.hailiang.study.apache.zookeeper.curator.watcher;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;

import com.hailiang.study.apache.zookeeper.curator.CuratorClientFactory;

public class WatcherTreeCache {

	public static void main(String[] args) throws Exception {
		CuratorFramework client = CuratorClientFactory.newClient();
		client.start();
		
		String znodePath = "/zk/treecache";
		if (client.checkExists().forPath(znodePath) == null) {
			client.create().creatingParentsIfNeeded().forPath(znodePath, "test".getBytes());
		}
		
		TreeCache tc = new TreeCache(client, "/zk/treecache");
		tc.start();
		
		Executor exec = Executors.newFixedThreadPool(1);
		
		tc.getListenable().addListener(new TreeCacheListener() {
			@Override
			public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
				System.out.println("==============start================");
				System.out.println(event);
				ChildData cd = event.getData();
				if (cd != null) {
					System.out.println("path = " + cd.getPath() + ", data = " + (cd.getData() == null ? "" : new String(cd.getData())));
				} else {
					System.out.println("ChildData is null");
				}
				switch (event.getType()) {
				case NODE_ADDED:
					System.out.println("增加节点:" + event.getData().getPath());
					break;
				case NODE_REMOVED:
					System.out.println("删除节点:" + event.getData().getPath());
					break;
				case NODE_UPDATED:
					System.out.println("更新节点:" + event.getData().getPath());
					break;
				default:
					break;
				}
				System.out.println("==============end================");
			}
		}, exec);
		
		TimeUnit.SECONDS.sleep(20);
	}
}
