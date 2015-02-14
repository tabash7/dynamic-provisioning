package WorkerTest;


import java.util.concurrent.*;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;

import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.*;



// this calss is use for run
public class run 
{
	 static AmazonDynamoDBClient client = new AmazonDynamoDBClient(new ProfileCredentialsProvider());
	 static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	 static String MessageTableName = "MessageDone";
	
	 
	 
	 
	 
	 
	 //this is the main function
	 public static void main(String[] args) throws Exception
	{
		 
		 //get the parameter of the  time to stop, if the stoptime is 0, then the machine will not stop
		 int timestop = Integer.parseInt(args[0]);
		//int timestop = 5000;
		 //port number
		int port = 8000;
		
		
		
		
		
		
		
		try {  
			System.out.println("Start listening");
            ServerSocket ss=new ServerSocket(port);  
            while(true)  
            {  
            	Socket s=ss.accept();
            	new executor(s,timestop).start();
            }
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	 
	
	
	 
}