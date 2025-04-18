package scanner.src;

public class main {

    public static void main(String[] args) {
        Icmp icmp = new Icmp();
        Tcp tcp = new Tcp();
        Udp udp = new Udp();

        icmp.scan("192.168.1.1");
        tcp.scan("192.168.1.1");
        udp.scan("192.168.1.1");
    }
}