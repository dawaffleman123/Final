package scanner.src;

public class Tcp implements scan{
    @Override
    public boolean scan(String ip) {
        System.out.println("Sending SYN to " + ip);
        return true;
    }
}