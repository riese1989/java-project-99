FROM gradle:8.13-jdk21

WORKDIR /project

COPY . .

RUN chmod +x gradlew

RUN ./gradlew installDist --no-daemon -x test

CMD ["./build/install/java-project-99/bin/java-project-99"]

