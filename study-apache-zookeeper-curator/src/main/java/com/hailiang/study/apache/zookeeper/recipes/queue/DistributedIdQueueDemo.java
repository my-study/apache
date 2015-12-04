package com.hailiang.study.apache.zookeeper.recipes.queue;

import java.io.UnsupportedEncodingException;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.recipes.queue.DistributedIdQueue;
import org.apache.curator.framework.recipes.queue.QueueBuilder;
import org.apache.curator.framework.recipes.queue.QueueConsumer;
import org.apache.curator.framework.recipes.queue.QueueSerializer;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.utils.CloseableUtils;

import com.hailiang.study.apache.zookeeper.curator.CuratorClientFactory;

public class DistributedIdQueueDemo {

	private static final String PATH = "/queue";
	
	public static void main(String[] args) throws Exception {
		CuratorFramework client = null;
		DistributedIdQueue<String> queue = null;
		
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
			
			queue = builder.buildIdQueue();
			queue.start();
			
			for (int i = 0; i < 10; i++) {
				queue.put("test-" + i, "Id-" + i);
				Thread.sleep((long) (25 * Math.random()));
				queue.remove("Id-" + i);
			}
			
			Thread.sleep(20000);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseableUtils.closeQuietly(queue);
	        CloseableUtils.closeQuietly(client);
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
				System.out.println("消费一条消息: " + message);
			}
		};
	}
	
	private static QueueSerializer<String> createQueueSerializer() {
		return new QueueSerializer<String>() {
			@Override
			public byte[] serialize(String item) {
				byte[] result = null;
				try {
					result = item.getBytes("UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				return result;
			}

			@Override
			public String deserialize(byte[] bytes) {
				String result = null;
				try {
					result = new String(bytes, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				return result;
			}
		};
	}
	
}
