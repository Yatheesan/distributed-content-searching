package com.msc.node.distributednode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.UUID;

@SpringBootApplication
public class DistributedNodeApplication {

	public static void main(String[] args) {
		SpringApplication.run(DistributedNodeApplication.class, args);
		try {
			String uniqueID = UUID.randomUUID().toString();
			Node node = new Node("node" + uniqueID);
			node.register();
			while (true){

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
