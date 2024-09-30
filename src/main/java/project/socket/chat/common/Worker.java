package project.socket.chat.common;

import java.util.concurrent.BlockingQueue;

public class Worker extends Thread {
	private final BlockingQueue<Runnable> tq;
	
	public Worker(BlockingQueue<Runnable> tq) {
		this.tq = tq;
	}
	
	@Override
	public void run() {
		while(!Thread.currentThread().isInterrupted()) {
			try {
				Runnable task = tq.take();
				task.run();
			} catch(InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
}
