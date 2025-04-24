package com.scanner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class Traceroute {
    private static final Logger logger = Logger.getLogger(Traceroute.class.getName());

    public String[] runTraceroute(String ip) {
        List<String> hosts = new ArrayList<>();
        try {
            // Determine the operating system and set the appropriate traceroute command
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder processBuilder;
            if (os.contains("win")) {
                processBuilder = new ProcessBuilder("tracert", ip); // Windows
            } else if (os.contains("mac") || os.contains("nix") || os.contains("nux")) {
                processBuilder = new ProcessBuilder("traceroute", ip); // macOS/Linux
            } else {
                throw new UnsupportedOperationException("Unsupported operating system: " + os);
            }

            logger.log(Level.INFO, "Starting traceroute to {0} on OS: {1}", new Object[]{ip, os});
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                logger.log(Level.INFO, line);

                // Extract host information from the traceroute output
                List<String> parsedHosts = parseHostsFromLine(line);
                hosts.addAll(parsedHosts);
            }

            process.waitFor();
            logger.log(Level.INFO, "Traceroute completed to {0}. Hosts: {1}", new Object[]{ip, hosts});
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error running traceroute to " + ip, e);
        }

        return hosts.toArray(new String[0]);
    }

    private List<String> parseHostsFromLine(String line) {
        List<String> hosts = new ArrayList<>();
        String[] tokens = line.split("\\s+");
        for (String token : tokens) {
            if (token.matches("([a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})") || token.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
                // Match domain names or IP addresses
                hosts.add(token);
            }
        }
        return hosts;
    }
}
