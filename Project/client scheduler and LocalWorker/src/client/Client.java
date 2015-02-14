package client;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import com.amazonaws.services.sqs.AmazonSQS;


public class Client {
	
	static boolean verbose = false;
	
	public static void main(String[] args) throws FileNotFoundException {

		String IP = args[0];
		int port = Integer.parseInt(args[1]);
		String workloadfile = args[2];
		ArrayList<String> tasks = new ArrayList<String>();

		AmazonSQS sqs = QueueManager.initSQS(); //Initializes the queues and stuff from SQS
		tasks = TaskLoader.loadfromFile(workloadfile); //Loads tasks into the queue
//		tasks = picTaskLoader.loadfromFile(workloadfile);
		System.out.println(tasks.size() + " tasks loaded from the workload file");
		// Send message to submitted queue
//		for (int i = 0; i < tasks.size(); i++){
//			QueueManager.sendToQueue(tasks.get(i), sqs, QueueManager.SubmittedTasksQueue); //Loads every task in the file into the queue
//		}
		
		for (int i = 0; i < 10; i++){
			QueueManager.sendToQueue(tasks.get(i), sqs, QueueManager.SubmittedTasksQueue); //Loads every task in the file into the queue
		}
		
		// Set up TCP client connect to scheduler
		try {  
            Socket s=new Socket(IP,port);//"localhost" "127.0.0.1s"            
            OutputStream os=s.getOutputStream();        
            os.write("submitTakst".getBytes()); 
            for (int j = 10; j < tasks.size(); j++){
    			QueueManager.sendToQueue(tasks.get(j), sqs, QueueManager.SubmittedTasksQueue); //Loads every task in the file into the queue
    		}
            InputStream is=s.getInputStream(); 
            byte []buf=new byte[100];  
            int len=is.read(buf);
            System.out.println(new String(buf,0,len));
            Thread.sleep(100);  
            os.close();    
            s.close();  
      
        } catch (Exception e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
		
		// Monitor the queue status
		
		while(true){
			System.out.println("\nPress Enter to get the status of the queues\n");
			@SuppressWarnings("resource")
			Scanner sc = new Scanner(System.in);
		    sc.nextLine();
			TaskMonitor.displayTasksStatus(sqs, verbose); //Displays the status of the tasks
			//sc.close();
		}

	}
	

}
