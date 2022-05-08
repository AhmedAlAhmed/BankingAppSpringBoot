FROM maven:3-openjdk-16
WORKDIR /BankingAppSpringBoot
COPY . .
#RUN ["mvn", "pre-clean"]
#RUN ["mvn", "package"]
COPY target/BankManagement-0.0.1-SNAPSHOT.jar BankApp.jar
ENTRYPOINT ["java", "-jar", "BankApp.jar"]
#RUN mvn package
#CMD mvn spring-boot:run
