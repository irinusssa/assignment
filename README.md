My Adobe Assignment
==========

Assignment A
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