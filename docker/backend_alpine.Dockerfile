FROM eclipse-temurin:11-jdk-alpine

LABEL maintainer="ySenih@erpya.com; EdwinBetanc0urt@outlook.com" \
	description="ADempiere Journal Import Service gRPC"

# Init ENV with default values
ENV \
	SERVER_PORT="50062" \
	SERVER_LOG_LEVEL="WARNING" \
	DB_HOST="localhost" \
	DB_PORT="5432" \
	DB_NAME="adempiere" \
	DB_USER="adempiere" \
	DB_PASSWORD="adempiere" \
	DB_TYPE="PostgreSQL" \
	ADEMPIERE_APPS_TYPE="" \
	TZ="America/Caracas"

EXPOSE ${SERVER_PORT}


# Add operative system dependencies
RUN	apk update && \
	apk add --no-cache \
		tzdata \
		bash \
	 	fontconfig \
		ttf-dejavu && \
	rm -rf /var/cache/apk/* && \
	rm -rf /tmp/* && \
	echo "Set Timezone..." && \
	echo $TZ > /etc/timezone


WORKDIR /opt/apps/server

# Copy src files
COPY docker/adempiere-journal-import-service /opt/apps/server
COPY docker/env.yaml /opt/apps/server/env.yaml
COPY docker/start.sh /opt/apps/server/start.sh


RUN addgroup adempiere && \
	adduser --disabled-password --gecos "" --ingroup adempiere --no-create-home adempiere && \
	chown -R adempiere /opt/apps/server/ && \
	chmod +x start.sh

USER adempiere

# Start app
ENTRYPOINT ["sh" , "start.sh"]
