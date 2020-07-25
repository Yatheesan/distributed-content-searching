# distributed-content-searching
Distributed Systems Project to Search Contents in a Distributed Application Layer Network

###You need the following prerequisite

* A text editor or IDE
* JDK 1.8 or later
* Gradle 4+ or Maven 3.2+

Build command - (Run the command inside the distributed-content-searching folder)

mvn clean install

or 

mvn -Dmaven.test.skip=true clean install

the build jar is available in side the following path
distributed-content-searching\distributed-node\target

command to run the node. 
java -jar distributed-node-0.0.1.jar

once you run the above command the node will join the network along with bootstrap server. 
Before running the command make sure bootstrap server is running in the network. 

Goals
Develop a simple overlay-based solution that allows a set of nodes to share contents (e.g., music files) among each other. 
Consider a set of nodes connected via some overlay topology. Each of the nodes has a set of files that it is willing to share 
with other nodes. Suppose node x is interested in a file f. x issues a  search query to the overlay to locate a at least one 
node y containing that particular file. Once the node is identified, the file f can be exchanged between X and y.
