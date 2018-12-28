# SD - 2017/2018
Distributed Systems Project - Binas System

## Requirements
You must have installed the following tools:
- Maven 3.x.x 
- Java Development Kit 8 (JDK 8)
- UDDI Naming (You can get it [here](https://github.com/tecnico-distsys/naming))

Also check if JAVA_HOME and M2_HOME are set properly.

## Installing
Run the following command in the root directory:
```
    mvn clean install -DskipTests
```

## How to run
### binas-ws
This is a Java Web Service defined by the WSDL file that generates Java code 
(contract-first approach, also called top-down approach).

The service runs in a stand-alone HTTP server.
When running, the web service awaits connections from clients.
You can check if the service is running using your web browser to see the 
generated WSDL file:
```
    http://localhost:8080/mediator-ws/endpoint?WSDL
```
To call the service you will need a web service client, including code generated from the WSDL.

To run, execute the following command under **/binas-ws/**:
```
    mvn exec:java
```

### station-ws
This is a Java Web Service defined by the WSDL file that generates Java code
(contract-first approach, also called top-down approach).

The service runs in a stand-alone HTTP server.

To start a new station run the following command under **/station-ws/**:
```
    mvn exec:java -Dws.i=k
```
Where **_k_** is a positive integer representing the station id. (By default is 1 if 
none provided).

When running, the web service awaits connections from clients.
You can check if the service is running using your web browser 
to see the generated WSDL file:
```
    http://localhost:8081/supplier-ws/endpoint?WSDL
```

To call the service you will need a web service client,
including code generated from the WSDL.

### binas-ws-cli and station-ws-cli
This is a simple Java Web Service client

The client uses the wsimport tool (included with the JDK since version 6)
to generate classes that can invoke the web service and
perform the Java to XML data conversion.

The client needs access to the WSDL file,
either using HTTP or using the local file system.

**You must start the server first.**

The default WSDL file location is **${basedir}/src/wsdl**.
The WSDL URL location can be specified in **pom.xml**
**/project/build/plugins/plugin\[artifactId="jaxws-maven-plugin"]/configuration/wsdlUrls**

The **jaxws-maven-plugin** is run at the "generate-sources" Maven phase (which is before the compile phase).

To generate stubs using wsimport:
```
  mvn generate-sources
```

To compile:
```
  mvn compile
```

To run using exec plugin:
```
  mvn exec:java
```
  
