package server;

import static shared.Display.display;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerApplication {
	
	static int PORT = 8001;
	static ServerApplication server = null;
	
	ArrayList<Client> clients = new ArrayList<Client>();
	ArrayList<String> messages = new ArrayList<String>();
	boolean running = true;
	int port;

	public static String[] helpText;
	
	public static void main(String[] args) throws Exception{
		server = new ServerApplication(PORT);
		server.start();
	}
	
	public ServerApplication(int port) {
		this.port = port;
		setHelpText();
	}
	
	public void sendAll(String message){
		for(Client c : clients){
			if(!c.sendMsg(message, ServerParser.MESSAGE)){
				clients.remove(c);
			}
		}
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
	
	private void setHelpText(){
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File("res/help.txt")));
			String line;
			ArrayList<String> ls = new ArrayList<String>();
			while((line = br.readLine())!=null){
				ls.add(line);
			}
			helpText = new String[ls.size()];
			for(int i=0;i<helpText.length;i++){
				helpText[i] = ls.get(i);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String[] usernames(){
		String[] s = new String[clients.size()];
		for(int i=0;i<s.length;i++){
			s[i]=clients.get(i).getUsername();
		}
		return s;
	}
	
}
