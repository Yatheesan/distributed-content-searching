package com.msc.node.distributednode;



import java.util.logging.Logger;

import com.msc.node.distributednode.search.MessagingService;

public class ResponseHandlerFactory {

    private static final Logger LOG = Logger.getLogger(ResponseHandlerFactory.class.getName());

    public static AbstractResponseHandler getResponseHandler(String keyword,
                                                             MessagingService messageBroker){
        switch (keyword){
            case "JOIN":
                System.out.println("JOIN Response Handler Started");
                AbstractResponseHandler pingHandler = PingHandling.getInstance();
                pingHandler.init(
                        messageBroker.getRoutingTable(),
                        messageBroker.getChannelOut(),
                        messageBroker.getTimeoutManager()
                );
                return pingHandler;

            case "BJOIN":
                AbstractResponseHandler bPingHandler = PingHandling.getInstance();
                bPingHandler.init(
                        messageBroker.getRoutingTable(),
                        messageBroker.getChannelOut(),
                        messageBroker.getTimeoutManager()
                );
                return bPingHandler;

            case "JOINOK":
                System.out.println("JOINOK Response Handler Started");
                AbstractResponseHandler pongHandler = PongHandling.getInstance();
                pongHandler.init(
                        messageBroker.getRoutingTable(),
                        messageBroker.getChannelOut(),
                        messageBroker.getTimeoutManager()
                );
                return pongHandler;

            case "BJOINOK":
                AbstractResponseHandler bpongHandler = PongHandling.getInstance();
                bpongHandler.init(
                        messageBroker.getRoutingTable(),
                        messageBroker.getChannelOut(),
                        messageBroker.getTimeoutManager()
                );
                return bpongHandler;

            case "SER":
                System.out.println("SER Response Handler Started");
                AbstractResponseHandler searchQueryHandler = SearchQueryHandling.getInstance();
                searchQueryHandler.init(messageBroker.getRoutingTable(),
                        messageBroker.getChannelOut(),
                        messageBroker.getTimeoutManager());
                return searchQueryHandler;

            case "SEROK":
                System.out.println("SEROK Response Handler Started");
                AbstractResponseHandler queryHitHandler = QueryHandling.getInstance();
                queryHitHandler.init(messageBroker.getRoutingTable(),
                        messageBroker.getChannelOut(),
                        messageBroker.getTimeoutManager());
                return queryHitHandler;

            case "LEAVE":
                System.out.println("LEAVE Response Handler Started");
                AbstractResponseHandler leaveHandler = PingHandling.getInstance();
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
