package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
	
	public static final int PORT = 8001;
	
	static ServerSocket server = null;
	static Socket socket = null;
	static BufferedReader in = null;
	
	public static void main(String[] args) throws Exception{
		server = new ServerSocket(PORT);
		System.out.println("Waiting...");
		socket = server.accept();
		System.out.println("Connected!");
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		while(true){
			try
			{
				String line = in.readLine();
				System.out.println(line);
				if(line.contentEquals("<exit>"))
					break;
			}
			catch(IOException ioe)
			{
				ioe.printStackTrace();
				break;
			}
		}
		if(in != null)
			in.close();
		if(socket != null)
			socket.close();
		if(server != null)
			server.close();
	}
	
}
