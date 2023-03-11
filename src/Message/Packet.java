package Message;

public class Packet {
	public String client_name;
	public String client_ip;
	public String destination_ip;
    byte[] payload;
    int pkt_no;
    public String msg_name;
    public Packet(int payload_size){
        payload = new byte[payload_size];
    }
}
