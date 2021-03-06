package server;

import java.time.LocalDateTime;

import static shared.Display.*;

import org.json.*;


public abstract class ServerParser {
	
	public static final int ERROR = 0, INFO = 1, HISTORY = 2, MESSAGE = 3;

	public static void recieve(String message, Client client){

		JSONObject json = new JSONObject(message);
		String type = json.getString("request");
		String content = "None";
		try{			
			content = json.getString("content");
		} catch (JSONException e) {
			
		}
		switch(type){
		case "login":
			if(!content.matches("[A-Za-z0-9]*")){
				client.sendMsg("Invalid username. Only characters allowed: A-Z, a-z and 0-9.", null, ERROR);
			} else {				
				client.login(content);
				client.log();
			}
			break;
		case "logout":
			if(client.getUsername()==null){
				client.sendMsg("Illegal request.", null, ERROR);
			} else {
				client._stop();
				ServerApplication.server.clients.remove(client);
			}
			break;
		case "msg":
			if(client.getUsername()==null){
				client.sendMsg("Illegal request.", null, ERROR);
			} else {								
				ServerApplication.server.messages.add("["+convertTimestamp(LocalDateTime.now().toString())+": "+client.getUsername()+"] "+content);
				ServerApplication.server.sendAll(content, client.getUsername(), MESSAGE);
			}
			display(content, client.getUsername());
			break;
		case "help":
			client.help();
			break;
		case "log":
			if(client.getUsername()==null){
				client.sendMsg("Illegal request.", null, ERROR);
			} else {				
				client.log();
			}
			break;
		case "names":
			if(client.getUsername()==null){
				client.sendMsg("Illegal request.", null, ERROR);
			} else {				
				client.names();
				break;
			}
		default:
			// TODO: ERROR
		}
	}
	
	private static String convertTimestamp(String timestamp) {
		String s = timestamp.split("T")[1];
		return s.split(":")[0] +":"+ s.split(":")[1] +":"+ s.split(":")[2];
	}
	
	public static String send(int type, String content, String username) throws Exception {
		if(username==null&&type==MESSAGE){
			throw new Exception("Sender cant be null when sending a message.");
		}
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
