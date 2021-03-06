package com.msc.node.distributednode;

import com.msc.node.distributednode.search.SearchResponse;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class QueryHandling implements AbstractResponseHandler {

	private static final Logger LOG = Logger.getLogger(QueryHandling.class.getName());

    private RoutingTable routingTable;

    private BlockingQueue<MessageCreator> channelOut;

    private TimeoutHandler timeoutHandler;

    private static QueryHandling queryHandler;

    private Map<String, SearchResponse> searchResutls;

    private long searchInitiatedTime;

    private QueryHandling(){

    }

    public static synchronized QueryHandling getInstance(){
        if (queryHandler == null){
            queryHandler = new QueryHandling();
        }

        return queryHandler;
    }

    @Override
    public synchronized void handleResponse(MessageCreator message) {
        LOG.fine("Received SEROK : " + message.getMessage()
                + " from: " + message.getAddress()
                + " port: " + message.getPort());

        StringTokenizer stringToken = new StringTokenizer(message.getMessage(), " ");

        String length = stringToken.nextToken();
        String keyword = stringToken.nextToken();
        int filesCount = Integer.parseInt(stringToken.nextToken());
        String address = stringToken.nextToken().trim();
        int port = Integer.parseInt(stringToken.nextToken().trim());

        String addressKey = String.format(Constants.ADDRESS_KEY_FORMAT, address, port);

        int hops = Integer.parseInt(stringToken.nextToken());

        while(filesCount > 0){

            String fileName = StringEncoderDecoder.decode(stringToken.nextToken());

            if (this.searchResutls != null){
                if(!this.searchResutls.containsKey(addressKey + fileName)){
                    this.searchResutls.put(addressKey + fileName,
                            new SearchResponse(fileName, address, port, hops,
                                    (System.currentTimeMillis() - searchInitiatedTime)));

                }
            }

            filesCount--;
        }
    }

    @Override
    public void init(RoutingTable routingTable, BlockingQueue<MessageCreator> channelOut, TimeoutHandler timeoutHandler) {
        this.routingTable = routingTable;
        this.channelOut = channelOut;
        this.timeoutHandler = timeoutHandler;
    }

    public void setSearchResutls(Map<String, SearchResponse> searchResutls) {
        this.searchResutls = searchResutls;
    }

    public void setSearchInitiatedTime(long currentTimeinMillis){
        this.searchInitiatedTime = currentTimeinMillis;
    }

}
