package com.msc.node.distributednode.search;

public class SearchResponse {

	private String name;
    private String address;
    private int port;
    private int tcpPort;
    private int numberOfHops;
    private long time;

    public SearchResponse(String name, String address, int port, int numberOfHops, long time) {
        this.name = name;
        this.address = address;
        this.port = port;
        this.tcpPort = port + 100;
        this.numberOfHops = numberOfHops;
        this.time = time;
    }

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

	public int getTcpPort() {
		return tcpPort;
	}

	public int getNumberOfHops() {
		return numberOfHops;
	}

	public long getTime() {
		return time;
	}


}
