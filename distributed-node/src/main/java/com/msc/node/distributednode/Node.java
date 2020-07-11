package com.msc.node.distributednode;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.List;
import java.util.logging.Logger;

import com.msc.node.distributednode.search.MessageBroker;
import com.msc.node.distributednode.search.SearchController;

public class Node {

    private BSClient bsClient;
    private String userName;
    private String ipAddress;
    private int port;
    private MessageBroker messageBroker;
    private SearchController searchController;
    private DatagramSocket socket;
//    private CommunicationManager communicationManager;

    private final Logger LOG = Logger.getLogger(Node.class.getName());

    public Node(String userName) throws Exception{
    	
       socket = new DatagramSocket();
       socket.connect(InetAddress.getByName("8.8.8.8"),10002);
       this.ipAddress = socket.getLocalAddress().getHostAddress();

        this.userName = userName;
        this.port = getFreePort();
        FileManager fileManager = FileManager.getInstance(userName);

        this.bsClient = new BSClient();
        this.messageBroker = new MessageBroker(ipAddress, port);

        this.searchController = new SearchController(this.messageBroker);

        messageBroker.start();

        LOG.fine("Node initiated on IP :" + ipAddress + " and Port :" + port);
        
        LOG.info(this.ipAddress);
    }

    public List<InetSocketAddress> register(){
        List<InetSocketAddress> targets = null;
        try{
            targets = this.bsClient.register(this.userName, this.ipAddress, this.port);
            LOG.info("Registered Node");
//            LOG.info(String.valueOf(targets));
            System.out.println(targets);
            informChildNodes(targets);
        } catch (IOException e) {
            LOG.severe("Registering node failed");
            e.printStackTrace();
        }
        return targets;
    }

    public void unRegister() {
        try{
            this.bsClient.unRegister(this.userName, this.ipAddress, this.port);
            this.messageBroker.sendLeave();
//            this.communicationManager.sendLeave();
        } catch (IOException e) {
            LOG.severe("Un-Registering node from network failed");
            e.printStackTrace();
        }
    }

    public void informChildNodes(List<InetSocketAddress> targets ){
        // method to inform other nodes based on BS given ip address of other nodes
        if(targets != null) {
            for (InetSocketAddress target: targets) {
                messageBroker.sendPing(target.getAddress().toString().substring(1), target.getPort());
            }
        }
    }


    public int doSearch(String keyword){
        return this.searchController.doSearch(keyword);
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
