package ru.spbstu.java.collections;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
	public static void main(String[] args) {
		
		List<String> list = new ArrayList<>();
		Map<String, Integer>  map = new HashMap<>();
		list.add("Hello");
		list.add(" ");
		list.add("World");
		
//		List<String> ll = List.of("Hello", " ");
////		ll.add("World");
//		
//		var intVal = 13;
//		
//		Stream<String> str = list.stream();
//		var res = str.map(x -> x.length())
//					.filter(elLenght ->  elLenght>2)
//					.collect(Collectors.toList());
//		list.stream().map(x -> {
//			map.put("", 13);
//			return x + "123";
//		});
//		
//		Runnable funct = () -> {};
		
		list.stream().map(x -> {
			System.out.print(x);
			return x;
		}).collect(Collectors.toList());
		
		System.out.println();
		list.forEach(x -> System.out.print(x));
		
		
	}
}
