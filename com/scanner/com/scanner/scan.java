package com.scanner;

public interface scan {
    Object[] scan(String ip); // Returns [isTheHostUp, avgPingTime]
}
