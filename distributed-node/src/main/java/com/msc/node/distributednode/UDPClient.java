package com.msc.node.distributednode;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;

public class UDPClient extends Thread {
    private final BlockingQueue<MessageCreator> channelOut;
    private final DatagramSocket socket;
    private volatile boolean process = true;
    public UDPClient(BlockingQueue<MessageCreator> channelOut, DatagramSocket socket) {
        this.channelOut = channelOut;
        this.socket = socket;
        System.out.println("UDP Client started");
    }

    @Override
    public void run() {
        while (process) {
            try {
                MessageCreator message = channelOut.take();
                String address = message.getAddress();
                int port = message.getPort();
                String payload = message.getMessage();
                DatagramPacket packet = new DatagramPacket(
                        payload.getBytes(),
                        payload.length(),
                        InetAddress.getByName(address),
                        port
                );
                socket.send(packet);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        socket.close();
    }
    public void stopProcessing() {
        this.process = false;
    }
}


