package com.hailiang.study.apache.zookeeper.curator.watcher;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;

import com.hailiang.zookeeper.curator.CuratorClientFactory;

public class WatcherNodeCache {

	public static void main(String[] args) throws Exception {
		CuratorFramework client = CuratorClientFactory.newClient();
		client.start();
		
		final String nodePath = "/zk/cnode";
		
		//创建节点
		if (client.checkExists().forPath(nodePath) == null) {
			client.create().creatingParentsIfNeeded().forPath(nodePath, "data content".getBytes("UTF-8"));
		}
		
		/**在注册监听器的时候，如果传入此参数，当事件触发时，逻辑由线程池处理**/
		ExecutorService execPool = Executors.newFixedThreadPool(2);
		
		final NodeCache nc = new NodeCache(client, nodePath, false);
		nc.start(true);
		
		System.out.println("节点[" + nodePath + "]数据为：" + new String(nc.getCurrentData().getData(), "UTF-8"));
		
		/**监听节点的数据变化情况**/
		nc.getListenable().addListener(new NodeCacheListener() {
			@Override
			public void nodeChanged() throws Exception {
				System.out.println("节点[" + nodePath + "]数据发生改变，新数据为：" + new String(nc.getCurrentData().getData(), "UTF-8"));
			}
		}, execPool);
		
		//nc.close();//不能关闭
		
		TimeUnit.HOURS.sleep(10);
	}
}
