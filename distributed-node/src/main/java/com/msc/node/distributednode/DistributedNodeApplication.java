package com.msc.node.distributednode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;
import java.util.UUID;

@SpringBootApplication
public class DistributedNodeApplication {

	public static void main(String[] args) {
		SpringApplication.run(DistributedNodeApplication.class, args);
		try {
			String uniqueID = UUID.randomUUID().toString();
			Node node = new Node("node" + uniqueID);
			node.register();
			Scanner scanner = new Scanner(System.in);
			while (true){
				// take an input for search query and pass
				// it to search manager and proceed the search
				System.out.println("\nChoose one of the following options : ");
				System.out.println("1) Do a search");
				System.out.println("2) Print the routing table");
				System.out.println("3) Exit the network");

				System.out.println("\nPlease enter the option : ");

				String commandOption = scanner.nextLine();

				if (commandOption.equals("1")){
					// search
				} else if (commandOption.equals("2")){
					node.printRoutingTable();
				} else if (commandOption.equals("3")){
					node.unRegister();
					System.exit(0);
				} else {
					System.out.println("Please enter a valid option...");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
