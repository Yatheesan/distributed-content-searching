package com.msc.node.distributednode;

import java.util.concurrent.BlockingQueue;

public interface AbstractMessageHandler {

    void init (
            RoutingTable routingTable,
            BlockingQueue<MessageCreator> outBlockingMessage,
            TimeoutHandler timeoutHandler);

}
