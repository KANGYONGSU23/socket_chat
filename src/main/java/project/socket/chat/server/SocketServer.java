package project.socket.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import lombok.extern.slf4j.Slf4j;
import project.socket.chat.common.ThreadPool;

@Slf4j
public class SocketServer {

	private static Map<String, Socket> client = new HashMap<>();

	public SocketServer() {
	}

	public static void main(String[] args) {

		ServerSocket serverSocket = null;

		try {
			// 서버 소켓 열기
			serverSocket = new ServerSocket(8000);
			System.out.println("[클라이언트 연결 대기중...]");

			//스레드 풀
			//캐시드 스레드 풀 = 기존의 스레드를 재사용할 수 있는 상태라면 재사용한다.
//			ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
			ThreadPool threadPool = new ThreadPool();
			
			while (true) {
				Socket socket = serverSocket.accept();
				ClientHandler clientHanler = new ClientHandler(socket, client);
				threadPool.execute(clientHanler);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			System.out.println("[서버 종료]");
			// 소켓과 스트림 닫기
			try {
				for (Entry<String, Socket> e : client.entrySet()) {
					if (e.getValue() != null)
						e.getValue().close();
				}
				if (serverSocket != null)
					serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
