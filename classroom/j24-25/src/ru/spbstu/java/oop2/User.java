package ru.spbstu.java.oop2;

public class User {
	
	
	public static class Pass{
		String pass;
		int salt;
		
	}
	
	private String name;
	private int age;
	private Pass password;
	
	
	
	public Pass getPassword() {
		return password;
	}
	public void setPassword(Pass password) {
		this.password = password;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	
}
