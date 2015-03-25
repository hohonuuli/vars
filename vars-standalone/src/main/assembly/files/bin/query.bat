@echo off
REM $Id: $
title VARS - Query
SET VARS_HOME=%~dp0..
SET VARS_CLASSPATH="%VARS_HOME%\conf";"%VARS_HOME%\lib\*"

echo [VARS] Starting VARS Query Application
java -cp %VARS_CLASSPATH% -Xms128m -Xmx256m -Duser.timezone=UTC vars.query.ui.App
