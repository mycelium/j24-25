package ru.spbstu.java.refsermod;

import java.io.Serializable;

public class Cat implements Serializable{
	private String name;
	private int age;
	private Tail tail;
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
	public Tail getTail() {
		return tail;
	}
	public void setTail(Tail tail) {
		this.tail = tail;
	}
	
	
}
