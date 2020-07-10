package com.msc.node.distributednode;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
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
        this.port = getFreePort();
        this.bsClient = new BSClient();
    }

    public List<InetSocketAddress> register(){
        List<InetSocketAddress> targets = null;
        try{
            targets = this.bsClient.register(this.userName, this.ipAddress, this.port);
            LOG.info("Registered Node");
            System.out.println(targets);
        } catch (IOException e) {
            LOG.severe("Registering node failed");
            e.printStackTrace();
        }
        return targets;
    }

    public void unRegister() {
        try{
            this.bsClient.unRegister(this.userName, this.ipAddress, this.port);
//            this.messageBroker.sendLeave();
        } catch (IOException e) {
            LOG.severe("Un-Registering node from network failed");
            e.printStackTrace();
        }
    }

    public void informChildNodes(List<InetSocketAddress> targets ){
        // method to inform otehr nodes based on BS given ip address of other nodes
    }

    public void printRoutingTable(){
        // print the routing table
        LOG.info("Route Table");
    }

    private int getFreePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            socket.setReuseAddress(true);
            int port = socket.getLocalPort();
            try {
                socket.close();
            } catch (IOException e) {
                // Ignore IOException on close()
            }
            return port;
        } catch (IOException e) {
            LOG.severe("Getting free port failed");
            throw new RuntimeException("Getting free port failed");
        }
    }
}
