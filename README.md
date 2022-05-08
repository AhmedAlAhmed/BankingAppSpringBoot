# Banking Application Assignment
Banking app assignment.

Run as docker:

Run MySQL service.
```
docker run -d -p 3306:3306 --name mysqldb_container -v mysql:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=root mysql 
```

Because MySQL8+ use `caching_sha2_password` any client will get this error `Public Key Retrieval is not allowed` when trying to connect.

```
docker exec -it mysqldb_container bash
mysql -u root -p
ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY 'root';
FLUSH PRIVILEGES;
```

Build an image from the current dockerfile
```
docker build -t bankingapp .
```

Create a shared network

```
docker network create bankingapp-network
```

Link MySQL service container with our new shared network
to make MySQL service accessible from other containers.
```
docker network connect bankingapp-network mysqldb_container
```

Run banking app container

You could check & configure `.env` file as your want.
```
 docker run -d -p 8080:8080 --name bankingapp --network bankingapp-network --env-file .env bankingapp
```

Now our spring application will be running on port `8080` you could change it from the command above.

