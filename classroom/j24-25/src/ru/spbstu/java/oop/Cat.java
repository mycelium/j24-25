package ru.spbstu.java.oop;

public class Cat extends Animal implements Cloneable, Dieable{
	
	private String name;
	private int age;
	private Tail tail;
	
	
	public Cat() {
		super();
		tail = new Tail();
		
	}
	

//	public Cat(String name, int age) {
//		super();
//		this.name = name;
//		this.age = age;
//	}



	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}


	void meow() {
		System.out.println("meow");
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
		if (age > 30) {
			throw new RuntimeException("Age is not correct");
		}
		this.age = age;
	}


	@Override
	public void feed() {
		
	}


	@Override
	public void die() {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
}
