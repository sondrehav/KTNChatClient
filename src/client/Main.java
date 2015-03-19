package client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Main {
	
	static final int PORT = 8000;
	static final String NAME = "78.91.39.141";
	
	static Socket socket = null;
	static Scanner input = null;
	static DataOutputStream output = null;

	public static void main(String[] args) throws IOException {
		socket = new Socket(NAME, PORT);
		input = new Scanner(System.in);
		output = new DataOutputStream(socket.getOutputStream());
		while(true){
			String line = input.nextLine();
			if(line.contentEquals("<exit>"))
				break;
			output.flush();
		}
		if(output != null)
			output.close();
		if(socket != null)
			socket.close();
	}

}
