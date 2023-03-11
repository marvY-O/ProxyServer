package Binder;
import java.io.*;  
import java.net.*; 
import Message.*; 


public class Binder {

    public static Socket bind(int port) throws IOException{
        Socket socket = new Socket();
        InetAddress inetAddress=InetAddress.getByName("localhost");  
        SocketAddress socketAddress=new InetSocketAddress(inetAddress, port);  
        socket.bind(socketAddress);  
        System.out.println("Inet address: "+socket.getInetAddress());  
        System.out.println("Port number: "+socket.getLocalPort());  
        return socket;
    }

    public static void close(Socket s) throws IOException{
        s.close();
    }

    public static InetAddress getAddress() throws IOException{
        InetAddress inetAddress=InetAddress.getByName("localhost"); 
        return inetAddress;
    }

    public static void send(String address, int port, MessageMC msg) throws IOException{
        Socket socket = new Socket(address, port);
        OutputStream os = socket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);
        System.out.printf("Sending object to %s:%d ",address,port);
        oos.writeObject(msg);
        socket.close();
    }
}
