package scheduler;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class startRW implements Runnable{
	public Node node;
	public startRW(Node node){
		this.node = node;
	}

	@Override
	public void run() {
		
		new clientThread(node);
	}

}

class clientThread extends Thread{
	private String IP;
	private int port;
	
	public clientThread(Node node){
		this.IP = node.IP;
		this.port = node.port;
		start();
	}
	
	public void run(){
		Socket socket = null;
		OutputStream os = null;  
        
		try {
			socket = new Socket(IP,port);
			System.out.println("startup");
			os=socket.getOutputStream();
			os.write("startup".getBytes());
			os.flush();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {   
            if (os != null)
				try {
					os.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
            if (socket != null)
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}   
        }
	}
}