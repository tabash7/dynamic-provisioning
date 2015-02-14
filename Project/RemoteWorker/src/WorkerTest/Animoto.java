package WorkerTest;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;


//to do the Animoto
public class Animoto implements Callable{

	long time_to_sleep;
	Thread t;
	Integer success;
	Integer fail;
	AmazonSQS sqs;
	Message message;
	String task;
	String name;

	
	public Animoto(String task, Thread t, AmazonSQS sqs, Message message,int index){

	//	System.out.println(time_to_sleep);
		this.t = t;
		this.sqs = sqs;
		this.message = message;
		this.success = 0;
		this.fail = 1;
		this.task= task;
		this.name = Integer.toString(index)+".jpg";
	}
	
	public static byte[] readInputStream(InputStream inStream) throws Exception{  
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        byte[] buffer = new byte[1024];  
        int len = 0;  
        while( (len=inStream.read(buffer)) != -1 ){  
            outStream.write(buffer, 0, len);  
        }  
        inStream.close();  
        return outStream.toByteArray();  
    }  
	
	public Integer call(){
		
		try {
			
			URL url = new URL(task);  
	        HttpURLConnection conn = (HttpURLConnection)url.openConnection();   
	        conn.setRequestMethod("GET");  
	        conn.setConnectTimeout(5*1000);  
	        InputStream inStream = conn.getInputStream();  
	        byte[] data = readInputStream(inStream);   
	        File imageFile = new File(name);  
	        FileOutputStream outStream = new FileOutputStream(imageFile);  
	        outStream.write(data);    
	        outStream.close();  
			return success;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return fail;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fail;
				
	}
}
