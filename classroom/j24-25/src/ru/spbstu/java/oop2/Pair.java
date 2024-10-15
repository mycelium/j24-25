package ru.spbstu.java.oop2;

public class Pair {
	private double x;
	private double y;
	
	public Pair(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	
	@Override
	public String toString() {
		return "x:"+x+", y:"+y;
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Pair) {
			return x==((Pair) obj).x && y==((Pair) obj).y;
		}
		return false;
	}
	
	
	
}
