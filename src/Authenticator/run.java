package Authenticator;
import java.io.IOException;

public class run {
    public static void main(String args[]) throws IOException{
        int ac_port = 5000;
        Authenticator ac = new Authenticator(ac_port);
        ac.start();
    }
}
