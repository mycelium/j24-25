package ru.spbstu.java.oop;

import java.io.Serializable;

public interface Dieable extends Serializable {
	
	final int field = 42;
	
//	public void die();
	
	default void die() {
		
	}
}
