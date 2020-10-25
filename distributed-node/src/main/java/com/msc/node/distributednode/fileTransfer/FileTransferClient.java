package com.msc.node.distributednode.fileTransfer;

import java.net.ConnectException;
import java.net.Socket;

public class FileTransferClient {

	public FileTransferClient(String IpAddress, int port, String fileName) throws Exception {
        try {
                long start = System.currentTimeMillis();
                Socket serverSock = new Socket(IpAddress, port);

                System.out.println("Connecting...");
                Thread t = new Thread(new ReceiveData(serverSock, fileName));
                t.start();
                long stop = System.currentTimeMillis();
        } catch (ConnectException e) {
                System.out.println("Connection refused, please try with different node...");
        }

    }

    

}
