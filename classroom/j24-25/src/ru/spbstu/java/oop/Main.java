package ru.spbstu.java.oop;

public class Main {
	public static void main(String[] args) throws CloneNotSupportedException {
		System.out.println("Hello world!");
		
		Cat bob = new Cat();
		
		Cat littleBob = (Cat) bob.clone();
		System.out.println(bob == littleBob);
		System.out.println(bob.hashCode());
		System.out.println(littleBob.hashCode());
		
		bob.setName("Bob");
		bob.setAge(12);
		
		bob.meow();
		
		Animal animal = new Animal() {
			
			@Override
			public void feed() {
				System.err.println("Drink blood");
			}
		};
		
		Runnable run = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
			}
		};
		
		Runnable task = () -> {
			
		};
	}
}
