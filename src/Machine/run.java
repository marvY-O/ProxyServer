package Machine;
import java.io.IOException;
import java.util.Scanner;

public class run {
    public static void main (String args[]) throws IOException{
    	Scanner sc = new Scanner(System.in);
    	System.out.println("Enter IP Address of the server: ");
    	//String ac_address = sc.next();
        String ac_address = "192.168.1.4";
       
        System.out.println("Enter IP Address of the server: ");
    	//int port = sc.nextInt();
        int  port = 5000;
        
        Machine_2 m = new Machine_2(ac_address, port);
        m.initiate();
    }
}
