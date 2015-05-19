package com.thisisnoble.javatest;

public class EventNotFoundException extends RuntimeException {
	
	public EventNotFoundException(){
		
	}
	
	public EventNotFoundException(String message){
		super(message);
	}

}
