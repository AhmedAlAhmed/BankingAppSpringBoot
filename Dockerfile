FROM maven:3-openjdk-16
WORKDIR /BankingAppSpringBoot
COPY . .
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} BankApp.jar
ENTRYPOINT ["java", "-jar", "BankApp.jar"]
