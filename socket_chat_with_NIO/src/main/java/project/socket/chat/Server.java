package project.socket.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Server {
	public static void main(String[] args) {
		// 연결된 클라이언트를 관리할 컬렉션
		Set<SocketChannel> allClient = new HashSet<>();

		try (ServerSocketChannel serverSocket = ServerSocketChannel.open()) {

			// 서비스 포트 설정 및 논블로킹 모드로 설정
			serverSocket.bind(new InetSocketAddress(15000));
			serverSocket.configureBlocking(false);

			// 채널 관리자(Selector) 생성 및 채널 등록
			Selector selector = Selector.open();
			serverSocket.register(selector, SelectionKey.OP_ACCEPT);

			System.err.println("============= [ 서버 준비 완료 ] =============");
			System.err.println("============= [ 클라이언트 대기 중 ] =============");

			// 버퍼의 모니터 출력을 위한 출력 채널 생성

			// 입출력 시 사용할 바이트버퍼 생성
			ByteBuffer inputBuf = ByteBuffer.allocate(1024);
			ByteBuffer outputBuf = ByteBuffer.allocate(1024);

			// 클라이언트 접속 시작
			while (true) {

				// 이벤트 발생할 때까지 스레드 블로킹
				selector.select();

				// 발생한 이벤트를 모두 Iterator에 담아줌
				Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

				// 발생한 이벤트들을 담은 Iterator의 이벤트를 하나씩 순서대로 처리함
				while (iterator.hasNext()) {

					// 현재 순서의 처리할 이벤트를 임시 저장하고 Iterator에서 지워줌
					SelectionKey key = iterator.next();
					iterator.remove();

					// 연결 요청중인 클라이언트를 처리할 조건문 작성
					if (key.isAcceptable()) {

						// 연결 요청중인 이벤트이므로 해당 요청에 대한 소켓 채널을 생성해줌
						ServerSocketChannel server = (ServerSocketChannel) key.channel();
						SocketChannel socket = server.accept();

						// Selector의 관리를 받기 위해서 논블로킹 채널로 바꿔줌
						socket.configureBlocking(false);

						// 연결된 클라이언트를 컬렉션에 추가
						allClient.add(socket);

						// 아이디를 입력받기 위한 출력을 해당 채널에 해줌
						socket.write(ByteBuffer.wrap("아이디를 입력해주세요. : ".getBytes()));

						// 아이디를 입력받을 차례이므로 읽기모드로 셀렉터에 등록해줌
						socket.register(selector, SelectionKey.OP_READ, new ClientInfo());
					}
					// 읽기 이벤트(클라이언트 -> 서버)가 발생한 경우
					else if (key.isReadable()) {

						// 현재 채널 정보를 가져옴 (attach된 사용자 정보도 가져옴)
						SocketChannel socket = (SocketChannel) key.channel();
						ClientInfo info = (ClientInfo) key.attachment();

						// 채널에서 데이터를 읽어옴
						try {
							socket.read(inputBuf);

							// 만약 클라이언트가 연결을 끊었다면 예외가 발생하므로 처리
						} catch (Exception e) {
							// 현재 SelectionKey를 셀렉터 관리대상에서 삭제
							key.cancel();

							// Set에서도 삭제
							allClient.remove(socket);

							// 서버에 종료 메세지 출력
							String end = info.getID() + "님의 연결이 종료되었습니다.\n";
							System.out.println(end);

							// 모든 클라이언트에게 메세지 출력
							outputBuf.put(end.getBytes());
							for (SocketChannel s : allClient) {
								// 자신을 제외한 클라이언트에게 종료 메세지 출력
								if (!socket.equals(s)) {
									outputBuf.flip();
									s.write(outputBuf);
								}
							}
							outputBuf.clear();
							continue;
						}

						// 현재 아이디가 없을 경우 아이디 등록
						if (info.isID()) {

							// 현재 inputBuf의 내용 중 개행문자를 제외하고 가져와서 ID로 넣어줌
							inputBuf.limit(inputBuf.position() - 2);
							inputBuf.position(0);
							byte[] b = new byte[inputBuf.limit()];
							inputBuf.get(b);
							info.setID(new String(b));

							// 서버에 출력
							String enter = info.getID() + "님이 입장하셨습니다.\n";

							System.out.println(enter);

							outputBuf.put(enter.getBytes());

							// 모든 클라이언트에게 메세지 출력
							for (SocketChannel s : allClient) {
								outputBuf.flip();
								s.write(outputBuf);
							}

							inputBuf.clear();
							outputBuf.clear();
							continue;

						}

						// 읽어온 데이터와 아이디 정보를 결합해 출력한 버퍼 생성
						inputBuf.flip();
						outputBuf.put((info.getID() + " : ").getBytes());
						outputBuf.put(inputBuf);
						outputBuf.flip();

						for (SocketChannel s : allClient) {
							if (!socket.equals(s)) {
								s.write(outputBuf);
								outputBuf.flip();
							}
						}

						inputBuf.clear();
						outputBuf.clear();

					}
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}

//package project.socket.chat;
//
//import java.io.IOException;
//import java.net.InetSocketAddress;
//import java.nio.ByteBuffer;
//import java.nio.channels.SelectionKey;
//import java.nio.channels.Selector;
//import java.nio.channels.ServerSocketChannel;
//import java.nio.channels.SocketChannel;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.Set;
//
//public class Server {
//
//	public static void main(String[] args) {
//
//		// 연결된 클라이언트를 관리할 컬렉션
//		Set<SocketChannel> allClient = new HashSet<>();
//
//		try (ServerSocketChannel serverSocket = ServerSocketChannel.open()) {
//
//			// 서비스 포트 설정 및 논블로킹 모드로 설정
//			serverSocket.bind(new InetSocketAddress(15000));
//			serverSocket.configureBlocking(false);
//
//			// 채널 관리자(Selector) 생성 및 채널 등록
//			Selector selector = Selector.open();
//			serverSocket.register(selector, SelectionKey.OP_ACCEPT); // 채널 등록
//
//			System.out.println("----------서버 접속 준비 완료----------");
//			// 버퍼의 모니터 출력을 위한 출력 채널 생성
//
//			// 입출력 시 사용할 바이트버퍼 생성
//			ByteBuffer inputBuf = ByteBuffer.allocate(1024);
//			ByteBuffer outputBuf = ByteBuffer.allocate(1024);
//
//			// 클라이언트 접속 시작
//			while (true) {
//
//				selector.select(); // 이벤트 발생할 때까지 스레드 블로킹
//
//				// 발생한 이벤트를 모두 Iterator에 담아줌
//				Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
//
//				// 발생한 이벤트들을 담은 Iterator의 이벤트를 하나씩 순서대로 처리함
//				while (iterator.hasNext()) {
//
//					// 현재 순서의 처리할 이벤트를 임시 저장하고 Iterator에서 지워줌
//					SelectionKey key = iterator.next();
//					iterator.remove();
//
//					// 연결 요청중인 클라이언트를 처리할 조건문 작성
//					if (key.isAcceptable()) {
//
//						// 연결 요청중인 이벤트이므로 해당 요청에 대한 소켓 채널을 생성해줌
//						ServerSocketChannel server = (ServerSocketChannel) key.channel();
//						SocketChannel clientSocket = server.accept();
//
//						// Selector의 관리를 받기 위해서 논블로킹 채널로 바꿔줌
//						clientSocket.configureBlocking(false);
//
//						// 연결된 클라이언트를 컬렉션에 추가
//						allClient.add(clientSocket);
//
//						// 아이디를 입력받기 위한 출력을 해당 채널에 해줌
//						clientSocket.write(ByteBuffer.wrap("아이디를 입력해주세요 : ".getBytes()));
//
//						// 아이디를 입력받을 차례이므로 읽기모드로 셀렉터에 등록해줌
//						clientSocket.register(selector, SelectionKey.OP_READ, new ClientInfo());
//
//					
//					// 읽기 이벤트(클라이언트 -> 서버)가 발생한 경우
//					} else if (key.isReadable()) {
//
//						// 현재 채널 정보를 가져옴 (attach된 사용자 정보도 가져옴)
//						SocketChannel readSocket = (SocketChannel) key.channel();
//						ClientInfo info = (ClientInfo) key.attachment();
//
//						// 채널에서 데이터를 읽어옴
//						try {
//							readSocket.read(inputBuf);
//
//							// 만약 클라이언트가 연결을 끊었다면 예외가 발생하므로 처리
//						} catch (Exception e) {
//							key.cancel(); // 현재 SelectionKey를 셀렉터 관리대상에서 삭제
//							allClient.remove(readSocket); // Set에서도 삭제
//							
//							// 서버에 종료 메세지 출력
//							String end = info.getID() + "님의 연결이 종료되었습니다.\n";
//							System.out.print(end);
//
//							// 자신을 제외한 클라이언트에게 종료 메세지 출력
//							outputBuf.put(end.getBytes());
//							for(SocketChannel s : allClient) {
//								if(!readSocket.equals(s)) {
//									outputBuf.flip();
//									s.write(outputBuf);
//								}
//							}
//							outputBuf.clear();
//							continue;
//						}
//
//						
//						// 현재 아이디가 없을 경우 아이디 등록
//						if (info.isID()) {
//							// 현재 inputBuf의 내용 중 개행문자를 제외하고 가져와서 ID로 넣어줌
//							inputBuf.limit(inputBuf.position() - 2);
//							inputBuf.position(0);
//							byte[] b = new byte[inputBuf.limit()];
//							inputBuf.get(b);
//							info.setID(new String(b));
//
//							// 서버에 출력
//							String enter = info.getID() + "님이 입장하셨습니다.\n";
//							System.out.print(enter);
//							
//							outputBuf.put(enter.getBytes());
//							
//							// 모든 클라이언트에게 메세지 출력
//							for(SocketChannel s : allClient) {
//							
//								outputBuf.flip();
//								s.write(outputBuf);
//							}
//							
//							inputBuf.clear();
//							outputBuf.clear();
//							continue;
//						}
//						
//						// 읽어온 데이터와 아이디 정보를 결합해 출력한 버퍼 생성
//						inputBuf.flip();
//						outputBuf.put((info.getID() + " : ").getBytes());
//						outputBuf.put(inputBuf);
//						outputBuf.flip();
//						
//						for(SocketChannel s : allClient) {
//							if (!readSocket.equals(s)) {
//								
//								s.write(outputBuf);
//								outputBuf.flip();
//							}
//						}
//						
//						inputBuf.clear();
//						outputBuf.clear();
//					}
//				}
//			}
//
//		} catch (
//
//		IOException e) {
//
//			e.printStackTrace();
//		}
//	}
//}