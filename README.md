<h1>Chat Application - Java Sockets</h1>
<p>This project is a multi-client chat application built with Java 17, designed to explore socket programming. The application is developed in two steps:</p>
<ul>
<li>Non-Secure Communication: The initial version uses plain sockets for communication.</li>
<li>Secure Communication: The last version implements SSL for secure communication using Java's SSL API.</li>
</ul>
<div>
The application supports:
<ul>
<li>Client-Server Communication: Clients interact directly with the server.</li>
<li>Client-Client Communication: Clients can communicate with each other through the server as a bridge.</li>
<li>Group Communication: Messages sent by one client can be broadcast to a group via the server.</li>
</ul>
</div>

<h3>Project Structure</h3>
<img src="./screens/structure.png">
<h3>Compile the Application</h3>
```
javac -d target/classes src/main/java/kad/dev/*.java
```
<h3>Package into a JAR</h3>
```
jar cfm target/server.jar src/main/recources/MANIFEST.TXT -C target/classes .
```
<h3>Run the Server</h3>
```
java -jar target/server.jar
```
<h5>After Running Server</h5>
<img src="./screens/run_server.png">
<h5>Connect as Telnet Clients</h5>
<h3>Telnet command:</h3>
```
telnet <server_socket_ip> <server_socket_port>
```
<img src="./screens/telnet.png">
<img src="./screens/server_after.png">
