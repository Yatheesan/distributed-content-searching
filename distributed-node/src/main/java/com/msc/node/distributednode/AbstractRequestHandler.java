package com.msc.node.distributednode;

public interface AbstractRequestHandler extends AbstractMessageHandler {

    void sendRequest(MessageCreator channelMessage);
}
