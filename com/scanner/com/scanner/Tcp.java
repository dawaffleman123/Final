package com.scanner;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.logging.Level;

public class Tcp implements scan {
    private static final Logger logger = Logger.getLogger(Tcp.class.getName());
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
        logger.log(Level.INFO, "Starting TCP scan on {0} from port {1} to port {2}", new Object[]{ip, startPort, endPort});

        int totalPorts = endPort - startPort + 1;
        int threadCount = Math.min(65535, totalPorts); // Scale threads up to a maximum of 30,000
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int port = startPort; port <= endPort; port++) {
            final int currentPort = port;
            executor.submit(() -> {
                try (Socket socket = new Socket()) {
                    socket.connect(new java.net.InetSocketAddress(ip, currentPort), timeout);
                    openPorts.add(currentPort);
                    logger.log(Level.INFO, "Port {0} is open.", currentPort);
                } catch (Exception e) {
                    // Port is closed or unreachable, no action needed
                }

                // Calculate and log progress percentage
                int progress = (int) (((double) (currentPort - startPort + 1) / totalPorts) * 100);
                if (currentPort % 1000 == 0 || progress == 100) { // Log every 1000 ports or at 100%
                    logger.log(Level.INFO, "Scanning progress: {0}% (Port {1})", new Object[]{progress, currentPort});
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.HOURS); // Wait for all tasks to complete
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Port scanning interrupted", e);
        }

        boolean isHostUp = !openPorts.isEmpty();
        logger.log(Level.INFO, "TCP scan completed on {0}. Open ports: {1}", new Object[]{ip, openPorts});
        return new Object[]{isHostUp, openPorts};
    }
}