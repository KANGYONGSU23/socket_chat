package project.socket.chat.common;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadPool {
	private List<Thread> threadList;
	private BlockingQueue<Runnable> tq;

	private final int maxThreads;
	private int currentThreadCount;

	public ThreadPool() {
		threadList = new ArrayList<>();
		tq = new LinkedBlockingDeque<>();
		this.maxThreads = 3;
		this.currentThreadCount = 2;
		createThread(currentThreadCount);
	}

	public ThreadPool(int threadCount, int maxThreadCount) {
		threadList = new ArrayList<>();
		tq = new LinkedBlockingDeque<>();
		maxThreads = maxThreadCount;
		currentThreadCount = threadCount;

		createThread(threadCount);
	}

	// 새 스레드 1개 생성
	private void createThread() {
		Thread thread = new Worker(tq);
		thread.start();
		threadList.add(thread);
	}

	// 새 스레드 생성 - 스레드 개수 설정.
	private void createThread(int i) {
		for (int j = 0; j < i; j++) {
			Thread thread = new Worker(tq);
			thread.start();
			threadList.add(thread);
		}
	}

	// 작업을 작업 큐에 할당.
	// producer
	public void execute(Runnable runnable) {
		try {
			synchronized (this) {
				// 빈 작업을 할당해서 스레드가 새로운 작업을 할 수 있는 상태인지 확인함.
				tq.offer(() -> {});
				
				// 빈 작업을 할당했을 때 큐에서 바로 빠져나가지 않고 남아있다면 = 모든 스레드가 작업 중. = 작업이 큐에서 대기.
				// 새로운 스레드를 만들어서 할당.
				if(tq.size() > 0 && currentThreadCount < maxThreads) {
					createThread();
					currentThreadCount++;
				}
			}
			tq.put(runnable);
		} catch (InterruptedException e) {
			log.error("작업을 할당할 수 없음.");
			e.printStackTrace();
		}
	}

}
