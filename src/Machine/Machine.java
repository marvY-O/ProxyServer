package Machine;
import java.io.*;  
import java.util.*;
import java.net.*; 
import Message.*; 

public class Machine {
    //private InetAddress selfAddress;
    String ac_address;
    int ac_port;
    Queue<Packet> buffer;
    Queue<Packet> receiveBuffer;

    public Machine(String ac_address, int ac_port) throws IOException{
        //selfAddress  = Binder.getAddress();
        this.ac_address = ac_address;
        this.ac_port = ac_port;
        this.buffer = new LinkedList<Packet>();
        this.receiveBuffer = new LinkedList<Packet>();
    }

    public void initiate() throws IOException{
    	Scanner sc = new Scanner(System.in);
        try{

            Socket s = new Socket(ac_address, ac_port);
            System.out.print(s);
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            System.out.println("Connected to server!!");
            
            Runnable receivePackets = new Runnable() {
            	@Override
            	public void run() {
            		Packet p;
            		System.out.println("Recv Started!");
            		while (true) {
            			try {
    						p = (Packet) ois.readObject();
    						if (p != null) {
        						receiveBuffer.add(p);
        						if (p.pkt_id == -1) {
        							break;
        						}
    						}
    						
    						
    					} catch (ClassNotFoundException e) {
    						System.out.printf("Error reading packets (Undefined Format): ");
    						e.printStackTrace();
    						try {
    							oos.close();
								ois.close();
								s.close();
							} catch (IOException e1) {
								System.out.printf("Error closing connection: ");
								e1.printStackTrace();
							}
    					} catch (IOException e) {
    						System.out.printf("Error receiving packets!!");
    						e.printStackTrace();
    						try {
    							oos.close();
								ois.close();
								s.close();
							} catch (IOException e1) {
								System.out.printf("Error closing connection: ");
								e1.printStackTrace();
							}
    						
    					}
            		}
            		
            		System.out.println("Creating File!!");
            		Vector<Byte> file = new Vector<Byte>();
            		final String outputPath = receiveBuffer.peek().msg_name; 
            		//final String outputPath = "newOutFile.txt";
            		
            		System.out.printf("Received "+outputPath+"from "+receiveBuffer.peek().client_ip+"\n");
                    while (!receiveBuffer.isEmpty()){
                    	Packet pkt = receiveBuffer.poll();
                    	
                        for (int j=0; j<pkt.payload.length && pkt.payload[j] != (byte)'\0'; j++){
                            file.add((Byte) pkt.payload[j]);
                        }
                    }
                    
                    byte[] byteFile = new byte[file.size()];
                    for (int i=0; i<file.size(); i++) {
                    	byteFile[i] = (byte) file.elementAt(i);
                    }
                    
                    try {
	            		FileOutputStream fos = new FileOutputStream(outputPath);
	                    fos.write(byteFile);
	                    fos.close();
	                    System.out.printf("File Saved to disk!\n");
                    }
                    catch(IOException e) {
                    	System.out.println("Error Saving file to disk!");
                    }
					
            	}
            };
            
            
            
            System.out.printf("Recieve file(y) or send file(n)?");
            String ans = sc.next();
            if (ans.equals("y")) {
            	Thread receivePacketsThread = new Thread(receivePackets);
                receivePacketsThread.start();
            }
            else {
            	String path, destIP, clientIP = InetAddress.getByName("localhost").getHostAddress();
                
                System.out.printf("Name file to send: ");
                //path = sc.next();
                path = "test.txt";
                
                System.out.printf("IP of Destination: ");
                //destIP = sc.next();
                destIP = "192.168.1.7";
                
                byte[] file;
                File fileobj = new File(path);

                try {
                    FileInputStream fis = new FileInputStream(fileobj);
                    file = new byte[(int) fileobj.length()];
                    fis.read(file);
                    fis.close();
                }
                catch(IOException e) {
                    System.out.print("Exception while opening file: ");
                    e.printStackTrace();
                    ois.close();
                    oos.close();
                    s.close();
                    return;
                }
                
                int pyld_size = 7;
                final int pkt_total = file.length/pyld_size + (file.length%pyld_size == 0 ? 0 : 1);
                //if (file.length%pyld_size != 0) pkt_total++;
                System.out.printf("\nMessage Size: %d Payload Size: %d Total Packets: %d\n\n",file.length, pyld_size, pkt_total);
                
                
                
                Runnable fragmentPackets = new Runnable() {
                	@Override
                	public void run() {
                		int index = 0;
                        //Packet[] pkts = new Packet[pkt_total];
                        for (int i=0; i<pkt_total+1; i++){
                        	System.out.println("Creating: " + i);
                            Packet pkt= new Packet(pyld_size);
                            pkt.client_name = "localhost";
                            pkt.client_ip = clientIP;
                            pkt.destination_ip = destIP;
                            pkt.pkt_no = i;
                            if (i == pkt_total) pkt.pkt_id = -1; 
                            pkt.msg_name = path;
                            int j=0;
                            for (; j<pyld_size && index<file.length; j++){
                                pkt.payload[j] = (byte) file[index];
                                index++;
                            }
                            if (index==file.length && j<pyld_size){
                                pkt.payload[j] = (byte) '\0';
                            }
                            synchronized(buffer) {
                            	buffer.add(pkt);
                            }
                        }
                	}
                };
                
                Thread fragmentPacketsThread = new Thread(fragmentPackets);
                fragmentPacketsThread.start();
                
                Runnable sendPackets = new Runnable() {
                	@Override
                	public void run() {
                		while (true) {
    	            		synchronized(buffer) {
    	            			if (!buffer.isEmpty()) {
    	            				try {
    	            					System.out.println("Sent: " + buffer.peek().pkt_id);
    	            					oos.writeObject(buffer.poll());
    	            				}
    	            				catch (IOException e) {
    	            					System.out.printf("Error sending packets: ");
    	            					e.printStackTrace();
    	            		            
    	            		            try {
											s.close();
											ois.close();
	    	            		            oos.close();
										} catch (IOException e1) {
											System.out.printf("Error closing connection: ");
											e1.printStackTrace();
										}
    	            				} 
    	            			}
    	            		}
                		}
                		
                		
                		
                	}
                };
                
                Thread sendPacketsThread = new Thread(sendPackets);
                sendPacketsThread.start();
                 
                Thread receivePacketsThread = new Thread(receivePackets);
                receivePacketsThread.start();
             
            }
            
            
        }catch(Exception e){
        	System.out.printf("There was an error connecting to the server: ");
            e.printStackTrace();
        }
    }
}
