package project.socket.chat.client;

import java.util.concurrent.BlockingQueue;

public class Scanner implements Runnable {

	private BlockingQueue<String> mq;

	public Scanner(BlockingQueue<String> mq) {
		this.mq = mq;
	}

	public void run() {
		java.util.Scanner scanner = new java.util.Scanner(System.in);
		String message;
		String name;

		try {
			// 처음 한 번은 유저 이름 보내기
			System.out.print("이름을 입력하세요 : ");
			name = scanner.nextLine();
			mq.put(name);

			while (true) {
				message = scanner.nextLine();
				if (message != null && message.trim() != "")
					mq.put(message);
				if (message.equals("exit"))
					break;
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			scanner.close();
		}
	}
}
