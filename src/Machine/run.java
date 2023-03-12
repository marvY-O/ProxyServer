package Machine;
import java.io.IOException;
import java.util.Scanner;

public class run {
    public static void main (String args[]) throws IOException{
    	Scanner sc = new Scanner(System.in);
    	System.out.printf("Enter IP Address of the server: ");
    	String ac_address = sc.next();
        //String ac_address = "192.168.1.4";
       
        System.out.printf("Enter port of the server: ");
    	int port = sc.nextInt();
        //int  port = 5000;
        
        Machine m = new Machine(ac_address, port);
        m.initiate();
    }
}
