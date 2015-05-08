# Scale In and Scale Out of Virtual Servers

### Project Description
- For any Web application there should be a server, which responds to requests of client. There are several issues on the server side as frequency of requests changes from hour to hour. 
- To handle this situation we need to have multiple servers so that the load can be sent to other servers, which have fewer loads. 
- So, in this project we use Virtualization Technology to create new Virtual servers and distribute load among all the Virtual servers which are capable to handle the load. 
- Sometimes, in real time the servers we have may not be sufficient. 
- So we leverage on the virtualization to clone new Virtual servers and use the concept of scaling out to increase number of servers. 
- In the same way when there are fewer loads we use scale in to delete virtual machines to save resources. 

### Architecture

![alt tag](https://cloud.githubusercontent.com/assets/8682258/7545893/ac11ad8a-f58d-11e4-8f97-b842f2e46754.png)<br></br>

##### Description

- Client communicates with the Load Balancer in the App server. All the incoming request are taken by the Load Balancer and then it forwards the request to one of the healthy Request Servers obtained from the database.
- All the Request Servers run a RESTful application named Digital Wallet System. 
- The Digital Wallet System application is used only for demonstrating how the load balancer works; it is not a part of the project deliverables.
- This application requires a database. So we used Database server to communicate with all request servers.


### To Run Application

- Import git project into spring sts. Follow below section from tutorial http://eclipsesource.com/blogs/tutorials/egit-tutorial/ -installing egit in eclipse -egit configuration -cloning repositories

- Rightclick pom.xml and then select maven clean and maven install from run as.

- Project-> Clean

- Right click and Run as-> "Run On Server"


