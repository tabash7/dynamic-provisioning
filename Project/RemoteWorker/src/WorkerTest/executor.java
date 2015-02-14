package WorkerTest;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.*;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;

import java.text.SimpleDateFormat;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;



//to execute the class
public class executor extends Thread{
	
	public Socket sock;
	AmazonSQS sqs = RemoteWorker.initSQS(); //Initializes the queues and stuff from SQS
	Message message;
	Integer mm = 1;
	//create the pool
	ExecutorService pool = Executors.newFixedThreadPool(100);
	boolean control = true;
	public int timestop;

	//create the DynamoDBClient
	static AmazonDynamoDBClient client = new AmazonDynamoDBClient(new ProfileCredentialsProvider());
	static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	static String MessageTableName = "MessageDone";
	
	
	
	public executor(Socket sock, int timestop){
		this.sock = sock;
		this.timestop = timestop;
		
	}
	
	// query to check is it duplicate task
	
	
	// function to get the message ID
	public static String getMSID(String task)
	{
		StringTokenizer st = new StringTokenizer(task);
		String command = st.nextToken();
		long time_to_sleep = Integer.parseInt(st.nextToken());
		String taskID = st.nextToken();
		
		return taskID;
		
	}
	
	
	//the  function to  do the query in the dynamoDB 
	  private static boolean getQuery(String id, String tableName)
	  {
		   
	        Map<String, AttributeValue> key = new HashMap<String, AttributeValue>();
	        key.put("Id", new AttributeValue().withN(id));
	        GetItemRequest getItemRequest = new GetItemRequest()
	             .withTableName(tableName)
	              .withKey(key)
	            //.withProjectionExpression("Id, ISBN, Title, Authors");
	            .withProjectionExpression("Id");
	        GetItemResult result = client.getItem(getItemRequest);
	        // Check the response.
	        	        
	        if(result.getItem() != null)
	        {
	     	  
	        	return false;
	   
	        }
	        else 
	        	return true;
	           
	    }
	
	  //update the task on the dynamoDB
	  private static void uploadSampleProducts(String tableName, String msid) 
	    {
	        
	        try {
	            // get the .
	            Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
	            item.put("Id", new AttributeValue().withN(msid));          
	            PutItemRequest itemRequest = new PutItemRequest().withTableName(tableName).withItem(item);
	            client.putItem(itemRequest);
	                
	        }   catch (AmazonServiceException ase) {
	            System.err.println("Failed to create item in " + tableName);
	        } 

	    }
	
	  
	 // this is the fuction to do the Animoto
	  public void Animoto(){
		  AmazonSQS sqs = RemoteWorker.initSQS(); //Initializes the queues and stuff from SQS
			Message message;
			Integer mm = 1;
			ExecutorService pool = Executors.newFixedThreadPool(100);
	        
			
		    int count = 0;
		    int index = 0;
			  
		     	while ((message = RemoteWorker.getNextMessage(sqs)) != null)
		     	{
//				System.out.println("Task received by remote worker\n");
				
		//		if(getQuery(getMSID(message.getBody()), "MessageDone"))
		    //      {
					  
//					 System.out.println("this is message id"+ message.getMessageId()); 
//		    	      System.out.println("new message, need to be executed"); 
		    	      RemoteWorker.removeTaskfromQueue(sqs, RemoteWorker.SubmittedTasksQueue, message);//Remove task from submitted queue	
					index++;
		    	  //execute the job
		    	    Thread t = new Thread();	
					Callable task = new Animoto(message.getBody(), t, sqs, message,index);
					Future flag = pool.submit(task);
					try 
					{
						mm = (Integer) flag.get();
					} catch (InterruptedException | ExecutionException e) 
					{
						e.printStackTrace();
					}
				    if(mm==0){
				    	count++;
				    	RemoteWorker.putTaskinQueue(sqs, RemoteWorker.CompletedTasksQueue, message);
						System.out.println("Task "+message.getBody()+" completed!\n");
				    }else
				    {
				    	System.out.println("Task "+message.getBody()+" failed!\n");
				    }
//					System.out.println(message.getBody());  
					// after finish the task, put the task into the dynamoDB, prevent the duplicate job, then next check 
				//	uploadSampleProducts(MessageTableName,getMSID(message.getBody()));
								    	 
		 //    }
		        
		  //   else  
		    //	 System.out.println("dupllicate message, no need to execute");
			//	RemoteWorker.removeTaskfromQueue(sqs, RemoteWorker.SubmittedTasksQueue, message);//Remove task from submitted queue	
				
		}
		     	
		     
			
	  }
	  
