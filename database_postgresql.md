---
layout: default
title: VARS - Setting up PostgreSQL
---

VARS can run on most database servers. Below is the documentation to setup VARS to run on [PostgreSQL](http://www.postgresql.org/)

## Installing PostgreSQL

To install PostgreSQL, please refer to the documentation at [http://www.postgresql.org/](http://www.postgresql.org/).

## Configuring PostgreSQL

Once the PostgreSQL database server is installed you can create and configure a VARS database as follows:

1. Initialize a database. Note that _/var/local/pgsql/data_ is just an example path. You may change that to suit your needs.  
`initdb -D /var/local/pgsql/data`
2. Modify _/var/local/pgsql/data/postgresql.conf_ to allow TCP/IP connections from anyhost. Add the following line:  
`listen_addresses='*'`
3. Modify _/var/local/pgsql/data/pg_hba.conf_ to allow connections from anyhost. Add the following line:  
`host  VARS  varsuser  0.0.0.0/0 password`
4. Start PostgreSQL:  
`postgres -D /var/local/pgsql/data`
5. Create the vars database with the following command:  
`createdb VARS`
6. Create a varsuser account. The user account needs to have aleast read/write/modify privelages. __Remember the password you use!!__ You'll need that later:  
`createuser varsuser --password`

## Setup the VARS Database on PostgreSQL

Once you've initialize a database, you will need to create the database tables that VARS needs. You can use your favorite database utility or you can use PostgreSQL's command line utility psql to run the table definitions below.  

- Run the VARS table definitions DDL from [vars-annotationSchemaOnPostgreSQL.ddl](https://github.com/hohonuuli/vars/blob/develop/vars-jpa/src/main/sql/vars-annotationSchemaOnPostgreSQL.ddl)
- Run the VARS-KB table definitions DDL from [vars-knowledgebaseSchemaOnPostgreSQL.ddl](https://github.com/hohonuuli/vars/blob/develop/vars-jpa/src/main/sql/vars-knowledgebaseSchemaOnPostgreSQL.ddl)
- Initialize the UniqueID table using SQL from [vars-insertInitialUniqueID.sql](https://github.com/hohonuuli/vars/blob/develop/vars-jpa/src/main/sql/vars-insertInitialUniqueID.sql)

## Configuring VARS

VARS will need to be modified to recognize your database.

<ol> 
<li>Find and download the JDBC driver for your version of postgreSQL from [http://jdbc.postgresql.org/](http://jdbc.postgresql.org/). (JDBC type 4 driver is best). Just download the driver and drop it into VARS_HOME/lib.</li>
<li>Note the following information. The default port for PostgreSQL is 5432 unless you changed it in postgresql.conf. The host is the either the fully qualified name or the IP address of the computer running PostgreSQL. Either the name or the IP address wil work. <pre>
JDBC URL: jdbc:postgresql://HOST:PORT/DATABASE [varies with your computer]  
USERNAME: ??? [probably will be 'varsuser' if you followed the directions above]  
PASSWORD: ??? [From above]  
DRIVER NAME: org.postgresql.Driver  
</pre></li>
<li>Make a temp directory somewhere to work in:  <br>
<code>mkdir tempdir;cd tempdir`</code></li>
<li>Copy the vars-jpa-XXX.jar from VARS_HOME/lib into the tempdir.<br>
<code>cp $VARS_HOME/lib/vars-jpa-6.0.2.jar tempdir</code></li>
<li>Unzip the vars-jpa jar. [JAR files are just zip files with a different extension].  <br>
<code>cd tempdir; mkdir trashme; unzip vars-jpa-6.0.2.jar -d trashme</code></li>
<li>Edit the following files:  <pre>
trashme/annotation-jdbc.properties  
trashme/knowledgebase-jdbc.properties  
trashme/query-jdbc.properties  
trashme/META-INF/persistence.xml  
</pre></li>
<li>Inside each of these files are lines that specify the jdbc-url, username, password, and driver name of the databases. Edit them as appropriate. If you've munged the VARS and VARS_KB tables into a single database [which is what these directions assume] then you'll only have one url to use everywhere, otherwise be mindful of which database you point at. For example, annotation-jdbc.properties and query-jdbc.properties should point at the VARS database. The knowedgebase-jdbc.properties file should point at the VARS_KB database. persistence.xml has several XML blocks named 'persistence-unit' in it. The ones named 'vars-jpa-knowledgebase', and 'vars-jpa-misc' should point at VARS_KB. 'vars-jpa-annotation' should point at the VARS database. You don't care about the 'vars-jpa-test' persistence unit.</li>
<li>Once you've made edits you need to jar everything back up.  <pre>
  # Assuming you're still in tempdir
  jar -cvf vars-jpa-6.0.2-postgresql.jar -C trashme *
</pre></li>
</ol>
This will create a new jar named vars-jpa-6.0.2-postgresql.jar. Delete VARS_HOME/lib/vars-jpa-6.0.2.jar and replace it with your new jar (VARS will automatically pick up the changes when you start it