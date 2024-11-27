### The builder stage, to compile the java app
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /build/

# Copy the pom.xml files to download dependencies
COPY pom.xml ./
COPY shorts-client/pom.xml shorts-client/pom.xml
COPY shorts-shared/pom.xml shorts-shared/pom.xml
COPY shorts-server/pom.xml shorts-server/pom.xml
COPY shorts-embed/pom.xml shorts-embed/pom.xml

# Download and install all the dependencies
RUN mvn dependency:go-offline

# Copy the src code into the image
COPY . ./

RUN mvn -Dmaven.test.skip=true clean package

### The runner stage, to run our app
FROM eclipse-temurin:17-jre AS runner

COPY --from=builder /build/shorts-embed/target/dist /app/shorts/dist

# Run
CMD ["java", "-jar", "/app/shorts/dist/shorts.jar", "/app/shorts/dist"]