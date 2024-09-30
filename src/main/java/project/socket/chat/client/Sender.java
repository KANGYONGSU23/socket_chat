package project.socket.chat.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class Sender implements Runnable {

	private Socket socket;
	private BlockingQueue<String> mq;

	public Sender(Socket socket, BlockingQueue<String> mq) {
		this.socket = socket;
		this.mq = mq;
	}

	public void run() {
		String message;
		PrintWriter out = null;

		try {
			out = new PrintWriter(socket.getOutputStream(), true);

			while (true) {
				message = mq.take();
				out.println(message);
				if (message.equals("exit"))
					break;
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null)
				out.close();
		}
	}
}
