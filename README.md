*This project is developed in Eclipse and supported by TicketMaster.*
# BestEvents
BestEvents is an interactive website to provide services of event search and personalized recommendations.

[Go to website](http://3.132.8.179/BestEvents/)


## Get Started
Signup in the login page first, login, and then information of events nearby according to your current loation will be fetched from TicketMaster (please allow the service to use your location information when asked).

You can click on the little star at the bottom right corner of teach event to favorite or save the event for later.

Our recommendation system is based on your favorite history. To get personalized recommendations, please add a couple of events to your favorite first.

Finally, enjoy the best events around!

## Development
This project is developed in Eclipse. To run locally, you will need:

### Install the dependencies
* [Java](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) (version must be 8 or hight)
* [Eclipse](https://www.eclipse.org/downloads/packages/) for Java EE (must be Photon or higher)
* [Apache Tomcat](http://tomcat.apache.org/download-90.cgi)
* [MAMP](https://www.mamp.info/en/)

### Host the project on HTTP server
Once clond to your local directory, this project can be imported as a new project to your Eclipse workspace. All you need is to add a new server (in our case, Tomcat) to the same workspace and host the project on it. 

### Set up MySQL database locally
Start MAMP and go to phpmyadmin (under tool), create a new database. Update [constants](/BestEvents/src/db/mysql/MySQLDBUtil.java) according to your settings. Run [MySQLTableCreation](/BestEvents/src/db/mysql/MySQLTableCreation.java) as java application to create tables(run this whenever you want to reset the tables). 

### Run
Start the sever to [test locally](http://localhost:8080/BestEvents/)

### For You Information
I implemented a [database connection interface](/BestEvents/src/db/DBConnection.java) while developing the project. It's very convenient if you want to use other databases other than MySQL. Remember to modify the [database factory](/BestEvents/src/db/DBConnectionFactory.java) accordingly.

