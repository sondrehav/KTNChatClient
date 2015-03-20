package shared;

import java.time.LocalTime;

public abstract class Display {
	
	public static void display(String message){
		System.out.println("["+LocalTime.now().toString()+"]\t"+message);
	}
	
	public static void display(String message, String username){
		System.out.println("["+LocalTime.now().toString()+"; "+username+"]\t"+message);
	}
	
	public static void displayErr(String message){
		System.err.println("["+LocalTime.now().toString()+"]\t"+message);
	}
	
}
