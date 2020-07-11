package com.msc.node.distributednode;

public interface TimeoutCallback {
    void onTimeout(String messageId);
    void onResponse(String messageId);
}
