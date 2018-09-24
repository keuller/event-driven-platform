## Event Platform

This project shows up a sample platform composed by 3 modules: Hub, Security and Monitor.

The purpose is to demonstrate how to create a event based platform using MQTT protocol to comunicate all modules. It's a demonstration of Microservices orquestration. 

### Clone Repo

```shell
git clone https://github.com/keuller/event-driven-platform.git
```

### Installing Locally

You make sure that you have Maven installed in your machine, first.

```shell
$ mvn clean compile install
```

### Running Modules

Each module must be performed individually, through Maven using the same command as below.

```shell
$ mvn clean compile exec:exec -q
``` 

**Note**: Hub module must be run fist over all other modules.

### Testing

You must point out your browser at **http://localhost:8080/health** to test whether the platform is healthy or not.