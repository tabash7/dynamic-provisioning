package WorkerTest;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.auth.AWSCredentials;


//for the remoteworker class
public class RemoteWorker
{
	public static String SubmittedTasksQueue = "https://sqs.us-west-2.amazonaws.com/062676108437/SubmittedTasks";
//	public static String InprocessTasksQueue = "https://sqs.us-west-2.amazonaws.com/062676108437/InprocessTasks";
	public static String CompletedTasksQueue = "https://sqs.us-west-2.amazonaws.com/062676108437/CompletedTasks";

  //init the sqs 
	public static AmazonSQS initSQS()
	{
		
		//Connects to Amazon AWS
		AmazonSQS sqs = new AmazonSQSClient(new ClasspathPropertiesFileCredentialsProvider());
		Region usWest = Region.getRegion(Regions.US_WEST_2);
		sqs.setRegion(usWest);
		return sqs;		
		
     }

	
	//the function to get the sqs message
public static Message getNextMessage(AmazonSQS sqs){
	
    ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(SubmittedTasksQueue);
    List<Message> SubmittedTasksQueue = sqs.receiveMessage(receiveMessageRequest).getMessages();
    if (!SubmittedTasksQueue.isEmpty()){
    	for (Message message : SubmittedTasksQueue) {
//    		System.out.println("Getting next task...\n");
    		return message;
    	}
    }
    System.out.println("No more tasks to be executed...\n");
	return null;
}


//the function to check the message
public static boolean checkMessage(AmazonSQS sqs){
	
    ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(SubmittedTasksQueue);
    List<Message> SubmittedTasksQueue = sqs.receiveMessage(receiveMessageRequest).getMessages();
    if (!SubmittedTasksQueue.isEmpty()){
    	return true;
    }else{
    	return false;
    }
	
}


//the function to remove the task
public static void removeTaskfromQueue(AmazonSQS sqs, String queue, Message message)
{
//	System.out.println("Removing task from the "+ queue +" tasks queue...\n");
    String messageReceiptHandle = message.getReceiptHandle();
    sqs.deleteMessage(new DeleteMessageRequest(queue, messageReceiptHandle));
}



//the function to get the number of the queue
public static int getqueueNumber(AmazonSQS sqs)
{
	
    ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(SubmittedTasksQueue);
    List<Message> SubmittedTasksQueue = sqs.receiveMessage(receiveMessageRequest).getMessages();
   
	return SubmittedTasksQueue.size() ;
}





//the number of put task in queue
public static void putTaskinQueue(AmazonSQS sqs, String queue, Message message)
{
//     System.out.println("Sending task to the "+ queue +" tasks queue...\n");
    sqs.sendMessage(new SendMessageRequest(queue, message.getBody()));
}




}
    




