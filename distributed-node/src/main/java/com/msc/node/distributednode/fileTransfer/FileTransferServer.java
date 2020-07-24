package com.msc.node.distributednode.fileTransfer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class FileTransferServer  implements Runnable{

	private ServerSocket serverSocket;
    private Socket clientsocket;
    private final Logger LOG = Logger.getLogger(FileTransferServer.class.getName());
    private String userName;

    public FileTransferServer(int port, String userName) throws Exception {
        // create socket
        serverSocket = new ServerSocket(port);
        this.userName = userName;
    }

    public int getPort(){
        return serverSocket.getLocalPort();
    }

    @Override
    public void run() {
        while (true) {

            try {
                clientsocket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Thread t = new Thread(new SendData(clientsocket, userName));
            t.start();
        }
    }
}
