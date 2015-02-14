package worker;

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
	
//	public static String SubmittedTasksQueue = "https://sqs.us-east-1.amazonaws.com/957925617529/SubmittedTasks";
//	public static String InprocessTasksQueue = "https://sqs.us-east-1.amazonaws.com/957925617529/InprocessTasks";
//	public static String CompletedTasksQueue = "https://sqs.us-east-1.amazonaws.com/957925617529/CompletedTasks";
	
	public static String SubmittedTasksQueue = "https://sqs.us-west-2.amazonaws.com/062676108437/SubmittedTasks";
	public static String InprocessTasksQueue = "https://sqs.us-west-2.amazonaws.com/062676108437/InprocessTasks";
	public static String CompletedTasksQueue = "https://sqs.us-west-2.amazonaws.com/062676108437/CompletedTasks";
	
	public static AmazonSQS initSQS(){
			
			//Connects to Amazon AWS
			AmazonSQS sqs = new AmazonSQSClient(new ClasspathPropertiesFileCredentialsProvider());
			Region usWest = Region.getRegion(Regions.US_WEST_2);
			sqs.setRegion(usWest);
			return sqs;		
			
	}
	
	public static Message getNextMessage(AmazonSQS sqs){
		
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(SubmittedTasksQueue);
        List<Message> SubmittedTasksQueue = sqs.receiveMessage(receiveMessageRequest).getMessages();
        if (!SubmittedTasksQueue.isEmpty()){
        	for (Message message : SubmittedTasksQueue) {
        		System.out.println("Getting next task...\n");
        		return message;
        	}
        }
        System.out.println("No more tasks to be executed...\n");
		return null;
	}
	
	public static void removeTaskfromQueue(AmazonSQS sqs, String queue, Message message){
		System.out.println("Removing task from the "+ queue +" tasks queue...\n");
        String messageReceiptHandle = message.getReceiptHandle();
        sqs.deleteMessage(new DeleteMessageRequest(queue, messageReceiptHandle));
	}
	
	public static void putTaskinQueue(AmazonSQS sqs, String queue, Message message){
		
        System.out.println("Sending task to the "+ queue +" tasks queue...\n");
        sqs.sendMessage(new SendMessageRequest(queue, message.getBody()));
		
	}

}
