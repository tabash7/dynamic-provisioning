package scheduler;

import java.net.ServerSocket;
import java.net.Socket;

public class Scheduler {

	/**
	 * @param args
	 */
	static boolean USE_REMOTE_WORKERS = false;
	static boolean USE_LOCAL_WORKERS = true;
	static int LOCAL_WORKERS_MAX = 5; //Number of workers to be instanciated by the Scheduler to process tasks
	static int REMOTE_WORKERS_MAX = 32;
//	static int NB_REMOTE_WORKERS = 0;
//	static public SpotRequests openrequests;
	
	public static void main(String[] args) throws Exception {
		// TODO 
		//Receives task from TaskReceiver 
		//Processes the task
		//Updates the state of the task
		//Scheduler Input: port -lw<num>/-rw
		
//		SpotRequests r = new SpotRequests();
		
//		boolean areWorkersAvailable;
//		Message message;

		int port = Integer.parseInt(args[0]);
		int REMOTE_WORKERS = 32;
		int LOCAL_WORKERS = 5;
		
		// Get input parameters
		// Do remote worker
		if (args[1].equals("-rw")){
			USE_REMOTE_WORKERS = true;
			USE_LOCAL_WORKERS = false;
			REMOTE_WORKERS = Math.min(REMOTE_WORKERS_MAX, Integer.parseInt(args[2]));
			
			System.out.println("Use of remote workers");
		}
		// Do local worker
		else if (args[1].equals("-lw")){
			USE_REMOTE_WORKERS = false;
			USE_LOCAL_WORKERS = true;
			LOCAL_WORKERS = Math.min(LOCAL_WORKERS_MAX, Integer.parseInt(args[2]));
				
			System.out.println("Use of local workers");
		}
		
//		AmazonSQS sqs = QueueManager.initSQS(); //Initializes the queues and stuff from SQS
		
		// Set up TCP server
		try {  
			// Start server
            ServerSocket ss=new ServerSocket(port);  
            while(true)  
            {  
            Socket s=ss.accept();  
             // Create schedulerserver
            new SchedulerServer(s,USE_REMOTE_WORKERS,USE_LOCAL_WORKERS,LOCAL_WORKERS,
            		REMOTE_WORKERS).start();  
            }  
       
        } catch (Exception e) {  
             
            e.printStackTrace();  
        }  
		
	}

	

}
