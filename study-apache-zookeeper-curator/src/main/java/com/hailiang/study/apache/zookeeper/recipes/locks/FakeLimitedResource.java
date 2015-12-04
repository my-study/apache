package com.hailiang.study.apache.zookeeper.recipes.locks;

import java.util.concurrent.atomic.AtomicBoolean;

public class FakeLimitedResource {
	private final AtomicBoolean inUse = new AtomicBoolean(false);
	public void use() throws InterruptedException {
		if (!inUse.compareAndSet(false, true)) {
			throw new IllegalStateException("Needs to be used by on client at a time");
		}
		try {
			Thread.sleep((long) (3 * Math.random()));
		} finally {
			inUse.set(false);
		}
	}
}
