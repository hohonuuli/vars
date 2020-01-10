# Launching a Test Postgres Database

__Important__: The current configuration does not save the database is you rebuild the container. If you need to do that you will need to mount a volumn to your docker container. The data directory inside the container where the postgres data lives is `/var/lib/postgresql/data/`.

Build the Docker image

```
docker build -t vars-postgres .
```

Start the container

```
docker run --name vars-postgres -e POSTGRES_PASSWORD=mysecretpassword -p 5432:5432 vars-postgres
```

Stoping the container

```
docker stop vars-postgres
docker rm vars-postgres
```
