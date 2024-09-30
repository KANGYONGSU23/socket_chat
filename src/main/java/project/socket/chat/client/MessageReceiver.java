package project.socket.chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class MessageReceiver implements Runnable {
	private Socket socket;

	public MessageReceiver(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		InputStream input = null;
		BufferedReader reader = null;

		try {
			input = socket.getInputStream();
			reader = new BufferedReader(new InputStreamReader(input));
			String inputMessage;
			while ((inputMessage = reader.readLine()) != null) {
				System.out.println(inputMessage);
				if (inputMessage.equals("[server와 연결 해제 됨]")) 
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (input != null)
					input.close();
				if (reader != null)
					reader.close();
				if (socket != null && !socket.isClosed())
					socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

}
