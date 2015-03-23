package server;

import static shared.Display.display;
import static shared.Display.displayErr;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.charset.Charset;

public class Client extends Thread {
	
	private Socket socket = null;
	private ObjectInputStream socketInput = null;
	private ObjectOutputStream socketOutput = null;
	private String username = null;
	boolean running = false;
		
	public Client(Socket socket) {
		display("Connection from " + String.valueOf(socket.getInetAddress()) + ".");
		this.socket = socket;
		try {
			socketInput = new ObjectInputStream(socket.getInputStream());
			socketOutput = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			displayErr(String.valueOf(socket.getInetAddress()) + " could not connect.");
		}
		running = true;
	}
	
	public void _stop(){
		running = false;
	}
	
	public void run(){
		String s = "";
		while(running){
			try {
				if(socketInput.available()>0){		
					for(int i = 0; i < socketInput.available(); i++){
						byte b = socketInput.readByte();
						if(b == 3){
							// NEW LINE DETECTED; END OF MESSAGE
							System.out.println("\nRECIEVING:\n");
							System.out.println(s);
							System.out.println("\n");
							ServerParser.recieve(s, this);
							s = "";
						} else {
							s += (char) b;
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				running = false;
			}
		}
		close();
	}

	public void close() {
		try{
			if(socketOutput != null) socketOutput.close();
			if(socketInput != null) socketInput.close();
			if(socket != null) socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean sendMsg(String msg, String sender, int type) {
		try{
			String message = ServerParser.send(type, msg, sender)+Character.toChars(3)[0];
			if(ServerApplication.debug){
				System.out.println("\nSENDING:\n");
				System.out.println(message);
				System.out.println("\n");
			}
			socketOutput.write(message.getBytes(Charset.forName("UTF-8")));
			socketOutput.reset();
			return true;
		} catch (Exception e) {
			displayErr("Error sending message to "+username+".");
		}
		return false;
	}

	public void login(String content) {
		for(String s : ServerApplication.server.usernames()){
			if(s==null){
				continue;
			}
			if(content.contentEquals(s)){
				sendMsg("Username taken.", null, ServerParser.ERROR);
				return;
			}
		}
		this.username = content;
		display("User \'"+content+"\' signed in.");
	}

	public String getUsername() {
		return username;
	}

	public void help() {
		String str = "";
		for(String s : ServerApplication.helpText){
			str += s + "\n";
		}
		sendMsg(str, null, ServerParser.INFO);
	}

	public void log() {
		String sw = "";
		for(String s : ServerApplication.server.messages){
			sw += s+"\n";
		}
		sendMsg(sw, null, ServerParser.HISTORY);
	}

	public void names() {
		String msg = "";
		for(String s : ServerApplication.server.usernames()){
			msg += s + "\n";
		}
		sendMsg(msg, null, ServerParser.INFO);
	}

}