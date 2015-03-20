package server;

import org.json.*;

public abstract class Parser {
	
	public static void recieve(String message, Client client){
		JSONObject json = new JSONObject(message);
		String type = json.getString("request");
		String content = json.getString("content");
		switch(type){
		case "login":
			// TODO: Do something
			break;
		case "logout":
			// TODO: Do something
			break;
		case "msg":
			// TODO: Do something
			break;
		case "help":
			// TODO: Do something
			break;
		case "log":
			// TODO: Do something
			break;
		case "names":
			// TODO: Do something
			break;
		default:
			// TODO: ERROR
		}
	}
	
}
