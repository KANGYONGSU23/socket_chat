package project.socket.chat.users;

import project.socket.chat.client.SocketClient;

public class User {
	public static void main(String[] args) {

		SocketClient client = SocketClient.getInstance();
		client.start();
	}
}
