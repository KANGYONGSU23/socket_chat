package project.socket.chat;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;

public class Client {

	public static void main(String[] args) {

		Thread systemIn;
		// 서버 IP와 포트로 연결되는 소켓채널 생성
		try (SocketChannel socket = SocketChannel.open
				(new InetSocketAddress("127.0.0.1", 15000))) {

			// 모니터 출력에 출력할 채널 생성
			WritableByteChannel out = Channels.newChannel(System.out);

			// 버퍼 생성
			ByteBuffer buf = ByteBuffer.allocate(1024);

			// 출력을 담당할 스레드 생성 및 실행
			systemIn = new Thread(new SystemIn(socket));
			systemIn.start();

			while (true) {

				socket.read(buf); // 읽어서 버퍼에 넣고
				buf.flip();
				out.write(buf); // 모니터에 출력
				buf.clear();
			}

		} catch (IOException e) {

			System.out.println("서버와 연결이 종료되었습니다.");
		}
	}
}

// 입력을 담당하는 클래스
class SystemIn implements Runnable {

	SocketChannel socket;
	

	// 연결된 소켓 채널과 모니터 출력용 채널을 생성자로 받음
	SystemIn(SocketChannel socket) {
		this.socket = socket;
	}

	@Override
	public void run() {

		// 키보드 입력받을 채널과 저장할 버퍼 생성
		ReadableByteChannel in = Channels.newChannel(System.in);
		ByteBuffer buf = ByteBuffer.allocate(1024);

		try {
			while (true) {
				in.read(buf); // 읽어올때까지 블로킹되어 대기상태
				buf.flip();
				socket.write(buf); // 입력한 내용을 서버로 출력
				buf.clear();
			}

		} catch (IOException e) {
			System.out.println("채팅 불가.");
		}
	}
}