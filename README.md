assignment
==========

My Adobe Assignment

The application is based on *NanoHttpd*, a light-weight HTTP server. It extends the sample `SimpleWebServer` which in turn extends the abstract class `NanoHTTPD`

3 modifications were made to the original implementation in order to comply to the assignment:
- in the original implementation, the web server provided a default `AsyncRunner` for the threads serving the requests and this default was creating a new `Thread` for each request. In this new implementation of the `AsyncRunner` interface, `ThreadPoolAsyncRunner`, I use a thread pool with a configurable number of threads, by the command line argument `-t` (defaults to 10).
- after overriding the `serve()` method (which was mandatory), the `Response` was modified to be able to display, for testing purposes, the `Thread` that serves each request.
- overriding the `stop` method of the `NanoHttpd` class allowed to safely shutdown the `ThreadPoolExecutor`.

