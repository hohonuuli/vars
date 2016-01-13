# Building VARS

In order to build VARS you will need to have the following installed:

1. [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
2. [Maven 3](http://maven.apache.org)
	

## FETCHING THE SOURCE CODE

The VARS source code is stored in a Git repository. Instructions for obtaining the VARS source code can be found at [https://github.com/hohonuuli/vars](https://github.com/hohonuuli/vars). 
For those familiar with hg, the checkout command is:
    
    git clone https://github.com/hohonuuli/vars.git
    
## YOUR FIRST BUILD

VARS is co-developed alongside several other external modules. Normally these modules are retrieved from the maven repository at [http://dl.bintray.com/hohonuuli/maven](http://dl.bintray.com/hohonuuli/maven) and you do not need to think about them since Maven takes care of fetching them for you. However, between VARS releases features may be added to these modules that can not be found in the versions stored in the Maven repository. If you attempt to build VARS and get errors. You may need to fetch the source code for  the modules and build and install them into your local repository. The related modules can be found at:

    # MBARIX4J at https://github.com/hohonuuli/mbarix4j
    # Execute the following commands to build and install in your local maven repository
    git clone https://github.com/hohonuuli/mbarix4j.git
    cd mbarix4j
    mvn install
    
    # VCR4J at https://github.com/hohonuuli/vcr4j
    # Execute the following commands to build and install in your local maven repository
    git clone https://github.com/hohonuuli/vcr4j.git
    cd vcr4j
    mvn install

    
Normally Maven expects you to have an internet connection when running builds. However, if you want to checkout the VARS code and build it offline you can do that as follows:

    # Fetch all the dependencies while you're online
    mvn dependency:go-offline
    
    #Build VARS later offline 
    mvn install -P dev -o
    
## BUILDING VARS
	
To Build VARS, run the following command on the command line:

	mvn clean install -P dev

A standalone application will be built to vars/vars-standalone/target/vars-standalone-[VERSION]-scripting.zip.

The 'environment' variable specifies what database you are targeting, for most folks that will be _dev_.


