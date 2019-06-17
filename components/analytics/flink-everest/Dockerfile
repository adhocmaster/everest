#
# Dockerfile to build/Initialize for Eliot Demo
#
# Initialization:
#        - Grafana Datasource with InfluxDB
#        - Grafana Dashboard
#        - Cassandra for Eliot Registry
#

FROM flink:1.6-alpine as base
FROM base as builder
RUN mkdir /install
WORKDIR /install


FROM base
#
COPY --from=builder /install /usr/local

RUN mkdir /app
COPY pipeline /app/pipeline

# Install curl, git, python
RUN apk add --no-cache curl influxdb python3 bash ca-certificates wget openssl iputils && \
    update-ca-certificates && \
    python3 -m ensurepip && \
    rm -r /usr/lib/python*/ensurepip && \
    pip3 install --upgrade pip setuptools && \
    if [ ! -e /usr/bin/pip ]; then ln -s pip3 /usr/bin/pip ; fi && \
    if [[ ! -e /usr/bin/python ]]; then ln -sf /usr/bin/python3 /usr/bin/python; fi && \
    rm -r /root/.cache && \
    pip install -r /app/pipeline/sinks/requirements.txt && \
    chmod +x /app/pipeline/*.sh

ADD https://storage.googleapis.com/kubernetes-release/release/v1.12.3/bin/linux/amd64/kubectl /usr/local/bin/kubectl
RUN chmod +x /usr/local/bin/kubectl 
# Basic check it works.
RUN  kubectl version --client

WORKDIR /app
CMD ["/bin/sh", "-c", "/app/pipeline/everest_cmd.sh"]
