package project.socket.chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class SocketClient {
	private static SocketClient instance;

	private SocketClient() {
	}

	public static synchronized SocketClient getInstance() {
		if (instance == null) {
			instance = new SocketClient();
		}
		return instance;
	}

	public void start() {

		BlockingQueue<String> mq = new SynchronousQueue<>();

		Socket socket;
		try {
			System.out.println("서버 연결 중...");

			socket = new Socket("127.0.0.1", 8000);

			
			// 접속 허용 대기
			Thread readyThread = new Thread(() -> {
				while (true) {
					try {
						BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						String message = reader.readLine();
						if (message.equals("%%서버 연결 허용")) {
							System.out.println("서버에 연결되었습니다.[127.0.0.1:8000]");
							break;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			try {
				readyThread.start();
				readyThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			Thread tr1 = new Thread(new Scanner(mq));
			Thread tr2 = new Thread(new Sender(socket, mq));
			Thread tr3 = new Thread(new MessageReceiver(socket));

			tr1.start();
			tr2.start();
			tr3.start();

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
