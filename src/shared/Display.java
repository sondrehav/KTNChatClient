package shared;

import java.time.LocalDateTime;
import java.time.LocalTime;

public abstract class Display {
	
	private static String time(){
		String s = String.valueOf(LocalTime.now().getHour())+":";
		int t = LocalTime.now().getMinute();
		int sec = LocalTime.now().getSecond();
		if(t<10){
			s+="0"+String.valueOf(t)+":";			
		} else {
			s+=String.valueOf(t)+":";
		}
		if(sec<10){
			s+="0"+String.valueOf(sec);
		} else {
			s+=String.valueOf(sec);
		}
		return s;
	}

	public static void display(String message){
		System.out.println("["+time()+"] "+message);
	}
	
	public static void display(String message, String username){
		System.out.println("["+time()+": "+username+"] "+message);
	}
	
	public static void display(String message, String username, String time){
		System.out.println("["+time+": "+username+"] "+message);
	}
	
//	public static void display(String message, String username, LocalDateTime time){
//		System.out.println("["+time+": "+username+"] "+message);
//	}
	
	public static void displayErr(String message){
		System.err.println("["+time()+"] "+message);
	}
	
	public static void displayRaw(String message){
		System.out.println(message);
	}
	
}
