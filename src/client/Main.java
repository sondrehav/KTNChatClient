package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Main {
	
	static final int PORT = 8000;
	static final String NAME = "78.91.39.141";
	
	static Socket socket = null;
	static BufferedReader socketInput = null;
	static PrintWriter socketOutput = null;
	static BufferedReader consoleInput = null;

	public static void main(String[] args) throws IOException {
		socket = new Socket(NAME, PORT);
		socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		socketOutput = new PrintWriter(socket.getOutputStream());
		consoleInput = new BufferedReader(new InputStreamReader(System.in));
		while(true){
			String line = consoleInput.readLine();
			if(line.contentEquals("<exit>"))
				break;
			socketOutput.println(line);
		}
		if(socketOutput != null)
			socketOutput.close();
		if(socketInput != null)
			socketInput.close();
		if(socket != null)
			socket.close();
	}

}
