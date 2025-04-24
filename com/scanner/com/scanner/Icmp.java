package com.scanner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Logger;
import java.util.logging.Level;

public class Icmp implements scan {
    private static final Logger logger = Logger.getLogger(Icmp.class.getName());

    @Override
    public Object[] scan(String ip) {
        try {
            // Determine the operating system and set the appropriate ping command
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder processBuilder;
            if (os.contains("win")) {
                processBuilder = new ProcessBuilder("ping", "-n", "4", ip); // Windows
            } else {
                processBuilder = new ProcessBuilder("ping", "-c", "4", ip); // Linux/Mac
            }

            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            boolean hostReachable = false;
            long totalPingTime = 0;
            int pingCount = 0;

            while ((line = reader.readLine()) != null) {
                logger.log(Level.INFO, line);

                // Parse ping time from output (platform-specific parsing may be needed)
                if (line.contains("time=")) {
                    int timeIndex = line.indexOf("time=") + 5;
                    int msIndex = line.indexOf(" ms", timeIndex);
                    String timeStr = line.substring(timeIndex, msIndex);
                    totalPingTime += Double.parseDouble(timeStr);
                    pingCount++;
                    hostReachable = true;
                }
            }

            process.waitFor();

            if (hostReachable && pingCount > 0) {
                long avgPingTime = totalPingTime / pingCount;
                logger.log(Level.INFO, "Host {0} is up. Average ping time: {1} ms.", new Object[]{ip, avgPingTime});
                return new Object[]{true, avgPingTime};
            } else {
                logger.log(Level.WARNING, "Host {0} is not reachable.", ip);
                return new Object[]{false, 0L};
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error scanning host " + ip, e);
            return new Object[]{false, 0L};
        }
    }
}