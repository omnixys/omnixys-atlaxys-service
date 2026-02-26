# syntax=docker/dockerfile:1.14.0

ARG JAVA_VERSION=25

# ---------------------------------------------------------------------------------------
# Stage 1: builder (Maven Build)
# ---------------------------------------------------------------------------------------
FROM azul/zulu-openjdk:${JAVA_VERSION} AS builder

ARG APP_NAME
ARG APP_VERSION

WORKDIR /source

# Copy Maven wrapper + build descriptors first for better layer caching
COPY mvnw pom.xml ./
COPY .mvn ./.mvn

# If you use multi-module, also copy parent/child poms accordingly
# COPY pom.xml ./
# COPY module-a/pom.xml module-a/pom.xml
# ...

# Now copy sources
COPY src ./src

# Build the Spring Boot jar
RUN chmod +x ./mvnw && \
    ./mvnw -B -DskipTests package

# Extract Spring Boot layers (Boot 4.x)
RUN JAR_FILE=$(ls -1 target/*.jar | grep -v original | head -n 1) && \
    echo "Using JAR: $JAR_FILE" && \
    java -Djarmode=tools -jar "$JAR_FILE" extract --layers --destination extracted

# ---------------------------------------------------------------------------------------
# Stage 2: final (Production image with JRE)
# ---------------------------------------------------------------------------------------
FROM azul/zulu-openjdk:${JAVA_VERSION}-jre AS final

ARG APP_NAME
ARG APP_VERSION
ARG CREATED
ARG REVISION
ARG JAVA_VERSION

LABEL org.opencontainers.image.title="${APP_NAME}-service" \
      org.opencontainers.image.description="Omnixys ${APP_NAME}-service – Java ${JAVA_VERSION}, built with Maven, Version ${APP_VERSION}, basiert auf Azul Zulu & Ubuntu Jammy." \
      org.opencontainers.image.version="${APP_VERSION}" \
      org.opencontainers.image.licenses="GPL-3.0-or-later" \
      org.opencontainers.image.vendor="omnixys" \
      org.opencontainers.image.authors="caleb.gyamfi@omnixys.com" \
      org.opencontainers.image.base.name="azul/zulu-openjdk:${JAVA_VERSION}-jre" \
      org.opencontainers.image.url="https://github.com/omnixys/omnixys-${APP_NAME}-service" \
      org.opencontainers.image.source="https://github.com/omnixys/omnixys-${APP_NAME}-service" \
      org.opencontainers.image.created="${CREATED}" \
      org.opencontainers.image.revision="${REVISION}" \
      org.opencontainers.image.documentation="https://github.com/omnixys/omnixys-${APP_NAME}-service/blob/main/README.md"

WORKDIR /workspace

RUN apt-get update && \
    apt-get upgrade --yes && \
    apt-get install --no-install-recommends --yes dumb-init=1.2.5-2 wget && \
    apt-get autoremove -y && \
    apt-get clean -y && \
    rm -rf /var/lib/apt/lists/* /tmp/* && \
    groupadd --gid 1000 app && \
    useradd --uid 1000 --gid app --no-create-home app && \
    chown -R app:app /workspace

USER app

# Copy extracted layers
COPY --from=builder --chown=app:app /source/extracted/dependencies/ ./
COPY --from=builder --chown=app:app /source/extracted/spring-boot-loader/ ./
COPY --from=builder --chown=app:app /source/extracted/snapshot-dependencies/ ./
COPY --from=builder --chown=app:app /source/extracted/application/ ./

EXPOSE 8080

# Use http unless you terminate TLS inside the app container
HEALTHCHECK --interval=30s --timeout=3s --retries=1 \
    CMD wget -qO- http://localhost:8080/actuator/health | grep UP || exit 1

ENTRYPOINT ["dumb-init", "java", "--enable-preview", "-jar", "application.jar"]