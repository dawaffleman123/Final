package com.scanner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * The Dns class tests DNS resolution using the nslookup command.
 * It supports multiple platforms (Windows, macOS, Linux) and allows
 * the use of a custom DNS server.
 */
public class Dns implements scan {
    private static final Logger logger = Logger.getLogger(Dns.class.getName());

    /**
     * Performs a DNS resolution test using nslookup.
     * @param domain The domain name to resolve.
     * @return An Object array containing:
     *         - A boolean indicating if the domain is resolvable.
     *         - A String array containing the resolved addresses for the domain.
     */
    @Override
    public Object[] scan(String domain) {
        return scan(domain, null); // Default to no custom DNS server
    }

    /**
     * Performs a DNS resolution test using nslookup with an optional custom DNS server.
     * @param domain The domain name to resolve.
     * @param customDnsServer The custom DNS server to use (can be null for default).
     * @return An Object array containing:
     *         - A boolean indicating if the domain is resolvable.
     *         - A String array containing the resolved addresses for the domain.
     */
    public Object[] scan(String domain, String customDnsServer) {
        List<String> addresses = new ArrayList<>();
        boolean isResolvable = false;

        try {
            // Determine the operating system and set the appropriate nslookup command
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder processBuilder;

            // Construct the nslookup command based on the OS and custom DNS server
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

            // Read the output of the nslookup command
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                logger.log(Level.INFO, line);

                // Extract addresses from the output
                if (line.contains("Address:")) {
                    String address = line.substring(line.indexOf("Address:") + 8).trim();
                    addresses.add(address);
                    isResolvable = true; // Mark as resolvable if at least one address is found
                }
            }

            process.waitFor();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error running nslookup for domain: " + domain, e);
        }

        // Return whether the domain is resolvable and the resolved addresses as an array
        return new Object[]{isResolvable, addresses.toArray(new String[0])};
    }
}
