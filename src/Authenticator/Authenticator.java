package Authenticator;
import java.io.IOException;
import java.io.*;
import java.net.*;
import java.util.*;
import Message.Packet;

public class Authenticator {
    int port;
    HashMap<InetAddress, Queue<Packet>> buffer;
    HashMap<InetAddress, String> certIDStore;
    
    public Authenticator(int port) {
        this.port = port;
        buffer = new HashMap<InetAddress, Queue<Packet>>();
        certIDStore = new HashMap<InetAddress, String>();
    }

    public void start() throws IOException{
        ServerSocket ss = new ServerSocket(port);

        while (true){
            Socket s = null; 
            try {
                s = ss.accept();
                System.out.printf("A new client is connected : %s\n", (String) s.getInetAddress().getHostAddress());
                //System.out.println();
                
                synchronized (buffer) {
                	buffer.put(s.getInetAddress(), new LinkedList<Packet>());
                }
                
                ClientHandler clientNew = new ClientHandler(s, buffer, certIDStore);
                
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
    

