package ru.spbstu.java.refsermod;

import java.io.Serializable;

public class Tail implements Serializable {
	private Cat cat;
	
	private double length;

	public Cat getCat() {
		return cat;
	}

	public void setCat(Cat cat) {
		this.cat = cat;
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}
	
	
	
	
	
}
