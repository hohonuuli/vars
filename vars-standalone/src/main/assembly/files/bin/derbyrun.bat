@echo off

REM The name of the machine that VARS is installed in.
SET DERBY_SERVER_HOST=localhost
SET DERBY_SERVER_PORT=1527
SET DERBY_USER=varsuser
SET DERBY_PASSWORD=vars0sourceforge
SET DERBY_HOME=%~dp0..
SET DERBY_CLASSPATH="%DERBY_HOME%\conf";"%DERBY_HOME%\lib\*"

REM This is the location where database files will be stored. To change
REM properties for a database system place the derby.properties file in 
REM this directory.
SET DERBY_SYSTEM_HOME=%DERBY_HOME%\database

SET DERBY_CONNECTION=jdbc:derby://%DERBY_SERVER_HOST%:%DERBY_SERVER_PORT%/VARS;user=%DERBY_USER%;password=%DERBY_PASSWORD%

echo "Sending '%1' command to the VARS database at: %DERBY_CONNECTION%"

java -cp %DERBY_CLASSPATH% -Dderby.system.home="%DERBY_SYSTEM_HOME%" org.apache.derby.drda.NetworkServerControl %1 -noSecurityManager -h %DERBY_SERVER_HOST% -p %DERBY_SERVER_PORT% -user %DERBY_USER%  -password %DERBY_PASSWORD%
