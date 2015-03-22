package server;

import java.time.LocalDateTime;

import static shared.Display.*;

import org.json.*;


public abstract class ServerParser {
	
	public static final int ERROR = 0, INFO = 1, HISTORY = 2, MESSAGE = 3;

	public static void recieve(String message, Client client){

		JSONObject json = new JSONObject(message);
		String type = json.getString("request");
		String content = json.getString("content");
		switch(type){
		case "login":
			client.login(content);
			break;
		case "logout":
			client._stop();
			ServerApplication.server.clients.remove(client);
			break;
		case "msg":
			display(content, client.getUsername());
			ServerApplication.server.messages.add("["+convertTimestamp(LocalDateTime.now().toString())+": "+client.getUsername()+"] "+content);
			ServerApplication.server.sendAll(content);
			break;
		case "help":
			client.help();
			break;
		case "log":
			client.log();
			break;
		case "names":
			client.names();
			break;
		default:
			// TODO: ERROR
		}
	}
	
	private static String convertTimestamp(String timestamp) {
		String s = timestamp.split("T")[1];
		return s.split(":")[0] +":"+ s.split(":")[1] +":"+ s.split(":");
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
