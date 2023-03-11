package Machine;
import java.io.IOException;

public class run {
    public static void main (String args[]) throws IOException{
    	System.out.println("Starting Machine...");
        String ac_address = "192.168.1.4";
        int port = 5000;
        Machine m = new Machine(ac_address, port);
        m.initiate();
    }
}
