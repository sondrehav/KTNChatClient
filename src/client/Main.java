package client;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Main {
	
	public static final int PORT = 8000;
	
	static ServerSocket server = null;
	static Socket socket = null;
	static DataInputStream inputStream = null;
	
	public static void main(String[] args) throws Exception{
		server = new ServerSocket(PORT);
		socket = server.accept();
		inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		while(true){			
			try
			{
				String line = inputStream.readUTF();
				System.out.println(line);
			}
			catch(IOException ioe)
			{
				ioe.printStackTrace();
				break;
			}
		}
		socket.close();
	}
	
}
