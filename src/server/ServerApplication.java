package server;

import static shared.Display.display;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerApplication {
	
	
	static int PORT = 8001;
	static ServerApplication server = null;
	
	ArrayList<Client> clients = new ArrayList<Client>();
	boolean running = true;
	int port;

	public static void main(String[] args) throws Exception{
		server = new ServerApplication(PORT);
		server.start();
	}
	
	public ServerApplication(int port) {
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
	
	

	
}
