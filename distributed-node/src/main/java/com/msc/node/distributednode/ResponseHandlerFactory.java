package com.msc.node.distributednode;



import java.util.logging.Logger;

import com.msc.node.distributednode.search.MessageBroker;

public class ResponseHandlerFactory {

    private static final Logger LOG = Logger.getLogger(ResponseHandlerFactory.class.getName());

    public static AbstractResponseHandler getResponseHandler(String keyword,
                                                             MessageBroker messageBroker){
        switch (keyword){
            case "PING":
                AbstractResponseHandler pingHandler = PingHandler.getInstance();
                pingHandler.init(
                        messageBroker.getRoutingTable(),
                        messageBroker.getChannelOut(),
                        messageBroker.getTimeoutManager()
                );
                return pingHandler;

            case "BPING":
                AbstractResponseHandler bPingHandler = PingHandler.getInstance();
                bPingHandler.init(
                        messageBroker.getRoutingTable(),
                        messageBroker.getChannelOut(),
                        messageBroker.getTimeoutManager()
                );
                return bPingHandler;

            case "PONG":
                AbstractResponseHandler pongHandler = PongHandler.getInstance();
                pongHandler.init(
                        messageBroker.getRoutingTable(),
                        messageBroker.getChannelOut(),
                        messageBroker.getTimeoutManager()
                );
                return pongHandler;

            case "BPONG":
                AbstractResponseHandler bpongHandler = PongHandler.getInstance();
                bpongHandler.init(
                        messageBroker.getRoutingTable(),
                        messageBroker.getChannelOut(),
                        messageBroker.getTimeoutManager()
                );
                return bpongHandler;

            case "SER":
                AbstractResponseHandler searchQueryHandler = SearchQueryHandler.getInstance();
                searchQueryHandler.init(messageBroker.getRoutingTable(),
                        messageBroker.getChannelOut(),
                        messageBroker.getTimeoutManager());
                return searchQueryHandler;

            case "SEROK":
                AbstractResponseHandler queryHitHandler = QueryHandler.getInstance();
                queryHitHandler.init(messageBroker.getRoutingTable(),
                        messageBroker.getChannelOut(),
                        messageBroker.getTimeoutManager());
                return queryHitHandler;

            case "LEAVE":
                AbstractResponseHandler leaveHandler = PingHandler.getInstance();
                leaveHandler.init(
                        messageBroker.getRoutingTable(),
                        messageBroker.getChannelOut(),
                        messageBroker.getTimeoutManager()
                );
                return leaveHandler;
            default:
                LOG.severe("Unknown keyword received in Response Handler : " + keyword);
                return null;
        }
    }
}
