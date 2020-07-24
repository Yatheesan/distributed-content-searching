package com.msc.node.distributednode;



import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

public class LeavingController implements AbstractRequestHandler {

    private RoutingTable routingTable;
    private BlockingQueue<MessageCreater> channelOut;
    private static LeavingController leaveHandler;

    public synchronized static LeavingController getInstance() {
        if (leaveHandler == null){
            leaveHandler = new LeavingController();
        }
        return leaveHandler;
    }

    public void leaveTheNetwork () {
        String payload = String.format(Constants.LEAVE_FORMAT,
                this.routingTable.getAddress(),
                this.routingTable.getPort());
        String rawMessage = String.format(Constants.MSG_FORMAT, payload.length() + 5,payload);
        ArrayList<Neighbour> neighbours = routingTable.getNeighbours();
        for (Neighbour n: neighbours) {
            MessageCreater message = new MessageCreater(n.getAddress(), n.getPort(),rawMessage);
            sendRequest(message);
        }

    }

    @Override
    public void init(RoutingTable routingTable,
                     BlockingQueue<MessageCreater> channelOut,
                     TimeoutHandler timeoutManager) {
        this.routingTable = routingTable;
        this.channelOut = channelOut;
    }

    @Override
    public void sendRequest(MessageCreater message) {
        try {
            channelOut.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
