package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
//import java.net.URL;
import java.nio.charset.Charset;
//import java.util.ResourceBundle;
import java.util.Scanner;

//import javafx.application.Application;
//import javafx.fxml.FXMLLoader;
//import javafx.fxml.Initializable;
//import javafx.scene.Scene;
//import javafx.scene.layout.Pane;
//import javafx.stage.Stage;
import static shared.Display.*;

public class ClientApplication/* extends Application implements Initializable*/ {
	
	static String SERVER = "localhost";
	static final int PORT = 8001;
	
	Socket socket = null;
	ObjectInputStream socketInput = null;
	ObjectOutputStream socketOutput = null;
	String username;
	
	ClientApplication(String username){
		this.username = username;
	}
	
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
			socketOutput = new ObjectOutputStream(socket.getOutputStream());
			socketInput = new ObjectInputStream(socket.getInputStream());
			display("Streams created.");
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
			socketOutput.write((ClientParser.send(type, msg)+Character.toChars(3)[0]).getBytes(Charset.forName("UTF-8")));
			socketOutput.reset();
		} catch (Exception e) {
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
	public static void main(String[] args) throws IOException {
		//launch(args);
		display("IP ADDR:  ");
		SERVER = sysin.nextLine();
		display("USERNAME: ");
		String username;
		while(!(username = sysin.nextLine()).matches("[A-Za-z0-9]*")){
			displayErr("\'"+username + "\' is not a valid username.");
		}
		ClientApplication client = new ClientApplication(username);
		if(!client.start()){
			sysin.close();
			System.exit(1);
		}
		boolean running = true;
		while(running){
			System.out.print("> ");
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
					client.sendMsg(msg, ClientParser.MESSAGE);
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

		ObjectInputStream in = null;
		
		ServerListener(ObjectInputStream in){
			this.in = in;
		}
		
		public void run() {
			String s = "";
			while(true){
				try {
					if(in.available()>0){		
						for(int i = 0; i < in.available(); i++){
							byte b = in.readByte();
							if(b == 3){
								// NEW LINE DETECTED; END OF MESSAGE
								ClientParser.recieve(s);
								s = "";
							} else {
								s += (char) b;
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
/*
	@Override
	public void start(Stage arg0) throws Exception {
		Pane p = (Pane) FXMLLoader.load(getClass().getResource("gui.fxml"));
		arg0.setScene(new Scene(p));
		arg0.show();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}
*/

}
