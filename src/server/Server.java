package server;

import static shared.Display.display;
import static shared.Display.displayErr;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class Server {
	
	
	static int PORT = 8001;
	static Server server = null;
	
	ArrayList<Client> clients = new ArrayList<Client>();
	boolean running = true;
	int port;

	public static void main(String[] args) throws Exception{
		server = new Server(PORT);
		server.start();
	}
	
	public Server(int port) {
		this.port = port;
	}

	public void start() {
		try{
			ServerSocket serverSocket = new ServerSocket(this.port);
			while(running){
				display("Waiting for connection on " + String.valueOf(this.port) + ".");
				Socket socket = serverSocket.accept();
				if(!running) break;
				Client client = new Client(socket);
				clients.add(client);
				display("Client added.");
				client.start();
			}
			try{
				for(Client client : clients){
					client.close();
				}
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stop(){
		running = false;
	}
	
	public static class Client extends Thread {
		
		private Socket socket = null;
		private ObjectInputStream socketInput = null;
		private ObjectOutputStream socketOutput = null;
		private int id;
		private String username;
//		String message;
//		String date;
		
		private static int c_id = 0;
		
		public Client(Socket socket) {
			id = ++c_id;
			this.socket = socket;
			display("Thread "+String.valueOf(id)+" is creating input- and outputstreams.");
			try {
				socketInput = new ObjectInputStream(socket.getInputStream());
				socketOutput = new ObjectOutputStream(socket.getOutputStream());
				username = (String) socketInput.readObject();
				display(username + " connected.");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			display("Streams created.");
		}
		
		public void run(){
			boolean running = true;
			String s = "";
			while(running){
				try {
					if(socketInput.available()>0){					
						for(int i = 0; i < socketInput.available(); i++){
							byte b = socketInput.readByte();
							if(b == 10){
								// NEW LINE DETECTED; END OF MESSAGE
								display(s, this.username);
								s = "";
							} else {
								s += (char) b;
							}
						}
					}
					if(s.contentEquals("LOGOUT")){
						display("Logging out.", this.username);
						running = false;
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
				sendMsg("LOGOUT");
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

	
}
