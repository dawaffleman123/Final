package scanner.src;

public class Udp implements scan {

    @Override
    public boolean scan(String ip) {
        System.out.println("Sending UDP to " + ip);
        return true;
    }
}