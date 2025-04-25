package com.scanner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class mainGUI {
    public static void main(String[] args) {
        // Create the main frame
        JFrame frame = new JFrame("Network Scanner");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 300);
        frame.setLayout(new BorderLayout());

        // Create a panel for input
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        // Input field for IP address or domain
        JLabel inputLabel = new JLabel("Enter IP/Domain:");
        JTextField inputField = new JTextField(20);

        // Add components to the input panel
        inputPanel.add(inputLabel);
        inputPanel.add(inputField);

        // Create a panel for buttons and results
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 3, 10, 10)); // 5 rows, 3 columns

        // Buttons, result labels, and spinner labels
        JButton icmpButton = new JButton("Ping (ICMP)");
        JLabel icmpResult = new JLabel("Result: ");
        JLabel icmpSpinner = new JLabel();
        JButton tcpButton = new JButton("TCP Scan");
        JLabel tcpResult = new JLabel("Result: ");
        JLabel tcpSpinner = new JLabel();
        JButton udpButton = new JButton("UDP Scan");
        JLabel udpResult = new JLabel("Result: ");
        JLabel udpSpinner = new JLabel();
        JButton tracerouteButton = new JButton("Traceroute");
        JLabel tracerouteResult = new JLabel("Result: ");
        JLabel tracerouteSpinner = new JLabel();
        JButton dnsButton = new JButton("DNS Lookup");
        JLabel dnsResult = new JLabel("Result: ");
        JLabel dnsSpinner = new JLabel();

        // Add buttons, result labels, and spinner labels to the button panel
        buttonPanel.add(icmpButton);
        buttonPanel.add(icmpResult);
        buttonPanel.add(icmpSpinner);
        buttonPanel.add(tcpButton);
        buttonPanel.add(tcpResult);
        buttonPanel.add(tcpSpinner);
        buttonPanel.add(udpButton);
        buttonPanel.add(udpResult);
        buttonPanel.add(udpSpinner);
        buttonPanel.add(tracerouteButton);
        buttonPanel.add(tracerouteResult);
        buttonPanel.add(tracerouteSpinner);
        buttonPanel.add(dnsButton);
        buttonPanel.add(dnsResult);
        buttonPanel.add(dnsSpinner);

        // Add the input panel and button panel to the frame
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);

        // Spinner icon
        Icon spinnerIcon = new ImageIcon(new ImageIcon("spinner.gif").getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));

        // Action listener for ICMP Ping
        icmpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = inputField.getText().trim();
                if (input.isEmpty()) {
                    icmpResult.setText("Result: Invalid IP");
                    return;
                }

                icmpSpinner.setIcon(spinnerIcon); // Show spinner
                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() {
                        Icmp icmp = new Icmp();
                        Object[] result = icmp.scan(input);

                        boolean isReachable = (boolean) result[0];
                        long avgPingTime = (long) result[1];

                        if (isReachable) {
                            icmpResult.setText("Result: " + avgPingTime + " ms");
                        } else {
                            icmpResult.setText("Result: Unreachable");
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
                        icmpSpinner.setIcon(null); // Hide spinner
                    }
                };
                worker.execute();
            }
        });

        // Action listener for TCP Scan
        tcpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = inputField.getText().trim();
                if (input.isEmpty()) {
                    tcpResult.setText("Result: Invalid IP");
                    return;
                }

                tcpSpinner.setIcon(spinnerIcon); // Show spinner
                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() {
                        Tcp tcp = new Tcp();
                        Object[] result = tcp.scan(input, 1, 1024); // Scan ports 1-1024

                        boolean isHostUp = (boolean) result[0];
                        @SuppressWarnings("unchecked")
                        java.util.List<Integer> openPorts = (java.util.List<Integer>) result[1];

                        if (isHostUp) {
                            tcpResult.setText("Result: Open Ports: " + openPorts);
                        } else {
                            tcpResult.setText("Result: Host Down");
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
                        tcpSpinner.setIcon(null); // Hide spinner
                    }
                };
                worker.execute();
            }
        });

        // Action listener for UDP Scan
        udpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = inputField.getText().trim();
                if (input.isEmpty()) {
                    udpResult.setText("Result: Invalid IP");
                    return;
                }

                udpSpinner.setIcon(spinnerIcon); // Show spinner
                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() {
                        Udp udp = new Udp();
                        Object[] result = udp.scan(input, 1, 1024); // Scan ports 1-1024

                        boolean isHostUp = (boolean) result[0];
                        @SuppressWarnings("unchecked")
                        java.util.List<Integer> openPorts = (java.util.List<Integer>) result[1];

                        if (isHostUp) {
                            udpResult.setText("Result: Open Ports: " + openPorts);
                        } else {
                            udpResult.setText("Result: Host Down");
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
                        udpSpinner.setIcon(null); // Hide spinner
                    }
                };
                worker.execute();
            }
        });

        // Action listener for Traceroute
        tracerouteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = inputField.getText().trim();
                if (input.isEmpty()) {
                    tracerouteResult.setText("Result: Invalid IP/Domain");
                    return;
                }

                tracerouteSpinner.setIcon(spinnerIcon); // Show spinner
                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() {
                        Traceroute traceroute = new Traceroute();
                        String[] hops = traceroute.runTraceroute(input);

                        if (hops.length > 0) {
                            tracerouteResult.setText("Result: " + hops.length + " hops");
                        } else {
                            tracerouteResult.setText("Result: Failed");
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
                        tracerouteSpinner.setIcon(null); // Hide spinner
                    }
                };
                worker.execute();
            }
        });

        // Action listener for DNS Lookup
        dnsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = inputField.getText().trim();
                if (input.isEmpty()) {
                    dnsResult.setText("Result: Invalid Domain");
                    return;
                }

                dnsSpinner.setIcon(spinnerIcon); // Show spinner
                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() {
                        Dns dns = new Dns();
                        Object[] result = dns.scan(input);

                        boolean isResolvable = (boolean) result[0];
                        String[] addresses = (String[]) result[1];

                        if (isResolvable) {
                            dnsResult.setText("Result: " + addresses.length + " addresses");
                        } else {
                            dnsResult.setText("Result: Not Resolvable");
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
                        dnsSpinner.setIcon(null); // Hide spinner
                    }
                };
                worker.execute();
            }
        });

        // Make the frame visible
        frame.setVisible(true);
    }
}
