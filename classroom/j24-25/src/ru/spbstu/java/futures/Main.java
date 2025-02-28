package ru.spbstu.java.futures;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Main {

	static ExecutorService executor = Executors.newFixedThreadPool(10000);

	public static void main(String[] args) {
		double[][] firstMatrix = fillMatrix(new double[2000][1500]);
		double[][] secondMatrix = fillMatrix(new double[1500][1000]);

		int count = 10;
		Long timeBefore = System.currentTimeMillis();
		for (int i = 0; i < count; i++) {
			multiplyCuncurencyMatrixByRowsTP(firstMatrix, secondMatrix);
		}
		executor.shutdown();
		try {
			executor.awaitTermination(60000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Long timeAfter = System.currentTimeMillis();
		System.out.println("Average time: " + (timeAfter - timeBefore));
	}

	static double[][] fillMatrix(double[][] matrix) {
		Random rand = new Random();
		for (int row = 0; row < matrix.length; row++) {
			for (int column = 0; column < matrix[0].length; column++) {
				matrix[row][column] = rand.nextDouble();
			}
		}
		return matrix;
	}

	static double[][] multiplyMatrix(double[][] leftMatrix, double[][] rightMatrix) {
		if (leftMatrix[0].length != rightMatrix.length)
			throw new RuntimeException("Dimensions don't correct");

		double[][] resultMatrix = new double[leftMatrix.length][rightMatrix[0].length];

		for (int row = 0; row < leftMatrix.length; row++) {
			for (int column = 0; column < rightMatrix[0].length; column++) {
				for (int index = 0; index < leftMatrix[0].length; index++) {
					resultMatrix[row][column] += leftMatrix[row][index] * rightMatrix[index][column];
				}
			}
		}

		return resultMatrix;
	}

	static double[][] multiplyCuncurencyMatrixByRows(double[][] leftMatrix, double[][] rightMatrix) {
		if (leftMatrix[0].length != rightMatrix.length)
			throw new RuntimeException("Dimensions don't correct");

		double[][] resultMatrix = new double[leftMatrix.length][rightMatrix[0].length];

		for (int row = 0; row < leftMatrix.length; row++) {
			final int element = row;
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					for (int column = 0; column < rightMatrix[0].length; column++) {
						for (int index = 0; index < leftMatrix[0].length; index++) {
							resultMatrix[element][column] += leftMatrix[element][index] * rightMatrix[index][column];
						}
					}
				}
			};
			Thread worker = new Thread(runnable);
			worker.start();
		}

		return resultMatrix;
	}

	static double[][] multiplyMatrixByElements(double[][] leftMatrix, double[][] rightMatrix) {
		if (leftMatrix[0].length != rightMatrix.length)
			throw new RuntimeException("Dimensions don't correct");

		double[][] resultMatrix = new double[leftMatrix.length][rightMatrix[0].length];

		for (int row = 0; row < leftMatrix.length; row++) {
			for (int column = 0; column < rightMatrix[0].length; column++) {
				final int elementRow = row;
				final int elementColumn = column;
				Runnable runnable = new Runnable() {
					@Override
					public void run() {
						for (int index = 0; index < leftMatrix[0].length; index++) {
							resultMatrix[elementRow][elementColumn] += leftMatrix[elementRow][index]
									* rightMatrix[index][elementColumn];
						}
					}
				};
				Thread worker = new Thread(runnable);
				worker.start();
			}
		}

		return resultMatrix;
	}

	static double[][] multiplyCuncurencyMatrixByRowsTP(double[][] leftMatrix, double[][] rightMatrix) {
		if (leftMatrix[0].length != rightMatrix.length)
			throw new RuntimeException("Dimensions don't correct");

		double[][] resultMatrix = new double[leftMatrix.length][rightMatrix[0].length];

		for (int row = 0; row < leftMatrix.length; row++) {
			final int element = row;
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					for (int column = 0; column < rightMatrix[0].length; column++) {
						for (int index = 0; index < leftMatrix[0].length; index++) {
							resultMatrix[element][column] += leftMatrix[element][index] * rightMatrix[index][column];
						}
					}
				}
			};
			executor.execute(runnable);
		}

		return resultMatrix;
	}

	static double[][] multiplyMatrixByElementsTP(double[][] leftMatrix, double[][] rightMatrix) {
		if (leftMatrix[0].length != rightMatrix.length)
			throw new RuntimeException("Dimensions don't correct");

		double[][] resultMatrix = new double[leftMatrix.length][rightMatrix[0].length];

		for (int row = 0; row < leftMatrix.length; row++) {
			for (int column = 0; column < rightMatrix[0].length; column++) {
				final int elementRow = row;
				final int elementColumn = column;
				Callable<Double> callable = new Callable<Double>() {

					@Override
					public Double call() throws Exception {
						double variable = 0;
						for (int index = 0; index < leftMatrix[0].length; index++) {
							variable += leftMatrix[elementRow][index] * rightMatrix[index][elementColumn];
						}
						return variable;
					}

				};
				Future<Double> future = executor.submit(callable);
				try {
					resultMatrix[row][column] = future.get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return resultMatrix;
	}

}
