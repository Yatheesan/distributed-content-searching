package com.msc.node.distributednode;

import com.msc.node.distributednode.Constants;
import com.msc.node.distributednode.TimeoutCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

//this class to handel the time out and if the message got timeout
// and that is not a regular ping message then that message will be removed
public class TimeoutHandler {
    private final Logger LOG = Logger.getLogger(TimeoutHandler.class.getName());
    private Map<String, TimeoutCallbackMap> requests = new HashMap<String, TimeoutCallbackMap>();

    public void registerRequest(String messaageId, long timeout, TimeoutCallback callback) {
        requests.put(messaageId, new TimeoutCallbackMap(timeout, callback));
    }

    public void registerResponse(String messageId) {
        LOG.fine("Registering Response : " + messageId);
        requests.remove(messageId);
    }

    public void checkForTimeout() {
        ArrayList<String> messagesToRemove = new ArrayList<>();
        for (String messageId: requests.keySet()) {
            if(requests.get(messageId).checkTimeout(messageId)) {
                if(messageId.equals(Constants.R_PING_MESSAGE_ID)) {
                    requests.get(messageId).timeoutTime = requests.get(messageId).timeoutTime
                            + requests.get(messageId).timeout;
                }else {
                    messagesToRemove.add(messageId);
                }

            }
        }
        for (String messageId: messagesToRemove) {
            requests.remove(messageId);
        }
    }

    private class TimeoutCallbackMap{
        private long timeoutTime;
        private TimeoutCallback callback;
        private long timeout;

        private TimeoutCallbackMap(long timeout, TimeoutCallback callback) {
            this.timeout = timeout;
            this.callback = callback;
            this.timeoutTime = System.currentTimeMillis() + timeout;
        }
        private boolean checkTimeout(String messageID) {
            if (System.currentTimeMillis() >= timeoutTime) {
                callback.onTimeout(messageID);
                return true;
            }
            return false;
        }
    }
}
