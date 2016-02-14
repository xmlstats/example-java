Java Example
============

This is a simple example that accesses the
[xmlstats](https://erikberg.com/api) API using Java. Example program listings
show two ways to create an HTTP request. One uses [Apache
HttpComponents](https://hc.apache.org/) and the other uses
[java.net](https://docs.oracle.com/javase/8/docs/api/java/net/package-summary.html)
packages. Both examples consume the HTTP response and deserialize the JSON data
into a POJO using [Jackson](https://github.com/FasterXML/jackson).

Requirements
------------
To run this example, you will need JDK 8, either [Maven](https://maven.apache.org/)
or [Gradle](https://gradle.org/), and an xmlstats account.

Getting Started
---------------
Clone the repository.

### Configure
Specify your API access token and e-mail address in
`src/main/resources/xmlstats.properties`.

### Compile
```
mvn compile
```
or
```
gradle build
```

### Run
##### Using Apache HttpComponents (recommended)
```
mvn exec:java -Dexec.mainClass=com.xmlstats.example.ExampleApacheHttp
```
or
```
gradle run -PmainClass=com.xmlstats.example.ExampleApacheHttp
```

##### Using java.net
```
mvn exec:java -Dexec.mainClass=com.xmlstats.example.ExampleJavaNet
```
or
```
gradle run -PmainClass=com.xmlstats.example.ExampleJavaNet
```
