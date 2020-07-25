package com.msc.node.distributednode.search;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.msc.node.distributednode.AbstractResponseHandler;
import com.msc.node.distributednode.MessageCreator;
import com.msc.node.distributednode.Constants;
import com.msc.node.distributednode.FileManagerHandler;
import com.msc.node.distributednode.LeavingController;
import com.msc.node.distributednode.PingHandling;
import com.msc.node.distributednode.ResponseHandlerFactory;
import com.msc.node.distributednode.RoutingTable;
import com.msc.node.distributednode.SearchQueryHandling;
import com.msc.node.distributednode.TimeoutCallback;
import com.msc.node.distributednode.TimeoutHandler;
import com.msc.node.distributednode.UDPClient;
import com.msc.node.distributednode.UDPServer;

public class MessagingService extends Thread {

    private final Logger LOG = Logger.getLogger(MessagingService.class.getName());

    private volatile boolean process = true;

    private final UDPServer server;
    private final UDPClient client;

    private BlockingQueue<MessageCreator> channelIn;
    private BlockingQueue<MessageCreator> channelOut;

    private RoutingTable routingTable;
    private PingHandling pingHandler;
    private LeavingController leaveHandler;
    private SearchQueryHandling searchQueryHandler;
    private FileManagerHandler fileManager;

    private TimeoutHandler timeoutHandler = new TimeoutHandler();

    public MessagingService(String address, int port) throws SocketException {
        channelIn = new LinkedBlockingQueue<MessageCreator>();
        DatagramSocket socket = new DatagramSocket(port);
        this.server = new UDPServer(channelIn, socket);

        channelOut = new LinkedBlockingQueue<MessageCreator>();
        this.client = new UDPClient(channelOut, new DatagramSocket());

        this.routingTable = new RoutingTable(address, port);

        this.pingHandler = PingHandling.getInstance();
        this.leaveHandler = LeavingController.getInstance();

        this.fileManager = FileManagerHandler.getInstance("");

        this.pingHandler.init(this.routingTable, this.channelOut, this.timeoutHandler);
        this.leaveHandler.init(this.routingTable, this.channelOut, this.timeoutHandler);

        this.searchQueryHandler = SearchQueryHandling.getInstance();
        this.searchQueryHandler.init(routingTable, channelOut, timeoutHandler);

        LOG.fine("starting server");
        timeoutHandler.registerRequest(Constants.R_PING_MESSAGE_ID, Constants.PING_INTERVAL, new TimeoutCallback() {
            @Override
            public void onTimeout(String messageId) {
                sendRoutinePing();
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
                MessageCreator message = channelIn.poll(100, TimeUnit.MILLISECONDS);
                if (message != null) {
                    LOG.info("Received Message: "
                            + " from: " + message.getAddress()
                            + " port: " + message.getPort());

                    AbstractResponseHandler abstractResponseHandler
                            = ResponseHandlerFactory.getResponseHandler(
                            message.getMessage().split(" ")[1],this
                    );

                    if (abstractResponseHandler != null){
                        abstractResponseHandler.handleResponse(message);
                    }

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

    public void sendPingMsg(String address, int port) {
        this.pingHandler.sendPing(address, port);
    }

    public void doSearch(String keyword){
        this.searchQueryHandler.doSearch(keyword);
    }

    public BlockingQueue<MessageCreator> getChannelIn() {
        return channelIn;
    }

    public BlockingQueue<MessageCreator> getChannelOut() {
        return channelOut;
    }

    public TimeoutHandler getTimeoutManager() {
        return timeoutHandler;
    }

    public RoutingTable getRoutingTable() {
        return routingTable;
    }


    private void sendRoutinePing() {
        ArrayList<String> neighbours = routingTable.toList();
        for (String n: neighbours) {
            String address = n.split(":")[0];
            int port = Integer.valueOf(n.split(":")[1]);
            sendPingMsg(address, port);

        }
    }

    public void sendLeave() {
        this.leaveHandler.leaveTheNetwork();
    }

    public String getFiles() {
        return this.fileManager.getFileNames();
    }
}

