package com.scanner;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

public class main {

    public static void main(String[] args) {
        // Set the logging level universally
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.SEVERE);

        Icmp icmp = new Icmp();
        Tcp tcp = new Tcp();
        Udp udp = new Udp();
        Traceroute traceroute = new Traceroute();
        String[] tracerouteResult = traceroute.runTraceroute("1.1.1.1");
        for (String host : tracerouteResult) {
            System.out.println(host);
        }
        Dns dns = new Dns();

        EventQueue.invokeLater(() -> {
            JFrame frame = new GUIFrame();
            frame.setTitle("Port Testing");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });


        System.out.println(icmp.scan("1.1.1.1")[1]);
        System.out.println(dns.scan("google.com", "10.72.69.206")[0]);
        System.out.println(tcp.scan("10.72.69.221")[1]);
        //udp.scan("192.168.1.1");
    }
};