REM Merge video annotations with data in a CSV file. The CSV file should have a column
REM named 'date' (no quotes). It may also have the following columns (names are case-insensitve):
REM salinity, temperature, depth, latitude, longitude, oxygen

REM Example:
REM merge_csv.bat T0198-11HD ~/Desktop/data/shipdatagalore.csv

SET VARS_HOME=%~dp0..
SET VARS_CLASSPATH="%VARS_HOME%\conf";"%VARS_HOME%\lib\*""

java -Xms16m -Xmx512m -Duser.timezone=UTC -Dfile.encoding=UTF8 -classpath "$VARS_CLASSPATH" org.mbari.vars.integration.GenericMerge %*
