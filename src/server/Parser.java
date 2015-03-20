package server;

import java.time.LocalDateTime;
import static shared.Display.*;

import org.json.*;


public abstract class Parser {
	
	public static final int ERROR = 0, INFO = 1, HISTORY = 2, MESSAGE = 3;
	
	public static void recieve(String message){
		JSONObject json = new JSONObject(message);
		String type = json.getString("request");
		switch(type){
		case "login":
			
			break;
		}
	}
	
	public static String send(int type, String content, String username) throws Exception {
		JSONObject object = new JSONObject();
		object.put("timestamp", String.valueOf(LocalDateTime.now()));
		switch(type){
		case ERROR:
			object.put("sender", "None");
			object.put("response", "error");
			object.put("content", content);
			break;
		case INFO:
			object.put("sender", "None");
			object.put("response", "info");
			object.put("content", content);
			break;
		case HISTORY:
			object.put("sender", "None");
			object.put("response", "history");
			object.put("content", content);
			break;
		case MESSAGE:
			object.put("sender", username);
			object.put("response", "message");
			object.put("content", content);
			break;
		default:
			displayErr("Wrong type, message not past");
		}
		String string = String.valueOf(object);
		return string;
	}
	
	
	
}
