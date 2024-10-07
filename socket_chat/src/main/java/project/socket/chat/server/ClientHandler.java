package project.socket.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ClientHandler implements Runnable {
	private Map<String, Socket> clientSockets;
	static List<PrintWriter> list = Collections.synchronizedList(new ArrayList<PrintWriter>());

	Socket socket;
	InputStreamReader in;
	PrintWriter out;
	BufferedReader reader;

	String inputMessage;

	String name = "";

	public ClientHandler(Socket socket, Map<String, Socket> clientSockets) {
		this.socket = socket;
		this.clientSockets = clientSockets;

		try {
			in = new InputStreamReader(socket.getInputStream());

//			ByteBuffer buffer= ByteBuffer.allocateDirect(1024);
			
			out = new PrintWriter(socket.getOutputStream(), true);
			reader = new BufferedReader(in);

			list.add(out);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			
			this.out.println("%%서버 연결 허용");
			
			System.out.println(socket.getInetAddress()+":"+socket.getPort());
			clientSockets.put(socket.getInetAddress()+":"+socket.getPort(), socket);
			
			name = reader.readLine();
			System.out.println(name + " 새연결생성");
			sendAll("[서버] " + name + " 님이 접속하셨습니다.");
			System.out.println(clientSockets);

			while (true) {
				
				
				
				// 클라 입력 받아와서 서버 콘솔에 출력
				inputMessage = reader.readLine();
				System.out.println("[" + name + "] " + inputMessage);

				// "exit"입력 시 접속 해제
				if (inputMessage.equals("exit")) {
					break;
				}

				// 다른 클라에게 모두 전송
				sendAll("[" + name + "] " + inputMessage);

			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			disconnectClient();
		}
	}

	private synchronized void sendAll(String message) {
		for (PrintWriter out : list) {
			if (out != this.out) {
				out.println(message);
			}
		}

	}

	private void disconnectClient() {
		try {
			System.out.println(name + " 연결종료");
			clientSockets.remove(socket.getInetAddress()+":"+socket.getPort()); // 연결 종료 시 소켓 제거

			sendAll("[서버] " + name + " 님이 연결을 종료하셨습니다.");
			this.out.println("[server와 연결 해제 됨]");
			list.remove(out);

			if (socket != null && !socket.isClosed())
				socket.close();
			if (reader != null)
				reader.close();
			if (in != null)
				in.close();
			if (out != null)
				out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
