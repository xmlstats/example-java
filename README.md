Java Example
============

This is a simple example showing how to access the
[xmlstats](https://erikberg.com/api) API using Java.  Example program listings
show two ways to create an HTTP request. One uses [Apache
HttpComponents](https://hc.apache.org/) and the other uses
[java.net](https://docs.oracle.com/javase/8/docs/api/java/net/package-summary.html)
packages. Both examples consume the HTTP response and deserialize the JSON data
into a POJO using [Jackson](https://github.com/FasterXML/jackson).

Requirements
------------
To run this example, you will need JDK 8, [Maven](https://maven.apache.org/)
3.x, and an xmlstats account.

Getting Started
---------------
Clone the repository.

### Edit Settings
Specify your xmlstats API access token and user agent in `example.properties`.
The user agent should follow guidelines listed at
[https://erikberg.com/api#ua](https://erikberg.com/api#ua).

### Compile
`mvn compile`

### Run
Using Apache HttpComponents (recommended)
`mvn exec:java -Dexec.mainClass=ExampleApacheHttp`

Using java.net
`mvn exec:java -Dexec.mainClass=ExampleJavaNet`

