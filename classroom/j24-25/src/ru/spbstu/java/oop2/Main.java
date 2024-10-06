package ru.spbstu.java.oop2;

import java.util.List;
import java.util.Random;

import static ru.spbstu.java.oop2.Role.*;

public class Main {
	
	
	private static int importantValue=10;
	private int anotherVal;
	
	{
		importantValue=42;
		anotherVal = 13;
	}

	record Point(int x, int y) {};
	
	
	public Main() {
		super();
		importantValue = 10;
		anotherVal = new Random().nextInt();
	}

	static class John{
		
	}
	
	public int gen() {
		importantValue++;
		return new Random().nextInt();
	}
	
	public static double generate() {
		return new Random().nextDouble();
	}
	
	public static void main(String[] args) {
		
//		int a = 15;
//		int b = 15;
//		Integer aObj = 150;
//		Integer bObj = 150;
//		System.out.println(a==b);
//		System.out.println(aObj == bObj);
//		
//		String test = "test";
//		String test2 = "test";
//		System.out.println(test == test2);
		var rand = new Main().gen();
		var doubleRand = Main.generate();
		
//		System.out.println(new Main().anotherVal);
//		System.out.println(new Main().anotherVal);
//		System.out.println(new Main().anotherVal);
//		System.out.println(new Main().anotherVal);
//		System.out.println(new Main().anotherVal);
		
		System.out.println(Role.ADMIN.getId());
		System.out.println(Role.USER.getId());
		System.out.println(Role.GUEST.getId());
		
		Role adminRole = Role.valueOf("ADMIN");
		Role userRole = Role.USER;
		Role guestRole = Role.getById(3);
		
		Role role = ADMIN;
		
		User user = new User();
		User.Pass pass = user.getPassword();
		List<Integer> ints = List.of(1,2,3);
		ints.stream().reduce(Integer::sum);
	}
	
}
