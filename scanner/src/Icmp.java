package scanner.src;

public class Icmp implements scan{
    @Override
    public boolean scan(String ip) {
        System.out.println("Sending ping to " + ip);
        return true;
    }
}