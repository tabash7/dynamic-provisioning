package client;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class QueueManager {
	
	static String SubmittedTasksQueue;
	static String CompletedTasksQueue;
	
public static AmazonSQS initSQS(){
		
		//Connects to Amazon AWS
		AmazonSQS sqs = new AmazonSQSClient(new ClasspathPropertiesFileCredentialsProvider());
//		Region usEast1 = Region.getRegion(Regions.US_EAST_1);
//		sqs.setRegion(usEast1);
		
		Region usWest = Region.getRegion(Regions.US_WEST_2);
		sqs.setRegion(usWest);

		try {
			// Creates the queues (something should be added in case the queue already exists

			System.out.println("Creating a new queue for submitted tasks\n");
			CreateQueueRequest createQueueRequest_submitted = new CreateQueueRequest("SubmittedTasks");
			SubmittedTasksQueue = sqs.createQueue(createQueueRequest_submitted).getQueueUrl();

			System.out.println("Creating a new queue for completed tasks\n");
			CreateQueueRequest createQueueRequest_completed = new CreateQueueRequest("CompletedTasks");
			CompletedTasksQueue = sqs.createQueue(createQueueRequest_completed).getQueueUrl();


		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which means your request made it " +
					"to Amazon SQS, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which means the client encountered " +
					"a serious internal problem while trying to communicate with SQS, such as not " +
					"being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
		return sqs;
		
	}
	
	public static void sendToQueue(String task, AmazonSQS sqs, String queue){
		
		sqs.sendMessage(new SendMessageRequest(queue, task));
		System.out.println("task : "+task+" sent to : "+queue);
		
	}

}
