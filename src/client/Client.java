package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	
	static final String SERVER = "localhost";
	static final int PORT = 8001;
	
	Socket socket = null;
	ObjectInputStream socketInput = null;
	ObjectOutputStream socketOutput = null;
	String username;
	
	Client(String username){
		this.username = username;
	}

	public boolean start(){
		try{
			socket = new Socket(SERVER, PORT);
		} catch (Exception e) {
			System.err.println("Failed to connect to server.");
			return false;
		}
		System.out.println("Connection accepted " + socket.getInetAddress() + ":" + socket.getPort());
		try{
			socketInput = new ObjectInputStream(socket.getInputStream());
			socketOutput = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			System.err.println("Could not open streams.");
			return false;
		}
		new ServerListener(socketInput).start();
		try{
			socketOutput.writeObject(username);
			System.out.println("KEKEKE");
		} catch (IOException e) {
			System.err.println("Error logging in.");
			disconnect();
			return false;
		}
		return true;
	}
	
	public void sendMsg(String msg) {
		try{
			socketOutput.writeObject(msg);
			socketOutput.flush();
		} catch (IOException e) {
			System.err.println("Error sending message to " + username);
		}
	}

	
	public void disconnect() {
		if(socketInput != null)
			try {
				socketInput.close();
				if(socketOutput != null) socketOutput.close();
				if(socket != null) socket.close();
			} catch (IOException e) {}
	}

	public static void main(String[] args) throws IOException {
		Client client = new Client("Sondre");
		if(!client.start()){
			return;
		}
		Scanner sysin = new Scanner(System.in);
		while(true){
			System.out.print("> ");
			String msg = sysin.nextLine();
			if(msg.contentEquals("LOGOUT")){
				client.sendMsg("LOGOUT");
				break;
			} else {
				client.sendMsg(msg);
			}
		}
		client.disconnect();
	}
	
	public static class ServerListener extends Thread {

		ObjectInputStream in = null;
		
		ServerListener(ObjectInputStream in){
			this.in = in;
		}
		
		public void run() {
			while(true){
				try{
					String msg = (String) in.readObject();
					System.out.println(msg);
					System.out.println("> ");
				} catch (IOException | ClassNotFoundException e) {
					System.err.println("Server has closed the connection.");
					break;
				}
			}
		}

	}


}
