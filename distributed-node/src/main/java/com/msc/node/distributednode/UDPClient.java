package com.msc.node.distributednode;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;

public class UDPClient extends Thread {
    private final BlockingQueue<MessageCreater> channelOut;
    private final DatagramSocket socket;
    private volatile boolean process = true;
    public UDPClient(BlockingQueue<MessageCreater> channelOut, DatagramSocket socket) {
        this.channelOut = channelOut;
        this.socket = socket;
        System.out.println("UDPClient started");
    }

    @Override
    public void run() {
        while (process) {
            try {
                MessageCreater message = channelOut.take();
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


