**What is ComSat?**

- Is an internetworking aid for ioFog nodes (short for Communications Satellite)
- Provides “double opt-in” connections for secure IoT connectivity

We call this module "ComSat" because it acts as a virtual communications satellite that ioFog nodes use as a relay point to converse with other nodes.

ComSat installs on any common version of Linux. It handles the traffic between Fog nodes. You can have as many ComSat instances in a Fog deployment as you want.

When you put ComSat, anywhere on your network (it can be even public Internet), anywhere that is visible to fog nodes, the ComSat facilitates this talk to each other, it facilitates opening a port on public internet to talk to the fog. Thus you can reach microservices remotely, and microservices come in together from different locations.


**ComSat Setup**

1.&ensp;In order to install ComSat, you need to have Java installed on your machine.

     sudo add-apt-repository ppa:webupd8team/java
     sudo apt-get update
     sudo apt-get install oracle-java8-installer

2 &ensp;Install Comsat

     curl -s https://packagecloud.io/install/repositories/iofog/comsat/script.deb.sh | sudo bash
     sudo apt-get install comsat (release version)
     or
     sudo apt-get install comsat-dev (developer's version)
	   
3.&ensp;Setup certificates if needed (After installation there are config.json, server-cert.per and server-key.per files present in the /etc/comsat directory)

     - config.json contains the list of existing connections
     - server-cert.per is a public key that tells that Fog Controller is allowed to ComSat
     - server-key.per is a private key that has its own identity and uses it to talk to ioFog agent

4.&ensp;Add comsat.conf config file to ComSat

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
     }' > /etc/comsat/comsat.conf

5.&ensp;Add ComSat to Fog Controller database

    fog-controller comsat -add <name> <domain> <publicIP>
    
 
**Logs**
- Log files are located at '/var/log/comsat'

**System Requirements (Recommended)**
- Processor: 64 bit Dual Core or better
- RAM: 1 GB minimum
- Hard Disk: 5 GB minimum
- Java Runtime (JRE) 8 or higher

**Platforms Supported (Ubuntu Linux)**
- 14.04 - Trusty Tahr
- 16.04 - Xenial Xerus


&ensp;- ComSat Update:

        sudo service comsat stop       
        sudo apt-get install --only-upgrade comsat
        sudo service comsat start
        or
        sudo service comsat stop
        sudo apt-get install --only-upgrade comsat-dev (developer's version)
        sudo service comsat stop        
