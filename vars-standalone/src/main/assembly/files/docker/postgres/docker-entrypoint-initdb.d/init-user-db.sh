#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE USER varsuser PASSWORD 'vars0sourceforge';
    GRANT ALL PRIVILEGES ON DATABASE "VARS" TO varsuser;
EOSQL