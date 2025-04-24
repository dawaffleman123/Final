package com.scanner;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.logging.Level;

public class Udp implements scan {
    private static final Logger logger = Logger.getLogger(Udp.class.getName());
    private int timeout = 1000; // Default timeout in milliseconds

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public Object[] scan(String ip) {
        return scan(ip, 1, 65535); // Default to scanning all ports
    }

    public Object[] scan(String ip, int startPort, int endPort) {
        List<Integer> openPorts = Collections.synchronizedList(new ArrayList<>());
        logger.log(Level.INFO, "Starting UDP scan on {0} from port {1} to port {2}", new Object[]{ip, startPort, endPort});

        boolean isHostUp = false;
        int totalPorts = endPort - startPort + 1;
        int threadCount = Math.min(30000, totalPorts); // Scale threads up to a maximum of 30,000
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int port = startPort; port <= endPort; port++) {
            final int currentPort = port;
            executor.submit(() -> {
                try (DatagramSocket socket = new DatagramSocket()) {
                    socket.setSoTimeout(timeout);
                    InetAddress address = InetAddress.getByName(ip);
                    byte[] buffer = new byte[1];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, currentPort);

                    socket.send(packet);

                    // Try to receive a response
                    DatagramPacket response = new DatagramPacket(new byte[1024], 1024);
                    socket.receive(response);

                    openPorts.add(currentPort);
                    logger.log(Level.INFO, "Port {0} is open.", currentPort);
                } catch (Exception e) {
                    // Port is closed or no response received, no action needed
                }

                // Log progress every 1000 ports
                int progress = (int) (((double) (currentPort - startPort + 1) / totalPorts) * 100);
                if (currentPort % 1000 == 0 || progress == 100) {
                    logger.log(Level.INFO, "Scanning progress: {0}% (Port {1})", new Object[]{progress, currentPort});
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.HOURS); // Wait for all tasks to complete
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "UDP port scanning interrupted", e);
        }

        isHostUp = !openPorts.isEmpty();
        logger.log(Level.INFO, "UDP scan completed on {0}. Open ports: {1}", new Object[]{ip, openPorts});
        return new Object[]{isHostUp, openPorts};
    }
}