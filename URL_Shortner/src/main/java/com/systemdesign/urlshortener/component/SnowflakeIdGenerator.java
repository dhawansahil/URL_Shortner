package com.systemdesign.urlshortener.component;

import org.springframework.stereotype.Component;

import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Enumeration;

/**
 * Distributed Sequence Generator (Twitter Snowflake)
 * Structure:
 * 1 bit - Sign
 * 41 bits - Timestamp (milliseconds since custom epoch)
 * 10 bits - Node ID (5 bits datacenter + 5 bits worker) - Adapted to 10 bits
 * machine id for simplicity here
 * 12 bits - Sequence number
 */
@Component
public class SnowflakeIdGenerator {

    private static final long UNUSED_BITS = 1; // Sign bit, Unused (always set to 0 for positive values)
    private static final long EPOCH_BITS = 41;
    private static final long NODE_ID_BITS = 10;
    private static final long SEQUENCE_BITS = 12;

    private static final long MAX_NODE_ID = (1L << NODE_ID_BITS) - 1;
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;

    // Custom Epoch (January 1, 2024 Midnight UTC = 1704067200000L)
    private static final long CUSTOM_EPOCH = 1704067200000L;

    private final long nodeId;
    private volatile long lastTimestamp = -1L;
    private volatile long sequence = 0L;

    public SnowflakeIdGenerator() {
        this.nodeId = createNodeId();
    }

    public synchronized long nextId() {
        long currentTimestamp = timestamp();

        if (currentTimestamp < lastTimestamp) {
            throw new IllegalStateException("Invalid System Clock!");
        }

        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                // Sequence Exhausted, wait till next millisecond.
                currentTimestamp = waitNextMillis(currentTimestamp);
            }
        } else {
            // Reset sequence to start with 0 for the new millisecond
            sequence = 0;
        }

        lastTimestamp = currentTimestamp;

        long id = currentTimestamp << (NODE_ID_BITS + SEQUENCE_BITS);
        id |= (nodeId << SEQUENCE_BITS);
        id |= sequence;
        return id;
    }

    private long timestamp() {
        return Instant.now().toEpochMilli() - CUSTOM_EPOCH;
    }

    private long waitNextMillis(long currentTimestamp) {
        while (currentTimestamp == lastTimestamp) {
            currentTimestamp = timestamp();
        }
        return currentTimestamp;
    }

    private long createNodeId() {
        long nodeId;
        try {
            StringBuilder sb = new StringBuilder();
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                byte[] mac = networkInterface.getHardwareAddress();
                if (mac != null) {
                    for (byte b : mac) {
                        sb.append(String.format("%02X", b));
                    }
                }
            }
            nodeId = sb.toString().hashCode() & MAX_NODE_ID;
        } catch (Exception ex) {
            nodeId = (new SecureRandom().nextInt()) & MAX_NODE_ID;
        }
        return nodeId;
    }
}
