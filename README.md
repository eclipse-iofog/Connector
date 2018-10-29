**What is Connector?**

- Is an internetworking aid for ioFog nodes
- Provides “double opt-in” connections for secure IoT connectivity

We call this module "Connector" because it acts as a virtual connection between ioFog nodes.

Connector installs on any common version of Linux. It handles the traffic between Fog nodes. You can have as many Connector instances in a Iofog deployment as you want.

When you put Connector, anywhere on your network (it can be even public Internet), anywhere that is visible to fog nodes, the Connector facilitates this talk to each other, it facilitates opening a port on public internet to talk to the fog. Thus you can reach microservices remotely, and microservices come in together from different locations.


**Connector Setup**

1.&ensp;In order to install Connector, you need to have Java installed on your machine.

     sudo add-apt-repository ppa:webupd8team/java
     sudo apt-get update
     sudo apt-get install oracle-java8-installer

2 &ensp;Install Connector

     curl -s https://packagecloud.io/install/repositories/iofog/connector/script.deb.sh | sudo bash
     sudo apt-get install iofog-connector (release version)
     or
     sudo apt-get install iofog-connector-dev (developer's version)
	   
3.&ensp;Setup certificates if needed (After installation there are config.json, server-cert.per and server-key.per files present in the /etc/connector directory)

     - config.json contains the list of existing connections
     - server-cert.per is a public key that tells that Iofog-Controller is allowed to Connector
     - server-key.per is a private key that has its own identity and uses it to talk to ioFog agent

4.&ensp;Add connector.conf config file to Connector

     sudo echo '{
      "ports": [
        "6000-6001",
        "7000-7002",
        "30000-39999",
        "40000-49999"
      ],
      "exclude": [
        "7001"
      ],
      "broker":12345,
      "address":"127.0.0.1",
      "dev":true
     }' > /etc/connector/connector.conf

5.&ensp;Add Connector to Iofog-Controller database

    iofog-controller connector -add <name> <domain> <publicIP>
    
 
**Logs**
- Log files are located at '/var/log/connector'

**System Requirements (Recommended)**
- Processor: 64 bit Dual Core or better
- RAM: 1 GB minimum
- Hard Disk: 5 GB minimum
- Java Runtime (JRE) 8 or higher

**Platforms Supported (Ubuntu Linux)**
- 14.04 - Trusty Tahr
- 16.04 - Xenial Xerus


&ensp;- Connector Update:

        sudo service iofog-connector stop       
        sudo apt-get install --only-upgrade iofog-connector
        sudo service iofog-connector start
        or
        sudo service iofog-connector stop
        sudo apt-get install --only-upgrade iofog-connector-dev (developer's version)
        sudo service iofog-connector stop        
