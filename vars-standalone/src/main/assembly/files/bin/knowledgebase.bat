@echo off
REM $Id: $
title VARS - Knowledgebase
SET VARS_HOME=%~dp0..
SET VARS_CLASSPATH="%VARS_HOME%\conf";"%VARS_HOME%\lib\*"

echo [VARS] Starting VARS Knowledgebase Application
java -cp %VARS_CLASSPATH% -Xms64m -Xmx128m -Duser.timezone=UTC vars.knowledgebase.ui.App
