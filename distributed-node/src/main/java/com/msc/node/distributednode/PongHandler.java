package com.msc.node.distributednode;


import java.util.StringTokenizer;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class PongHandler implements AbstractRequestHandler, AbstractResponseHandler{

    private final Logger LOG = Logger.getLogger(PongHandler.class.getName());

    private BlockingQueue<ChannelMessage> channelOut;

    private RoutingTable routingTable;

    private static PongHandler pongHandler;
    private TimeoutHandler timeoutHandler;

    private PongHandler(){

    }

    public synchronized static PongHandler getInstance(){
        if (pongHandler == null){
            pongHandler = new PongHandler();
        }

        return pongHandler;
    }

    @Override
    public void sendRequest(ChannelMessage message) {

    }

    @Override
    public void handleResponse(ChannelMessage message) {
        LOG.fine("Received PONG : " + message.getMessage()
                + " from: " + message.getAddress()
                + " port: " + message.getPort());

        StringTokenizer stringToken = new StringTokenizer(message.getMessage(), " ");
        String value = stringToken.nextToken();
        String keyword = stringToken.nextToken();
        String address = stringToken.nextToken().trim();
        int port = Integer.parseInt(stringToken.nextToken().trim());
        if(keyword.equals("BPONG")) {
            if(routingTable.getCount() < Constants.MIN_NEIGHBOURS) {
                this.routingTable.addNeighbour(address, port, message.getPort());
            }
        } else {
            this.timeoutHandler.registerResponse(String.format(Constants.PING_MESSAGE_ID_FORMAT,address,port));
            this.routingTable.addNeighbour(address, port, message.getPort());

        }

    }

    

	@Override
	public void init(RoutingTable routingTable, BlockingQueue<ChannelMessage> channelOut,
			TimeoutHandler timeoutHandler) {
		this.routingTable = routingTable;
        this.channelOut = channelOut;
        this.timeoutHandler = timeoutHandler;
		
	}
}
