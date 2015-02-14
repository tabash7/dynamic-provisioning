package scheduler;

import java.io.Serializable;

public class Node{
	
	public String peerName;
	public String IP;
	public int port;
	
	public Node(){
		
	}
	
	public Node(String IP, int port){
		this.IP = IP;
		this.port = port;
	}
	
	public Node(String peerName, String IP, int port){
		this.peerName = peerName;
		this.IP = IP;
		this.port = port;
	}

	public void NodeInfo() {
		System.out.println(peerName + " " + IP + " " + port);
	}
}

