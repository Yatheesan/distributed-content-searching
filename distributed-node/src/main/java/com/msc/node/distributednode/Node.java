package com.msc.node.distributednode;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.List;
import java.util.logging.Logger;

import com.msc.node.distributednode.fileTransfer.FileTransferClient;
import com.msc.node.distributednode.fileTransfer.FileTransferServer;
import com.msc.node.distributednode.search.MessagingService;
import com.msc.node.distributednode.search.SearchController;
import com.msc.node.distributednode.search.SearchResponse;

public class Node {

    private BSClient bsClient;
    private String userName;
    private String ipAddress;
    private int port;
    private MessagingService messageBroker;
    private SearchController searchController;
    private DatagramSocket socket;
    private FileTransferServer fileTransferServer;

    private final Logger LOG = Logger.getLogger(Node.class.getName());

    public Node(String userName) throws Exception{
    	
       socket = new DatagramSocket();
       socket.connect(InetAddress.getByName("8.8.8.8"),10002);
       this.ipAddress = socket.getLocalAddress().getHostAddress();

        this.userName = userName;
        this.port = getFreePort();
        System.out.println("Assigned Port:"+ port);
        FileManagerHandler fileManager = FileManagerHandler.getInstance(userName);
        this.fileTransferServer = new FileTransferServer(this.port + 100, userName);
        Thread t = new Thread(fileTransferServer);
        t.start();

        this.bsClient = new BSClient();
        this.messageBroker = new MessagingService(ipAddress, port);

        this.searchController = new SearchController(this.messageBroker);

        messageBroker.start();

        LOG.fine("Node initiated on IP :" + ipAddress + " and Port :" + port);
        
        LOG.info(this.ipAddress);
    }

    public List<InetSocketAddress> register(){
        List<InetSocketAddress> targets = null;
        try{
            targets = this.bsClient.register(this.userName, this.ipAddress, this.port);
            LOG.info("Already Registered Node Details in the network:");
            LOG.info(String.valueOf(targets));
            informChildNodes(targets);
        } catch (IOException e) {
            LOG.severe("Registering node process failed");
            e.printStackTrace();
        }
        return targets;
    }

    public void unRegister() {
        try{
            this.bsClient.unRegister(this.userName, this.ipAddress, this.port);
            this.messageBroker.sendLeave();
        } catch (IOException e) {
            LOG.severe("Un-Registering node from network failed");
            e.printStackTrace();
        }
    }

    public void informChildNodes(List<InetSocketAddress> targets ){
        // method to inform other nodes based on BS given ip address of other nodes
        if(targets != null) {
            for (InetSocketAddress target: targets) {
                messageBroker.sendPingMsg(target.getAddress().toString().substring(1), target.getPort());
            }
        } else {
            LOG.info("Neighbour nodes not found to get connect..");
        }
    }

    public void getFile(int fileOption) {
        try {
        	SearchResponse fileDetail = this.searchController.getFileDetails(fileOption);
            System.out.println("The file you requested is " + fileDetail.getName());
            FileTransferClient fileTransferClient = new FileTransferClient(fileDetail.getAddress(), fileDetail.getTcpPort(),
                    fileDetail.getName());

            System.out.println("Waiting for file download...");
            Thread.sleep(Constants.FILE_DOWNLOAD_TIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
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

    public void printRoutingTable(){
        this.messageBroker.getRoutingTable().print();
    }

    public String getRoutingTable() {
        return this.messageBroker.getRoutingTable().toString();
    }
}
