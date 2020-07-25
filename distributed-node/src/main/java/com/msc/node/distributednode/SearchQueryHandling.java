package com.msc.node.distributednode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class SearchQueryHandling implements AbstractResponseHandler, AbstractRequestHandler {

    private final Logger LOG = Logger.getLogger(SearchQueryHandling.class.getName());

    private RoutingTable routingTable;

    private BlockingQueue<MessageCreator> channelOut;

    private TimeoutHandler timeoutHandler;

    private static SearchQueryHandling searchQueryHandler;

    private FileManagerHandler fileManager;

    private SearchQueryHandling(){
        fileManager = FileManagerHandler.getInstance("");
    }

    public synchronized static SearchQueryHandling getInstance(){
        if (searchQueryHandler == null){
            searchQueryHandler = new SearchQueryHandling();
        }
        return searchQueryHandler;
    }

    public void doSearch(String keyword) {

        String payload = String.format(Constants.QUERY_FORMAT,
                this.routingTable.getAddress(),
                this.routingTable.getPort(),
                StringEncoderDecoder.encode(keyword),
                Constants.HOP_COUNT);

        String rawMessage = String.format(Constants.MSG_FORMAT, payload.length() + 5, payload);

        MessageCreator initialMessage = new MessageCreator(
                this.routingTable.getAddress(),
                this.routingTable.getPort(),
                rawMessage);

        this.handleResponse(initialMessage);
    }

    @Override
    public void sendRequest(MessageCreator message) {
        try {
            channelOut.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(RoutingTable routingTable, BlockingQueue<MessageCreator> channelOut,
                     TimeoutHandler timeoutHandler) {
        this.routingTable = routingTable;
        this.channelOut = channelOut;
        this.timeoutHandler = timeoutHandler;
    }

    @Override
    public void handleResponse(MessageCreator message) {
        LOG.fine("Received SER : " + message.getMessage()
                + " from: " + message.getAddress()
                + " port: " + message.getPort());

        StringTokenizer stringToken = new StringTokenizer(message.getMessage(), " ");

        String length = stringToken.nextToken();
        String keyword = stringToken.nextToken();
        String address = stringToken.nextToken().trim();
        int port = Integer.parseInt(stringToken.nextToken().trim());

        String fileName = StringEncoderDecoder.decode(stringToken.nextToken().trim());
        int hops = Integer.parseInt(stringToken.nextToken().trim());

        //search for the file in the current node
        Set<String> resultSet = fileManager.searchForFile(fileName);
        int fileNamesCount = resultSet.size();

        if (fileNamesCount != 0) {

            StringBuilder fileNamesString = new StringBuilder("");

            Iterator<String> itr = resultSet.iterator();

            while(itr.hasNext()){
                fileNamesString.append(StringEncoderDecoder.encode(itr.next()) + " ");
            }

            String payload = String.format(Constants.QUERY_HIT_FORMAT,
                    fileNamesCount,
                    routingTable.getAddress(),
                    routingTable.getPort(),
                    Constants.HOP_COUNT- hops,
                    fileNamesString.toString());

            String rawMessage = String.format(Constants.MSG_FORMAT, payload.length() + 5, payload);

            MessageCreator queryHitMessage = new MessageCreator(address,
                    port,
                    rawMessage);

            this.sendRequest(queryHitMessage);
        }

        //if the hop count is greater than zero send the message to all neighbours again

        if (hops > 0){
            ArrayList<Neighbour> neighbours = this.routingTable.getNeighbours();

            for(Neighbour neighbour: neighbours){

                //skip sending search query to the same node again
                if (neighbour.getAddress().equals(message.getAddress())
                        && neighbour.getClientPort() == message.getPort()) {
                    continue;
                }

                String payload = String.format(Constants.QUERY_FORMAT,
                        address,
                        port,
                        StringEncoderDecoder.encode(fileName),
                        hops - 1);

                String rawMessage = String.format(Constants.MSG_FORMAT, payload.length() + 5, payload);

                MessageCreator queryMessage = new MessageCreator(neighbour.getAddress(),
                        neighbour.getPort(),
                        rawMessage);

                this.sendRequest(queryMessage);
            }
        }
    }
}
