package ru.spbstu.java.exceptions;

import java.util.List;

public class Main {
	
	
	public static void main(String[] args) {
		var res = 1; 
		try {
			System.out.println((res / 0));		
			int x = Integer.valueOf("abc");
		} catch (ArithmeticException e) {
			System.err.println(e.getMessage());
		}
		System.out.println("Hello");
		
		System.out.println(calculate());
		
		try {
			doSmth();
		} catch (CustomException e) {
		}
		Object o = null;
		
//		o.hashCode();
//		sumList(null);
		
		int x = 0;
		Integer y = null;
		x = y;
	}
	
	public static int calculate() {
		
		try {
			return 0;
		} catch (ArithmeticException e) {
			return 1;
		}
		catch (Exception e1) {
			
		}
		finally {
			return 42;
		}
	}
	
	
	private static int sumList(List<Integer> list) {
		return list.stream().reduce(Integer::sum).get();
	}
	
	private static void doSmth() throws CustomException {
		throw new CustomException();
	}
	
	private static void doSmth2() throws Exception {
		throw new Exception("CustomException");
	}
}
