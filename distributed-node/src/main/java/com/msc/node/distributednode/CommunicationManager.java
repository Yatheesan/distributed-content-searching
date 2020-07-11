package com.msc.node.distributednode;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

//to handle message between nodes
public class CommunicationManager extends Thread {

    private final Logger LOG = Logger.getLogger(CommunicationManager.class.getName());

    private volatile boolean process = true;

    private final UDPServer server;
    private final UDPClient client;

    private BlockingQueue<ChannelMessage> channelIn;
    private BlockingQueue<ChannelMessage> channelOut;

    private RoutingTable routingTable;
    private PingHandler pingHandler;
//    private LeaveHandler leaveHandler;
//    private SearchQueryHandler searchQueryHandler;
    private FileManager fileManager;
    //time out manager renamed
    private TimeoutHandler timeoutHandler = new TimeoutHandler();

    public CommunicationManager(String address, int port) throws SocketException {
        channelIn = new LinkedBlockingQueue<ChannelMessage>();
        DatagramSocket socket = new DatagramSocket(port);
        this.server = new UDPServer(channelIn, socket);

        channelOut = new LinkedBlockingQueue<ChannelMessage>();
        this.client = new UDPClient(channelOut, new DatagramSocket());

        this.routingTable = new RoutingTable(address, port);
//
        this.pingHandler = PingHandler.getInstance();
//        this.leaveHandler = LeaveHandler.getInstance();
//
//        this.fileManager = FileManager.getInstance("");
//
        this.pingHandler.init(this.routingTable, this.channelOut);
//        this.leaveHandler.init(this.routingTable, this.channelOut, this.timeoutManager);
//
//        this.searchQueryHandler = SearchQueryHandler.getInstance();
//        this.searchQueryHandler.init(routingTable, channelOut, timeoutManager);
//
        LOG.fine("starting server");
        timeoutHandler.registerRequest(Constants.R_PING_MESSAGE_ID, Constants.PING_INTERVAL, new TimeoutCallback() {
            @Override
            public void onTimeout(String messageId) {
                sendRoutinePingOnTimeOut();
            }

            @Override
            public void onResponse(String messageId) {
            }

        });
    }

    @Override
    public void run(){
        this.server.start();
        this.client.start();
        this.process();
    }

    public void process() {
        while (process) {
            try {
                ChannelMessage message = channelIn.poll(100, TimeUnit.MILLISECONDS);
                if (message != null) {
                    LOG.info("Received Message: " + message.getMessage()
                            + " from: " + message.getAddress()
                            + " port: " + message.getPort());

//                    AbstractResponseHandler abstractResponseHandler
//                            = ResponseHandlerFactory.getResponseHandler(
//                            message.getMessage().split(" ")[1],
//                            this
//                    );

//                    if (abstractResponseHandler != null){
//                        abstractResponseHandler.handleResponse(message);
//                    }
//
                }
                    timeoutHandler.checkForTimeout();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopProcessing() {
        this.process = false;
        server.stopProcessing();
    }

    public void sendPing(String address, int port) {
        this.pingHandler.sendPing(address, port);
    }

    public void doSearch(String keyword){
//        this.searchQueryHandler.doSearch(keyword);
    }

    public BlockingQueue<ChannelMessage> getChannelIn() {
        return channelIn;
    }

    public BlockingQueue<ChannelMessage> getChannelOut() {
        return channelOut;
    }

    public TimeoutHandler getTimeoutManager() {
        return timeoutHandler;
    }

    public RoutingTable getRoutingTable() {
        return routingTable;
    }


    private void sendRoutinePingOnTimeOut() {
        ArrayList<String> neighbours = routingTable.toList();
        for (String n: neighbours) {
            String address = n.split(":")[0];
            int port = Integer.valueOf(n.split(":")[1]);
            sendPing(address, port);
        }
    }

    public void sendLeave() {
//        this.leaveHandler.sendLeave();
    }

//    public String getFiles() {
//        return this.fileManager.getFileNames();
//    }

}
