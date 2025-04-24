package com.scanner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class Dns implements scan {
    private static final Logger logger = Logger.getLogger(Dns.class.getName());
    private String customDnsServer = null; // Optional custom DNS server

    public void setCustomDnsServer(String dnsServer) {
        this.customDnsServer = dnsServer;
    }

    @Override
    public Object[] scan(String domain) {
        List<String> results = new ArrayList<>();
        boolean isResolvable = false;

        try {
            // Determine the operating system and set the appropriate nslookup command
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder processBuilder;

            if (os.contains("win")) {
                if (customDnsServer != null) {
                    processBuilder = new ProcessBuilder("nslookup", domain, customDnsServer);
                } else {
                    processBuilder = new ProcessBuilder("nslookup", domain);
                }
            } else {
                if (customDnsServer != null) {
                    processBuilder = new ProcessBuilder("nslookup", domain, customDnsServer);
                } else {
                    processBuilder = new ProcessBuilder("nslookup", domain);
                }
            }

            logger.log(Level.INFO, "Running nslookup for domain: {0} using DNS server: {1}", new Object[]{domain, customDnsServer});
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                logger.log(Level.INFO, line);
                results.add(line);

                // Check if the domain is resolvable
                if (line.contains("Name:") || line.contains("Address:")) {
                    isResolvable = true;
                }
            }

            process.waitFor();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error running nslookup for domain: " + domain, e);
        }

        return new Object[]{isResolvable, results};
    }
}
