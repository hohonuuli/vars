@ECHO OFF
rem ---------------------------------------------------------------------------
rem Execute groovy script using the projects library directory.
rem ---------------------------------------------------------------------------

SET VARS_HOME=%~dp0..
SET VARS_CLASSPATH="%VARS_HOME%\conf";"%VARS_HOME%\lib\*"

SET GROOVY_CLASSPATH="%VARS_HOME%\scripts\groovy";%VARS_CLASSPATH%;"%VARS_HOME%\lib\groovy-repl\*"

if dummy==dummy%1 (
SET ARGS="%VARS_HOME%\scripts\groovy\openshell.groovy" 
) ELSE (
SET ARGS=%*
)

java -Xms16m -Xmx512m -Duser.timezone="UTC" -classpath %GROOVY_CLASSPATH% -Djava.security.manager -Djava.security.policy=="%VARS_HOME%/conf/java.policy" groovy.ui.GroovyMain %ARGS%