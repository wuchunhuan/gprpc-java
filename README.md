# gprpc-java
A fast and light wight rpc framework based on Google protobuf and netty.

## Overview
First version of gprpc-java is only a client-side rpc library, which of cousre can call rpc services build on C++. Gprpc-java will be fully implemented with server-side features in java in the near future. By then, Java and C++ applications(clients and servers) based on gprpc library will be able to call each other with the same IDL files(proto files with service definitions).

## Features
- Synchronous and asynchronous call
- Precise time out control on rpc calls

## TODO
- Server-side implementation

## Documentation
- [gprpc in detail](https://github.com/wuchunhuan/gprpc/blob/master/docs/gprpc_detail.md)

## Dependencies
```
<dependencies>
<dependency>
<groupId>org.slf4j</groupId>
<artifactId>slf4j-api</artifactId>
<version>1.7.25</version>
</dependency>
<dependency>
<groupId>org.slf4j</groupId>
<artifactId>slf4j-simple</artifactId>
<version>1.7.25</version>
</dependency>
<dependency>
<groupId>io.netty</groupId>
<artifactId>netty-all</artifactId>
<version>4.1.9.Final</version>
</dependency>
<dependency>
<groupId>com.google.protobuf</groupId>
<artifactId>protobuf-java</artifactId>
<version>3.4.0</version>
</dependency>
<dependency>
<groupId>com.googlecode.protobuf-java-format</groupId>
<artifactId>protobuf-java-format</artifactId>
<version>1.2</version>
</dependency>
</dependencies>
```

## Support
wuchunhuan@gmail.com
