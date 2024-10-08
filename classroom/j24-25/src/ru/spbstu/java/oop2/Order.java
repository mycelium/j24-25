package ru.spbstu.java.oop2;

public abstract class Order {
	
	public abstract void processBucket();
	public abstract void processPayment();
	public abstract void processDelivery();
	
	public void processOrder() {
		
		processBucket();
		
		processPayment();
		//TODO add log
		processDelivery();
	}
}
