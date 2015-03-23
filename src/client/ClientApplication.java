package client;

import static shared.Display.display;
import static shared.Display.displayErr;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Scanner;

public class ClientApplication {
	
	public static boolean debug = true; 
	
	static String SERVER = "localhost";
	static final int PORT = 8001;
	
	Socket socket = null;
	InputStream socketInput = null;
	OutputStream socketOutput = null;
	String username;
	
//	ClientApplication(String username){
//		super();
//		this.username = username;
//	}
//	
	ServerListener serverListener = null;
	public boolean start(){
		try{
			socket = new Socket(SERVER, PORT);
		} catch (Exception e) {
			displayErr("Failed to connect to server.");
			return false;
		}
		display("Connection accepted " + socket.getInetAddress() + ":" + socket.getPort());
		try{
			socketOutput = socket.getOutputStream();
			socketInput = socket.getInputStream();
		} catch (IOException e) {
			displayErr("Could not open streams.");
			return false;
		}
		serverListener = new ServerListener(socketInput);
		serverListener.start();
		if(!sendMsg(username, ClientParser.LOGIN)){
			disconnect();
			return false;
		}
		return true;
	}
	
	public boolean sendMsg(String msg, int type) {
		if(username==null&&type!=ClientParser.LOGIN){
			displayErr("Not logged in.");
			return false;
		}
		try{
			String message = ClientParser.send(type, msg);
			if(debug){
				System.out.println("\nSENDING:\n");
				System.out.println(message);
				System.out.println("\n");
			}
			socketOutput.write(message.getBytes(Charset.forName("UTF-8")));
			socketOutput.flush();
		} catch (Exception e) {
			e.printStackTrace();
			displayErr("Error sending message to server");
			return false;
		}
		return true;
	}

	
	public void disconnect() {
		if(socketInput != null)
			try {
				socketInput.close();
				if(socketOutput != null) socketOutput.close();
				if(socket != null) socket.close();
			} catch (IOException e) {}
	}

	static Scanner sysin = new Scanner(System.in);
	static ClientApplication client;
	public static void main(String[] args) throws IOException {
		display("IP ADDR:  ");
		SERVER = sysin.nextLine();
		display("USERNAME: ");
		String username;
//		while(!().matches("[A-Za-z0-9]*")){
//			displayErr("\'"+username + "\' is not a valid username.");
//		}
		username = sysin.nextLine();
		client = new ClientApplication();
		client.username = username;
		if(!client.start()){
			sysin.close();
			System.exit(1);
		}
		boolean running = true;
		while(running){
			String msg = sysin.nextLine();
			try {
				switch(msg){
				case "#help":
					client.sendMsg("", ClientParser.HELP);
					break;
				case "#log":
					client.sendMsg("", ClientParser.LOG);
					break;
				case "#users":
					client.sendMsg("", ClientParser.NAMES);
					break;
				case "#logout":
					client.sendMsg("", ClientParser.LOGOUT);
					running = false;
					break;
				default:
					if(msg.matches("^#login .*")){
						String u = msg.split("^#login ")[1];
						client.username = u;
						client.sendMsg(u, ClientParser.LOGIN);
					} else {						
						client.sendMsg(msg, ClientParser.MESSAGE);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		display("Logging out...");
		sysin.close();
		client.disconnect();
		System.exit(0);
	}
	
	public static class ServerListener extends Thread {

		InputStream in = null;
		
		ServerListener(InputStream in){
			this.in = in;
		}
		
		public void run() {
			String s = "";
			int braces = 0;
			while(true){
				try {
					if(in.available()>0){		
						for(int i = 0; i < in.available(); i++){
							byte b = (byte)in.read();
							if(b == 123){
								braces++;
							} else if (b == 125){
								braces--;
							}
							s += (char) b;
							if(braces == 0){
								// NEW LINE DETECTED; END OF MESSAGE
								if(debug){
									System.out.println("\nRECIEVING:\n");
									System.out.println(s);
									System.out.println("\n");
								}
								ClientParser.recieve(s);
								s = "";
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
			}
		}

	}

}
