package Message;

import java.io.Serializable;

public class Packet implements Serializable {
    public String client_name;
    public String client_ip;
    public String destination_ip;
    public byte[] payload;
    public int pkt_no;
    public int pkt_id;
    public String msg_name;
    public Packet(int payload_size){
        payload = new byte[payload_size];
    }
};