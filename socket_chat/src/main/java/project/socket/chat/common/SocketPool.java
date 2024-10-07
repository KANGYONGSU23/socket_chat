//package project.socket.chat.common;
//
//import java.io.IOException;
//import java.net.Socket;
//import java.util.ArrayList;
//import java.util.List;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//public class SocketPool {
//	private static SocketPool instance;
//
//	private List<Socket> clientSockets = new ArrayList<>();
//	private int usedSocktsNum = 0;
//	
//	private SocketPool() {
//	}
//
//	// 같은 인스턴스 참조
//	public static synchronized SocketPool getInstance() {
//		if (instance == null) {
//			instance = new SocketPool();
//			instance.init();
//		}
//		return instance;
//	}
//	
//	private void init() {
//		this.clientSockets.clear();
//		this.usedSocktsNum = 0;
//		for(int i = 0 ; i < 5 ; i++) {
//			newSocket();
//		}
//		
//		log.debug("[SocketPool] "+clientSockets.size()+"개의 소켓 생성됨.");
//	}
//	
//	// 소켓 리스트 모두 가져옴
//	public List<Socket> getSocketAll() {
//		return clientSockets;
//	}
//
//	// 사용 중이지 않은 Socket 가져옴
//	public Socket getSocket() {
//		if(clientSockets.size() == usedSocktsNum) {
//			newSocket();
//			usedSocktsNum++;
//		}
//		return clientSockets.get(usedSocktsNum);
//	}
//	
//	// 사용한 소켓 반환.
//	// 반환당한 소켓은 제일 뒤로 보냄
//	public void returnSocket(Socket socket) {
//		clientSockets.remove(socket);
//		clientSockets.add(socket);
//		usedSocktsNum--;
//	}
//
//	// 새로우 소켓 생성
//	private void newSocket() {
//		try {
//			// #todo host ip랑 포트번호 어떻게 좀 하기.
//			clientSockets.add(new Socket("127.0.0.1", 8000));
//		} catch (IOException e) {
//			log.error("소켓 생성 실패");
//			e.printStackTrace();
//		}
//	}
//
//}
//
