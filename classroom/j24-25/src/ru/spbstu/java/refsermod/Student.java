package ru.spbstu.java.refsermod;

import java.util.UUID;

public class Student {
	private String secret;
	
	
	
	public Student() {
		super();
		this.secret = UUID.randomUUID().toString();
	}



	public String getSecret() {
		throw new RuntimeException("...");
	}
}
