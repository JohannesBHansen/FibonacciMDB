Message-Driven EJB Fibonacci Calculator
=======================================

This application was written for demonstrating Enterprise Messaging through the use of JMS 1.1 and EJB 3.1 Message-Driven Bean (MDB) technology in JBoss AS 6.1.0 and later. The client in this application is implemented as JSP page, which invokes a Servlet to send numerous text messages via message-driven beans to a JMS service (HornetQ, by default). The server will "process" each message by calculating a Fibonacci sequence according to the parameters specified by the client, and insert the Fibonacci result and its elapsed processing time to an in-process database (hsqldb, by default) via JDBC. The client's web broswer will be redirect to a Servlet which continuously monitors the server's progress by reading the database records and plotting Fibonacci processing-times via Google Charts.

This application was forked from the "helloworld-mdb" project, distributed as part of the JBoss 7.1.0 Quickstart package, available <a href="https://github.com/jbossas/quickstart.git">here</a>.

An IntelliJ project is provided, which includes an application server configuration for running with JBoss 6.1.0.


Prerequisites
-------------

To be able to run this project, the following software must be installed on your computer:

   * Java Development Kit (JDK) version 1.6 or later.
   * JBoss Application Server version 6.1.0, or later. The "FibonacciMDBQueue" must be defined in the JBoss JMS service (aka "HornetQ"). If using HornetQ, add the following lines in `$JBOSS_HOME/server/default/deploy/hornetq/hornetq-jms.xml` 
   
				   <queue name="FibonacciMDBQueue">
				      <entry name="/queue/FibonacciMDBQueue"/>
				   </queue>
				   
   * A Web browser
   * (optional) IntelliJ IDEA, version 10.5 or later


Usage
-----

Running in IntelliJ:

   1.  Open the project in IntelliJ
   2.  Update the JBoss Run configs
   3.  Update paths in Project Settings for Project, Modules, Libraries, and Artifacts 
   4.  Deploy and/or run the project
   5.  Open, http://localhost:8080/HelloWorldMDB/


Running directly in JBoss 6.1.0 (without IntelliJ):

   1.  Deploy the war package with, cp -R out/artifacts/HelloWorldMDB.war /opt/jboss-6.1.0.Final/server/default/deploy/
   2.  Start JBoss with, /opt/jboss-6.1.0.Final/run.sh 
   3.  Open, http://localhost:8080/HelloWorldMDB/


Author
------

Created by Ian Downard, March 2012.

Screenshot
------

![FibonacciMDB Screenshot](http://dl.dropbox.com/u/6145542/blog%20photos/JMS%20Load%20Testing%20Screenshot.png "FibonacciMDB Screenshot")