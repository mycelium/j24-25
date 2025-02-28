package ru.spbstu.java.concurrency;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Philosophers {

	private static class Phil implements Runnable {
		private Lock leftStick;
		private Lock rightStick;

		private int eated = 0;

		public Phil(Lock leftStick, Lock rightStick) {
			super();
			this.leftStick = leftStick;
			this.rightStick = rightStick;
		}

		@Override
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				rightStick.lock();
				if (!leftStick.tryLock()) {
					rightStick.unlock();
				} else {
					try {
						eated++;
						Thread.sleep(1000);
						System.out.println(Thread.currentThread().getName() + " eated " + eated + " already");
					} catch (InterruptedException e) {
						System.err.println(e.getMessage());
					} finally {
						rightStick.unlock();
						leftStick.unlock();
					}
				}
			}

		}

	}

	public static void main(String[] args) {

		Lock stick1 = new ReentrantLock();
		Lock stick2 = new ReentrantLock();
		Lock stick3 = new ReentrantLock();
		Lock stick4 = new ReentrantLock();
		Lock stick5 = new ReentrantLock();

		Phil phil1 = new Phil(stick1, stick5);
		Phil phil2 = new Phil(stick2, stick1);
		Phil phil3 = new Phil(stick3, stick2);
		Phil phil4 = new Phil(stick4, stick3);
		Phil phil5 = new Phil(stick5, stick4);

		new Thread(phil1).start();
		new Thread(phil2).start();
		new Thread(phil3).start();
		new Thread(phil4).start();
		new Thread(phil5).start();

	}

}
