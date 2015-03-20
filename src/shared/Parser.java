package shared;

public abstract class Parser {
	
	public static final int LOGIN = 0, LOGOUT = 1, MESSAGE = 2, HELP = 3, LOG = 4;
	
	public static String send(int type, String message){
		String string = "";
		switch(type){
		case LOGIN:
		
			break;
		case LOGOUT:
			
			break;
		case MESSAGE:
			
			break;
		case HELP:
			
			break;
		case LOG:
			
			break;
		default:
			System.err.println("Wrong type.");
		}
		return string;
	}
	
}
