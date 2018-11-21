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

<br>
<br>
<br>

**Connector API**<br>
<br>
Connector exposes API and it’s API where you have a set of identities. Fog Controller has the proper identity and it’s able to tell Connector “I want you to open up some connections”. Fog Controller uses Connector API to tell it to do and Connector simply replies whether it is successful or not successful.<br>
<br>
ioFog Agent connects to Connectors and through connecting Connectors traffic is able to move between fog nodes. In addition Connector has the capability to open traffic to the outside world so the outside users can get route into fog node.<br>
<br>
**Connector offers two connectivity types:**<br>

**1)    The first type, called a public pipe, provides a way to securely access Fog software and data from anywhere on in the world. Connector punches through firewalls and NATed networks to perform automatic internetworking of the Fog.**<br>
<br>
Let’s describe what mapping is. Mapping is a way for describing a port opening, no matter whether you create a public or private pipe. It is an object that has an internal and an external port.<br>
<br>
*The Endpoint and Response (below) of a public pipe connection is displayed below (Add functionality):*<br>
<br>
**Endpoint**: /api/v2/mapping/add<br>
**Method**: POST<br>
**Header Content-Type**: application/x-www-form-urlencoded<br>
**Parameters**: mapping={"type":"public","maxconnections":60,"heartbeatabsencethreshold":200000}<br>
<br>
"maxconnections" means how many connection threads the ioFog agent will make with the Connector . You can have many users at the same time.<br>
"heartbeatabsencethreshold" means if we don’t have a heartbeat signal from the ioFog agent within 20 sec, we kill that connection thread.<br>
<br>
**Response:**<br>

*{<br>
     "status": "ok",<br>
     "id": "2ae8ff72-7447-47de-a4ec-123eb214d63e",<br>
     "port1": 32768,<br>
     "port2": 32769,<br>
     "passcode1": "0b403b65-c5a0-476f-92f5-ffc7ca0f85ef",<br>
     "passcode2": "",<br>
     "timestamp": 1542719018626<br>
}*<br>
<br>
“id” is your ID for the mapping<br>
"port1" - port that will be used by the ioFog agent<br>
"port2" - port that will be by the Connector for public URL access<br>
"passcode1” is used by the ioFog agent to establish a secure connection to the Connector. The Fog agent will receive the information through the Fog controller and tell you that you need to connect.<br>
<br>
*The Endpoint of public pipe connection is displayed below (Remove):*<br>
<br>
**Endpoint:** /api/v2/mapping/remove<br>
**Method:** POST<br>
**Header Content-Type:** application/x-www-form-urlencoded<br>
**Parameters:** mappingid=e2454159-ed8c-4d00-a885-fdd87de811de<br>
<br>
**Response:**<br>
<br>
*{<br>
     "status": "ok",<br>
     "id": "2ae8ff72-7447-47de-a4ec-123eb214d63e",<br>
     "timestamp": 1542719354334<br>
}*<br>
<br>
**2)    The second type, called a private pipe, consumes bandwidth on the Connector but stabilizes connectivity between Fog nodes that can’t normally see each other.** <br>
<br>
Connector is available for 2 different ioFog agents talking to each other.<br>
<br>
*The Endpoint and Response (below) of a private pipe connection is displayed below (Add functionality):*<br>
<br>
**Endpoint:** /api/v2/mapping/add<br>
**Method:** POST<br>
**Header Content-Type:** application/x-www-form-urlencoded<br>
**Parameters:** {"type":"private","maxconnectionsport1":1, "maxconnectionsport2":1, "heartbeatabsencethresholdport1":200000, "heartbeatabsencethresholdport2":200000}<br>
<br>
**Response:**<br>
<br>
*{<br>
     "status": "ok",<br>
     "id": "e2454159-ed8c-4d00-a885-fdd87de811de",<br>
     "port1": 32770,<br>
     "port2": 32771,<br>
     "passcode1": "3dbd413c-10e9-4e40-a9cb-f4b8fb2b8b56",<br>
     "passcode2": "7f4eb783-c2ab-4517-8aaf-c8395054193e",<br>
     "timestamp": 1542719231127<br>
}*<br>
<br>
The parameters description is the same as is described above for a public pipe.<br>
Here “port1" will come out in "port2", and vice versa. Without the passcodes you will be immediately rejected.<br>
<br>
*The Endpoint of private pipe connection is displayed below (Remove):*<br>
<br>
**Endpoint:** /api/v2/mapping/remove<br>
**Method:** POST<br>
**Header Content-Type:** application/x-www-form-urlencoded<br>
**Parameters:** mappingid=e2454159-ed8c-4d00-a885-fdd87de811de<br>
<br>
**Response:**<br>
<br>
*{<br>
     "status": "ok",<br>
     "id": "2ae8ff72-7447-47de-a4ec-123eb214d63e",<br>
     "timestamp": 1542719354334<br>
}*<br>
<br>
<br>
<br>
**! In Public mode the URL is generated as follows:**<br>
<br>
Example: ${protocol}://${address}${port2} <br>
where <br>
{protocol} is either http:// or https://<br>
{address} is either IP address or domain name<br>
<br>
*In iofog-connector.config file*<br>
When "dev": true, it's http connection. <br>
When "dev": false, it's https connection. <br>
