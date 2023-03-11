package Machine;
import java.io.*;  
import java.util.*;
import java.net.*; 
import Message.*; 

public class Machine {
    //private InetAddress selfAddress;
    String ac_address;
    int ac_port;

    public Machine(String ac_address, int ac_port) throws IOException{
        //selfAddress  = Binder.getAddress();
        this.ac_address = ac_address;
        this.ac_port = ac_port;
    }

    public void initiate() throws IOException{
    	Scanner sc = new Scanner(System.in);
        try{

            Socket s = new Socket(ac_address, ac_port);
            System.out.print(s);
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            System.out.println("Connected to server ... ");
            
            
            Packet p = new Packet(0);
            p.msg_name = "GO FOR THE HEAD!!";
            System.out.printf("IP AC: ");
            p.destination_ip = sc.next();
            
            if (p.destination_ip.length() != 1) {
            	oos.writeObject(p);
            }
            else {
            	System.out.println("Listening for data!");
            }
            
            
            Runnable recv = new Runnable() {
            	@Override
            	public void run() {
            		Packet p;
            		System.out.println("Recv Started!");
            		while (true) {
            			try {
    						p = (Packet) ois.readObject();
    						//if (p.destination_ip == InetAddress.getLocalHost().getHostAddress());
    						System.out.println(p.msg_name);
    						
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
            
            Thread t = new Thread(recv);
            t.start();
              
            //ois.close();
            //oos.close();
            //s.close();
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
