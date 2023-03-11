package Authenticator;
import java.io.IOException;
import java.io.*;
import java.net.*;
import java.util.*;
import Message.Packet;

public class Authenticator {
    int port;
    HashMap<InetAddress, Queue<Packet>> buffer;
    
    public Authenticator(int port) {
        this.port = port;
        buffer = new HashMap<InetAddress, Queue<Packet>>();
    }

    public void start() throws IOException{
        ServerSocket ss = new ServerSocket(port);
        while (true){
            Socket s = null; 
            try {
                s = ss.accept();
                System.out.println("A new client is connected : " );
                System.out.println(s.getInetAddress());
                
                synchronized (buffer) {
                	buffer.put(s.getInetAddress(), new LinkedList<Packet>());
                }
                
                ClientHandler clientNew = new ClientHandler(s, buffer);
                
                Thread t = new Thread(clientNew);
                t.start();                  
                
            }
            catch (Exception e){
                s.close();
                e.printStackTrace();
            }
        }
    }

}
    

