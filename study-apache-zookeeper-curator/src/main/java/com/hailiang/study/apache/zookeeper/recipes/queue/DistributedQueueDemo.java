package com.hailiang.study.apache.zookeeper.recipes.queue;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.recipes.queue.DistributedQueue;
import org.apache.curator.framework.recipes.queue.QueueBuilder;
import org.apache.curator.framework.recipes.queue.QueueConsumer;
import org.apache.curator.framework.recipes.queue.QueueSerializer;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.utils.CloseableUtils;

import com.hailiang.study.apache.zookeeper.curator.CuratorClientFactory;

public class DistributedQueueDemo {
	private static final String PATH = "/queue";
	
	public static void main(String[] args) throws Exception {
		CuratorFramework client = null;
		DistributedQueue<String> queue = null;
		
		try {
			client = CuratorClientFactory.newClient();
			client.getCuratorListenable().addListener(new CuratorListener() {
				@Override
				public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {
					System.out.println("CuratorEvent: " + event.getType().name());
				}
			});
			client.start();
			
			QueueConsumer<String> consumer = createQueueConsumer();
			QueueBuilder<String> builder = QueueBuilder.builder(client, consumer, createQueueSerializer(), PATH);
			queue = builder.buildQueue();
			queue.start();
			
			for (int i = 0; i < 10; i++) {
				queue.put("test-" + i);
				Thread.sleep((long)(3 * Math.random()));
			}
			System.out.println(getCurrentTimeStr() + ": 所有的数据已经生产完毕");
			
			Thread.sleep(20000);
		} finally {
			CloseableUtils.closeQuietly(client);
			CloseableUtils.closeQuietly(queue);
		}
	}
	
	private static QueueConsumer<String> createQueueConsumer() {
		return new QueueConsumer<String>() {
			@Override
			public void stateChanged(CuratorFramework client, ConnectionState newState) {
				System.out.println("connection new state: " + newState.name());
			}

			@Override
			public void consumeMessage(String message) throws Exception {
				System.out.println(getCurrentTimeStr() + ": 消费一条数据: " + message);
			}
		};
	}
	
	private static QueueSerializer<String> createQueueSerializer() {
        return new QueueSerializer<String>() {

            @Override
            public byte[] serialize(String item) {
                return item.getBytes();
            }

            @Override
            public String deserialize(byte[] bytes) {
                return new String(bytes);
            }

        };
    }
	
	private static String getCurrentTimeStr() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return sdf.format(new Date());
	}
}
