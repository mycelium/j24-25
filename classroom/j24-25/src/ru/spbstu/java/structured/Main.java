package ru.spbstu.java.structured;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;

public class Main {
	public static void main(String[] args) {
		Path customersFile = Paths.get("input/customers-2000000.csv");

		//String customerName = "Savannah Cherry";
		String customerName = "Васян";
		long startTime = System.currentTimeMillis();
		var foundCustomerId = getCustomerId(customersFile, customerName);
		if (foundCustomerId.isPresent()) {
			System.out.println(foundCustomerId.get());
		}
		long endTime = System.currentTimeMillis();

		System.out.println("Time: " + (endTime - startTime));

		int chunkSize = 80000;

		splitData(customersFile, chunkSize);
		startTime = System.currentTimeMillis();
		foundCustomerId = getCustomerIdByNameWithCF(customerName);
		if (foundCustomerId.isPresent()) {
			System.out.println(foundCustomerId.get());
		}
		endTime = System.currentTimeMillis();
		System.out.println("Parallel Time: " + (endTime - startTime));

	}

	private static Optional<String> getCustomerId(Path customersFile, String name) {
		try (BufferedReader reader = Files.newBufferedReader(customersFile)) {
			String line;
			while ((line = reader.readLine()) != null) {
				var foundValue = getCustomerIdFromLine(line, name);
				if (foundValue.isPresent()) {
					return foundValue;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Optional.empty();
	}

	public static Optional<String> getCustomerIdFromLine(String line, String name) {
		var customerValues = line.split(",");
		if (name.equals(customerValues[2] + " " + customerValues[3]))
			return Optional.of(customerValues[1]);
		return Optional.empty();
	}

	private static void splitData(Path customersFile, int chunkSize) {
		int linesCounter = 0;
		try (BufferedReader reader = Files.newBufferedReader(customersFile)) {
			String line;
			List<String> lines = new ArrayList<String>();
			while ((line = reader.readLine()) != null) {
				lines.add(line);
				linesCounter++;
				if (linesCounter % chunkSize == 0) {
					Path outputFile = Paths.get("input/chunks", String.valueOf(linesCounter / chunkSize) + ".csv");
					Files.write(outputFile, lines);
					lines.clear();
				}
			}
			if (!lines.isEmpty()) {
				Path outputFile = Paths.get("input", String.valueOf(linesCounter / chunkSize) + ".csv");
				Files.write(outputFile, lines);
				lines.clear();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Optional<String> getCustomerIdByNameWithCF(String name) {

		try (ExecutorService exservice = Executors.newVirtualThreadPerTaskExecutor()){
			

			
			var jobs = Files.list(Paths.get("input/chunks"))
					.map(chunk -> CompletableFuture.supplyAsync(() -> getCustomerId(chunk, name), exservice)).toList();

			CompletableFuture<Optional<String>>[] jj = new CompletableFuture[jobs.size()];
			jj = jobs.toArray(jj);

			CompletableFuture.allOf(jj).join();
			return jobs.stream().filter(partitionResult -> partitionResult.isDone()).map(partitionResult -> {
				Optional<String> result = Optional.empty();
				try {
					result = partitionResult.get();
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
				return result;
			}).filter(customerId -> customerId.isPresent()).findAny().orElse(Optional.empty());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return Optional.empty();
	}

	public static Optional<String> getCustomerIdByNameWithSC(String name) {

		try (var scope = new StructuredTaskScope.ShutdownOnSuccess<String>()) {
			var jobs = Files.list(Paths.get("input/chunks")).map(chunk -> {
				Subtask<String> subtask = scope.fork(() -> {
					Optional<String> customerId = getCustomerId(chunk, name);
					return customerId.get();
				});
				return subtask;
			}).toList();

			scope.join();
			return Optional.ofNullable(scope.result());

		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {

		} finally {
			return Optional.empty();
		}
	}
}
