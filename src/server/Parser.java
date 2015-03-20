package server;

import org.json.*;

public abstract class Parser {
	
	public static void recieve(String message){
		JSONObject json = new JSONObject(message);
		String type = json.getString("request");
		switch(type){
		case "login":
			
			break;
		}
	}
	
}
