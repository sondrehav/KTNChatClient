package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Scanner;

import shared.Parser;
import static shared.Display.*;

public class Client {
	
	static String SERVER = "localhost";
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
			socketOutput.writeObject(username);
			socketOutput.reset();
		} catch (IOException e) {
			displayErr("Error logging in.");
			disconnect();
			return false;
		}
		return true;
	}
	
	public void sendMsg(String msg) {
		try{
			socketOutput.write((msg + "\n").getBytes(Charset.forName("UTF-8")));
			socketOutput.reset();
		} catch (IOException e) {
			displayErr("Error sending message to " + username);
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
		Client client = new Client(sysin.nextLine());
		if(!client.start()){
			sysin.close();
			return;
		}
		while(true){
			System.out.print("> ");
			String msg = sysin.nextLine();
			try {
				client.sendMsg(Parser.send(Parser.MESSAGE, msg));
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(msg.contentEquals("LOGOUT")){
				break;
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
