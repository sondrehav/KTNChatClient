package shared;

import java.time.LocalDateTime;

public class Message {
	public static void main(String[] args){
		System.out.println(new Message("Hello?", LocalDateTime.now(), "hah"));
	}
	String content;
	LocalDateTime timestamp;
	String username;
	public Message(String content, LocalDateTime timestamp, String username) {
		this.content = content;
		this.timestamp = timestamp;
		this.username = username;
	}
	public void print(){
		//display(content, username, timestamp);
	}
}
