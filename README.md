#OmniBox
A distributed system to support file storage and transfers. Additional features: loadbalancing and redundancy. 
- Server do not host any file, just keeps a "system state" about the logged users, repositories connected and handles the system loadbalancing
- Repository is used as a storage unit and should satisfy the client's request. A repository replicates all the files received to the other ones.
- OperationsCenter (RMI) prints the current state of the system (downloads, uploads, connected/disconnected clients, ..)
- Client (GUI) allows the user to download, upload or delete files

##Usage
"jars" folder contains all the necessary jars to start the system.

####Server
```
java -Djava.net.preferIPv4Stack=true -jar Server.jar <port> <db file>
```
*"-Djava.net.preferIPv4Stack=true" arg is to assure that the server can deal with multicast requests if executed on a machine connected to the network using a wireless network device. Port 7000 is reserved to multicast socket.*

Example:
```
java -Djava.net.preferIPv4Stack=true -jar Server.jar 6000 users.db
```

####Client
```
java -jar Client.jar <server port> <server ip> <local directory to save the files>
```

Example:
```
java -jar Client.jar 6000 192.168.1.25 /Users/omniuser/downloads/
```

*If you do not know the ip address, provide just the port*

Example:
```
java -Djava.net.preferIPv4Stack=true -jar Client.jar 6000
```
####Repository
```
java -jar Repository.jar <server port> <server ip> <local directory to save the files>
```

Example:
```
java -jar Repository.jar 6000 192.168.1.25 /Users/omnirepository/r1/
```

####OperationsCenter
```
java -jar OperationsCenter.jar <server ip>
```

Example:
```
java -jar OperationsCenter.jar 192.168.1.25
```

##User Credentials
The users.db already contains some registered users:
- user: danilo / password: danilo123
- user: goncalo / password: goncalo123
- user: mario / password: mario123
