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
						if(b == 10){
							// NEW LINE DETECTED; END OF MESSAGE
							Parser.recieve(s, this);
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
	
	private synchronized boolean sendMsg(String msg) {
		if(!socket.isConnected()){
			close();
			return false;
		}
		try{
			socketOutput.write((msg + "\n").getBytes(Charset.forName("UTF-8")));
			socketOutput.reset();
		} catch (IOException e) {
			displayErr("Error sending message to " + username);
		}
		return true;
	}

}