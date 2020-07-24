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
				System.out.println("B) Leave the network");

				System.out.println("\nPlease enter the option : ");

				String commandOption = scanner.nextLine();

				if (commandOption.equals("A")){
					System.out.println("\nPlease provide a file name : ");
					String searchQuery = scanner.nextLine();

					if (searchQuery != null && !searchQuery.equals("")) {
						int results = node.doSearch(searchQuery);
						
						if (results != 0) {

							while (true) {

								try {
									System.out.println("\nEnter the option number of the file you need to download : ");
									String fileOption = scanner.nextLine();

									int option = Integer.parseInt(fileOption);

									if (option > results) {
										System.out.println("Incorrect file!!");
										continue;
									}

									node.getFile(option);
									break;

								} catch (NumberFormatException e) {
									System.out.println("Incorrect file");
								}
							}
						}

					} else {
						System.out.println("Invalid search!!!");
					}
				} else if (commandOption.equals("B")){
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
