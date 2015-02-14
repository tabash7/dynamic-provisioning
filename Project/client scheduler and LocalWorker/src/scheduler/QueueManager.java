package scheduler;

import java.util.List;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class QueueManager {
	
	
	public static String SubmittedTasksQueue = "https://sqs.us-west-2.amazonaws.com/062676108437/SubmittedTasks";
	public static String CompletedTasksQueue = "https://sqs.us-west-2.amazonaws.com/062676108437/CompletedTasks";
	
	// Init SQS
	public static AmazonSQS initSQS(){
			
			//Connects to Amazon AWS
			AmazonSQS sqs = new AmazonSQSClient(new ClasspathPropertiesFileCredentialsProvider());
			Region usWest = Region.getRegion(Regions.US_WEST_2);
			sqs.setRegion(usWest);
			return sqs;		
			
	}
	
	// Get message from SQS on Amazon Web Service
	public static Message getNextMessage(AmazonSQS sqs){
		
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(SubmittedTasksQueue);
        List<Message> SubmittedTasksQueue = sqs.receiveMessage(receiveMessageRequest).getMessages();
        if (!SubmittedTasksQueue.isEmpty()){
        	for (Message message : SubmittedTasksQueue) {
        		System.out.println("Getting next task...\n");
        		return message;
        	}
        }
        System.out.println("No more tasks to process...\n");
		return null;
	}
	
	// Check the message queue size
	public static int checkMessage(AmazonSQS sqs){
		int queueLen=0;
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(SubmittedTasksQueue);
        List<Message> SubmittedTasksQueue = sqs.receiveMessage(receiveMessageRequest).getMessages();
        queueLen = SubmittedTasksQueue.size();
        return queueLen;
        
	}
	
	// Remove task from the SQS 
	public static void removeTaskfromQueue(AmazonSQS sqs, String queue, Message message){
		System.out.println("Removing task from the "+ queue +" tasks queue...\n");
        String messageReceiptHandle = message.getReceiptHandle();
        sqs.deleteMessage(new DeleteMessageRequest(queue, messageReceiptHandle));
	}
	
	// Put task in the queue
	public static void putTaskinQueue(AmazonSQS sqs, String queue, Message message){
		
        System.out.println("Sending task to the "+ queue +" tasks queue...\n");
        sqs.sendMessage(new SendMessageRequest(queue, message.getBody()));
		
	}

}
