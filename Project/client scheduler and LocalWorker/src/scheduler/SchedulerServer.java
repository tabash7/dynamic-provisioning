package scheduler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;

public class SchedulerServer extends Thread{
	public Socket sock; 
	public boolean USE_REMOTE_WORKERS;
	public boolean USE_LOCAL_WORKERS;
	public int LOCAL_WORKERS_MAX; 
	public int REMOTE_WORKERS_MAX;
	public int NB_REMOTE_WORKERS = 0;
	public SpotRequests openrequests;
	public ArrayList<Integer> result;
	
    public SchedulerServer(Socket sock,boolean USE_REMOTE_WORKERS,boolean USE_LOCAL_WORKERS,
    		int LOCAL_WORKERS_MAX,int REMOTE_WORKERS_MAX)  {  
        this.sock=sock;  
        this.USE_LOCAL_WORKERS = USE_LOCAL_WORKERS;
        this.USE_REMOTE_WORKERS = USE_REMOTE_WORKERS;
        this.LOCAL_WORKERS_MAX = LOCAL_WORKERS_MAX;
        this.REMOTE_WORKERS_MAX = REMOTE_WORKERS_MAX;
        this.NB_REMOTE_WORKERS = 0;
        this.result = new ArrayList<Integer>();
        readConfig();
    }  
    // Check the task if completed
    public boolean checkTask(){
    	boolean boo = true;
    	Integer n = new Integer(1);
    	for(int i=0;i<result.size();i++){
    		if(result.get(i)==n){
    			boo = false;
    		}
    	}
    	return boo;
    }
    
    // Read configure file 
    public void readConfig(){
		String s = new String();
		try{
					
			File file = new File(WorkerInfo.local.config);
			if(file.isFile() && file.exists()){
				InputStreamReader read = new InputStreamReader(new FileInputStream(file));
				BufferedReader bufferReader = new BufferedReader(read);
				while((s = bufferReader.readLine()) != null){
					
					String info[] = s.split(" ");
					Node worker = new Node();
					worker.peerName = info[0];
					worker.IP = info[1];
					worker.port = Integer.parseInt(info[2]);
					WorkerInfo.local.workerInfo.add(worker);
					
					}
				}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
    
    public void run()  
    {  
        try{  
        OutputStream os=sock.getOutputStream();  
        InputStream is=sock.getInputStream();   
        byte []buf=new byte[100];  
        int len=is.read(buf);   
        if("submitTakst".equals(new String(buf,0,len))){
        	System.out.println("Receive tasks requests!");
        	boolean areWorkersAvailable;
        	Message message;
        	SpotRequests r = null;
        	Integer mm = 1;
    		try {
    			r = new SpotRequests();
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
        	AmazonSQS sqs = QueueManager.initSQS(); //Initializes the queues and stuff from SQS
        	if(USE_LOCAL_WORKERS){
        		System.out.println("Using local workers!");
    			// Create thread pool which has a max thread num
    			ExecutorService pool = Executors.newFixedThreadPool(LOCAL_WORKERS_MAX);
    			
    			// Get message from the SQS
    			while ((message = QueueManager.getNextMessage(sqs)) != null){
    				 	
    				System.out.println("Task received by local worker\n");
    				
    				QueueManager.removeTaskfromQueue(sqs, QueueManager.SubmittedTasksQueue, message);//Remove task from submitted queue
    				
    				Thread t = new Thread();	
    				
    				// Create thread to sleep
    				Callable task = new LocalSleep(message.getBody(), t, sqs, message);
    				Future flag = pool.submit(task);
    				try {
    					mm = (Integer) flag.get();
    				} catch (InterruptedException | ExecutionException e) {
    					e.printStackTrace();
    				}
    				result.add(mm);
    				// If the task execute successfully, print out completed!
    			    if(mm==0){
    			    	QueueManager.putTaskinQueue(sqs, QueueManager.CompletedTasksQueue, message);
    					System.out.println("Task "+message.getBody()+" completed!\n");
    			    }else{
    			    	System.out.println("Task "+message.getBody()+" failed!\n");
    			    }
    				
    			}
    			pool.shutdown();
    			

    		}	
    		if(USE_REMOTE_WORKERS){
    			System.out.println("Using remote workers!");
    			int queueLen = QueueManager.checkMessage(sqs);
//    			int index = 0;
    			int workerNum = 0;
    			if(queueLen!=0){
    				// Static Task
//    				for(int workerNum=0;workerNum<WorkerInfo.local.workerInfo.size();workerNum++){
//    					Node node1 = WorkerInfo.local.workerInfo.get(workerNum);
//        				System.out.println(node1.IP+" "+node1.port);
//        				// In the beginning, start 1 worker
//        				startRW remoteWorker = new startRW(node1);
//        				Thread thread = new Thread(remoteWorker);
//        				thread.start();
//    				}
    				
    				Node node1 = WorkerInfo.local.workerInfo.get(workerNum);
    				System.out.println(node1.IP+" "+node1.port);
    				// In the beginning, start 1 worker
    				startRW remoteWorker = new startRW(node1);
    				Thread thread = new Thread(remoteWorker);
    				thread.start();
    				workerNum++;
    				System.out.println("The "+workerNum+"th worker is started!");
    				
    				// Listening SQS size
    				// Dynamic scheduling of instances
    				int lenStart = queueLen;
    				int lenEnd;
    				long runtime = 0;
    				long start = System.currentTimeMillis();
    				while(workerNum < WorkerInfo.local.workerInfo.size()){
    					lenEnd = QueueManager.checkMessage(sqs);
    					long end = System.currentTimeMillis();
    					runtime = end - start;
    					// Make a dicision to start a new instances or not
    					// Dynamic start instances
    					if(runtime>500 && (lenEnd>lenStart)){
    						lenStart = lenEnd;
    						Node nodeQ = WorkerInfo.local.workerInfo.get(workerNum);
    	    				System.out.println(nodeQ.IP+" "+nodeQ.port);
    						startRW remoteWorkerQ = new startRW(nodeQ);
    	    				Thread threadQ = new Thread(remoteWorkerQ);
    	    				threadQ.start();	
    	    				workerNum++;
    	    				System.out.println("The "+workerNum+"th worker is started!");
    	    				
    					}
    				}

    				
    			}
    			
    		}
    		
    		if(checkTask()){
        		os.write("All tasks are successful!".getBytes());
				System.out.println("All tasks are successful!");
			}else{
				os.write("There exists tasks that failed!".getBytes());
				System.out.println("There exists tasks that failed!");
			}
        	
        }
        
        os.close();  
        is.close();  
        sock.close();  
        }  
        catch(Exception e)  
        {  
              
        }  
    }

}
