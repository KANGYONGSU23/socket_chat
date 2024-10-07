package project.socket.chat;
class ClientInfo {

	// 아직 아이디 입력이 안된 경우 true
	private boolean idCheck = true;
	private String id;

	// ID가 들어있는지 확인
	boolean isID() {

		return idCheck;
	}

	// ID를 입력받으면 false로 변경
	private void setCheck() {

		idCheck = false;
	}

	// ID 정보 반환
	String getID() {

		return id;
	}

	// ID 입력
	void setID(String id) {
		this.id = id;
		setCheck();
	}
}