# TMA-Monitor `Java` Client Library 

Main dependency to use during the development of `TMA Monitor` probes developed in `Java`.


## Prerequisites

You need [maven](https://maven.apache.org/) to build this project.

If you cannot install `maven` in your system, you should use the `docker` image available at [libraries](../). In this case, `docker` is mandatory.


## Build

To build the library, you need to run the following command.

```sh
mvn clean install
```

**Note:** As an alternative, you can take advantage of the [libraries](../) docker image to ease your deployment.

## Usage

To use the monitor-client in the development of your probe, you just need to include the library in your [maven](https://maven.apache.org/) project, using the code below.

```xml
<dependency>
    <groupId>eu.atmosphere.tmaf</groupId>
    <artifactId>monitor-client</artifactId>
    <version>0.1</version>
</dependency>
```


Note: check the [probe-demo](../../probes/probe-demo) for mode detailed demonstration of the usage.