	  //this is the function to execute the task
	  
	  public int executeTask(int timestop){
		  AmazonSQS sqs = RemoteWorker.initSQS(); //Initializes the queues and stuff from SQS
			Message message;
			Integer mm = 1;
			ExecutorService pool = Executors.newFixedThreadPool(100);
	        
			
		    int count = 0;
			
//			 System.out.println( "this is queu number" +RemoteWorker.getqueueNumber(sqs));
			    boolean control = true;
			 
			 
			 while(control)
				 {
			 
			 
			 
		     	while ((message = RemoteWorker.getNextMessage(sqs)) != null)
		     	{
//				System.out.println("Task received by remote worker\n");
				
	               //before execute the task, check the task is duplicate or not	            
		           // Get an item.
				
				 //System.out.println(getQuery(Integer.toString(message.hashCode()), "MessageDone"));
				//System.out.println(message.hashCode());
				
				if(getQuery(getMSID(message.getBody()), "MessageDone"))
		          {
					  
//					 System.out.println("this is message id"+ message.getMessageId()); 
//		    	      System.out.println("new message, need to be executed"); 
		    	      RemoteWorker.removeTaskfromQueue(sqs, RemoteWorker.SubmittedTasksQueue, message);//Remove task from submitted queue	
					
		    	  //execute the job
		    	    Thread t = new Thread();	
					Callable task = new LocalSleep(message.getBody(), t, sqs, message);
					Future flag = pool.submit(task);
					try 
					{
						mm = (Integer) flag.get();
					} catch (InterruptedException | ExecutionException e) 
					{
						e.printStackTrace();
					}
				    if(mm==0){
				    	count++;
				    	RemoteWorker.putTaskinQueue(sqs, RemoteWorker.CompletedTasksQueue, message);
//						System.out.println("Task "+message.getBody()+" completed!\n");
				    }else
				    {
				    	System.out.println("Task "+message.getBody()+" failed!\n");
				    }
//					System.out.println(message.getBody());  
					// after finish the task, put the task into the dynamoDB, prevent the duplicate job, then next check 
					uploadSampleProducts(MessageTableName,getMSID(message.getBody()));
								    	 
		     }
		        
		     else  
		    	 System.out.println("dupllicate message, no need to execute");
				RemoteWorker.removeTaskfromQueue(sqs, RemoteWorker.SubmittedTasksQueue, message);//Remove task from submitted queue	
				
		}
		     	
		     	long runtime = 0;
				long start = System.currentTimeMillis();
				// Set cutoff time = 3s
				while(runtime<timestop)
				{
			        long end = System.currentTimeMillis();
					runtime = end - start;
					if(RemoteWorker.checkMessage(sqs))
					{
//						control = false;
						break;
					}
					else
					{
						
					}
				}
				
				if(RemoteWorker.checkMessage(sqs))
				{
					
				}

				else
				{
					control = false;
					System.out.println("jump out of the execute loop");
					System.out.println("Toatal successful task number is "+count);
				}
		     	
		      }
			 return count;
	  }
	public void run(){
		try{  
//	        OutputStream os=sock.getOutputStream();  
	        InputStream is=sock.getInputStream();   
	        byte []buf=new byte[100];  
	        int len=is.read(buf);   
	        if("startup".equals(new String(buf,0,len))){
	        	long runtime = 0;
				long start = System.currentTimeMillis();
				System.out.println("Start time:"+start);
	        	int count = executeTask(timestop);
//				Animoto();
	        	long end = System.currentTimeMillis();
				System.out.println("End time:"+end);
				runtime = end-start;
				System.out.println("Run time:"+runtime);
			    
				double flop = count/1000.0;
				flop = flop/runtime;
				System.out.println("Result is "+flop);
	       
	        }
	        
//	        os.close();  
	        is.close();  
	        sock.close();  
	        }  
	        catch(Exception e)  
	        {  
	              
	        }  
	}

}
