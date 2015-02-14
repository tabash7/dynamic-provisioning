package worker;

public class Sleep implements Runnable{
	
	long time_to_sleep;
	Thread t;
	public Sleep(String task, Thread t){
		System.out.println(task.substring(6));
		this.time_to_sleep = Integer.parseInt(task.substring(6));
		this.t = t;
	}
	public void run(){
		
		try {
			Thread.sleep(this.time_to_sleep);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
