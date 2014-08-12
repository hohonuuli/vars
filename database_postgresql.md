---
layout: default
title: VARS - Setting up PostgreSQL
---

VARS can run on most database servers. Below is the documentation to setup VARS to run on [PostgreSQL](http://www.postgresql.org/)

## Installing PostgreSQL

To install PostgreSQL, please refer to the documentation at [http://www.postgresql.org/](http://www.postgresql.org/).

## Configuring PostgreSQL

Once the PostgreSQL database server is installed you can create and configure a VARS database as follows:

1. Initialize a database. Note that _/var/local/pgsql/data_ is just an example path. You may change that to suit your needs. `initdb -D /var/local/pgsql/data`
2. Modify _/var/local/pgsql/data/postgresql.conf_ to allow TCP/IP connections from anyhost. Add the following line: `listen_addresses='*'`
3. Modify _/var/local/pgsql/data/pg_hba.conf_ to allow connections from anyhost. Add the following line: `host  VARS  varsuser  0.0.0.0/0 password`
4. Start PostgreSQL: `postgres -D /var/local/pgsql/data`
5. Create the vars database with the following command: `createdb VARS`
6. Create a varsuser account. The user account needs to have aleast read/write/modify privelages. __Remember the password you use!!__ You'll need that later: `createuser varsuser --password`