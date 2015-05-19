package com.thisisnoble.javatest;

public class ProcessorNotFoundException extends RuntimeException {
	
	public ProcessorNotFoundException(){
		
	}
	
	public ProcessorNotFoundException(String message){
		super(message);
	}

}
