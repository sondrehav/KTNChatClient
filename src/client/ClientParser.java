package client;
import static shared.Display.display;
import static shared.Display.displayErr;
import static shared.Display.displayRaw;

import org.json.JSONObject;

public abstract class ClientParser {
	
	public static final int LOGIN = 0, LOGOUT = 1, MESSAGE = 2, HELP = 3, LOG = 4, NAMES = 5;
	
	public static void recieve(String message){

		JSONObject json = new JSONObject(message);

		String timestamp = json.getString("timestamp");
		String sender = json.getString("sender");
		String response = json.getString("response");
		String content = json.getString("content");
		switch(response){
		case "error":
			displayErr(content);
			break;
		case "info":
			displayRaw(content);
			break;
		case "history":
			displayRaw(content);
			break;
		case "message":
			String t = convertTimestamp(timestamp);
			display(content, sender, t);
			break;
		default:
			displayErr("Message format not correct.");
		}
	}
	
	private static String convertTimestamp(String timestamp) {
		String s = timestamp.split("T")[1];
		return s.split(":")[0] +":"+ s.split(":")[1] +":"+ s.split(":")[2];
	}

	public static String send(int type, String message) throws Exception {
		JSONObject object = new JSONObject();
		switch(type){
		case LOGIN:
			object.put("request", "login");
			object.put("content", message);
			break;
		case LOGOUT:
			object.put("request", "logout");
			object.put("content", "None");
			break;
		case MESSAGE:
			object.put("request", "msg");
			object.put("content", message);
			break;
		case HELP:
			object.put("request", "help");
			object.put("content", "None");
			break;
		case LOG:
			object.put("request", "log");
			object.put("content", "None");
			break;
		case NAMES:
			object.put("request", "names");
			object.put("content", "None");
			break;
		default:
			displayErr("Wrong type, message not parsed. Type number was "+String.valueOf(type)+".");
		}
		String string = String.valueOf(object);
		return string;
	}
	
	
}
