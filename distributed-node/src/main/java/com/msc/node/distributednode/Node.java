package com.msc.node.distributednode;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
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
        socket.connect(InetAddress.getByName("8.8.8.8"),10002);
        this.ipAddress = socket.getLocalAddress().getHostAddress();
        LOG.info(this.ipAddress);
        this.userName = userName;
    }

    private List<InetSocketAddress> register(){
        List<InetSocketAddress> targets = null;
        return targets;
    }

    public void unRegister() {
        // method to unregister from network
    }

    public void joinOtherNodes(){
        // method to inform otehr nodes based on BS given ip address of other nodes
    }

    public void printRoutingTable(){
        // print the routing table
    }
}
