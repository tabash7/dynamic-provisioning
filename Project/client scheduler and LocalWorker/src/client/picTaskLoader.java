package client;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


public class picTaskLoader {
	
	private static File fFile;
	static ArrayList<String> tasks = new ArrayList<String>();
	
	
	public static ArrayList<String> loadfromFile(String workloadfile) throws FileNotFoundException{
		
		/*
		try {
			BufferedReader file = new BufferedReader(new FileReader(workloadfile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		fFile = new File(workloadfile);
	    return processEachLine();

		//Goes through a file and processes each line to be formatted as a task
		
	}
	
	public final static ArrayList<String> processEachLine() throws FileNotFoundException {
		   
		//Note that FileReader is used, not File, since File is not Closeable
	    Scanner scanner = new Scanner(new FileReader(fFile));
	    try {
	      //first use a Scanner to get each line
	      while ( scanner.hasNextLine() ){
	    	  tasks.add(scanner.next());
	      }
	    }
	    finally {
	      //ensure the underlying stream is always closed
	      //this only has any effect if the item passed to the Scanner
	      //constructor implements Closeable (which it does in this case).	    	
	      scanner.close();
	      
	    }
	    return tasks;
	  }
	
	

}
