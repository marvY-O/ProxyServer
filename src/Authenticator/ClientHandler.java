package Authenticator;
import java.io.*;
import Message.*;
import java.net.*;
import java.util.HashMap;
import java.util.Queue;

class ClientHandler implements Runnable {
    
    Socket s;
    public HashMap<InetAddress, Queue<Packet>> buffer;
      
    public ClientHandler(Socket sc, HashMap<InetAddress, Queue<Packet>> buffer) throws IOException{
        s = sc;
        this.buffer = buffer;
    }
  
    @Override
    public void run() {
    	ObjectOutputStream oos;
    	ObjectInputStream ois;

        try{
        	
        	System.out.println("Thread started!");
    		
    		oos = new ObjectOutputStream(s.getOutputStream());
    		ois = new ObjectInputStream(s.getInputStream());
    		
    		Runnable receiver = new Runnable() {
                @Override
                public void run() {
                    //System.out.println("Hello from thread " + Thread.currentThread().getName());
                	System.out.println("Receiver Started on server!");
                	while (true) {
                			Packet p;
							try {
								p = (Packet) ois.readObject();
								//if (p.destination_ip == InetAddress.getLocalHost().getHostAddress());
								InetAddress destAddr = InetAddress.getByName(p.destination_ip);
								synchronized (buffer) {
									if (buffer.containsKey(destAddr)) {
										buffer.get(destAddr).add(p);
									}
								}
							} catch (ClassNotFoundException e) {
								// TODO Auto-generated catch block
								//e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								//e.printStackTrace();
							}
                	}
                	
                }
            };
            
            Runnable sender = new Runnable() {
                @Override
                public void run() {
                    //System.out.println("Hello from thread " + Thread.currentThread().getName());
                	System.out.println("Sender Started on server!");
                	while (true) {
                		if (buffer.get(s.getInetAddress()).size() == 0) continue;
                		synchronized (buffer) {
	            			Packet curPacket = buffer.get(s.getInetAddress()).peek();
	            			buffer.get(s.getInetAddress()).poll();
	            			try {
	            				oos.writeObject(curPacket);
	            			} catch(IOException e) {
	            				e.printStackTrace();
	            			}
                		}
                	}
                	
                }
            };
            
            
            Thread recv = new Thread(receiver);
            recv.start();
            
            Thread send = new Thread(sender);
            send.start();
             
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
       
    
                
}