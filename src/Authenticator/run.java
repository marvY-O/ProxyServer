package Authenticator;
import java.io.IOException;
import java.net.*;
import java.util.*;

public class run {
    public static void main(String args[]) throws IOException{
        int ac_port = 5000;
        System.out.println("Server started at address: ");
        try {
            Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
            while( networkInterfaceEnumeration.hasMoreElements()){
                for ( InterfaceAddress interfaceAddress : networkInterfaceEnumeration.nextElement().getInterfaceAddresses()) {
                    if ( interfaceAddress.getAddress().isSiteLocalAddress())
                        System.out.println(interfaceAddress.getAddress().getHostAddress()+":"+ac_port);
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        Authenticator ac = new Authenticator(ac_port);
        ac.start();
        
    }
}
