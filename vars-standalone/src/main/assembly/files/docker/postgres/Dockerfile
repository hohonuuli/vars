FROM postgres:9.5

MAINTAINER Brian Schlining (brian@mbari.org)

ENV POSTGRES_INITDB_ARGS="--auth=trust --auth-host=trust"
ENV POSTGRES_PASSWORD=mysecretpassword
ENV POSTGRES_DB='VARS'

ADD docker-entrypoint-initdb.d /docker-entrypoint-initdb.d

#ADD conf/pg_hba.conf /var/lib/postgresql/data/pg_hba.conf 

CMD ["postgres", "-h", "*"]