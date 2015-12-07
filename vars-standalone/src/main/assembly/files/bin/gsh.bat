@echo off
REM $Id: $
title VARS - Groovy Shell
SET APP_HOME=%~dp0..
SET GSH_CLASSPATH="%APP_HOME%\conf";"%APP_HOME%\lib\*";"%APP_HOME%\scripts\groovy"
echo [VARS] Starting VARS Groovy Scripting Shell

set argC=0
for %%x in (%*) do Set /A argC+=1

SET JAVA_OPTS="-Djava.library.path=%APP_HOME%/native -Duser.timezone=UTC -Dfile.encoding=UTF8"
SET CLASSPATH=%GSH_CLASSPATH%


IF %argC% EQU 0 (
  groovysh --classpath %GSH_CLASSPATH%
) ELSE (
  groovy -cp %GSH_CLASSPATH% %*
)

