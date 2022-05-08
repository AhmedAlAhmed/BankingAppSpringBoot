# Banking Application Assignment

### RESTFul-based API contains the following functionalities
- Deposit money of `AED` into any account.
- Withdraw cash from any account.
- Transfer money between any two accounts.
- Ability to get a sorted & paginated list of accounts.
- Ability to query any account balance.

### Features
- Docker container.
- Cacheable the balance to avoid long response time.
- Cumulative balance column in `accounts` table to avoid re-calculate balances/transactions on every request. 
- Integrated with `Stripe` payment gateway to allow customers to use their own Visa/Master cards within our bank to perform international transfer operations.
- Integrated with `OpenAPI v3.0` so all end-points are auto-documented.
- Consumed by a small `ReactJS` page to allow employee to enter customer details without `MITM (Man In The Middle Attack)` attacks
  - We send the card details of the customer to `Stripe` server in order to generate a `token` we save this token in order to create a Stripe customer on `Stripe` server, then we save the `Stripe customer ID` within our accounts table, so we are able to send payments from customer card which is already linked with international banks.
- Added a simple API for login the employees into our banking system to grant them `JWT tokens`.



### Limitations: (as test)
- I supported only one currency `AED`, in case of support multiple ones, we need an integrations with market-based services to provide ability to get the rate between each pair of currencies.
- Worked without Job brokers services like SQS or Kafka.
- For a better handling of `Stripe` operations we need to upload the project on a real-server to support webhooks.

### Run as docker:

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


### Access OpenAPI docs
- Visit the link: `{{HOST}}:{{PORT}}/swagger-ui.html`
- You could change the link from `application.properties` file.


**NOTE: **

If you want to work with international transfer (Stripe) in our case, you need to follow the instructions in this repo

[https://github.com/AhmedAlAhmed/BankingAppReactJS](https://github.com/AhmedAlAhmed/BankingAppReactJS)

I have implemented a very simple code of UI in REACTJS to handle the communication with `Stripe` server.