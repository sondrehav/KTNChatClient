package server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
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
				System.out.println("Waiting for connection on " + String.valueOf(this.port) + ".");
				Socket socket = serverSocket.accept();
				if(!running) break;
				Client client = new Client(socket);
				clients.add(client);
				System.out.println("Client added.");
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
			System.out.println("Thread "+String.valueOf(id)+" is creating input- and outputstreams.");
			try {
				socketInput = new ObjectInputStream(socket.getInputStream());
				socketOutput = new ObjectOutputStream(socket.getOutputStream());
				username = (String) socketInput.readObject();
				System.out.println(username + " connected.");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Streams created.");
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
								System.out.println(s);
								s = "";
							} else {
								s += (char) b;
							}
						}
					}
					if(s.contentEquals("LOGOUT")){
						this.close();
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
				socketOutput.writeObject(msg);
			} catch (IOException e) {
				System.out.println("Error sending message to " + username);
			}
			return true;
		}

	}

	
}
