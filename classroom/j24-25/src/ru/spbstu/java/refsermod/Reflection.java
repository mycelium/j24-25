package ru.spbstu.java.refsermod;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Reflection {

	public int add(int a, int b) {
		return a + b;
	}

	public static void main(String[] args) {
		
//		Student student = new Student();
//		
//		Class clazz = Student.class;
//		
//		clazz.getDeclaredFields();
//		try {
//			Field sec = clazz.getDeclaredField("secret");
//			
//			sec.setAccessible(true);
//			Object secret = sec.get(student);
//			
//			System.out.println(secret.toString());
//		} catch (NoSuchFieldException | SecurityException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		System.out.println(student.getSecret());
		
		
		try {
			Class clazz = Class.forName("ru.spbstu.java.refsermod.Reflection");
			Class partypes[] = new Class[2];
			partypes[0] = Integer.TYPE;
			partypes[1] = Integer.TYPE;
			Method meth = clazz.getMethod("add", partypes);

			Reflection instance = new Reflection();
			Object arglist[] = new Object[2];
			arglist[0] = Integer.valueOf(131);
			arglist[1] = Integer.valueOf(108);
			Object retobj = meth.invoke(instance, arglist);
			Integer retval = (Integer) retobj;
			System.out.println(retval.intValue());
		} catch (Throwable e) {
			System.err.println(e);
		}
	}
}
