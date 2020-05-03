# Chat App Assignment - AT

Chat App is application for distributed communication between clients and servers of the same system, made as part of Distributed Artificial Intelligence and Intelligent Agents course.

## Download instructions

These commands will get you a copy of project for dev and testing purposes
```
$ git clone https://github.com/kresovicmilan/at-chat-app.git
```

## Prerequisites

To successfully run the application on your local machine please install following software

* JBoss in Eclipse
* WildFly v11.0.0.Final [*(download link)*](https://wildfly.org/downloads/)
* Gson v2.8.2 [*(download link)*](https://repo1.maven.org/maven2/com/google/code/gson/gson/2.8.2/) *(or find it in this project)*

## Configuration - Eclipse

  1. Add WildFly server to your environment with JavaSe-1.8 and with following configuration file: **standalone-full-ha.xml**
  2. Import project into the workspace
  3. Go to *Java build path* of each project and change path of **JRE system library** to *jre1.8.0*
  4. Go to *Java build path* of **JAR2020** project and change path of **Gson library** to path where *gson library* is stored
  5. Go to *Preferences -> Java -> Compiler -> Building* and change **Circular dependencies** to **Warning**

## Configuration - Project
  1. Go to *JAR2020/src/META-INF/ip_config.txt* and change IP address to suitable IP address in the following way

```
master:<master-ip-address>:<master-port>
host:<host-ip-address>:<host-port>
```

  **Important:** If you're setting up the master server put the same IP address and port on both lines. Example:

```
master:0.0.0.0:8080
host:0.0.0.0:8080
```

  2. Go to *WAR2020/WebContent/home.html* and change **hostSocket** variable on the line 25 to the IP address and port of the host that is getting set up.

```
if (sessionStorage.getItem('host') === "") {
	hostSocket = "ws://<host-ip-address>:<host-port>/WAR2020/ws";
} else {
	hostSocket = "ws://" + sessionStorage.getItem('hostIp') + "ws";
}
```

  **Example:**

```
if (sessionStorage.getItem('host') === "") {
	hostSocket = "ws://0.0.0.0:8080/WAR2020/ws";
} else {
	hostSocket = "ws://" + sessionStorage.getItem('hostIp') + "ws";
}
```

## Starting
  
  1. First start your master host
  2. Turn off firewall if you have it on
  3. Run **EAR2020** project on server and choose to manually define it
  4. Choose **WildFly 11** and in **Server's host name** place IP address of your host
  5. Go to:
  ```
  <host-ip-address>:<host-port>/WAR2020/
  ```
