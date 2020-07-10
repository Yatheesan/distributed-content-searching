package com.msc.node.distributednode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DistributedNodeApplication {

	public static void main(String[] args) {
		SpringApplication.run(DistributedNodeApplication.class, args);
		try {
			Node node = new Node("node");
			while (true){

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
