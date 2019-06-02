#
# Dockerfile to build/Initialize for Kafka + JMX Exporter as javaagent
#
#
# Description:
#        - based on:
#           kafka solsso:kafka:2.1.0
#           jmx exporter https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/0.6/jmx_prometheus_javaagent-0.6.jar
#

FROM solsson/kafka:2.1.0
ENV JMX_PROMETHEUS_JAVAAGENT_VERSION=0.6
ENV JMX_PROMETHEUS_JAVAAGENT_JAR=jmx_prometheus_javaagent-$JMX_PROMETHEUS_JAVAAGENT_VERSION.jar
ENV JMX_PROMETHEUS_JAVAAGENT_JAR_URL="https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/$JMX_PROMETHEUS_JAVAAGENT_VERSION/$JMX_PROMETHEUS_JAVAAGENT_JAR"
ENV KAFKA_CONFIG_YAML="kafka-0-8-2.yaml"
ENV KAFKA_OPTS="-javaagent:/opt/kafka/$JMX_PROMETHEUS_JAVAAGENT_JAR=5556:/opt/kafka/$KAFKA_CONFIG_YAML"

COPY $KAFKA_CONFIG_YAML /opt/kafka
RUN set -ex; \
  export DEBIAN_FRONTEND=noninteractive; \
  runDeps=''; \
  buildDeps='curl ca-certificates procps iputils-ping'; \
  apt-get update && apt-get install -y $runDeps $buildDeps --no-install-recommends; \
  apt-get update && apt-get install -y $buildDeps --no-install-recommends; \
  curl -SLs -o /opt/kafka/$JMX_PROMETHEUS_JAVAAGENT_JAR $JMX_PROMETHEUS_JAVAAGENT_JAR_URL

WORKDIR /opt/kafka

ENTRYPOINT ["docker-help"]