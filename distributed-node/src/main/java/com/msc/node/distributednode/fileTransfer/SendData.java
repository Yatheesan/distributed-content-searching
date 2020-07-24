package com.msc.node.distributednode.fileTransfer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Logger;

import com.msc.node.distributednode.FileManager;

public class SendData implements Runnable {

    private Socket clientSocket;
    private BufferedReader in = null;

    private final Logger LOG = Logger.getLogger(SendData.class.getName());

    private String userName;

    public SendData(Socket client, String userName) {
        this.clientSocket = client;
        this.userName = userName;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(
                    clientSocket.getInputStream()));
            DataInputStream dIn = new DataInputStream(clientSocket.getInputStream());
            String fileName = dIn.readUTF();

            if (fileName != null) {
                sendFile(FileManager.getInstance("").getFile(fileName));
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendFile(File file) {
        try {
            //handle file read
            File myFile = file;
            byte[] mybytearray = new byte[(int) myFile.length()];

            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            DataInputStream dis = new DataInputStream(bis);
            dis.readFully(mybytearray, 0, mybytearray.length);

            //handle file send over socket
            OutputStream os = clientSocket.getOutputStream();

            //Sending file name and file size to the server
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeUTF(myFile.getName());
            dos.writeLong(mybytearray.length);
            dos.write(mybytearray, 0, mybytearray.length);
            dos.flush();
            fis.close();
            LOG.fine("File " + file.getName() + " sent to client.");
        } catch (Exception e) {
            LOG.severe("File does not exist!");
            e.printStackTrace();
        }
    }

}
