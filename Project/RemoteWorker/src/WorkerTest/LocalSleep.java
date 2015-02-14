package WorkerTest;


import java.util.StringTokenizer;
import java.util.concurrent.Callable;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;



//all the class is to implementation the sleep task
public class LocalSleep implements Callable{
	//some variable use fort the fuction
	long time_to_sleep;
	Thread t;
	Integer success;
	Integer fail;
	AmazonSQS sqs;
	Message message;
	String command;
	String taskID;
	
	public LocalSleep(String task, Thread t, AmazonSQS sqs, Message message){
		StringTokenizer st = new StringTokenizer(task);
		this.command = st.nextToken();
		this.time_to_sleep = Integer.parseInt(st.nextToken());
		this.taskID = st.nextToken();
	//	System.out.println(time_to_sleep);
		this.t = t;
		this.sqs = sqs;
		this.message = message;
		this.success = 0;
		this.fail = 1;
	}
	public Integer call(){
		
		try {
			Thread.sleep(this.time_to_sleep);
			return success;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return fail;
		}
				
	}
}

