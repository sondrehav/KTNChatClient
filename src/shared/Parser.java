package shared;
import org.json.*;

public abstract class Parser {
	
	public static final int LOGIN = 0, LOGOUT = 1, MESSAGE = 2, HELP = 3, LOG = 4, NAMES = 5;
	
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
		default:
			
			System.err.println("Wrong type.");
		}
		String string = String.valueOf(object);
		return string;
	}
	
	
}
