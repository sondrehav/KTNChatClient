package server;

import static shared.Display.display;
import static shared.Display.displayErr;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;

public class Client extends Thread {
	
	private Socket socket = null;
	private InputStream socketInput = null;
	private OutputStream socketOutput = null;
	private String username = null;
	boolean running = false;
		
	public Client(Socket socket) {
		display("Connection from " + String.valueOf(socket.getInetAddress()) + ".");
		this.socket = socket;
		System.out.println(socket);
		try {
			socketInput = socket.getInputStream();
			socketOutput = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
			displayErr(String.valueOf(socket.getInetAddress()) + " could not connect.");
		}
		running = true;
	}
	
	public void _stop(){
		running = false;
	}
	
	public void run(){
		String s = "";
		int braces = 0;
		while(running){
			try {
				if(socketInput.available()>0){		
					for(int i = 0; i < socketInput.available(); i++){
						byte b = (byte)socketInput.read();
						if(b == 123){
							braces++;
						} else if (b == 125){
							braces--;
						}
						s += (char) b;
						if(braces == 0){
							// NEW LINE DETECTED; END OF MESSAGE
							if(ServerApplication.debug){								
								System.out.println("\nRECIEVING:\n");
								System.out.println(s);
								System.out.println("\n");
							}
							ServerParser.recieve(s, this);
							s = "";
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
			String message = ServerParser.send(type, msg, sender);
			if(ServerApplication.debug){
				System.out.println("\nSENDING:\n");
				System.out.println(message);
				System.out.println("\n");
			}
			socketOutput.write(message.getBytes(Charset.forName("UTF-8")));
			socketOutput.flush();
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