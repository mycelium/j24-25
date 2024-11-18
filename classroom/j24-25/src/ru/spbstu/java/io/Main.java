package ru.spbstu.java.io;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class Main {
	public static void main(String[] args) {
		
		InputStream is = null;
		OutputStream os = null;
		
		FilterInputStream fis;
		FilterOutputStream fos;
		
		File firstFile = new File("tst.txt");
		String content = "Hello world";
		try {
			firstFile.createNewFile();
			is = new FileInputStream(firstFile);
			os = new FileOutputStream(firstFile);
			os.write(content.getBytes());
			os.flush();
			String result = new String(is.readAllBytes());
			System.out.println(result);
			
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}finally {
			try {
				is.close();
				os.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try(OutputStream os2 = new FileOutputStream(firstFile)){
			os2.write(new String("\n Hi!").getBytes());
		}catch (IOException e) {
			System.err.println(e.getMessage());
		}
		
		Scanner sc = new Scanner(System.in);
		
		BufferedOutputStream bos = new BufferedOutputStream(os);
		InputStream bais = new ByteArrayInputStream(new byte[10]);
		ByteArrayOutputStream baos; 
		
		ZipInputStream zis;
		ZipOutputStream zos;
		
		Path path = Paths.get("/home/user","lab","file.txt");
		
		try {
			System.out.println(Files.readString(Paths.get("tst.txt")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		// TODO learn how to incrementally write to files
		Channel ch;
		FileChannel fch;
		ByteBuffer bb;
		
	}
}
