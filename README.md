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

     curl -s https://packagecloud.io/install/repositories/iofog/iofog-connector/script.deb.sh | sudo bash
     sudo apt-get install iofog-connector
	   
3.&ensp;Setup certificates if needed (After installation there are configs.json, server-cert.pem and server-key.pem files present in the /etc/iofog-connector directory)

     - configs.json contains the list of existing connections
     - server-cert.pem is a public key that tells that Iofog-Controller is allowed to Connector
     - server-key.pem is a private key that has its own identity and uses it to talk to ioFog agent

4.&ensp;Add iofog-connector.conf config file to Connector

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
     }' > /etc/iofog-connector/iofog-connector.conf

5.&ensp;Add Connector to Iofog-Controller database

    iofog-controller connector -add -n <name> -d <domain> -i <publicIP>
    
 
**Logs**
- Log files are located at '/var/log/iofog-connector'

**System Requirements (Recommended)**
- Processor: 64 bit Dual Core or better
- RAM: 1 GB minimum
- Hard Disk: 5 GB minimum
- Java Runtime (JRE) 8 or higher

**Platforms Supported (Ubuntu Linux)**
- 14.04 - Trusty Tahr
- 16.04 - Xenial Xerus


&ensp; *Connector Update*:

        sudo service iofog-connector stop       
        sudo apt-get install --only-upgrade iofog-connector
        sudo service iofog-connector start
        or
        sudo service iofog-connector stop
        sudo apt-get install --only-upgrade iofog-connector-dev (developer's version)
        sudo service iofog-connector stop   
<br>
<br>
<br>

**Connector CLI**

*Connector Usage*

$ iofog-connector <command>

*Command List*

start -- Start connector service. <br>
stop -- Stop connector service.  <br>
help -- Display usage information.  <br>
version – Display the software version and license information. <br>
status –  Display current status information about the software. <br>

*Start* <br>
service iofog-connector start <br>

*Stop* <br>
service iofog-connector stop <br>

*Help* <br>
Option: -h, -? <br> 
GNU long option: --help<br>           

*Version* <br>
Option: -v   <br> 
GNU long option: --version<br>
