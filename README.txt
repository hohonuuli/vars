
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

In order to build VARS you will need to have the following installed:
	1) Java 6 (https://jdk6.dev.java.net/)
	2) Maven 2 or 3 (http://maven.apache.org)
	
VARS also requires that QuickTime for Java to be installed in order to run
the annotation application. However, you do not need QuickTime to compile VARS.
QuickTime for Java is included by default on Mac OS X. On Windows, you will need
to install it. You can download if from http://developer.apple.com/quicktime/. 
Alternatively, it is installed when you install iTunes.
	
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
FETCHING THE SOURCE CODE
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The VARS source code is stored in a Mercurial repository. Instructions for 
obtaining the VARS source code can be found at 
http://code.google.com/p/vars-redux/source/checkout. 
For those familiar with hg, the checkout command is:
    
    hg clone https://vars-redux.googlecode.com/hg/ vars-redux
    
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
YOUR FIRST BUILD
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
VARS is co-developed alongside several other external modules. Normally these
modules are retrieved from the maven repository at 
http://mbari-maven-repository.googlecode.com/svn/repository/
and you do not need to think about them since Maven takes care of fetching them
for you. However, between VARS releases features may be added to these modules
that can not be found in the versions stored in the Maven repository. If you
attempt to build VARS and get errors. You may need to fetch the source code for 
the modules and build and install them into your local repository. The related
modules can be found at:

    # MBARIX4J at http://code.google.com/p/mbarix4j/
    # Execute the following commands to build and install in your local maven repository
    svn checkout http://mbarix4j.googlecode.com/svn/trunk/ mbarix4j
    cd mbarix4j
    mvn install
    
    # VCR4J at http://code.google.com/p/vcr4j/
    # Execute the following commands to build and install in your local maven repository
    svn checkout http://vcr4j.googlecode.com/svn/trunk/ vcr4j
    cd vcr4j
    mvn install

    # QTX4J at http://code.google.com/p/qtx4j/
    # Execute the following commands to build and install in your local maven repository
    svn checkout http://qtx4j.googlecode.com/svn/trunk/ qtx4j
    cd qtx4j
    mvn install
    
Normally Maven expects you to have an internet connection when running builds. However,
if you want to checkout the VARS code and build it offline you can do that as follows:

    # Fetch all the dependencies while you're online
    mvn dependency:go-offline
    
    #Build VARS later offline 
    mvn install -P dev -o
    
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
BUILDING VARS
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
To Build VARS, run the following command on the command line:
	mvn clean install -P dev

A standalone application will be built to  
vars-redux/vars-standalone/target/vars-standalone-[VERSION]-scripting.zip.

The 'environment' variable specifies what database you are targeting, for most
folks that will be 'dev'. i


