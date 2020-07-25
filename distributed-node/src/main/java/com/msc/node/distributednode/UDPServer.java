package com.msc.node.distributednode;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.BlockingQueue;

public class UDPServer extends Thread {
    private final BlockingQueue<MessageCreator> channelIn;
    private final DatagramSocket socket;
    private volatile boolean process = true;
    public UDPServer(BlockingQueue<MessageCreator> channelIn, DatagramSocket socket) {
        this.channelIn = channelIn;
        this.socket = socket;
        System.out.println("UDP Server started");
    }

    @Override
    public void run() {
        while (process) {

            try {
                byte[] response = new byte[65536];
                DatagramPacket packet = new DatagramPacket(response, response.length);
                socket.receive(packet);
                String address = ((packet.getSocketAddress().toString()).substring(1)).split(":")[0];
                int port = Integer.parseInt(((packet.getSocketAddress().toString()).substring(1)).split(":")[1]);
                String body = new String(response, 0, response.length);
                MessageCreator message = new MessageCreator(address, port, body);
                channelIn.put(message);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        socket.close();
    }
    public void stopProcessing() {
        System.out.println("UDP Server stopped");
        this.process = false;
    }
}
