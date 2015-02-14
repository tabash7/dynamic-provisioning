package worker;


import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;

public class Worker {

	/**
	 * @param args
	 */
	static boolean USE_REMOTE_WORKERS = false;
	static boolean USE_LOCAL_WORKERS = true;
	
	public static void main(String[] args) throws Exception {
		// TODO 
		//Receives task from TaskReceiver 
		//Processes the task
		//Updates the state of the task
		
		

		Message message;
		
		
		AmazonSQS sqs = QueueManager.initSQS(); //Initializes the queues and stuff from SQS
		
		
			while ((message = QueueManager.getNextMessage(sqs)) != null){
			
					
					QueueManager.removeTaskfromQueue(sqs, QueueManager.SubmittedTasksQueue, message);//Remove task from submitted queue
					QueueManager.putTaskinQueue(sqs, QueueManager.InprocessTasksQueue, message);//Put task in inprocess queue
					System.out.println("Task received by the worker\n");
					 		
					Thread t = new Thread();	
					Runnable task = new Sleep(message.getBody(), t);
					task.run();

					QueueManager.removeTaskfromQueue(sqs, QueueManager.InprocessTasksQueue, message);//Remove task from submitted queue
					QueueManager.putTaskinQueue(sqs, QueueManager.CompletedTasksQueue, message);//Put task in completed queue
					System.out.println("Task completed\n");
				
				
			}
								
			
	}

	

}
