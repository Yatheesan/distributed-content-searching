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
				System.out.println("A) Search for a file");
				System.out.println("B) Get the routing table");
				System.out.println("C) Leave the network");

				System.out.println("\nPlease enter the option : ");

				String commandOption = scanner.nextLine();

				if (commandOption.equals("A")){
					System.out.println("\nPlease provide a file name : ");
					String searchQuery = scanner.nextLine();

					if (searchQuery != null && !searchQuery.equals("")) {
						node.doSearch(searchQuery);

					} else {
						System.out.println("Invalid search!!!");
					}
				} else if (commandOption.equals("B")){
					node.printRoutingTable();
				} else if (commandOption.equals("C")){
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
