package com.msc.node.distributednode.fileTransfer;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class ReceiveData implements Runnable {

    private Socket serverSock;
    private BufferedReader in = null;
    private String fileName;

   // private TextArea textArea;

    public ReceiveData(Socket server, String fileName) {
        this.serverSock = server;
        this.fileName = fileName;
    }



    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(
                    serverSock.getInputStream()));
            DataOutputStream dOut = new DataOutputStream(serverSock.getOutputStream());
            dOut.writeUTF(fileName);
            dOut.flush();
            receiveFile();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveFile() {
        try {
            int bytesRead;

            DataInputStream serverData = new DataInputStream(serverSock.getInputStream());

            String fileName = serverData.readUTF();
            OutputStream output = new FileOutputStream(fileName);
            long size = serverData.readLong();
            byte[] buffer = new byte[1024];
            System.err.println("File Size : "+ size);
            System.err.println("Download started...");
            while (size > 0 && (bytesRead = serverData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                output.write(buffer, 0, bytesRead);
                size -= bytesRead;
                System.err.println("Pending File Size to download : " + size);
            }
            output.close();
            serverData.close();
            System.err.println("File download completed...");
        } catch (IOException ex) {
            System.err.println("server error. Connection closed.");
            ex.printStackTrace();
        }
    }
}
