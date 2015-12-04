package com.hailiang.study.apache.zookeeper.curator.watcher;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.utils.CloseableUtils;

import com.hailiang.study.apache.zookeeper.curator.CuratorClientFactory;

/**
 * 请先执行WatcherNodeCache
 */
public class WatcherPathChildrenCache {

	public static void main(String[] args) throws Exception {
		CuratorFramework client = CuratorClientFactory.newClient();
		client.start();
		
		final String nodePath = "/zk";
		
		
		
		/**在注册监听器的时候，如果传入此参数，当事件触发时，逻辑由线程池处理**/
		ExecutorService execPool = Executors.newFixedThreadPool(1);
		
		final PathChildrenCache pcc = new PathChildrenCache(client, nodePath, true);
		pcc.start(StartMode.POST_INITIALIZED_EVENT);
		
		/**监听节点的数据变化情况**/
		pcc.getListenable().addListener(new PathChildrenCacheListener() {
			@Override
			public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
				switch (event.getType()) {
				case CHILD_ADDED:
					System.out.println("增加节点:" + event.getData().getPath());
					break;
				case CHILD_REMOVED:
					System.out.println("删除节点:" + event.getData().getPath());
					break;
				case CHILD_UPDATED:
					System.out.println("更新节点:" + event.getData().getPath());
					break;
				default:
                    break;
				}
			}
		}, execPool);
		
		TimeUnit.SECONDS.sleep(100);
		
		execPool.shutdown();
		CloseableUtils.closeQuietly(client);
	}
}
