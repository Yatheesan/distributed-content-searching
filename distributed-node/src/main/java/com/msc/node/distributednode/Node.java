package com.msc.node.distributednode;

import java.net.DatagramSocket;
import java.util.logging.Logger;

public class Node {
    private BSClient bsClient;

    private String userName;
    private String ipAddress;
    private int port;
    private DatagramSocket socket;

    private final Logger LOG = Logger.getLogger(Node.class.getName());

    public Node(String userName) throws Exception{
        socket = new DatagramSocket();
        this.ipAddress = socket.getLocalAddress().getHostAddress();
        LOG.info(this.ipAddress);
    }
}
