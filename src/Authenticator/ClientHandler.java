package Authenticator;
import java.io.*;
import Database.*;
import Message.*;
import java.net.*;
import java.util.HashMap;
import java.util.Queue;
import java.security.SecureRandom;

class ClientHandler implements Runnable {
    
    Socket s;
    public HashMap<InetAddress, Queue<Packet>> buffer;
    public HashMap<InetAddress, String> certIDStore;
      
    public ClientHandler(Socket sc, HashMap<InetAddress, Queue<Packet>> buffer, HashMap<InetAddress, String> certIDStore) throws IOException{
        s = sc;
        this.buffer = buffer;
        this.certIDStore = certIDStore;
    }

    public static String generate() {
    	String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom RANDOM = new SecureRandom();
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
  
    @Override
    public void run() {
    	ObjectOutputStream oos;
    	ObjectInputStream ois;

        try{
    		
    		oos = new ObjectOutputStream(s.getOutputStream());
    		ois = new ObjectInputStream(s.getInputStream());
    		
    		SecurityCertificate cert;
    		while (true) {
    			try {
    				cert = (SecurityCertificate) ois.readObject();
    				if (cert != null) break;
    			} catch(IOException e) {
    				
    			} catch(ClassNotFoundException e) {
    				
    			}
    		}
    		
    		if (dbUsers.verify(cert.username, cert.password)) {
    			cert.CertificateID = generate();
    			certIDStore.put(s.getInetAddress(), cert.CertificateID);
    			System.out.printf("%s -> %s\n", s.getInetAddress().getHostAddress(), cert.CertificateID);
    		}
    		else {
    			cert.CertificateID = "NULL";
    		}
    		
    		oos.writeObject(cert);
    		
    		Runnable receiver = new Runnable() {
                @Override
                public void run() {

                	while (true) {
                			Packet p;
							try {
								p = (Packet) ois.readObject();
								//if (p.destination_ip == InetAddress.getLocalHost().getHostAddress());
								InetAddress destAddr = InetAddress.getByName(p.destination_ip);
								if (!p.cert_id.equals(certIDStore.get(s.getInetAddress()))){
									System.out.println("FALSE SECURITY CERTIFICATE ID!!");
									continue;
								}
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

                	while (true) {
                		if (buffer.get(s.getInetAddress()).size() == 0) continue;
                		Packet curPacket;
                		synchronized (buffer) {
	            			curPacket = buffer.get(s.getInetAddress()).peek();
	            			buffer.get(s.getInetAddress()).poll();
                		}
            			try {
            				curPacket.cert_id = certIDStore.get(s.getInetAddress());
            				oos.writeObject(curPacket);
            			} catch(IOException e) {
            				e.printStackTrace();
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