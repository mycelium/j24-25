package ru.spbstu.java.refsermod;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class Main {
	public static void main(String[] args) {
		Tail tail = new Tail();
		tail.setLength(0.5);
		
		Cat bob = new Cat();
		bob.setName("Bob");
		bob.setAge(42);
		bob.setTail(tail);
		
		tail.setCat(bob);
		
		try(OutputStream os = new FileOutputStream("out/bob")) {
			ObjectOutputStream oos = new ObjectOutputStream(os);
			
			oos.writeObject(bob);
			
			ObjectInputStream iis = new ObjectInputStream(new FileInputStream("out/bob"));
			
			Cat bob2 = (Cat) iis.readObject();
			
			System.out.println(bob.getName().equals(bob2.getName()));
			System.out.println(bob == bob2);
			System.out.println(bob.getTail() == bob2.getTail());
			System.out.println(bob.getTail().getLength() == bob2.getTail().getLength());
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
