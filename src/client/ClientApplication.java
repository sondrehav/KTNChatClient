package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Scanner;

import static shared.Display.*;
import static client.Parser.*;

public class ClientApplication {
	
	static String SERVER = "localhost";
	static final int PORT = 8001;
	
	Socket socket = null;
	ObjectInputStream socketInput = null;
	ObjectOutputStream socketOutput = null;
	String username;
	
	ClientApplication(String username){
		this.username = username;
	}

	public boolean start(){
		try{
			socket = new Socket(SERVER, PORT);
		} catch (Exception e) {
			displayErr("Failed to connect to server.");
			return false;
		}
		display("Connection accepted " + socket.getInetAddress() + ":" + socket.getPort());
		try{
			socketOutput = new ObjectOutputStream(socket.getOutputStream());
			socketInput = new ObjectInputStream(socket.getInputStream());
			display("Streams created.");
		} catch (IOException e) {
			displayErr("Could not open streams.");
			return false;
		}
		new ServerListener(socketInput).start();
		try{
			socketOutput.writeObject(client.Parser.send(client.Parser.LOGIN, username));
			socketOutput.reset();
		} catch (Exception e) {
			displayErr("Error logging in.");
			disconnect();
			return false;
		}
		return true;
	}
	
	public void sendMsg(String msg, int type) {
		try{
			socketOutput.write((client.Parser.send(type, msg)+"\n").getBytes(Charset.forName("UTF-8")));
			socketOutput.reset();
		} catch (Exception e) {
			displayErr("Error sending message to server");
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
		Scanner sysin = new Scanner(System.in);
		display("IP ADDR:  ");
		SERVER = sysin.nextLine();
		display("USERNAME: ");
		ClientApplication client = new ClientApplication(sysin.nextLine());
		if(!client.start()){
			sysin.close();
			return;
		}
		boolean running = true;
		while(running){
			System.out.print("> ");
			String msg = sysin.nextLine();
			try {
				switch(msg){
				case "#help":
					client.sendMsg("\n", HELP);
					break;
				case "#log":
					client.sendMsg("\n", LOG);
					break;
				case "#users":
					client.sendMsg("\n", NAMES);
					break;
				case "#logout":
					client.sendMsg("\n", LOGOUT);
					running = false;
					break;
				default:
					client.sendMsg(msg+"\n", MESSAGE);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		sysin.close();
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
					display(msg);
					System.out.println("> ");
				} catch (IOException | ClassNotFoundException e) {
					displayErr("Server has closed the connection.");
					break;
				}
			}
		}

	}


}
