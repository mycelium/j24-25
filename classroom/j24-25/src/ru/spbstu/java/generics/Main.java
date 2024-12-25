package ru.spbstu.java.generics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Main {
	
	private static class Account {
		int age;
		String name;
		double amount;
	}
	
	public static void main(String[] args) {
		
//		List accounts = new ArrayList();
//		double sum=0.0;
//		
//		for (Object acc : accounts) {
//			if (acc instanceof Account) {
//				sum+= ((Account) acc).amount;
//			}
//		}
//		
//		List<Account> accs = new LinkedList<>();
//		for (Account acc : accs) {
//		}
//		
//		Animal cat = new Cat();
//		cat.feed(Integer.valueOf(12));
		
		Object[] animals = new Animal[10];
		animals[0] = new Cat();
		animals[1] = new String("Hello world");
		System.out.println(animals[1]);
		
	}
}
