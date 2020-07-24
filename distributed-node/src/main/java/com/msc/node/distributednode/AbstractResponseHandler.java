package com.msc.node.distributednode;

public interface AbstractResponseHandler extends AbstractMessageHandler {

    void handleResponse(MessageCreater channelMessage);
}
