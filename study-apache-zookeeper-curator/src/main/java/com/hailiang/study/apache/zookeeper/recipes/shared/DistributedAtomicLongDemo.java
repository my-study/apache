package com.hailiang.study.apache.zookeeper.recipes.shared;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.CloseableUtils;

import com.google.common.collect.Lists;
import com.hailiang.study.apache.zookeeper.curator.CuratorClientFactory;

public class DistributedAtomicLongDemo {
	private static final int QTY = 5;
	private static final String PATH = "/distributed-atomic-long";
	
	public static void main(String[] args) throws Exception {
		CuratorFramework client = null;
		ExecutorService exec = null;
		try {
			client = CuratorClientFactory.newClient();
			client.start();
			
			List<DistributedAtomicLong> dalList = Lists.newArrayList();
			exec = Executors.newFixedThreadPool(QTY);
			for (int i = 0; i < QTY; i++) {
				final DistributedAtomicLong count = new DistributedAtomicLong(client, PATH, new RetryNTimes(10, 10));
				dalList.add(count);
				
				Callable<Void> task = new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						try {
                            //Thread.sleep(rand.nextInt(1000));
                            AtomicValue<Long> value = count.increment();
                            //AtomicValue<Long> value = count.decrement();
                            //AtomicValue<Long> value = count.add((long)rand.nextInt(20));
                            System.out.println("succeed: " + value.succeeded());
                            if (value.succeeded())
                                System.out.println("Increment: from " + value.preValue() + " to " + value.postValue());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

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
			CloseableUtils.closeQuietly(client);
		}
	}
	
}
