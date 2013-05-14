My Adobe Assignment
==========

Assignment A
-------------------

Version 0.2
-------------------

The new version adds **HTTP/1.1 connection persistence** between the server and the clients. In order to continue using the base of the application `NanoHttpd` and also to be able to handle **keep-alive requests**, I had to modify the sources for `NanoHttpd`. The proper way to do this would have been branching the *github NanoHttpd repository*, but due to some difficulties that I encountered doing this and due to the close deadline, I decided to **copy** the modified sources inside my own project. Next I will go through the modifications that I did to the `NanoHttpd` class (and inner classes):
- I added a default timeout of 10 seconds on the `Socket`.

```java
	final Socket finalAccept = myServerSocket.accept();
    // set default timeout of 10 seconds
    finalAccept.setSoTimeout(10 * 1000);
```

- the socket is closed automatically on timeout or if the *Connection* header is missing with the value *keep-alive* or if the *Connection* header is present with the value *close*.

```java
	System.out.println("Opened connection with client ...");
	try {
		if (!session.isConnectionKeepAlive()
				|| session.isConnectionClose()) {
			finalAccept.close();
			System.out.println("\t... close connection.");
		}
	} catch (IOException ignored) {
	}
```

- in case of a persistent connection, I added a timeout of 10 seconds to the `Response`, using the header *Keep-Alive: timeout=10* (this is reduntant if the socket has a 10 seconds timeout, but was added to cover some tests that I did without the socket timeout). The *Keep-Alive: timeout=10* header overrides the default setting of the clients (browsers) which are generally of about 1 minute.

```java
	if (isKeepAlive()) {
		pw.print("Connection: keep-alive\r\n");
		pw.print("Keep-Alive: timeout=10\r\n");
	} else {
		pw.print("Connection: close\r\n");
	}
```

The functional tests were done using the **netstat** command line. Example:

	...>netstat -a -p TCP
		
	Active Connections

	  Proto  Local Address          Foreign Address        State
	  ...
	  TCP    127.0.0.1:8080         IRINA:0                LISTENING
	  TCP    127.0.0.1:8080         IRINA:61603            ESTABLISHED
	  ...

The connection stays open until timeout.

Version 0.1
-------------------

The application is based on **NanoHttpd**, a light-weight HTTP server. It extends the sample `SimpleWebServer` which in turn extends the abstract class `NanoHTTPD`.

3 modifications were made to the original implementation in order to comply to the assignment:
- in the original implementation, the web server provided a default `AsyncRunner` for the threads serving the requests and this default was creating a new `Thread` for each request. In this new implementation of the `AsyncRunner` interface, `ThreadPoolAsyncRunner`, I use a thread pool with a configurable number of threads, by the command line argument `-t` (defaults to 10).
- after overriding the `serve()` method (which was mandatory), the `Response` was modified to be able to display, for testing purposes, the `Thread` that serves each request.
- overriding the `stop()` method of the `NanoHttpd` class allowed to safely shutdown the `ThreadPoolExecutor` and end the idle threads.

This solution was chosen due to its simplicity, the fact that it is an open-source project, but also it contained most of the elements needed in this assignment and could be easily extended in order to provide the **thread pooling**. 

Assignment B
-------------------

As a documentation for this functionality I've used *http://jqueryui.com/draggable/* and *http://api.jqueryui.com/draggable/*. Small "patches" were added to the code to solve some minor issues:
- the `iFrame` coordinates weren't updated with the top and left offset of its position (I've used the correction suggested in *http://stackoverflow.com/questions/6817758/drag-and-drop-elements-into-an-iframe-droppable-area-has-wrong-coordinates-and*).
- for other DOM elements than `div` I had to prevent the default handle for the **click** event.

The second assignment does not require the web server to be running as the html files can be opened as static files in the browser, but in order to package the two assignments together, it is delivered in the *webapp* folder of the *web server*.

Execution
-------------------

Use this command line to build the application

	mvn clean compile assembly:single

I've also included a *repository* folder together with the sources, which contains the dependency jars of the **NanoHttpd** project.

Use this command line to execute the application

	java -jar target/adobe-assignment-jar-with-dependencies.jar

The allowed command line parameters are

	-h	host, defaults to localhost
	-p	port, defaults to 8080
	-d	root directory, defaults to src/webapp
	-t	max threads number, defaults to 10
	
The test page *many.htm* is an exemple of concurrent requests, using `iframe`s.

The test page *assignmentAdobeB.html* exemplifies the drag & drop feature for the DOM elements.